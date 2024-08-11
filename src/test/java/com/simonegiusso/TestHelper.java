package com.simonegiusso;

import lombok.Value;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UtilityClass
class TestHelper {

    static Collection<String> readWordsFromFile(Path filePath) {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static <T> T printExecutionTime(Callable<T> codeBlock, String message, int iterations) {
        long averageExecutionTime = 0;
        TimedResult<T> timedResult = null;

        for (int i = 0; i < iterations; i++) {
            timedResult = runWithTiming(codeBlock);
            averageExecutionTime += timedResult.getComputationTime() / iterations;
        }

        System.out.println(message + ". Execution time: " + averageExecutionTime + " milliseconds");
        return timedResult.getResult();
    }

    private <T> TimedResult<T> runWithTiming(Callable<T> codeBlock) {
        long startTime = currentTimeMillis();
        T result;
        try {
            result = codeBlock.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new TimedResult(result, currentTimeMillis() - startTime);
    }

    static <T> void assertEachElementIsEqual(Collection<T> collection) {
        var array = collection.toArray();

        for (int i = 0; i + 1 < array.length; i++) {
            assertEquals(array[i], array[i + 1]);
        }
    }

    static String generateRandomWord(int length) {
        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            char randomChar = (char) ('a' + random.nextInt('z' - 'a' + 1));
            sb.append(randomChar);
        }
        return sb.toString();
    }

    @Value
    class TimedResult<T> {
        T result;
        long computationTime;
    }

}
