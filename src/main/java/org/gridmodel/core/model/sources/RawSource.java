package org.gridmodel.core.model.sources;

import java.io.InputStream;

public class RawSource implements Source {

    private final String sourceName;

    private final InputStream inputStream;

    public RawSource(String sourceName,
                     InputStream inputStream) {
        this.sourceName = sourceName;
        this.inputStream = inputStream;
    }

    @Override
    public String name() {
        return sourceName;
    }

    @Override
    public final InputStream dataStream() {
        return inputStream;
    }
}
