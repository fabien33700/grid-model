package org.flatbase.core;

import org.flatbase.dataspace.DataSpace;
import org.flatbase.dataspace.DataSpaceImpl;
import org.flatbase.index.IndexDefinitionBase;
import org.flatbase.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;
import static org.flatbase.utils.Utils.isEmpty;
import static org.flatbase.utils.Utils.split;
import static org.flatbase.utils.Utils.format;

public class Instance {

    private static final String CSV_SEPARATOR = ";";
    private static final String MESSAGE_INJECTED = "File {} injected - {} row(s) proceeded in {} ms.";
    private static final String ENABLED_DEBUG = "Debugging logs enabled.";

    private Configuration configuration;
    private DataSpace dataspace;

    private boolean booted = false;
    private boolean debug = false;

    private Logger logger;

    Instance() {
        configuration = new Configuration();
        dataspace = new DataSpaceImpl();
    }

    public void debug(String message, Object... args) {
        if (!debug) return;

        if (logger == null)
            logger = LoggerFactory.getLogger(getClass());

        logger.debug(message, args);
    }

    public DataSpace dataspace() {
        return dataspace;
    }

    @SuppressWarnings("unchecked")
    public <T> Instance index(String columnName,
                              Function<String, T> adapter,
                              Comparator<T> comparator) {
        if (booted) {
            throw new IllegalStateException("An already booted instance cannot be configured anymore.");
        }
        configuration.indexDefs().put(columnName, new IndexDefinitionBase<>(adapter, comparator));
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
        processInjection(sourceFile);
    }

    @SuppressWarnings("unchecked")
    private void processInjection(File sourceFile) {
        booted = true;

        long start = currentTimeMillis();
        long volume = dataspace.volume();
        try (
                BufferedReader reader = new BufferedReader(
                        new FileReader(sourceFile)))
        {
            String line = reader.readLine();
            List<String> columns = split(line, CSV_SEPARATOR);

            while ((line = reader.readLine()) != null) {
                List<String> values = split(line, CSV_SEPARATOR);
                Map<String, String> valuesMap = new HashMap<>();

                Iterator<String> itColumns = columns.iterator();
                Iterator<String> itValues = values.iterator();

                while (itColumns.hasNext() && itValues.hasNext()) {
                    String nextColumn = itColumns.next();
                    String nextValue = itValues.next();
                    if (!isEmpty(nextValue)) {
                        valuesMap.put(nextColumn, nextValue.trim());
                    }
                }
                dataspace.incSequence();

                valuesMap.keySet().stream()
                    .filter(configuration.indexDefs()::containsKey)
                    .filter(valuesMap::containsKey)
                    .forEachOrdered(c -> dataspace.index()
                            .putIndexData(c, dataspace.sequence(),
                                    valuesMap.get(c), configuration.indexDefs().get(c)));

                dataspace.data().put(dataspace.sequence(), valuesMap);
                dataspace.columns().addAll(columns);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        volume = dataspace.volume() - volume;
        debug(MESSAGE_INJECTED, sourceFile.getName(), volume, currentTimeMillis() - start);
    }



    private File prepareFileObject(URI sourceUri) throws FileNotFoundException {
        if (!Files.exists(Paths.get(sourceUri))) {
            throw new FileNotFoundException(sourceUri.toString());
        }
        return new File(sourceUri);
    }

    public Instance enableDebug() {
        debug = true;
        debug(ENABLED_DEBUG);
        return this;
    }
}
