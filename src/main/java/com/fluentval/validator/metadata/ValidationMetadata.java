package com.fluentval.validator.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fluentval.validator.ValidationIdentifier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public abstract class ValidationMetadata {
    
    private final ValidationIdentifier identifier;
    private final String errorCode;
    private final Map<String, String> messageParameters = new HashMap<>();

    protected ValidationMetadata(ValidationIdentifier identifier, String errorCode, Map<String, String> messageParameters) {
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
        this.errorCode = Objects.requireNonNull(errorCode, "ErrorCode cannot be null");
    }

    protected void addMessageParameter(String key, String message) {
        messageParameters.put(key, message);
    }

}