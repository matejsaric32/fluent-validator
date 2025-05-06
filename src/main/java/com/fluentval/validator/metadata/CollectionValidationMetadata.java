package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class CollectionValidationMetadata extends ValidationMetadata {

    protected CollectionValidationMetadata(ValidationIdentifier identifier,
                                           DefaultValidationCode code,
                                           Map<String, String> messageParameters) {
        super(identifier, code.getCode(), messageParameters);

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    public static final class NotEmpty extends CollectionValidationMetadata {

        public NotEmpty(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_EMPTY, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    public static final class IsEmpty extends CollectionValidationMetadata {

        public IsEmpty(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_EMPTY, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class MinSize extends CollectionValidationMetadata {
        private final int minSize;

        public MinSize(ValidationIdentifier identifier, int minSize) {
            super(identifier, DefaultValidationCode.MIN_SIZE, new HashMap<>());
            if (minSize < 0) throw new IllegalArgumentException("Minimum size cannot be negative");
            this.minSize = minSize;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_SIZE, String.valueOf(minSize));
        }
    }

    @Getter
    public static final class MaxSize extends CollectionValidationMetadata {
        private final int maxSize;

        public MaxSize(ValidationIdentifier identifier, int maxSize) {
            super(identifier, DefaultValidationCode.MAX_SIZE, new HashMap<>());
            if (maxSize < 0) throw new IllegalArgumentException("Maximum size cannot be negative");
            this.maxSize = maxSize;

            // Add message parameters
            addMessageParameter(MessageParameter.MAX_SIZE, String.valueOf(maxSize));
        }
    }

    @Getter
    public static final class ExactSize extends CollectionValidationMetadata {
        private final int exactSize;

        public ExactSize(ValidationIdentifier identifier, int exactSize) {
            super(identifier, DefaultValidationCode.EXACT_SIZE, new HashMap<>());
            if (exactSize < 0) throw new IllegalArgumentException("Size cannot be negative");
            this.exactSize = exactSize;

            // Add message parameters
            addMessageParameter(MessageParameter.EXACT_SIZE, String.valueOf(exactSize));
        }
    }

    @Getter
    public static final class SizeRange extends CollectionValidationMetadata {
        private final int minSize;
        private final int maxSize;

        public SizeRange(ValidationIdentifier identifier, int minSize, int maxSize) {
            super(identifier, DefaultValidationCode.SIZE_RANGE, new HashMap<>());
            if (minSize < 0) throw new IllegalArgumentException("Minimum size cannot be negative");
            if (maxSize < minSize) throw new IllegalArgumentException("Maximum size must be greater than or equal to minimum size");
            this.minSize = minSize;
            this.maxSize = maxSize;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_SIZE, String.valueOf(minSize));
            addMessageParameter(MessageParameter.MAX_SIZE, String.valueOf(maxSize));
        }

    }

    @Getter
    public static final class AllMatch<E> extends CollectionValidationMetadata {
        private final Predicate<E> condition;
        private final String conditionDescription;

        public AllMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
            super(identifier, DefaultValidationCode.ALL_MATCH, new HashMap<>());
            this.condition = Objects.requireNonNull(condition);
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    public static final class AnyMatch<E> extends CollectionValidationMetadata {
        private final Predicate<E> condition;
        private final String conditionDescription;

        public AnyMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
            super(identifier, DefaultValidationCode.ANY_MATCH, new HashMap<>());
            this.condition = Objects.requireNonNull(condition);
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    public static final class NoneMatch<E> extends CollectionValidationMetadata {
        private final Predicate<E> condition;
        private final String conditionDescription;

        public NoneMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
            super(identifier, DefaultValidationCode.NONE_MATCH, new HashMap<>());
            this.condition = Objects.requireNonNull(condition);
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    @Getter
    public static final class NoDuplicates extends CollectionValidationMetadata {

        public NoDuplicates(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_DUPLICATES, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class Contains<E> extends CollectionValidationMetadata {
        private final E element;

        public Contains(ValidationIdentifier identifier, E element) {
            super(identifier, DefaultValidationCode.COLLECTION_CONTAINS, new HashMap<>());
            this.element = element;

            // Add message parameters
            addMessageParameter(MessageParameter.ELEMENT, element != null ? element.toString() : "null");
        }
    }

    @Getter
    public static final class DoesNotContain<E> extends CollectionValidationMetadata {
        private final E element;

        public DoesNotContain(ValidationIdentifier identifier, E element) {
            super(identifier, DefaultValidationCode.DOES_NOT_CONTAIN, new HashMap<>());
            this.element = element;

            // Add message parameters
            addMessageParameter(MessageParameter.ELEMENT, element != null ? element.toString() : "null");
        }
    }

    @Getter
    public static final class ContainsAll<E> extends CollectionValidationMetadata {
        private final Collection<E> elements;

        public ContainsAll(ValidationIdentifier identifier, Collection<E> elements) {
            super(identifier, DefaultValidationCode.CONTAINS_ALL, new HashMap<>());
            this.elements = new ArrayList<>(Objects.requireNonNull(elements));
            if (elements.isEmpty()) throw new IllegalArgumentException("Element collection must not be empty");

            // Add message parameters
            addMessageParameter(MessageParameter.ELEMENTS, elements.toString());
        }
    }

    @Getter
    public static final class ContainsNone<E> extends CollectionValidationMetadata {
        private final Collection<E> elements;

        public ContainsNone(ValidationIdentifier identifier, Collection<E> elements) {
            super(identifier, DefaultValidationCode.CONTAINS_NONE, new HashMap<>());
            this.elements = new ArrayList<>(Objects.requireNonNull(elements));
            if (elements.isEmpty()) throw new IllegalArgumentException("Element collection must not be empty");

            // Add message parameters
            addMessageParameter(MessageParameter.ELEMENTS, elements.toString());
        }
    }

    // Factory methods
    public static NotEmpty notEmpty(ValidationIdentifier identifier) {
        return new NotEmpty(identifier);
    }

    public static IsEmpty isEmpty(ValidationIdentifier identifier) {
        return new IsEmpty(identifier);
    }

    public static MinSize minSize(ValidationIdentifier identifier, int minSize) {
        return new MinSize(identifier, minSize);
    }

    public static MaxSize maxSize(ValidationIdentifier identifier, int maxSize) {
        return new MaxSize(identifier, maxSize);
    }

    public static ExactSize exactSize(ValidationIdentifier identifier, int exactSize) {
        return new ExactSize(identifier, exactSize);
    }

    public static SizeRange sizeRange(ValidationIdentifier identifier, int minSize, int maxSize) {
        return new SizeRange(identifier, minSize, maxSize);
    }

    public static <E> AllMatch<E> allMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
        return new AllMatch<>(identifier, condition, description);
    }

    public static <E> AnyMatch<E> anyMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
        return new AnyMatch<>(identifier, condition, description);
    }

    public static <E> NoneMatch<E> noneMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
        return new NoneMatch<>(identifier, condition, description);
    }

    public static NoDuplicates noDuplicates(ValidationIdentifier identifier) {
        return new NoDuplicates(identifier);
    }

    public static <E> Contains<E> contains(ValidationIdentifier identifier, E element) {
        return new Contains<>(identifier, element);
    }

    public static <E> DoesNotContain<E> doesNotContain(ValidationIdentifier identifier, E element) {
        return new DoesNotContain<>(identifier, element);
    }

    public static <E> ContainsAll<E> containsAll(ValidationIdentifier identifier, Collection<E> elements) {
        return new ContainsAll<>(identifier, elements);
    }

    public static <E> ContainsNone<E> containsNone(ValidationIdentifier identifier, Collection<E> elements) {
        return new ContainsNone<>(identifier, elements);
    }
}