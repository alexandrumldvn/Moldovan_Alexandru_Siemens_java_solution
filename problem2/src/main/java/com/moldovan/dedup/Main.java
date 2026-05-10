package com.moldovan.dedup;

import java.nio.file.Path;
import java.util.List;

public final class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("usage: java -jar dup-finder.jar <folder>");
            System.exit(1);
        }
        Path root = Path.of(args[0]);
        DuplicateFinder finder = new DuplicateFinder();

        List<DupGroup> groups = finder.findDuplicates(root);

        if (groups.isEmpty()) {
            System.out.println("no duplicate files found under " + root.toAbsolutePath());
            return;
        }

        System.out.println("found " + groups.size() + " duplicate group(s) under "
                + root.toAbsolutePath());
        long wasted = 0;
        for (int i = 0; i < groups.size(); i++) {
            DupGroup g = groups.get(i);
            System.out.println();
            System.out.println("group " + (i + 1) + ": " + g.getFiles().size()
                    + " copies, " + g.getSizeBytes() + " bytes each");
            System.out.println("hash: " + g.getHash());
            g.getFiles().forEach(p -> System.out.println("  " + p));
            wasted += (long) (g.getFiles().size() - 1) * g.getSizeBytes();
        }
        System.out.println();
        System.out.println("wasted space (extra copies): " + wasted + " bytes");
    }
}
