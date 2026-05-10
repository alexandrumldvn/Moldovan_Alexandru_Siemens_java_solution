package com.moldovan.dedup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DuplicateFinder {

    public List<DupGroup> findDuplicates(Path root) throws IOException {
        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("not a directory: " + root);
        }

        List<Path> allFiles;
        try (Stream<Path> walk = Files.walk(root)) {
            allFiles = walk.filter(Files::isRegularFile).collect(Collectors.toList());
        }

        Map<Long, List<Path>> bySize = allFiles.stream()
                .collect(Collectors.groupingBy(this::sizeOf));

        List<DupGroup> result = new ArrayList<>();
        for (Map.Entry<Long, List<Path>> e : bySize.entrySet()) {
            if (e.getValue().size() < 2) continue;
            long size = e.getKey();

            Map<String, List<Path>> byHash = new HashMap<>();
            for (Path p : e.getValue()) {
                String h = hashSafe(p);
                if (h == null) continue; // unreadable, skip
                byHash.computeIfAbsent(h, k -> new ArrayList<>()).add(p);
            }

            byHash.entrySet().stream()
                    .filter(en -> en.getValue().size() >= 2)
                    .map(en -> new DupGroup(en.getKey(), size, en.getValue()))
                    .forEach(result::add);
        }

        result.sort((a, b) -> {
            int c = Integer.compare(b.getFiles().size(), a.getFiles().size());
            if (c != 0) return c;
            return Long.compare(b.getSizeBytes(), a.getSizeBytes());
        });
        return result;
    }

    private long sizeOf(Path p) {
        try {
            return Files.size(p);
        } catch (IOException e) {
            return -1L; // unreadable, will land in its own bucket of (likely) one file
        }
    }

    private String hashSafe(Path p) {
        try {
            return FileHasher.sha256(p);
        } catch (IOException e) {
            return null;
        }
    }
}
