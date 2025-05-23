package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;

public class ValidationRuleBuilder<T> {
    private ValidationRule<T> rule;
    private ValidationMetadata.ValidationSeverity severity;
    private String category;
    private String group;
    private Boolean blocking;
    
    private ValidationRuleBuilder(ValidationRule<T> rule) {
        this.rule = rule;
    }
    
    public static <T> ValidationRuleBuilder<T> of(ValidationRule<T> rule) {
        return new ValidationRuleBuilder<>(rule);
    }
    
    public ValidationRuleBuilder<T> withSeverity(ValidationMetadata.ValidationSeverity severity) {
        this.severity = severity;
        return this;
    }
    
    public ValidationRuleBuilder<T> withCategory(String category) {
        this.category = category;
        return this;
    }
    
    public ValidationRuleBuilder<T> withGroup(String group) {
        this.group = group;
        return this;
    }
    
    public ValidationRuleBuilder<T> blocking(boolean blocking) {
        this.blocking = blocking;
        return this;
    }
    
    public ValidationRule<T> build() {
        ValidationRule<T> configuredRule = rule;
        
        if (severity != null || category != null || group != null || blocking != null) {
            configuredRule = (value, result, identifier) -> {
                int initialFailureCount = result.getFailures().size();
                
                rule.validate(value, result, identifier);
                
                for (int i = initialFailureCount; i < result.getFailures().size(); i++) {
                    ValidationResult.Failure failure = result.getFailures().get(i);
                    if (severity != null) failure.getValidationMetadata().setSeverity(severity);
                    if (category != null) failure.getValidationMetadata().setCategory(category);
                    if (group != null) failure.getValidationMetadata().setValidationGroup(group);
                    if (blocking != null) failure.getValidationMetadata().setBlocking(blocking);
                }
            };
        }
        
        return configuredRule;
    }
}