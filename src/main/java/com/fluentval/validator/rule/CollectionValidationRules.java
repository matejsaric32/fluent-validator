package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.CollectionValidationMetadata;

import java.util.Collection;
import java.util.function.Predicate;

public final class CollectionValidationRules {

    private CollectionValidationRules() {
        // Utility class
    }

    public static <T extends Collection<?>> ValidationRule<T> notEmpty() {
        return (value, result, identifier) -> {
            if (value != null && value.isEmpty()) {
                result.addFailure(new ValidationResult.Failure(
                    CollectionValidationMetadata.notEmpty(identifier)
                ));
            }
        };
    }

    public static <T extends Collection<?>> ValidationRule<T> isEmpty() {
        return (value, result, identifier) -> {
            if (value != null && !value.isEmpty()) {
                result.addFailure(new ValidationResult.Failure(
                    CollectionValidationMetadata.isEmpty(identifier)
                ));
            }
        };
    }

    public static <T extends Collection<?>> ValidationRule<T> minSize(int min) {
        return (value, result, identifier) -> {
            if (value != null && value.size() < min) {
                result.addFailure(new ValidationResult.Failure(
                    CollectionValidationMetadata.minSize(identifier, min)
                ));
            }
        };
    }

    public static <T extends Collection<?>> ValidationRule<T> maxSize(int max) {
        return (value, result, identifier) -> {
            if (value != null && value.size() > max) {
                result.addFailure(new ValidationResult.Failure(
                    CollectionValidationMetadata.maxSize(identifier, max)
                ));
            }
        };
    }

    public static <T extends Collection<?>> ValidationRule<T> exactSize(int size) {
        return (value, result, identifier) -> {
            if (value != null && value.size() != size) {
                result.addFailure(new ValidationResult.Failure(
                    CollectionValidationMetadata.exactSize(identifier, size)
                ));
            }
        };
    }

    public static <T extends Collection<?>> ValidationRule<T> sizeRange(int min, int max) {
        return (value, result, identifier) -> {
            if (value != null && (value.size() < min || value.size() > max)) {
                result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.sizeRange(identifier, min, max)
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> allMatch(
        Predicate<E> condition, String conditionDescription) {
        return (value, result, identifier) -> {
            if (value != null && !value.stream().allMatch(condition)) {
                result.addFailure(new ValidationResult.Failure(
                    CollectionValidationMetadata.allMatch(
                        identifier, condition, conditionDescription
                    )
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> anyMatch(
        Predicate<E> condition, String conditionDescription) {
        return (value, result, identifier) -> {
            if (value != null && !value.isEmpty() && value.stream().noneMatch(condition)) {
                result.addFailure(new ValidationResult.Failure(
                    CollectionValidationMetadata.anyMatch(
                        identifier, condition, conditionDescription
                    )
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> noneMatch(
        Predicate<E> condition, String conditionDescription) {
        return (value, result, identifier) -> {
            if (value != null && value.stream().anyMatch(condition)) {
                result.addFailure(new ValidationResult.Failure(
                    CollectionValidationMetadata.noneMatch(
                        identifier, condition, conditionDescription
                    )
                ));
            }
        };
    }

    public static <T extends Collection<?>> ValidationRule<T> noDuplicates() {
        return (value, result, identifier) -> {
            if (value != null) {
                long distinctCount = value.stream().distinct().count();
                if (distinctCount != value.size()) {
                    result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.noDuplicates(identifier)
                    ));
                }
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> contains(E element) {
        return (value, result, identifier) -> {
            if (value != null && !value.contains(element)) {
                result.addFailure(new ValidationResult.Failure(
                    CollectionValidationMetadata.contains(identifier, element)
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> doesNotContain(E element) {
        return (value, result, identifier) -> {
            if (value != null && value.contains(element)) {
                result.addFailure(new ValidationResult.Failure(
                    CollectionValidationMetadata.doesNotContain(identifier, element)
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> containsAll(Collection<E> elements) {
        return (value, result, identifier) -> {
            if (value != null && !value.containsAll(elements)) {
                result.addFailure(new ValidationResult.Failure(
                    CollectionValidationMetadata.containsAll(identifier, elements)
                ));
            }
        };
    }

    public static <T extends Collection<E>, E> ValidationRule<T> containsNone(Collection<E> elements) {
        return (value, result, identifier) -> {
            if (value != null) {
                boolean hasCommonElement = value.stream().anyMatch(elements::contains);
                if (hasCommonElement) {
                    result.addFailure(new ValidationResult.Failure(
                        CollectionValidationMetadata.containsNone(identifier, elements)
                    ));
                }
            }
        };
    }
}