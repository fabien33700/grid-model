package org.gridmodel.core;

import org.gridmodel.core.errors.AlreadyBootedInstanceException;
import org.gridmodel.core.errors.NotBootedInstanceException;
import org.gridmodel.core.model.CustomColumn;
import org.gridmodel.core.model.Dataspace;
import org.gridmodel.core.model.ExtractionContext;
import org.gridmodel.core.model.Row;
import org.gridmodel.core.model.impl.DataspaceImpl;
import org.gridmodel.core.model.sources.Source;
import org.gridmodel.index.IndexAdapter;
import org.gridmodel.index.IndexTree;
import org.gridmodel.index.impl.ColumnIndexTree;
import org.gridmodel.index.impl.CustomIndexTree;
import org.gridmodel.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static org.gridmodel.core.model.ExtractionContext.build;
import static org.gridmodel.misc.Utils.*;

/**
 * A GridModel instance.
 *   Represents the memory model containing data imported from CSV file,
 *   and also all the tools that allows to querying and presenting those data.
 *   (developed for educational purposes)
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
            throw new AlreadyBootedInstanceException();
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
            throw new AlreadyBootedInstanceException();
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
            throw new NotBootedInstanceException();
        }
        return new QueryBuilder(this, columns);
    }

    /**
     * Query the instance, returning a query builder.
     * @param columns The columns to select (iterable)
     * @return A QueryBuilder bound to the instance
     */
    public QueryBuilder query(Iterable<String> columns) {
        String[] arrayColumns = fromIterable(columns, ArrayList::new).toArray(new String[] {});
        return query(arrayColumns);
    }

    /**
     * Appends a CSV source file to the instance, by loading it
     *   from filenames.
     * @param source The source to load
     * @return The instance
     * @throws IOException The resource could not be loaded
     */
    public Instance append(Source source) throws IOException {
        if (!booted) {
            initIndexStructure();
            booted = true;
        }

        processInjection(source);
        return this;
    }

    public Instance append(Iterable<Source> sources) throws IOException {
        for (Source source : sources)
            append(source);

        return this;
    }

    private void initIndexStructure() {
        configuration.indexes().forEach((name, adapter) ->
                dataspace.indexesTrees().put(name, new ColumnIndexTree<>(name, adapter)));

        configuration.customColumns().forEach((name, custom) ->
                dataspace.indexesTrees().put(name, new CustomIndexTree<>(name, custom)));
    }

    /**
     * Process the injection of the given file.
     * @param source The data source from which inject data
     */
    @SuppressWarnings("unchecked")
    private void processInjection(Source source) throws IOException {
        long count = dataspace.rowCount();
        long start = currentTimeMillis();

        // Opening the file
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(source.dataStream())))
        {

            String line = reader.readLine();
            List<String> injectedColumns = new ArrayList<>(asList(line.split(CSV_SEPARATOR)));

            // For each file line
            while ((line = reader.readLine()) != null) {
                long rowIndex = dataspace.nextVal();
                String[] values = line.split(CSV_SEPARATOR);
                Row row = new Row();

                // For each values
                for (int i = 0; i < values.length; i++) {
                    String columnName = injectedColumns.get(i);
                    row.put(columnName, values[i]);

                    // Index
                    if (dataspace.indexesTrees().containsKey(columnName) && // current column is indexed
                            !isEmpty(values[i]) && // current row has a value
                            !configuration.customColumns().keySet().contains(columnName))  // current column is not custom
                    {
                        IndexTree<String, ?> tree =
                                (IndexTree<String, ?>) dataspace.indexesTrees().get(columnName);
                        tree.append(values[i], rowIndex);
                    }
                }
                // Custom columns
                configuration.customColumns().forEach((name, custom) -> {
                    ExtractionContext ctx = build(source.name(), name, rowIndex);
                    IndexTree<ExtractionContext, ?> tree =
                            (IndexTree<ExtractionContext, ?>) dataspace.indexesTrees().get(name);
                    tree.append(ctx, rowIndex);

                    row.put(name, custom.apply(ctx));
                    dataspace.addColumn(name);
                });

                // Adding the row
                dataspace.data().put(rowIndex, row);
            }

            // Merging the new columns in the dataspace's columns
            injectedColumns.stream()
                    .filter(not(dataspace.columns()::contains))
                    .forEachOrdered(dataspace.columns()::add);

            Collections.sort(dataspace.columns());
        }

        count = dataspace.rowCount() - count;
        logger.debug(MESSAGE_INJECTED, source.name(), count, currentTimeMillis() - start);
    }
}
