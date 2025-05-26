package com.fluentval.validator.message;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.metadata.DefaultValidationCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of ValidationMessageRegistry that provides comprehensive
 * message provider management with automatic registration, code-specific routing,
 * and intelligent fallback mechanisms for validation error message generation.
 *
 * <p>DefaultValidationMessageRegistry implements a sophisticated provider management
 * system that balances simplicity with flexibility, offering automatic provider
 * registration for common scenarios while supporting fine-grained control for
 * specialized message handling requirements.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 * <li><strong>Automatic Registration</strong> - Seamless provider registration with automatic code mapping</li>
 * <li><strong>Code-Specific Routing</strong> - Direct provider assignment for specific validation codes</li>
 * <li><strong>Intelligent Fallback</strong> - Graceful degradation to default provider when needed</li>
 * <li><strong>Default Provider Management</strong> - Configurable default provider with automatic setup</li>
 * <li><strong>Comprehensive Coverage</strong> - Built-in support for all standard validation codes</li>
 * <li><strong>Runtime Flexibility</strong> - Dynamic provider registration and modification</li>
 * </ul>
 *
 * <p><strong>Provider Selection Algorithm:</strong></p>
 * <ol>
 * <li>Check for explicit code-to-provider mappings (highest priority)</li>
 * <li>Use registered provider that supports the validation code</li>
 * <li>Fall back to configured default provider (guaranteed coverage)</li>
 * </ol>
 *
 * <p><strong>Thread Safety:</strong> This implementation is not thread-safe by default.
 * In multi-threaded environments, external synchronization should be applied when
 * modifying provider registrations concurrently with message generation operations.</p>
 *
 * <p><strong>Performance Characteristics:</strong> Provider lookup operations are
 * optimized for O(1) performance through HashMap-based mappings, making message
 * generation suitable for high-frequency validation scenarios.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationMessageRegistry
 * @see ValidationMessageProvider
 * @see DefaultMessageProvider
 * @see DefaultValidationCode
 */
public class DefaultValidationMessageRegistry implements ValidationMessageRegistry {

    /**
     * Map storing explicit associations between validation codes and message providers.
     * This map provides direct routing for specific validation codes to designated
     * providers, enabling fine-grained control over message generation.
     */
    private final Map<String, ValidationMessageProvider> providers = new HashMap<>();

    /**
     * Default message provider used as fallback when no specialized provider
     * is available for a particular validation code. This provider ensures
     * comprehensive message coverage for all validation scenarios.
     */
    private ValidationMessageProvider validationMessageProvider;

    /**
     * Constructs a new DefaultValidationMessageRegistry with the default message provider.
     *
     * <p>This constructor initializes the registry with a {@link DefaultMessageProvider}
     * as the default provider, ensuring immediate availability of comprehensive English-language
     * error messages for all standard validation codes. The default provider is automatically
     * registered for all validation codes it supports.</p>
     *
     * <p><strong>Initialization Process:</strong></p>
     * <ol>
     * <li>Create new {@link DefaultMessageProvider} instance</li>
     * <li>Set as default validation message provider</li>
     * <li>Register provider for all supported validation codes</li>
     * <li>Establish provider mapping tables</li>
     * </ol>
     */
    public DefaultValidationMessageRegistry() {
        this.validationMessageProvider = new DefaultMessageProvider();
        registerProvider(validationMessageProvider);
    }

    /**
     * Constructs a new DefaultValidationMessageRegistry with a custom default message provider.
     *
     * <p>This constructor allows initialization with a custom message provider that serves
     * as the default for all validation codes. This is useful for applications requiring
     * specialized message handling, localization, or custom message sources from the start.</p>
     *
     * <p><strong>Initialization Process:</strong></p>
     * <ol>
     * <li>Set provided message provider as default</li>
     * <li>Register provider for all validation codes it supports</li>
     * <li>Establish provider mapping tables based on provider capabilities</li>
     * </ol>
     *
     * <p><strong>Example Usage:</strong></p>
     * <pre>{@code
     * ValidationMessageProvider customProvider = new CustomBusinessMessageProvider();
     * DefaultValidationMessageRegistry registry = new DefaultValidationMessageRegistry(customProvider);
     * }</pre>
     *
     * @param validationMessageProvider the message provider to use as the default fallback
     * @throws NullPointerException if validationMessageProvider is null
     */
    public DefaultValidationMessageRegistry(ValidationMessageProvider validationMessageProvider) {
        this.validationMessageProvider = validationMessageProvider;
        registerProvider(this.validationMessageProvider);
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Implementation Details:</strong></p>
     * <ul>
     * <li>Iterates through all {@link DefaultValidationCode} enum values</li>
     * <li>Tests provider support using {@link ValidationMessageProvider#supports(String)}</li>
     * <li>Creates direct mappings for all supported codes</li>
     * <li>Overwrites existing mappings for codes supported by the new provider</li>
     * </ul>
     *
     * <p><strong>Registration Strategy:</strong> This implementation uses an optimistic
     * registration approach, mapping all supported codes immediately rather than
     * performing runtime capability checks. This provides O(1) provider lookup
     * performance at the cost of slightly higher memory usage.</p>
     *
     * <p><strong>Provider Precedence:</strong> More recently registered providers
     * take precedence over previously registered providers for overlapping validation
     * codes, allowing for provider replacement and specialization.</p>
     *
     * @param provider the validation message provider to register
     * @throws NullPointerException if provider is null
     */
    @Override
    public void registerProvider(ValidationMessageProvider provider) {
        for (DefaultValidationCode code : DefaultValidationCode.values()) {
            if (provider.supports(code.getCode())) {
                providers.put(code.getCode(), provider);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Implementation Details:</strong> This method creates a direct mapping
     * between the validation code and provider, bypassing any automatic registration
     * logic. The mapping is stored in the internal providers map and takes precedence
     * over any general provider registrations.</p>
     *
     * <p><strong>Mapping Persistence:</strong> Explicit code-to-provider mappings
     * remain in effect until explicitly changed or overwritten by subsequent calls
     * to this method or provider re-registration operations.</p>
     *
     * @param code the validation code to associate with the provider
     * @param provider the message provider to handle the specified code
     * @throws NullPointerException if code or provider is null
     */
    @Override
    public void setProviderForCode(String code, ValidationMessageProvider provider) {
        providers.put(code, provider);
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Implementation Details:</strong></p>
     * <ul>
     * <li>Performs O(1) lookup in providers map for the validation code</li>
     * <li>Falls back to default provider if no specialized provider is found</li>
     * <li>Delegates actual message generation to the selected provider</li>
     * <li>Returns the generated message without additional processing</li>
     * </ul>
     *
     * <p><strong>Provider Selection Logic:</strong> The method first checks for
     * explicit code-to-provider mappings in the providers map. If no specific
     * mapping exists, it falls back to the configured default validation message
     * provider, ensuring that all validation codes can generate meaningful messages.</p>
     *
     * <p><strong>Error Handling:</strong> This implementation relies on the underlying
     * message providers to handle error conditions appropriately. Any exceptions
     * thrown by providers are propagated to the caller without modification.</p>
     *
     * @param code the validation code identifying the validation failure type
     * @param identifier the validation identifier providing validation context
     * @param parameters parameter map for message template substitution
     * @return localized error message from the appropriate provider
     * @throws NullPointerException if any parameter is null
     */
    @Override
    public String getMessage(String code, ValidationIdentifier identifier, Map<String, Object> parameters) {
        ValidationMessageProvider provider = providers.getOrDefault(code, validationMessageProvider);
        return provider.getMessage(code, identifier, parameters);
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Implementation Details:</strong></p>
     * <ul>
     * <li>Updates the internal default provider reference</li>
     * <li>Automatically registers the new provider for all supported codes</li>
     * <li>Maintains existing explicit code-to-provider mappings</li>
     * <li>Ensures comprehensive fallback coverage for all validation scenarios</li>
     * </ul>
     *
     * <p><strong>Registration Side Effects:</strong> Setting a new default provider
     * triggers automatic registration via {@link #registerProvider(ValidationMessageProvider)}.
     * This ensures that the new default provider is properly integrated into the
     * provider selection mechanism for all codes it supports.</p>
     *
     * <p><strong>Existing Mappings:</strong> Explicit code-to-provider mappings
     * created via {@link #setProviderForCode(String, ValidationMessageProvider)}
     * are preserved and continue to take precedence over the new default provider
     * for their respective validation codes.</p>
     *
     * @param provider the validation message provider to use as the default
     * @throws NullPointerException if provider is null
     */
    @Override
    public void setValidationMessageProvider(ValidationMessageProvider provider) {
        this.validationMessageProvider = provider;
        registerProvider(provider);
    }
}