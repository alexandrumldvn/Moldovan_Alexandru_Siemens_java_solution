# Problem 2 - File Duplicate Finder

This is my optional Problem 2. It is a small standalone Java app, not related to the train app from Problem 1.

What it does

You give it a folder. It walks through the folder (and all subfolders), finds files that have the exact same content, and prints them grouped together. It also tells you how much space the duplicate copies are wasting.


Idea behind the solution

The straight forward way would be: for every pair of files, compare them byte by byte. That is N*N work and would be slow on big folders.

I did it like this instead:

1. Walk the folder and collect every regular file.
2. Group the files by their size. Two files of different sizes can never be duplicates, so this already throws out most of the work.
3. For each size group that has 2 or more files, compute the SHA-256 hash of each file.
4. Group those by hash. Any group with 2 or more files is a set of duplicates.
5. Sort the result: the groups with the most copies first.

Why size first: hashing reads the whole file, which is expensive. The size check is basically free, so it makes sense to filter with it before hashing.

Files in the project

- `FileHasher` - utility that reads a file in chunks and returns its SHA-256 as a hex string.
- `DupGroup` - small object that holds one group: hash, file size, and the list of paths.
- `DuplicateFinder` - the main logic. Walks the folder and returns a list of `DupGroup`.
- `Main` - parses the folder argument and prints the result.

I did not use any fancy design pattern here. The app is small so I kept it as 3 plain classes and a `Main`.

I used streams a few times: `Files.walk(...).filter(Files::isRegularFile)`, `Collectors.groupingBy(this::sizeOf)`, and a stream to filter the hash buckets that have 2+ files.

Tests

JUnit 5, in `src/test/java`. They use `@TempDir` so the tests do not touch the real disk.

- `findsTwoIdenticalFiles` - two files with the same content, one without.
- `noDupsReturnsEmpty` - three different files, no result.
- `worksInNestedFolders` - duplicate split across a subfolder.
- `sameSizeButDifferentContentNotDup` - two files of equal size but different bytes, hash should split them.
- `multipleGroupsReturned` - 3 copies of one thing and 2 copies of another, both groups returned, biggest one first.
- `rejectsNonDir` - passing a file path instead of a directory throws `IllegalArgumentException`.

All 6 tests pass.

Example input and output

I made a test folder like this:


/tmp/dup-demo/
  a.txt          "hello\n"
  b.txt          "hello\n"
  d.txt          "different\n"
  e.txt          "abc\n"
  sub/
    c.txt        "hello\n"
    f.txt        "abc\n"


Output:

found 2 duplicate group(s) under /tmp/dup-demo

group 1: 3 copies, 6 bytes each
hash: 5891b5b522d5df086d0ff0b110fbd9d21bb4fc7163af34d08286a2e846f6be03
  /tmp/dup-demo/b.txt
  /tmp/dup-demo/sub/c.txt
  /tmp/dup-demo/a.txt

group 2: 2 copies, 4 bytes each
hash: edeaaff3f1774ad2888673770c6d64097e391bc362d7d6fb34982ddf0efd18cb
  /tmp/dup-demo/sub/f.txt
  /tmp/dup-demo/e.txt

wasted space (extra copies): 16 bytes


"wasted space" is the size of the extra copies. For a group of 3 files, the first one is the "original" and the other 2 are wasted.

If you run it on a folder with no duplicates, you get:

no duplicate files found under /tmp/whatever


If you give it a path that is not a folder you get an error:

Exception in thread "main" java.lang.IllegalArgumentException: not a directory: /tmp/some-file.txt


Things I did not do

- I did not add a flag to delete the duplicates. The point of this small project is to find them, not to risk breaking the user's files.
- I did not parallelize the hashing. For a student project this is fast enough.
- I skip files that cannot be read instead of crashing. That seemed safer than blowing up in the middle of a scan.
