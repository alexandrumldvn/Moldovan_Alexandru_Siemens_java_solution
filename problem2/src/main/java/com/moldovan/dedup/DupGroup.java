package com.moldovan.dedup;

import java.nio.file.Path;
import java.util.List;

public class DupGroup {

    private final String hash;
    private final long sizeBytes;
    private final List<Path> files;

    public DupGroup(String hash, long sizeBytes, List<Path> files) {
        this.hash = hash;
        this.sizeBytes = sizeBytes;
        this.files = List.copyOf(files);
    }

    public String getHash() { return hash; }
    public long getSizeBytes() { return sizeBytes; }
    public List<Path> getFiles() { return files; }

    @Override
    public String toString() {
        return "DupGroup{hash=" + hash + " size=" + sizeBytes
                + " count=" + files.size() + " files=" + files + "}";
    }
}
