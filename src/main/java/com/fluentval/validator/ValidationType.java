package com.fluentval.validator;

public final class ValidationType {

    private ValidationType() {
        // Utility class
    }

    public static <T> ValidationRule<T> type() {
        return (value, result, identifier) -> {
            // Empty rule that only establishes type T
            // Does nothing - serves only as a type placeholder
        };
    }
}