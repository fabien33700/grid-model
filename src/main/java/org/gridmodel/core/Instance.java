package org.gridmodel.core;

import org.gridmodel.core.errors.AlreadyBootedInstance;
import org.gridmodel.core.errors.NotBootedInstance;
import org.gridmodel.core.model.CustomColumn;
import org.gridmodel.core.model.Dataspace;
import org.gridmodel.core.model.ExtractionContext;
import org.gridmodel.core.model.Row;
import org.gridmodel.core.model.impl.DataspaceImpl;
import org.gridmodel.index.IndexAdapter;
import org.gridmodel.index.IndexTree;
import org.gridmodel.index.impl.ColumnIndexTree;
import org.gridmodel.index.impl.CustomIndexTree;
import org.gridmodel.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static org.gridmodel.Utils.isEmpty;
import static org.gridmodel.core.model.ExtractionContext.build;

/**
 * A GridModel instance.
 *
 * @author Fabien
 */
public class Instance {

    /**
     * Static constants
     */
    private static final String CSV_SEPARATOR = ";";
    private static final String MESSAGE_INJECTED = "File {} injected - {} row(s) proceeded in {} ms.";

    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * The instance configuration
     */
    private final Configuration configuration;

    /**
     * The instance dataspace
     */
    private final Dataspace dataspace;

    /**
     * A flag to know if instance has received data
     */
    private boolean booted = false;

    /**
     * The instance configuration
     */
    Instance() {
        configuration = new Configuration();
        dataspace = new DataspaceImpl();
    }

    public Dataspace dataspace() {
        return dataspace;
    }

    /**
     * Add an index for a column.
     * @param columnName The column name
     * @param adapter The index adapter function
     * @param <T> The column value type
     * @return The instance
     */
    public <T extends Comparable<T>> Instance index(
            String columnName, IndexAdapter<T> adapter) {
        if (booted) {
            throw new AlreadyBootedInstance();
        }
        configuration.indexes().put(columnName, adapter);
        return this;
    }

    /**
     * Add a custom column.
     * @param columnName The column name
     * @param adapter The custom function adapter
     * @param <T> The column value type
     * @return The instance
     */
    public <T extends Comparable<T>> Instance custom(
            String columnName, CustomColumn<T> adapter) {
        if (booted) {
            throw new AlreadyBootedInstance();
        }
        configuration.customColumns().put(columnName, adapter);
        return this;
    }

    /**
     * Query the instance, returning a query builder.
     * @param columns The columns to select
     * @return A QueryBuilder bound to the instance
     */
    public QueryBuilder query(String... columns) {
        if (!this.booted) {
            throw new NotBootedInstance();
        }
        return new QueryBuilder(this, columns);
    }

    /**
     * Appends a CSV source file to the instance, by loading it
     *   from class resources.
     * @param target The class in which search resources to load
     * @param sources An array of the sources to load
     * @return The instance
     * @throws URISyntaxException The given URI is not valid
     * @throws IOException The resource could not be loaded
     */
    public Instance append(Class<?> target, String... sources)
            throws URISyntaxException, IOException {
        for (String resource : sources) {
            append(target.getResource(resource).toURI());
        }
        return this;
    }

    /**
     * Appends a CSV source file to the instance, by loading it
     *   from filenames.
     * @param sourceFilenames An array of the sources to load
     * @return The instance
     * @throws URISyntaxException The given URI is not valid
     * @throws IOException The resource could not be loaded
     */
    public Instance append(String... sourceFilenames) throws URISyntaxException, IOException {
        for (String sourceFilename : sourceFilenames) {
            append(new URI(sourceFilename));
        }
        return this;
    }

    private void append(URI sourceUris) throws IOException {
        File sourceFile = prepareFileObject(sourceUris);
        // init index structures
        configuration.indexes().forEach((name, adapter) ->
                dataspace.indexesTrees().put(name, new ColumnIndexTree<>(name, adapter)));

        configuration.customColumns().forEach((name, custom) ->
                dataspace.indexesTrees().put(name, new CustomIndexTree<>(name, custom)));

        processInjection(sourceFile);
    }

    /**
     * Process the injection of the given file.
     * @param sourceFile The file from which inject data
     */
    @SuppressWarnings("unchecked")
    private void processInjection(File sourceFile) {
        booted = true;
        long count = dataspace.rowCount();
        long start = currentTimeMillis();

        // Opening the file
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile)))
        {
            String line = reader.readLine();
            Stream.of(line.split(CSV_SEPARATOR)).forEachOrdered(dataspace::addColumn);

            // For each file line
            while ((line = reader.readLine()) != null) {
                long rowIndex = dataspace.nextVal();
                String[] values = line.split(CSV_SEPARATOR);
                Row row = new Row();

                // For each values
                for (int i = 0; i < values.length; i++) {
                    String columnName = dataspace.columns().get(i);
                    row.put(columnName, values[i]);

                    // Index
                    if (dataspace.indexesTrees().containsKey(columnName) &&
                            !isEmpty(values[i])) {
                        IndexTree<String, ?> tree =
                                (IndexTree<String, ?>) dataspace.indexesTrees().get(columnName);
                        tree.append(values[i], rowIndex);
                    }
                }
                // Custom columns
                configuration.customColumns().forEach((name, custom) -> {
                    ExtractionContext ctx = build(sourceFile.getName(), name, rowIndex);
                    IndexTree<ExtractionContext, ?> tree =
                            (IndexTree<ExtractionContext, ?>) dataspace.indexesTrees().get(name);
                    tree.append(ctx, rowIndex);

                    row.put(name, custom.apply(ctx));
                    dataspace.addColumn(name);
                });

                // Adding the row
                dataspace.data().put(rowIndex, row);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        count = dataspace.rowCount() - count;
        logger.debug(MESSAGE_INJECTED, sourceFile.getName(), count, currentTimeMillis() - start);
    }

    private File prepareFileObject(URI sourceUri) throws IOException {
        try {
            if (!Files.exists(Paths.get(sourceUri))) {
                throw new FileNotFoundException(sourceUri.toString());
            }
            return new File(sourceUri);
        } catch (IllegalArgumentException ex) {
            throw new FileNotFoundException(sourceUri.toString());
        }
    }
}
