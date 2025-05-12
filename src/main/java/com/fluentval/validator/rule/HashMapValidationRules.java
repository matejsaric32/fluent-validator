package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.MapValidationMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.fluentval.validator.rule.ValidationRuleUtils.createSkipNullRule;

public final class HashMapValidationRules {

    private HashMapValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static <K, V> boolean isNotEmpty(final Map<K, V> map) {
            return !map.isEmpty();
        }

        static <K, V> boolean isEmpty(final Map<K, V> map) {
            return map.isEmpty();
        }

        static <K, V> boolean hasMinimumSize(final Map<K, V> map, final int minSize) {
            return map.size() >= minSize;
        }

        static <K, V> boolean hasMaximumSize(final Map<K, V> map, final int maxSize) {
            return map.size() <= maxSize;
        }

        static <K, V> boolean hasExactSize(final Map<K, V> map, final int exactSize) {
            return map.size() == exactSize;
        }

        static <K, V> boolean isInSizeRange(final Map<K, V> map, final int minSize, final int maxSize) {
            return map.size() >= minSize && map.size() <= maxSize;
        }

        static <K, V> boolean containsKey(final Map<K, V> map, final K key) {
            return map.containsKey(key);
        }

        static <K, V> boolean doesNotContainKey(final Map<K, V> map, final K key) {
            return !map.containsKey(key);
        }

        static <K, V> boolean containsAllKeys(final Map<K, V> map, final Collection<K> keys) {
            return map.keySet().containsAll(keys);
        }

        static <K, V> boolean containsValue(final Map<K, V> map, final V value) {
            return map.containsValue(value);
        }

        static <K, V> boolean doesNotContainValue(final Map<K, V> map, final V value) {
            return !map.containsValue(value);
        }

        static <K, V> boolean allKeysMatch(final Map<K, V> map, final Predicate<K> condition) {
            return map.keySet().stream().allMatch(condition);
        }

        static <K, V> boolean allValuesMatch(final Map<K, V> map, final Predicate<V> condition) {
            return map.values().stream().allMatch(condition);
        }

        static <K, V> boolean allEntriesMatch(final Map<K, V> map, final BiPredicate<K, V> condition) {
            return map.entrySet().stream().allMatch(entry -> condition.test(entry.getKey(), entry.getValue()));
        }

        static <K, V> boolean anyKeyMatches(final Map<K, V> map, final Predicate<K> condition) {
            return !map.isEmpty() && map.keySet().stream().anyMatch(condition);
        }

        static <K, V> boolean anyValueMatches(final Map<K, V> map, final Predicate<V> condition) {
            return !map.isEmpty() && map.values().stream().anyMatch(condition);
        }

        static <K, V> boolean anyEntryMatches(final Map<K, V> map, final BiPredicate<K, V> condition) {
            return !map.isEmpty() && map.entrySet().stream().anyMatch(entry -> condition.test(entry.getKey(), entry.getValue()));
        }

        static <K, V> boolean noKeyMatches(final Map<K, V> map, final Predicate<K> condition) {
            return map.keySet().stream().noneMatch(condition);
        }

        static <K, V> boolean noValueMatches(final Map<K, V> map, final Predicate<V> condition) {
            return map.values().stream().noneMatch(condition);
        }

        static <K, V> boolean noEntryMatches(final Map<K, V> map, final BiPredicate<K, V> condition) {
            return map.entrySet().stream().noneMatch(entry -> condition.test(entry.getKey(), entry.getValue()));
        }
    }

    public static <K, V> ValidationRule<Map<K, V>> notEmpty() {
        return createSkipNullRule(
                ValidationFunctions::isNotEmpty,
                MapValidationMetadata::notEmpty
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> isEmpty() {
        return createSkipNullRule(
                ValidationFunctions::isEmpty,
                MapValidationMetadata::isEmpty
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> minSize(final int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.hasMinimumSize(map, min),
                identifier -> MapValidationMetadata.minSize(identifier, min)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> maxSize(final int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Maximum size cannot be negative");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.hasMaximumSize(map, max),
                identifier -> MapValidationMetadata.maxSize(identifier, max)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> exactSize(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.hasExactSize(map, size),
                identifier -> MapValidationMetadata.exactSize(identifier, size, size)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> sizeRange(final int min, final int max) {
        if (min < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }

        if (max < 0) {
            throw new IllegalArgumentException("Maximum size cannot be negative");
        }

        if (min > max) {
            throw new IllegalArgumentException("Minimum size cannot be greater than maximum size");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isInSizeRange(value, min, max)) {
                result.addFailure(new ValidationResult.Failure(
                        MapValidationMetadata.sizeRange(identifier, min, max, value.size())
                ));
            }
        };
    }

    public static <K, V> ValidationRule<Map<K, V>> containsKey(K key) {
        Objects.requireNonNull(key, "Key cannot be null");

        return createSkipNullRule(
                map -> ValidationFunctions.containsKey(map, key),
                identifier -> MapValidationMetadata.containsKey(identifier, key)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> doesNotContainKey(K key) {
        Objects.requireNonNull(key, "Key cannot be null");

        return createSkipNullRule(
                map -> ValidationFunctions.doesNotContainKey(map, key),
                identifier -> MapValidationMetadata.doesNotContainKey(identifier, key)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> containsAllKeys(Collection<K> keys) {
        Objects.requireNonNull(keys, "Keys collection cannot be null");

        if (keys.isEmpty()) {
            throw new IllegalArgumentException("Keys collection cannot be empty");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.containsAllKeys(map, keys),
                identifier -> MapValidationMetadata.containsAllKeys(identifier, keys)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> containsValue(V value) {
        Objects.requireNonNull(value, "Value cannot be null");

        return createSkipNullRule(
                map -> ValidationFunctions.containsValue(map, value),
                identifier -> MapValidationMetadata.containsValue(identifier, value)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> doesNotContainValue(V value) {
        Objects.requireNonNull(value, "Value cannot be null");

        return createSkipNullRule(
                map -> ValidationFunctions.doesNotContainValue(map, value),
                identifier -> MapValidationMetadata.doesNotContainValue(identifier, value)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> allKeysMatch(Predicate<K> condition, String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.allKeysMatch(map, condition),
                identifier -> MapValidationMetadata.allKeysMatch(identifier, condition, conditionDescription)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> allValuesMatch(Predicate<V> condition, String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.allValuesMatch(map, condition),
                identifier -> MapValidationMetadata.allValuesMatch(identifier, condition, conditionDescription)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> allEntriesMatch(BiPredicate<K, V> condition, String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.allEntriesMatch(map, condition),
                identifier -> MapValidationMetadata.allEntriesMatch(identifier, condition, conditionDescription)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> anyKeyMatches(Predicate<K> condition, String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.anyKeyMatches(map, condition),
                identifier -> MapValidationMetadata.anyKeyMatches(identifier, condition, conditionDescription)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> anyValueMatches(Predicate<V> condition, String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.anyValueMatches(map, condition),
                identifier -> MapValidationMetadata.anyValueMatches(identifier, condition, conditionDescription)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> anyEntryMatches(BiPredicate<K, V> condition, String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.anyEntryMatches(map, condition),
                identifier -> MapValidationMetadata.anyEntryMatches(identifier, condition, conditionDescription)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> noKeyMatches(Predicate<K> condition, String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.noKeyMatches(map, condition),
                identifier -> MapValidationMetadata.noKeyMatches(identifier, condition, conditionDescription)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> noValueMatches(Predicate<V> condition, String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.noValueMatches(map, condition),
                identifier -> MapValidationMetadata.noValueMatches(identifier, condition, conditionDescription)
        );
    }

    public static <K, V> ValidationRule<Map<K, V>> noEntryMatches(BiPredicate<K, V> condition, String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.noEntryMatches(map, condition),
                identifier -> MapValidationMetadata.noEntryMatches(identifier, condition, conditionDescription)
        );
    }
}