package org.gridmodel.core.model.sources;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;

public class FileSource implements Source {

    private final File file;

    public FileSource(String filename) throws FileNotFoundException {
        this(Paths.get(filename));
    }

    public FileSource(Path path) throws FileNotFoundException {
        requireNonNull(path);
        if (!Files.exists(path))
            throw new FileNotFoundException(path.getFileName().toString());

        this.file = path.toFile();
    }

    public FileSource(File file) {
        this.file = file;
    }

    @Override
    public String name() {
        return file.getName();
    }

    @Override
    public InputStream dataStream() throws IOException {
        return new FileInputStream(file);
    }
}
