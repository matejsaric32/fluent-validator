package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.CollectionValidationMetadata;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

public final class CollectionValidationRules {

    private CollectionValidationRules() {
        // Utility class
    }

    public static <T extends Collection<?>> ValidationRule<T> notEmpty() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.isEmpty()) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.notEmpty(identifier)
                ));
            }
        };
    }

    public static <T extends Collection<?>> ValidationRule<T> isEmpty() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!value.isEmpty()) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.isEmpty(identifier)
                ));
            }
        };
    }

    public static <T extends Collection<?>> ValidationRule<T> minSize(final int min) {

        if (min < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.size() < min) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.minSize(identifier, min)
                ));
            }
        };
    }

    public static <T extends Collection<?>> ValidationRule<T> maxSize(final int max) {

        if (max < 0) {
            throw new IllegalArgumentException("Maximum size cannot be negative");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.size() > max) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.maxSize(identifier, max)
                ));
            }
        };
    }

    public static <T extends Collection<?>> ValidationRule<T> exactSize(final int size) {

        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.size() != size) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.exactSize(identifier, size, size)
                ));
            }
        };
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

            if (value.size() < min || value.size() > max) {
                CollectionValidationMetadata.SizeRange metadata =
                        CollectionValidationMetadata.sizeRange(identifier, min, max, value.size());

                result.addFailure(new ValidationResult.Failure(metadata));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> allMatch(
            final Predicate<E> condition, final String conditionDescription) {

        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!value.stream().allMatch(condition)) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.allMatch(identifier, condition, conditionDescription)
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> anyMatch(
            final Predicate<E> condition, final String conditionDescription) {

        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.isEmpty() || value.stream().noneMatch(condition)) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.anyMatch(identifier, condition, conditionDescription)
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> noneMatch(
            final Predicate<E> condition, final String conditionDescription) {

        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.stream().anyMatch(condition)) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.noneMatch(identifier, condition, conditionDescription)
                ));
            }
        };
    }

    public static <T extends Collection<?>> ValidationRule<T> noDuplicates() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            long distinctCount = value.stream().distinct().count();
            if (distinctCount != value.size()) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.noDuplicates(identifier)
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> contains(final E element) {
        // Note: element can be null as collections can contain null elements

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!value.contains(element)) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.contains(identifier, element)
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> doesNotContain(final E element) {

        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.contains(element)) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.doesNotContain(identifier, element)
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> containsAll(final Collection<E> elements) {

        Objects.requireNonNull(elements, "Elements collection cannot be null");

        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Elements collection cannot be empty");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!value.containsAll(elements)) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.containsAll(identifier, elements)
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> containsNone(final Collection<E> elements) {

        Objects.requireNonNull(elements, "Elements collection cannot be null");

        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Elements collection cannot be empty");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            boolean hasCommonElement = value.stream().anyMatch(elements::contains);
            if (hasCommonElement) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.containsNone(identifier, elements)
                ));
            }
        };
    }
}