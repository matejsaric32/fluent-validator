package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Data
@EqualsAndHashCode
@ToString
public abstract class ValidationMetadata {

    public enum ValidationSeverity { ERROR, WARNING, INFO }

    private final ValidationIdentifier identifier;
    private final String errorCode;
    private final Map<String, Object> messageParameters = new HashMap<>();

    // Enhanced metadata fields
    private ValidationSeverity severity = ValidationSeverity.ERROR;
    private String category;
    private String validationGroup;
    private boolean blocking = true;
    private Instant validationTime = Instant.now();
    private String source;
    private String additionalErrorCode;

    protected ValidationMetadata(ValidationIdentifier identifier, String errorCode) {
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
        this.errorCode = Objects.requireNonNull(errorCode, "ErrorCode cannot be null");
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    public ValidationMetadata setSeverity(ValidationSeverity severity) {
        this.severity = severity;
        addMessageParameter(MessageParameter.SEVERITY, severity.name());
        return this;
    }

    public ValidationMetadata setCategory(String category) {
        this.category = category;
        if (category != null) {
            addMessageParameter(MessageParameter.CATEGORY, category);
        }
        return this;
    }

    protected void addMessageParameter(String key, Object message) {
        messageParameters.put(key, message);
    }

    protected void addMessageParameter(MessageParameter key, Object message) {
        addMessageParameter(key.getKey(), message);
    }

    public ValidationMetadata enrich(Consumer<ValidationMetadata> enricher) {
        enricher.accept(this);
        return this;
    }
}