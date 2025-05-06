package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.CommonValidationMetadata;

import java.util.function.Predicate;

public final class CommonValidationRules {

    private CommonValidationRules() {
        // Utility class
    }

    private static final String NOT_NULL_CODE = "VGG01";
    private static final String MUST_BE_NULL_CODE = "VGG02";
    private static final String IS_EQUAL_CODE = "VGG03";
    private static final String IS_NOT_EQUAL_CODE = "VGG04";
    private static final String SATISFIES_CODE = "VGG05";
    private static final String IS_INSTANCE_OF_CODE = "VGG06";
    private static final String IS_NOT_INSTANCE_OF_CODE = "VGG07";
    private static final String IS_SAME_AS_CODE = "VGG08";
    private static final String IS_NOT_SAME_AS_CODE = "VGG09";

    public static <T> ValidationRule<T> notNull() {
        return (value, result, identifier) -> {
            if (value == null) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new CommonValidationMetadata.NotNull(
                            identifier,
                            NOT_NULL_CODE
                        ))
                );
            }
        };
    }

    public static <T> ValidationRule<T> mustBeNull() {
        return (value, result, identifier) -> {
            if (value != null) {
                result.addFailure(
                    new ValidationResult.Failure(new CommonValidationMetadata.MustBeNull(
                        identifier,
                        MUST_BE_NULL_CODE
                    ))
                );
            }
        };
    }

    public static <T> ValidationRule<T> isEqual(T object) {
        return (value, result, identifier) -> {
            if (value != null && !value.equals(object)) {
                result.addFailure(
                    new ValidationResult.Failure(new CommonValidationMetadata.Equal<>(
                        identifier,
                        IS_EQUAL_CODE,
                        object.getClass()
                    ))
                );
            }
        };
    }

    public static <T> ValidationRule<T> isNotEqual(T object) {
        return (value, result, identifier) -> {
            if (value != null && value.equals(object)) {
                result.addFailure(
                    new ValidationResult.Failure(new CommonValidationMetadata.NotEqual<>(
                        identifier,
                        IS_NOT_EQUAL_CODE,
                        object.getClass()
                    ))
                );
            }
        };
    }

    public static <T> ValidationRule<T> satisfies(Predicate<T> predicate,
        String message) {
        return (value, result, identifier) -> {
            if (value != null && !predicate.test(value)) {
                result.addFailure(
                    new ValidationResult.Failure(new CommonValidationMetadata.Satisfies<>(
                        identifier,
                        SATISFIES_CODE,
                        predicate,
                        message
                    ))
                );
            }
        };
    }

    public static <T> ValidationRule<T> isInstanceOf(Class<?> clazz) {
        return (value, result, identifier) -> {
            if (value != null && !clazz.isInstance(value)) {
                result.addFailure(
                    new ValidationResult.Failure(new CommonValidationMetadata.InstanceOf(
                        identifier,
                        IS_INSTANCE_OF_CODE,
                        clazz
                    ))
                );
            }
        };
    }

    public static <T> ValidationRule<T> isNotInstanceOf(Class<?> clazz) {
        return (value, result, identifier) -> {
            if (value != null && clazz.isInstance(value)) {
                result.addFailure(
                    new ValidationResult.Failure(new CommonValidationMetadata.NotInstanceOf(
                        identifier,
                        IS_NOT_INSTANCE_OF_CODE,
                        clazz
                    ))
                );
            }
        };
    }

    public static <T> ValidationRule<T> isSameAs(T object) {
        return (value, result, identifier) -> {
            if (value != object) {
                result.addFailure(
                    new ValidationResult.Failure(new CommonValidationMetadata.SameAs(
                        identifier,
                        IS_SAME_AS_CODE,
                        object.getClass()
                    ))
                );
            }
        };
    }

    public static <T> ValidationRule<T> isNotSameAs(T object) {
        return (value, result, identifier) -> {
            if (value == object) {
                result.addFailure(
                    new ValidationResult.Failure(new CommonValidationMetadata.NotSameAs(
                        identifier,
                        IS_NOT_SAME_AS_CODE,
                        object.getClass()
                    ))
                );
            }
        };
    }

}