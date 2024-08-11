package com.simonegiusso;

import com.github.rohansuri.art.AdaptiveRadixTree;
import com.github.rohansuri.art.BinaryComparables;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.GraphLayout;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.simonegiusso.MathUtils.logBase2;
import static com.simonegiusso.TestHelper.assertEachElementIsEqual;
import static com.simonegiusso.TestHelper.generateRandomWord;
import static com.simonegiusso.TestHelper.printExecutionTime;
import static com.simonegiusso.TestHelper.readWordsFromFile;

class AdaptiveRadixTreeTest {

    private static final boolean COMPUTE_MEMORY_USAGE = false;

    private static final Path BASE_FILE_PATH = Paths.get("src", "test", "resources");
    private static final Path WORDS_FILE_PATH = Paths.get(BASE_FILE_PATH.toString(), "actors.txt");
    private static final Path WORDS_TO_FIND_FILE_PATH = Paths.get(BASE_FILE_PATH.toString(), "actorsToFind.txt");

    @Test
    void beachMarkWithWords() {
        runTestWith(
                readWordsFromFile(WORDS_FILE_PATH),
                readWordsFromFile(WORDS_TO_FIND_FILE_PATH),
                new TreeMap<>(),
                new AdaptiveRadixTree<>(BinaryComparables.forString())
        );
    }

    @Test
    void beachMarkWithWordsWithCommonPrefix() {
        String prefix = generateRandomWord(500);

        runTestWith(
                readWordsFromFile(WORDS_FILE_PATH).stream().map(s -> prefix + s).toList(),
                readWordsFromFile(WORDS_TO_FIND_FILE_PATH).stream().map(s -> prefix + s).toList(),
                new TreeMap<>(),
                new AdaptiveRadixTree<>(BinaryComparables.forString())
        );
    }

    private void runTestWith(
            Collection<String> input,
            Collection<String> inputToFind,
            Map<String, Object>... maps
    ) {
        System.out.printf("Log_2(%d) = %f%n", input.size(), logBase2(input.size()));
        System.out.printf("K = %d%n", inputToFind.stream().mapToInt(String::length).max().getAsInt());
        System.out.printf("Total checks = %d%n", inputToFind.size());
        List<Long> hits = new LinkedList<>();

        for (Map<String, Object> map : maps) {
            hits.add(measureMapImplementation(map, input, inputToFind));
        }

        System.out.printf("%nNumber of hits: %d%n", hits.get(0));
        assertEachElementIsEqual(hits);
    }

    private static long measureMapImplementation(Map<String, Object> map, Collection<String> words, Collection<String> wordsToCheck) {
        String implementationName = map.getClass().getSimpleName();
        addWords(map, words);
        if (COMPUTE_MEMORY_USAGE)
            System.out.println("Memory usage in KB: " + GraphLayout.parseInstance(map).totalSize() / 1024);
        return printExecutionTime(() -> containsCheck(map, wordsToCheck), implementationName + " contains", 50);
    }

    private static void addWords(Map<String, Object> map, Collection<String> words) {
        for (String word : words) {
            map.put(word, new Object());
        }

    }

    private static <T> long containsCheck(Map<String, T> map, Collection<String> words) {
        long wordsFound = 0;
        for (String word : words) {
            wordsFound += map.containsKey(word) ? 1 : 0;
        }
        return wordsFound;
    }
}
