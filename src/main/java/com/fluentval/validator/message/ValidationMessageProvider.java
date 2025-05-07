package com.fluentval.validator.message;

import com.fluentval.validator.ValidationIdentifier;

import java.util.Map;

public interface ValidationMessageProvider {

    String getMessage(String code, ValidationIdentifier identifier, Map<String, Object> parameters);
    
    boolean supports(String code);
}