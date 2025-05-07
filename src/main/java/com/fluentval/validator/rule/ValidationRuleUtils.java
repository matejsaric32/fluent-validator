package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.ValidationMetadata;

import java.util.function.Function;
import java.util.function.Predicate;

public class ValidationRuleUtils {

    public static <T> ValidationRule<T> createRule(
            final Predicate<T> validationFunction,
            final Function<ValidationIdentifier, ValidationMetadata> metadataFactory) {

        return (value, result, identifier) -> {
            if (!validationFunction.test(value)) {
                result.addFailure(new ValidationResult.Failure(
                        metadataFactory.apply(identifier)
                ));
            }
        };
    }

    public static <T> ValidationRule<T> createSkipNullRule(
            final Predicate<T> validationFunction,
            final Function<ValidationIdentifier, ValidationMetadata> metadataFactory) {

        return (value, result, identifier) -> {
            if (value == null) {
                return; // Skip validation for null value
            }

            if (!validationFunction.test(value)) {
                result.addFailure(new ValidationResult.Failure(
                        metadataFactory.apply(identifier)
                ));
            }
        };
    }
}
