package com.fluentval.validator;

import com.fluentval.validator.metadata.ValidationMetadata;
import lombok.Getter;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface ValidationRule<T> {

    void validate(final T value,
        final ValidationResult result,
        final ValidationIdentifier identifier);

    default ValidationRule<T> and(final ValidationRule<T> other) {
        return (value, result, identifier) -> {
            validate(value, result, identifier);
            if (!result.hasErrorForIdentifier(identifier)) {
                other.validate(value, result, identifier);
            }
        };
    }

    default ValidationRule<T> andAlways(final ValidationRule<T> other) {
        return (value, result, identifier) -> {
            validate(value, result, identifier);
            other.validate(value, result, identifier);
        };
    }

    default ValidationRule<T> andIf(final Predicate<T> condition,
        final ValidationRule<T> other) {
        return (value, result, identifier) -> {
            validate(value, result, identifier);
            if (!result.hasErrorForIdentifier(identifier) && condition.test(value)) {
                other.validate(value, result, identifier);
            }
        };
    }

    default ValidationRule<T> andIfNoErrorsFor(ValidationIdentifier[] identifiers, ValidationRule<T> other) {
        return (value, result, identifier) -> {
            validate(value, result, identifier);

            boolean hasNoErrors = true;
            for (ValidationIdentifier id : identifiers) {
                if (result.hasErrorForIdentifier(id)) {
                    hasNoErrors = false;
                    break;
                }
            }

            if (hasNoErrors) {
                other.validate(value, result, identifier);
            }
        };
    }

    default ValidationRule<T> andIfNoErrorsFor(ValidationRule<T> other, ValidationIdentifier... identifiers) {
        return andIfNoErrorsFor(identifiers, other);
    }

    default ValidationRule<T> andIfNoErrorFor(ValidationIdentifier identifier, ValidationRule<T> other) {
        return (value, result, currentIdentifier) -> {
            validate(value, result, currentIdentifier);

            if (!result.hasErrorForIdentifier(identifier)) {
                other.validate(value, result, currentIdentifier);
            }
        };
    }

    default ValidationRule<T> breakIf(final Predicate<T> breakCondition) {
        return (value, result, identifier) -> {
            if (!breakCondition.test(value)) {
                validate(value, result, identifier);
            }
        };
    }

    default ValidationRule<T> peek(final Consumer<ValidationState<T>> peekConsumer) {
        return (value, result, identifier) -> {
            validate(value, result, identifier);
            peekConsumer.accept(new ValidationState<>(value, result, identifier));
        };
    }

    @Deprecated(since = "This method is for debugging only")
    default ValidationRule<T> debug(final String name) {
        return (value, result, identifier) -> {
            System.out.println(
                "DEBUG [" + name + "] Before validation for identifier: " + identifier.value());
            System.out.println("Value: " + (value == null ? "null" : value.toString()));
            System.out.println("Has errors: " + result.hasErrorForIdentifier(identifier));

            validate(value, result, identifier);

            System.out.println(
                "DEBUG [" + name + "] After validation for identifier: " + identifier.value());
            System.out.println("Has errors: " + result.hasErrorForIdentifier(identifier));
            if (result.hasErrorForIdentifier(identifier)) {
                System.out.println("Errors: " + result.getErrorsForIdentifier(identifier));
            }
            System.out.println("---");
        };
    }

    default ValidationRule<T> named(final String name) {
        return (value, result, identifier) -> {
            ValidationRule<T> original = this;
            try {
                original.validate(value, result, identifier);
            } catch (Exception e) {
                throw new RuntimeException(
                    "Error in validation rule '" + name + "' for identifier '" + identifier.value()
                    + "'", e);
            }
        };
    }

    static <V> ValidationRule<V> fail(final ValidationMetadata validationMetadata) {
        return (value, result, identifier) -> result.addFailure(
            new ValidationResult.Failure(validationMetadata));
    }

    static <V> ValidationRule<V> noop() {
        return (value, result, identifier) -> {
        };
    }

    // Add a method to enrich validation metadata
    default ValidationRule<T> withMetadata(Consumer<ValidationMetadata> enricher) {
        return (value, result, identifier) -> {
            // Keep track of failures before validation
            int initialFailureCount = result.getFailures().size();

            // Execute the validation
            validate(value, result, identifier);

            // Enrich any new failures
            for (int i = initialFailureCount; i < result.getFailures().size(); i++) {
                result.getFailures().get(i).withEnrichedMetadata(enricher);
            }
        };
    }

    default ValidationRule<T> withSeverity(ValidationMetadata.ValidationSeverity severity) {
        return withMetadata(metadata -> metadata.setSeverity(severity));
    }

    default ValidationRule<T> withCategory(String category) {
        return withMetadata(metadata -> metadata.setCategory(category));
    }

    default ValidationRule<T> withGroup(String group) {
        return withMetadata(metadata -> metadata.setValidationGroup(group));
    }

    default ValidationRule<T> blocking(boolean blocking) {
        return withMetadata(metadata -> metadata.setBlocking(blocking));
    }

    @Getter
    class ValidationState<T> {

        private final T value;
        private final ValidationResult result;
        private final ValidationIdentifier identifier;

        public ValidationState(final T value,
            final ValidationResult result,
            final ValidationIdentifier identifier) {
            this.value = value;
            this.result = result;
            this.identifier = identifier;
        }

        public boolean hasError() {
            return result.hasErrorForIdentifier(identifier);
        }

        public List<ValidationResult.Failure> getErrors() {
            return result.getErrorsForIdentifier(identifier);
        }

    }

}