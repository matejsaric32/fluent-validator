package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class CollectionValidationMetadata extends ValidationMetadata {

    // Error code constants
    public static final String NOT_EMPTY_CODE = "VGC01";
    public static final String IS_EMPTY_CODE = "VGC02";
    public static final String MIN_SIZE_CODE = "VGC03";
    public static final String MAX_SIZE_CODE = "VGC04";
    public static final String EXACT_SIZE_CODE = "VGC05";
    public static final String SIZE_RANGE_CODE = "VGC06";
    public static final String ALL_MATCH_CODE = "VGC07";
    public static final String ANY_MATCH_CODE = "VGC08";
    public static final String NONE_MATCH_CODE = "VGC09";
    public static final String NO_DUPLICATES_CODE = "VGC10";
    public static final String CONTAINS_CODE = "VGC11";
    public static final String DOES_NOT_CONTAIN_CODE = "VGC12";
    public static final String CONTAINS_ALL_CODE = "VGC13";
    public static final String CONTAINS_NONE_CODE = "VGC14";

    // Message templates
    private static final String NOT_EMPTY_MESSAGE = "Collection '%s' must not be empty.";
    private static final String IS_EMPTY_MESSAGE = "Collection '%s' must be empty.";
    private static final String MIN_SIZE_MESSAGE = "Collection '%s' must have at least %s elements.";
    private static final String MAX_SIZE_MESSAGE = "Collection '%s' must not have more than %s elements.";
    private static final String EXACT_SIZE_MESSAGE = "Collection '%s' must have exactly %s elements.";
    private static final String SIZE_RANGE_MESSAGE = "Collection '%s' must have between %s and %s elements. Current size: %s.";
    private static final String ALL_MATCH_MESSAGE = "All elements in collection '%s' must satisfy the condition: %s.";
    private static final String ANY_MATCH_MESSAGE = "At least one element in collection '%s' must satisfy the condition: %s.";
    private static final String NONE_MATCH_MESSAGE = "No elements in collection '%s' may satisfy the condition: %s.";
    private static final String NO_DUPLICATES_MESSAGE = "Collection '%s' must not contain duplicates.";
    private static final String CONTAINS_MESSAGE = "Collection '%s' must contain element: %s.";
    private static final String DOES_NOT_CONTAIN_MESSAGE = "Collection '%s' must not contain element: %s.";
    private static final String CONTAINS_ALL_MESSAGE = "Collection '%s' must contain all elements: %s.";
    private static final String CONTAINS_NONE_MESSAGE = "Collection '%s' must not contain any of the elements: %s.";

    protected CollectionValidationMetadata(ValidationIdentifier identifier, String errorCode, String message) {
        super(identifier, errorCode, message);
    }

    public static final class NotEmpty extends CollectionValidationMetadata {
        public NotEmpty(ValidationIdentifier identifier) {
            this(identifier, NOT_EMPTY_CODE);
        }

        public NotEmpty(ValidationIdentifier identifier, String errorCode) {
            super(identifier, errorCode, formatMessage(NOT_EMPTY_MESSAGE, identifier.value()));
        }

        public NotEmpty(ValidationIdentifier identifier, String errorCode, String message) {
            super(identifier, errorCode, message);
        }
    }

    public static final class IsEmpty extends CollectionValidationMetadata {
        public IsEmpty(ValidationIdentifier identifier) {
            this(identifier, IS_EMPTY_CODE);
        }

        public IsEmpty(ValidationIdentifier identifier, String errorCode) {
            super(identifier, errorCode, formatMessage(IS_EMPTY_MESSAGE, identifier.value()));
        }

        public IsEmpty(ValidationIdentifier identifier, String errorCode, String message) {
            super(identifier, errorCode, message);
        }
    }

    @Getter
    public static final class MinSize extends CollectionValidationMetadata {
        private final int minSize;

        public MinSize(ValidationIdentifier identifier, int minSize) {
            this(identifier, MIN_SIZE_CODE, minSize);
        }

        public MinSize(ValidationIdentifier identifier, String errorCode, int minSize) {
            super(identifier, errorCode, formatMessage(MIN_SIZE_MESSAGE, identifier.value(), String.valueOf(minSize)));
            if (minSize < 0) throw new IllegalArgumentException("Minimum size cannot be negative");
            this.minSize = minSize;
        }

        public MinSize(ValidationIdentifier identifier, String errorCode, int minSize, String message) {
            super(identifier, errorCode, message);
            if (minSize < 0) throw new IllegalArgumentException("Minimum size cannot be negative");
            this.minSize = minSize;
        }

    }

    @Getter
    public static final class MaxSize extends CollectionValidationMetadata {
        private final int maxSize;

        public MaxSize(ValidationIdentifier identifier, int maxSize) {
            this(identifier, MAX_SIZE_CODE, maxSize);
        }

        public MaxSize(ValidationIdentifier identifier, String errorCode, int maxSize) {
            super(identifier, errorCode, formatMessage(MAX_SIZE_MESSAGE, identifier.value(), String.valueOf(maxSize)));
            if (maxSize < 0) throw new IllegalArgumentException("Maximum size cannot be negative");
            this.maxSize = maxSize;
        }

        public MaxSize(ValidationIdentifier identifier, String errorCode, int maxSize, String message) {
            super(identifier, errorCode, message);
            if (maxSize < 0) throw new IllegalArgumentException("Maximum size cannot be negative");
            this.maxSize = maxSize;
        }

    }

    @Getter
    public static final class ExactSize extends CollectionValidationMetadata {
        private final int exactSize;

        public ExactSize(ValidationIdentifier identifier, int exactSize) {
            this(identifier, EXACT_SIZE_CODE, exactSize);
        }

        public ExactSize(ValidationIdentifier identifier, String errorCode, int exactSize) {
            super(identifier, errorCode, formatMessage(EXACT_SIZE_MESSAGE, identifier.value(), String.valueOf(exactSize)));
            if (exactSize < 0) throw new IllegalArgumentException("Size cannot be negative");
            this.exactSize = exactSize;
        }

        public ExactSize(ValidationIdentifier identifier, String errorCode, int exactSize, String message) {
            super(identifier, errorCode, message);
            if (exactSize < 0) throw new IllegalArgumentException("Size cannot be negative");
            this.exactSize = exactSize;
        }

    }

    @Getter
    public static final class SizeRange extends CollectionValidationMetadata {
        private final int minSize;
        private final int maxSize;

        public SizeRange(ValidationIdentifier identifier, int minSize, int maxSize) {
            this(identifier, SIZE_RANGE_CODE, minSize, maxSize);
        }

        public SizeRange(ValidationIdentifier identifier, String errorCode, int minSize, int maxSize) {
            super(identifier, errorCode, formatMessage(SIZE_RANGE_MESSAGE, identifier.value(), String.valueOf(minSize), String.valueOf(maxSize), "N/A"));
            if (minSize < 0) throw new IllegalArgumentException("Minimum size cannot be negative");
            if (maxSize < minSize) throw new IllegalArgumentException("Maximum size must be greater than or equal to minimum size");
            this.minSize = minSize;
            this.maxSize = maxSize;
        }

        public SizeRange(ValidationIdentifier identifier, String errorCode, int minSize, int maxSize, String message) {
            super(identifier, errorCode, message);
            if (minSize < 0) throw new IllegalArgumentException("Minimum size cannot be negative");
            if (maxSize < minSize) throw new IllegalArgumentException("Maximum size must be greater than or equal to minimum size");
            this.minSize = minSize;
            this.maxSize = maxSize;
        }

        public String getMessageWithActualSize(int actualSize) {
            return formatMessage(SIZE_RANGE_MESSAGE, getIdentifier().value(), String.valueOf(minSize), String.valueOf(maxSize), String.valueOf(actualSize));
        }
    }

    @Getter
    public static final class AllMatch<E> extends CollectionValidationMetadata {
        private final Predicate<E> condition;
        private final String conditionDescription;

        public AllMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
            this(identifier, ALL_MATCH_CODE, condition, description);
        }

        public AllMatch(ValidationIdentifier identifier, String errorCode, Predicate<E> condition, String description) {
            super(identifier, errorCode, formatMessage(ALL_MATCH_MESSAGE, identifier.value(), description));
            this.condition = Objects.requireNonNull(condition);
            this.conditionDescription = description;
        }

        public AllMatch(ValidationIdentifier identifier, String errorCode, Predicate<E> condition, String description, String message) {
            super(identifier, errorCode, message);
            this.condition = Objects.requireNonNull(condition);
            this.conditionDescription = description;
        }

    }

    @Getter
    public static final class AnyMatch<E> extends CollectionValidationMetadata {
        private final Predicate<E> condition;
        private final String conditionDescription;

        public AnyMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
            this(identifier, ANY_MATCH_CODE, condition, description);
        }

        public AnyMatch(ValidationIdentifier identifier, String errorCode, Predicate<E> condition, String description) {
            super(identifier, errorCode, formatMessage(ANY_MATCH_MESSAGE, identifier.value(), description));
            this.condition = Objects.requireNonNull(condition);
            this.conditionDescription = description;
        }

        public AnyMatch(ValidationIdentifier identifier, String errorCode, Predicate<E> condition, String description, String message) {
            super(identifier, errorCode, message);
            this.condition = Objects.requireNonNull(condition);
            this.conditionDescription = description;
        }

    }

    @Getter
    public static final class NoneMatch<E> extends CollectionValidationMetadata {
        private final Predicate<E> condition;
        private final String conditionDescription;

        public NoneMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
            this(identifier, NONE_MATCH_CODE, condition, description);
        }

        public NoneMatch(ValidationIdentifier identifier, String errorCode, Predicate<E> condition, String description) {
            super(identifier, errorCode, formatMessage(NONE_MATCH_MESSAGE, identifier.value(), description));
            this.condition = Objects.requireNonNull(condition);
            this.conditionDescription = description;
        }

        public NoneMatch(ValidationIdentifier identifier, String errorCode, Predicate<E> condition, String description, String message) {
            super(identifier, errorCode, message);
            this.condition = Objects.requireNonNull(condition);
            this.conditionDescription = description;
        }

    }

    @Getter
    public static final class NoDuplicates extends CollectionValidationMetadata {
        public NoDuplicates(ValidationIdentifier identifier) {
            this(identifier, NO_DUPLICATES_CODE);
        }

        public NoDuplicates(ValidationIdentifier identifier, String errorCode) {
            super(identifier, errorCode, formatMessage(NO_DUPLICATES_MESSAGE, identifier.value()));
        }

        public NoDuplicates(ValidationIdentifier identifier, String errorCode, String message) {
            super(identifier, errorCode, message);
        }
    }

    @Getter
    public static final class Contains<E> extends CollectionValidationMetadata {
        private final E element;

        public Contains(ValidationIdentifier identifier, E element) {
            this(identifier, CONTAINS_CODE, element);
        }

        public Contains(ValidationIdentifier identifier, String errorCode, E element) {
            super(identifier, errorCode, formatMessage(CONTAINS_MESSAGE, identifier.value(), element != null ? element.toString() : "null"));
            this.element = element;
        }

        public Contains(ValidationIdentifier identifier, String errorCode, E element, String message) {
            super(identifier, errorCode, message);
            this.element = element;
        }

    }

    @Getter
    public static final class DoesNotContain<E> extends CollectionValidationMetadata {
        private final E element;

        public DoesNotContain(ValidationIdentifier identifier, E element) {
            this(identifier, DOES_NOT_CONTAIN_CODE, element);
        }

        public DoesNotContain(ValidationIdentifier identifier, String errorCode, E element) {
            super(identifier, errorCode, formatMessage(DOES_NOT_CONTAIN_MESSAGE, identifier.value(), element != null ? element.toString() : "null"));
            this.element = element;
        }

        public DoesNotContain(ValidationIdentifier identifier, String errorCode, E element, String message) {
            super(identifier, errorCode, message);
            this.element = element;
        }

    }

    @Getter
    public static final class ContainsAll<E> extends CollectionValidationMetadata {
        private final Collection<E> elements;

        public ContainsAll(ValidationIdentifier identifier, Collection<E> elements) {
            this(identifier, CONTAINS_ALL_CODE, elements);
        }

        public ContainsAll(ValidationIdentifier identifier, String errorCode, Collection<E> elements) {
            super(identifier, errorCode, formatMessage(CONTAINS_ALL_MESSAGE, identifier.value(), elements.toString()));
            this.elements = new ArrayList<>(Objects.requireNonNull(elements));
            if (elements.isEmpty()) throw new IllegalArgumentException("Element collection must not be empty");
        }

        public ContainsAll(ValidationIdentifier identifier, String errorCode, Collection<E> elements, String message) {
            super(identifier, errorCode, message);
            this.elements = new ArrayList<>(Objects.requireNonNull(elements));
            if (elements.isEmpty()) throw new IllegalArgumentException("Element collection must not be empty");
        }

    }

    @Getter
    public static final class ContainsNone<E> extends CollectionValidationMetadata {
        private final Collection<E> elements;

        public ContainsNone(ValidationIdentifier identifier, Collection<E> elements) {
            this(identifier, CONTAINS_NONE_CODE, elements);
        }

        public ContainsNone(ValidationIdentifier identifier, String errorCode, Collection<E> elements) {
            super(identifier, errorCode, formatMessage(CONTAINS_NONE_MESSAGE, identifier.value(), elements.toString()));
            this.elements = new ArrayList<>(Objects.requireNonNull(elements));
            if (elements.isEmpty()) throw new IllegalArgumentException("Element collection must not be empty");
        }

        public ContainsNone(ValidationIdentifier identifier, String errorCode, Collection<E> elements, String message) {
            super(identifier, errorCode, message);
            this.elements = new ArrayList<>(Objects.requireNonNull(elements));
            if (elements.isEmpty()) throw new IllegalArgumentException("Element collection must not be empty");
        }

    }
}
