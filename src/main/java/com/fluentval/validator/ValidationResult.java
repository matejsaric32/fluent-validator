package com.fluentval.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fluentval.validator.metadata.ValidationMetadata;
import lombok.Getter;
import lombok.ToString;

public class ValidationResult {
    private final List<Failure> failures = new ArrayList<>();
    private final Map<ValidationIdentifier, List<Failure>> failuresByIdentifier = new HashMap<>();

    public void addFailure(final Failure failure) {
        failures.add(failure);
        failuresByIdentifier
            .computeIfAbsent(failure.getValidationMetadata().getIdentifier(), k -> new ArrayList<>())
            .add(failure);
    }

    public boolean hasErrors() {
        return !failures.isEmpty();
    }

    public boolean hasErrorForIdentifier(final ValidationIdentifier identifier) {
        return failuresByIdentifier.containsKey(identifier) &&
               !failuresByIdentifier.get(identifier).isEmpty();
    }

    public List<Failure> getFailures() {
        return new ArrayList<>(failures);
    }

    public List<Failure> getErrorsForIdentifier(final ValidationIdentifier identifier) {
        return failuresByIdentifier.containsKey(identifier)
            ? new ArrayList<>(failuresByIdentifier.get(identifier))
            : List.of();
    }

    public Map<ValidationIdentifier, List<Failure>> getFailuresByIdentifier() {
        Map<ValidationIdentifier, List<Failure>> copy = new HashMap<>();
        failuresByIdentifier.forEach((k, v) -> copy.put(k, new ArrayList<>(v)));
        return copy;
    }

    public void merge(final ValidationResult other) {
        other.getFailures().forEach(this::addFailure);
    }

    public String getErrorMessages() {
        return failures.stream()
            .map(failure -> failure.getValidationMetadata().getIdentifier().value() + ": " + failure.getValidationMetadata().getMessage())
            .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        if (!hasErrors()) {
            return "ValidationResult: No errors";
        }
        return "ValidationResult: " + getErrorMessages();
    }

    public static ValidationResult success(final ValidationMetadata validationMetadata, final ValidationIdentifier identifier) {
        return new ValidationResult(); // Empty result means success
    }

    public static ValidationResult failure(final ValidationMetadata validationMetadata) {
        ValidationResult result = new ValidationResult();
        result.addFailure(new Failure(validationMetadata));
        return result;
    }

    @Getter
    @ToString
    public static final class Failure {
        private final ValidationMetadata validationMetadata;

        public Failure(ValidationMetadata validationMetadata) {
            this.validationMetadata = validationMetadata;
        }
    }
}