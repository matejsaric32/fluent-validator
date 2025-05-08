package com.fluentval.validator;

import java.util.ArrayList;
import java.util.List;

public class ScopedValidationResult extends ValidationResult {
    private final ValidationResult parentResult;
    
    public ScopedValidationResult(ValidationResult parentResult) {
        this.parentResult = parentResult;
    }
    
    @Override
    public boolean hasErrors() {
        return super.hasErrors() || parentResult.hasErrors();
    }
    
    @Override
    public boolean hasErrorForIdentifier(final ValidationIdentifier identifier) {
        return super.hasErrorForIdentifier(identifier) || 
               parentResult.hasErrorForIdentifier(identifier);
    }
    
    @Override
    public List<Failure> getFailures() {
        List<Failure> allFailures = new ArrayList<>(parentResult.getFailures());
        allFailures.addAll(super.getFailures());
        return allFailures;
    }

    public List<Failure> getScopedFailures() {
        return new ArrayList<>(super.getFailures());
    }}