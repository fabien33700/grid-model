package org.gridmodel.core.model.sources;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents the behavior of a source. A source is the object
 * from which the instance retrieves raw data.
 * @author Fabien
 */
public interface Source {
    /**
     * Returns the source name
     * @return A string that identify the source
     */
    String name();

    /**
     * The data stream from the source
     * @return an input stream
     */
    InputStream dataStream() throws IOException;
}
