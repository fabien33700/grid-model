package org.flatbase.core;

import org.flatbase.index.IndexDefinition;
import org.flatbase.index.IndexStructure;
import org.flatbase.model.Dataspace;
import org.flatbase.model.DataspaceImpl;
import org.flatbase.model.Row;
import org.flatbase.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static org.flatbase.misc.Utils.isEmpty;

/**
 * A Flatbase instance.
 * @author Fabien
 */
public class Instance {

    private static final String CSV_SEPARATOR = ";";
    private static final String MESSAGE_INJECTED = "File {} injected - {} row(s) proceeded in {} ms.";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Configuration configuration;
    private Dataspace dataspace;
    private boolean booted = false;

    Instance() {
        configuration = new Configuration();
        dataspace = new DataspaceImpl();
    }

    public Dataspace dataspace() {
        return dataspace;
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> Instance index(
            String columnName,
            Function<String, T> adapter) {
        if (booted) {
            throw new IllegalStateException("An already booted instance cannot be configured anymore.");
        }
        configuration.indexes().put(columnName, new IndexDefinition<>(columnName, adapter));
        return this;
    }

    public QueryBuilder query(String ... columns) {
        return new QueryBuilder(this, columns);
    }

    public Instance inject(Class<?> target, String ... resources)
            throws URISyntaxException, IOException {
        for (String resource : resources) {
            inject0(target.getResource(resource).toURI());
        }
        return this;
    }

    public Instance inject(String ... sourceFilenames)
            throws IOException, URISyntaxException {
        for (String sourceFilename : sourceFilenames) {
            inject0(new URI(sourceFilename));
        }
        return this;
    }

    private void inject0(URI sourceUris) throws IOException {
        File sourceFile = prepareFileObject(sourceUris);
        initIndexStructures();
        processInjection(sourceFile);
    }

    private void initIndexStructures() {
        configuration.indexes().forEach((name, definition) ->
            dataspace.structures().put(name, new IndexStructure<>(definition)));
    }

    @SuppressWarnings("unchecked")
    private void processInjection(File sourceFile) {
        booted = true;
        long count = dataspace.rowCount();
        long start = currentTimeMillis();
        try (
                BufferedReader reader = new BufferedReader(
                        new FileReader(sourceFile)))
        {
            String line = reader.readLine();
            Stream.of(line.split(CSV_SEPARATOR))
                    .forEachOrdered(dataspace.columns()::add);

            while ((line = reader.readLine()) != null) {
                long rowIndex = dataspace.nextVal();
                String[] values = line.split(CSV_SEPARATOR);
                Row row = new Row();
                for (int i = 0; i < values.length; i++) {
                    String columnName = dataspace.columns().get(i);
                    row.put(columnName, values[i]);

                    if (dataspace.structures().containsKey(columnName) &&
                        !isEmpty(values[i])) {
                        dataspace.structures().get(columnName)
                                .append(values[i], rowIndex);
                    }
                }

                // Custom columns
                dataspace.data().put(rowIndex, row);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        count = dataspace.rowCount() - count;
        logger.debug(MESSAGE_INJECTED, sourceFile.getName(), count, currentTimeMillis() - start);
    }



    private File prepareFileObject(URI sourceUri) throws FileNotFoundException {
        if (!Files.exists(Paths.get(sourceUri))) {
            throw new FileNotFoundException(sourceUri.toString());
        }
        return new File(sourceUri);
    }
}
