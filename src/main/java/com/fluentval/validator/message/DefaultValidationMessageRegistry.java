package com.fluentval.validator.message;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.metadata.DefaultValidationCode;

import java.util.HashMap;
import java.util.Map;

public class DefaultValidationMessageRegistry {
    private final Map<String, ValidationMessageProvider> providers = new HashMap<>();
    private ValidationMessageProvider defaultProvider;

    public DefaultValidationMessageRegistry() {
        this.defaultProvider = new DefaultMessageProvider();
        registerProvider(defaultProvider);
    }

    public DefaultValidationMessageRegistry(ValidationMessageProvider validationMessageProvider) {
        this.defaultProvider = validationMessageProvider;
        registerProvider(defaultProvider);
    }

    public void registerProvider(ValidationMessageProvider provider) {
        for (DefaultValidationCode code : DefaultValidationCode.values()) {
            if (provider.supports(code.getCode())) {
                providers.put(code.getCode(), provider);
            }
        }
    }

    public void setProviderForCode(String code, ValidationMessageProvider provider) {
        providers.put(code, provider);
    }

    public String getMessage(String code, ValidationIdentifier identifier, Map<String, Object> parameters) {
        ValidationMessageProvider provider = providers.getOrDefault(code, defaultProvider);
        return provider.getMessage(code, identifier, parameters);
    }

    public void setDefaultProvider(ValidationMessageProvider provider) {
        this.defaultProvider = provider;
        registerProvider(provider);
    }
}