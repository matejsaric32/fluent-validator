package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class CollectionValidationMetadata extends ValidationMetadata {

    protected CollectionValidationMetadata(ValidationIdentifier identifier,
                                           DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    public static final class NotEmpty extends CollectionValidationMetadata {
        private NotEmpty(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_EMPTY);
            // Field parameter already added in parent constructor
        }
    }

    public static final class IsEmpty extends CollectionValidationMetadata {
        private IsEmpty(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_EMPTY);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class MinSize extends CollectionValidationMetadata {
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
    public static final class MaxSize extends CollectionValidationMetadata {
        private final int maximunSize;

        private MaxSize(ValidationIdentifier identifier, int maximunSize) {
            super(identifier, DefaultValidationCode.MAX_SIZE);
            this.maximunSize = maximunSize;

            // Add message parameters
            addMessageParameter(MessageParameter.MAX_SIZE, String.valueOf(maximunSize));
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ExactSize extends CollectionValidationMetadata {
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
    public static final class SizeRange extends CollectionValidationMetadata {
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
    public static final class AllMatch<E> extends CollectionValidationMetadata {
        private final Predicate<E> condition;
        private final String conditionDescription;

        private AllMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
            super(identifier, DefaultValidationCode.ALL_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AnyMatch<E> extends CollectionValidationMetadata {
        private final Predicate<E> condition;
        private final String conditionDescription;

        private AnyMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
            super(identifier, DefaultValidationCode.ANY_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoneMatch<E> extends CollectionValidationMetadata {
        private final Predicate<E> condition;
        private final String conditionDescription;

        private NoneMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
            super(identifier, DefaultValidationCode.NONE_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoDuplicates extends CollectionValidationMetadata {
        private NoDuplicates(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_DUPLICATES);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Contains<E> extends CollectionValidationMetadata {
        private final E element;

        private Contains(ValidationIdentifier identifier, E element) {
            super(identifier, DefaultValidationCode.COLLECTION_CONTAINS);
            this.element = element;

            // Add message parameters
            addMessageParameter(MessageParameter.ELEMENT, element != null ? element.toString() : "null");
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class DoesNotContain<E> extends CollectionValidationMetadata {
        private final E element;

        private DoesNotContain(ValidationIdentifier identifier, E element) {
            super(identifier, DefaultValidationCode.DOES_NOT_CONTAIN);
            this.element = element;

            // Add message parameters
            addMessageParameter(MessageParameter.ELEMENT, element != null ? element.toString() : "null");
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ContainsAll<E> extends CollectionValidationMetadata {
        private final Collection<E> elements;

        private ContainsAll(ValidationIdentifier identifier, Collection<E> elements) {
            super(identifier, DefaultValidationCode.CONTAINS_ALL);
            this.elements = new ArrayList<>(elements);

            // Add message parameters
            addMessageParameter(MessageParameter.ELEMENTS, elements.toString());
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ContainsNone<E> extends CollectionValidationMetadata {
        private final Collection<E> elements;

        private ContainsNone(ValidationIdentifier identifier, Collection<E> elements) {
            super(identifier, DefaultValidationCode.CONTAINS_NONE);
            this.elements = new ArrayList<>(elements);

            // Add message parameters
            addMessageParameter(MessageParameter.ELEMENTS, elements.toString());
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

    public static <E> AllMatch<E> allMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AllMatch<>(identifier, condition, description);
    }

    public static <E> AnyMatch<E> anyMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AnyMatch<>(identifier, condition, description);
    }

    public static <E> NoneMatch<E> noneMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new NoneMatch<>(identifier, condition, description);
    }

    public static NoDuplicates noDuplicates(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new NoDuplicates(identifier);
    }

    public static <E> Contains<E> contains(ValidationIdentifier identifier, E element) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new Contains<>(identifier, element);
    }

    public static <E> DoesNotContain<E> doesNotContain(ValidationIdentifier identifier, E element) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(element, "Element must not be null");
        return new DoesNotContain<>(identifier, element);
    }

    public static <E> ContainsAll<E> containsAll(ValidationIdentifier identifier, Collection<E> elements) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(elements, "Elements collection must not be null");
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Elements collection must not be empty");
        }
        return new ContainsAll<>(identifier, elements);
    }

    public static <E> ContainsNone<E> containsNone(ValidationIdentifier identifier, Collection<E> elements) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(elements, "Elements collection must not be null");
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Elements collection must not be empty");
        }
        return new ContainsNone<>(identifier, elements);
    }
}