package com.laptop.ltn.laptop_store_server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility for logging differences between objects in test cases.
 * This is useful for debugging test failures by clearly showing what changed.
 */
public class TestDataDiffLogger {
    private static final Logger logger = LoggerFactory.getLogger(TestDataDiffLogger.class);

    /**
     * Log differences between two objects of the same type.
     *
     * @param testName A descriptive name for the test context
     * @param original The original object
     * @param modified The modified object
     * @param <T>      Type of the objects being compared
     */
    public static <T> void logDiff(String testName, T original, T modified) {
        if (original == null && modified == null) {
            logger.info("[{}] Both objects are null - no differences", testName);
            return;
        }

        if (original == null) {
            logger.info("[{}] Original object is null, modified is not null", testName);
            return;
        }

        if (modified == null) {
            logger.info("[{}] Modified object is null, original is not null", testName);
            return;
        }

        if (!original.getClass().equals(modified.getClass())) {
            logger.info("[{}] Objects are of different types: {} vs {}",
                    testName, original.getClass().getName(), modified.getClass().getName());
            return;
        }

        List<String> differences = findDifferences(original, modified);

        if (differences.isEmpty()) {
            logger.info("[{}] No differences found between objects", testName);
        } else {
            logger.info("[{}] Found {} differences:", testName, differences.size());
            differences.forEach(diff -> logger.info("  - {}", diff));
        }
    }

    /**
     * Find differences between two objects by comparing their field values.
     */
    private static <T> List<String> findDifferences(T original, T modified) {
        List<String> differences = new ArrayList<>();
        Class<?> clazz = original.getClass();

        // Get all fields including inherited ones
        List<Field> allFields = getAllFields(clazz);

        for (Field field : allFields) {
            field.setAccessible(true);
            try {
                Object origValue = field.get(original);
                Object modValue = field.get(modified);

                if (!Objects.equals(origValue, modValue)) {
                    differences.add(String.format("%s: %s -> %s",
                            field.getName(), origValue, modValue));
                }
            } catch (IllegalAccessException e) {
                logger.warn("Could not access field: {}", field.getName(), e);
            }
        }

        return differences;
    }

    /**
     * Get all fields from a class and its superclasses.
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();

        // Get fields from this class and all superclasses
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                fields.add(field);
            }
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }
}
