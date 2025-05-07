package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.CollectionValidationMetadata;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import static com.fluentval.validator.rule.ValidationRuleUtils.createSkipNullRule;

public final class CollectionValidationRules {

    private CollectionValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static <T extends Collection<?>> boolean isNotEmpty(final T collection) {
            return !collection.isEmpty();
        }

        static <T extends Collection<?>> boolean isEmpty(final T collection) {
            return collection.isEmpty();
        }

        static <T extends Collection<?>> boolean hasMinimumSize(final T collection, final int minSize) {
            return collection.size() >= minSize;
        }

        static <T extends Collection<?>> boolean hasMaximumSize(final T collection, final int maxSize) {
            return collection.size() <= maxSize;
        }

        static <T extends Collection<?>> boolean hasExactSize(final T collection, final int exactSize) {
            return collection.size() == exactSize;
        }

        static <T extends Collection<?>> boolean isInSizeRange(final T collection, final int minSize, final int maxSize) {
            return collection.size() >= minSize && collection.size() <= maxSize;
        }

        static <T extends Collection<E>, E> boolean allElementsMatch(final T collection, final Predicate<E> condition) {
            return collection.stream().allMatch(condition);
        }

        static <T extends Collection<E>, E> boolean anyElementMatches(final T collection, final Predicate<E> condition) {
            return !collection.isEmpty() && collection.stream().anyMatch(condition);
        }

        static <T extends Collection<E>, E> boolean noElementMatches(final T collection, final Predicate<E> condition) {
            return collection.stream().noneMatch(condition);
        }

        static <T extends Collection<?>> boolean hasNoDuplicates(final T collection) {
            return collection.stream().distinct().count() == collection.size();
        }

        static <T extends Collection<E>, E> boolean containsElement(final T collection, final E element) {
            return collection.contains(element);
        }

        static <T extends Collection<E>, E> boolean doesNotContainElement(final T collection, final E element) {
            return !collection.contains(element);
        }

        static <T extends Collection<E>, E> boolean containsAllElements(final T collection, final Collection<E> elements) {
            return collection.containsAll(elements);
        }

        static <T extends Collection<E>, E> boolean containsNoElements(final T collection, final Collection<E> elements) {
            return collection.stream().noneMatch(elements::contains);
        }
    }

    public static <T extends Collection<?>> ValidationRule<T> notEmpty() {
        return createSkipNullRule(
                ValidationFunctions::isNotEmpty,
                CollectionValidationMetadata::notEmpty
        );
    }

    public static <T extends Collection<?>> ValidationRule<T> isEmpty() {
        return createSkipNullRule(
                ValidationFunctions::isEmpty,
                CollectionValidationMetadata::isEmpty
        );
    }

    public static <T extends Collection<?>> ValidationRule<T> minSize(final int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.hasMinimumSize(collection, min),
                identifier -> CollectionValidationMetadata.minSize(identifier, min)
        );
    }

    public static <T extends Collection<?>> ValidationRule<T> maxSize(final int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Maximum size cannot be negative");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.hasMaximumSize(collection, max),
                identifier -> CollectionValidationMetadata.maxSize(identifier, max)
        );
    }

    public static <T extends Collection<?>> ValidationRule<T> exactSize(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.hasExactSize(collection, size),
                identifier -> CollectionValidationMetadata.exactSize(identifier, size, size)
        );
    }

    public static <T extends Collection<?>> ValidationRule<T> sizeRange(final int min, final int max) {
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
                        CollectionValidationMetadata.sizeRange(identifier, min, max, value.size())
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> allMatch(
            final Predicate<E> condition, final String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.allElementsMatch(collection, condition),
                identifier -> CollectionValidationMetadata.allMatch(identifier, condition, conditionDescription)
        );
    }

    public static <T extends Collection<E>, E> ValidationRule<T> anyMatch(
            final Predicate<E> condition, final String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.anyElementMatches(collection, condition),
                identifier -> CollectionValidationMetadata.anyMatch(identifier, condition, conditionDescription)
        );
    }

    public static <T extends Collection<E>, E> ValidationRule<T> noneMatch(
            final Predicate<E> condition, final String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.noElementMatches(collection, condition),
                identifier -> CollectionValidationMetadata.noneMatch(identifier, condition, conditionDescription)
        );
    }

    public static <T extends Collection<?>> ValidationRule<T> noDuplicates() {
        return createSkipNullRule(
                ValidationFunctions::hasNoDuplicates,
                CollectionValidationMetadata::noDuplicates
        );
    }

    public static <T extends Collection<E>, E> ValidationRule<T> contains(final E element) {
        Objects.requireNonNull(element, "Element cannot be null");

        return createSkipNullRule(
                collection -> ValidationFunctions.containsElement(collection, element),
                identifier -> CollectionValidationMetadata.contains(identifier, element)
        );
    }

    public static <T extends Collection<E>, E> ValidationRule<T> doesNotContain(final E element) {
        Objects.requireNonNull(element, "Element cannot be null");

        return createSkipNullRule(
                collection -> ValidationFunctions.doesNotContainElement(collection, element),
                identifier -> CollectionValidationMetadata.doesNotContain(identifier, element)
        );
    }

    public static <T extends Collection<E>, E> ValidationRule<T> containsAll(final Collection<E> elements) {
        Objects.requireNonNull(elements, "Elements collection cannot be null");

        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Elements collection cannot be empty");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.containsAllElements(collection, elements),
                identifier -> CollectionValidationMetadata.containsAll(identifier, elements)
        );
    }

    public static <T extends Collection<E>, E> ValidationRule<T> containsNone(final Collection<E> elements) {
        Objects.requireNonNull(elements, "Elements collection cannot be null");

        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Elements collection cannot be empty");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.containsNoElements(collection, elements),
                identifier -> CollectionValidationMetadata.containsNone(identifier, elements)
        );
    }
}