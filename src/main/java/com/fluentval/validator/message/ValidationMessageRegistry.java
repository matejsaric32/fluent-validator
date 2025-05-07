package com.fluentval.validator.message;

import com.fluentval.validator.ValidationIdentifier;
import java.util.Map;

public interface ValidationMessageRegistry {
    
    void registerProvider(ValidationMessageProvider provider);
    
    void setProviderForCode(String code, ValidationMessageProvider provider);
    
    String getMessage(String code, ValidationIdentifier identifier, Map<String, Object> parameters);
    
    void setDefaultProvider(ValidationMessageProvider provider);
}