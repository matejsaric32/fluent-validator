package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class MapValidationMetadata extends ValidationMetadata {

    protected MapValidationMetadata(ValidationIdentifier identifier,
                                    DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    public static final class NotEmpty extends MapValidationMetadata {
        private NotEmpty(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_EMPTY);
            // Field parameter already added in parent constructor
        }
    }

    public static final class IsEmpty extends MapValidationMetadata {
        private IsEmpty(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_EMPTY);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class MinSize extends MapValidationMetadata {
        private final int minimumSize;

        private MinSize(ValidationIdentifier identifier, int minimumSize) {
            super(identifier, DefaultValidationCode.MIN_SIZE);
            this.minimumSize = minimumSize;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_SIZE, String.valueOf(minimumSize));
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class MaxSize extends MapValidationMetadata {
        private final int maximumSize;

        private MaxSize(ValidationIdentifier identifier, int maximumSize) {
            super(identifier, DefaultValidationCode.MAX_SIZE);
            this.maximumSize = maximumSize;

            // Add message parameters
            addMessageParameter(MessageParameter.MAX_SIZE, String.valueOf(maximumSize));
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ExactSize extends MapValidationMetadata {
        private final int requiredSize;
        private final int actualSize;

        private ExactSize(ValidationIdentifier identifier, int requiredSize, int actualSize) {
            super(identifier, DefaultValidationCode.EXACT_SIZE);
            this.requiredSize = requiredSize;
            this.actualSize = actualSize;

            // Add message parameters
            addMessageParameter(MessageParameter.EXACT_SIZE, String.valueOf(requiredSize));
            addMessageParameter(MessageParameter.ACTUAL_SIZE, String.valueOf(actualSize));
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class SizeRange extends MapValidationMetadata {
        private final int minSize;
        private final int maxSize;
        private final int actualSize;

        private SizeRange(ValidationIdentifier identifier, int minSize, int maxSize, int actualSize) {
            super(identifier, DefaultValidationCode.SIZE_RANGE);
            this.minSize = minSize;
            this.maxSize = maxSize;
            this.actualSize = actualSize;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_SIZE, String.valueOf(minSize));
            addMessageParameter(MessageParameter.MAX_SIZE, String.valueOf(maxSize));
            addMessageParameter(MessageParameter.ACTUAL_SIZE, String.valueOf(actualSize));
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ContainsKey<K> extends MapValidationMetadata {
        private final K key;

        private ContainsKey(ValidationIdentifier identifier, K key) {
            super(identifier, DefaultValidationCode.COLLECTION_CONTAINS);
            this.key = key;

            // Add message parameters
            addMessageParameter(MessageParameter.KEY, key != null ? key.toString() : "null");
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class DoesNotContainKey<K> extends MapValidationMetadata {
        private final K key;

        private DoesNotContainKey(ValidationIdentifier identifier, K key) {
            super(identifier, DefaultValidationCode.DOES_NOT_CONTAIN);
            this.key = key;

            // Add message parameters
            addMessageParameter(MessageParameter.KEY, key != null ? key.toString() : "null");
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ContainsAllKeys<K> extends MapValidationMetadata {
        private final Collection<K> keys;

        private ContainsAllKeys(ValidationIdentifier identifier, Collection<K> keys) {
            super(identifier, DefaultValidationCode.CONTAINS_ALL);
            this.keys = new ArrayList<>(keys);

            // Add message parameters
            addMessageParameter(MessageParameter.KEYS, keys.toString());
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ContainsValue<V> extends MapValidationMetadata {
        private final V value;

        private ContainsValue(ValidationIdentifier identifier, V value) {
            super(identifier, DefaultValidationCode.COLLECTION_CONTAINS);
            this.value = value;

            // Add message parameters
            addMessageParameter(MessageParameter.VALUE, value != null ? value.toString() : "null");
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class DoesNotContainValue<V> extends MapValidationMetadata {
        private final V value;

        private DoesNotContainValue(ValidationIdentifier identifier, V value) {
            super(identifier, DefaultValidationCode.DOES_NOT_CONTAIN);
            this.value = value;

            // Add message parameters
            addMessageParameter(MessageParameter.VALUE, value != null ? value.toString() : "null");
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AllKeysMatch<K> extends MapValidationMetadata {
        private final Predicate<K> condition;
        private final String conditionDescription;

        private AllKeysMatch(ValidationIdentifier identifier, Predicate<K> condition, String description) {
            super(identifier, DefaultValidationCode.ALL_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AllValuesMatch<V> extends MapValidationMetadata {
        private final Predicate<V> condition;
        private final String conditionDescription;

        private AllValuesMatch(ValidationIdentifier identifier, Predicate<V> condition, String description) {
            super(identifier, DefaultValidationCode.ALL_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AllEntriesMatch<K, V> extends MapValidationMetadata {
        private final BiPredicate<K, V> condition;
        private final String conditionDescription;

        private AllEntriesMatch(ValidationIdentifier identifier, BiPredicate<K, V> condition, String description) {
            super(identifier, DefaultValidationCode.ALL_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AnyKeyMatches<K> extends MapValidationMetadata {
        private final Predicate<K> condition;
        private final String conditionDescription;

        private AnyKeyMatches(ValidationIdentifier identifier, Predicate<K> condition, String description) {
            super(identifier, DefaultValidationCode.ANY_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AnyValueMatches<V> extends MapValidationMetadata {
        private final Predicate<V> condition;
        private final String conditionDescription;

        private AnyValueMatches(ValidationIdentifier identifier, Predicate<V> condition, String description) {
            super(identifier, DefaultValidationCode.ANY_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AnyEntryMatches<K, V> extends MapValidationMetadata {
        private final BiPredicate<K, V> condition;
        private final String conditionDescription;

        private AnyEntryMatches(ValidationIdentifier identifier, BiPredicate<K, V> condition, String description) {
            super(identifier, DefaultValidationCode.ANY_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoKeyMatches<K> extends MapValidationMetadata {
        private final Predicate<K> condition;
        private final String conditionDescription;

        private NoKeyMatches(ValidationIdentifier identifier, Predicate<K> condition, String description) {
            super(identifier, DefaultValidationCode.NONE_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoValueMatches<V> extends MapValidationMetadata {
        private final Predicate<V> condition;
        private final String conditionDescription;

        private NoValueMatches(ValidationIdentifier identifier, Predicate<V> condition, String description) {
            super(identifier, DefaultValidationCode.NONE_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoEntryMatches<K, V> extends MapValidationMetadata {
        private final BiPredicate<K, V> condition;
        private final String conditionDescription;

        private NoEntryMatches(ValidationIdentifier identifier, BiPredicate<K, V> condition, String description) {
            super(identifier, DefaultValidationCode.NONE_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    // Factory methods
    public static NotEmpty notEmpty(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new NotEmpty(identifier);
    }

    public static IsEmpty isEmpty(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new IsEmpty(identifier);
    }

    public static MinSize minSize(ValidationIdentifier identifier, int minSize) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        if (minSize < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }
        return new MinSize(identifier, minSize);
    }

    public static MaxSize maxSize(ValidationIdentifier identifier, int maxSize) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        if (maxSize < 0) {
            throw new IllegalArgumentException("Maximum size cannot be negative");
        }
        return new MaxSize(identifier, maxSize);
    }

    public static ExactSize exactSize(ValidationIdentifier identifier, int exactSize, int actualSize) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        if (exactSize < 0) {
            throw new IllegalArgumentException("Exact size cannot be negative");
        }
        if (actualSize < 0) {
            throw new IllegalArgumentException("Actual size cannot be negative");
        }
        return new ExactSize(identifier, exactSize, actualSize);
    }

    public static SizeRange sizeRange(ValidationIdentifier identifier, int minSize, int maxSize, int actualSize) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        if (minSize < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }
        if (maxSize < minSize) {
            throw new IllegalArgumentException("Maximum size must be greater than or equal to minimum size");
        }
        if (actualSize < 0) {
            throw new IllegalArgumentException("Actual size cannot be negative");
        }
        return new SizeRange(identifier, minSize, maxSize, actualSize);
    }

    public static <K> ContainsKey<K> containsKey(ValidationIdentifier identifier, K key) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new ContainsKey<>(identifier, key);
    }

    public static <K> DoesNotContainKey<K> doesNotContainKey(ValidationIdentifier identifier, K key) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(key, "Key must not be null");
        return new DoesNotContainKey<>(identifier, key);
    }

    public static <K> ContainsAllKeys<K> containsAllKeys(ValidationIdentifier identifier, Collection<K> keys) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(keys, "Keys collection must not be null");
        if (keys.isEmpty()) {
            throw new IllegalArgumentException("Keys collection must not be empty");
        }
        return new ContainsAllKeys<>(identifier, keys);
    }

    public static <V> ContainsValue<V> containsValue(ValidationIdentifier identifier, V value) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new ContainsValue<>(identifier, value);
    }

    public static <V> DoesNotContainValue<V> doesNotContainValue(ValidationIdentifier identifier, V value) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(value, "Value must not be null");
        return new DoesNotContainValue<>(identifier, value);
    }

    public static <K> AllKeysMatch<K> allKeysMatch(ValidationIdentifier identifier, Predicate<K> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AllKeysMatch<>(identifier, condition, description);
    }

    public static <V> AllValuesMatch<V> allValuesMatch(ValidationIdentifier identifier, Predicate<V> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AllValuesMatch<>(identifier, condition, description);
    }

    public static <K, V> AllEntriesMatch<K, V> allEntriesMatch(ValidationIdentifier identifier, BiPredicate<K, V> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AllEntriesMatch<>(identifier, condition, description);
    }

    public static <K> AnyKeyMatches<K> anyKeyMatches(ValidationIdentifier identifier, Predicate<K> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AnyKeyMatches<>(identifier, condition, description);
    }

    public static <V> AnyValueMatches<V> anyValueMatches(ValidationIdentifier identifier, Predicate<V> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AnyValueMatches<>(identifier, condition, description);
    }

    public static <K, V> AnyEntryMatches<K, V> anyEntryMatches(ValidationIdentifier identifier, BiPredicate<K, V> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AnyEntryMatches<>(identifier, condition, description);
    }

    public static <K> NoKeyMatches<K> noKeyMatches(ValidationIdentifier identifier, Predicate<K> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new NoKeyMatches<>(identifier, condition, description);
    }

    public static <V> NoValueMatches<V> noValueMatches(ValidationIdentifier identifier, Predicate<V> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new NoValueMatches<>(identifier, condition, description);
    }

    public static <K, V> NoEntryMatches<K, V> noEntryMatches(ValidationIdentifier identifier, BiPredicate<K, V> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new NoEntryMatches<>(identifier, condition, description);
    }
}