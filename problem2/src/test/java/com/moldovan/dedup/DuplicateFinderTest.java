package com.moldovan.dedup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateFinderTest {

    private final DuplicateFinder finder = new DuplicateFinder();

    @Test
    void findsTwoIdenticalFiles(@TempDir Path tmp) throws Exception {
        Files.writeString(tmp.resolve("a.txt"), "hello world");
        Files.writeString(tmp.resolve("b.txt"), "hello world");
        Files.writeString(tmp.resolve("c.txt"), "different");

        List<DupGroup> groups = finder.findDuplicates(tmp);
        assertEquals(1, groups.size());
        DupGroup g = groups.get(0);
        assertEquals(2, g.getFiles().size());
        assertEquals("hello world".getBytes().length, g.getSizeBytes());
    }

    @Test
    void noDupsReturnsEmpty(@TempDir Path tmp) throws Exception {
        Files.writeString(tmp.resolve("a.txt"), "one");
        Files.writeString(tmp.resolve("b.txt"), "two");
        Files.writeString(tmp.resolve("c.txt"), "three");

        List<DupGroup> groups = finder.findDuplicates(tmp);
        assertTrue(groups.isEmpty());
    }

    @Test
    void worksInNestedFolders(@TempDir Path tmp) throws Exception {
        Path sub = Files.createDirectory(tmp.resolve("sub"));
        Files.writeString(tmp.resolve("top.txt"), "same content");
        Files.writeString(sub.resolve("nested.txt"), "same content");

        List<DupGroup> groups = finder.findDuplicates(tmp);
        assertEquals(1, groups.size());
        assertEquals(2, groups.get(0).getFiles().size());
    }

    @Test
    void sameSizeButDifferentContentNotDup(@TempDir Path tmp) throws Exception {
        Files.writeString(tmp.resolve("a.txt"), "abcd");
        Files.writeString(tmp.resolve("b.txt"), "wxyz");

        List<DupGroup> groups = finder.findDuplicates(tmp);
        assertTrue(groups.isEmpty());
    }

    @Test
    void multipleGroupsReturned(@TempDir Path tmp) throws Exception {
        Files.writeString(tmp.resolve("a1.txt"), "aaa");
        Files.writeString(tmp.resolve("a2.txt"), "aaa");
        Files.writeString(tmp.resolve("a3.txt"), "aaa");
        Files.writeString(tmp.resolve("b1.txt"), "bb");
        Files.writeString(tmp.resolve("b2.txt"), "bb");

        List<DupGroup> groups = finder.findDuplicates(tmp);
        assertEquals(2, groups.size());
        assertEquals(3, groups.get(0).getFiles().size());
        assertEquals(2, groups.get(1).getFiles().size());
    }

    @Test
    void rejectsNonDir(@TempDir Path tmp) throws Exception {
        Path f = tmp.resolve("just-a-file.txt");
        Files.writeString(f, "x");
        assertThrows(IllegalArgumentException.class, () -> finder.findDuplicates(f));
    }
}
