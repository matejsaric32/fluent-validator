package com.fluentval.validator.message;

import com.fluentval.validator.ValidationIdentifier;

import java.util.Map;

/**
 * Registry interface for managing multiple validation message providers and coordinating
 * message generation across different validation domains, languages, and customization requirements.
 * This interface defines the contract for message provider registration, organization, and
 * centralized message retrieval in complex validation scenarios.
 *
 * <p>ValidationMessageRegistry serves as the central coordination point for message generation
 * in the validation framework, enabling sophisticated message provider management including
 * provider specialization, fallback mechanisms, localization support, and domain-specific
 * message customization.</p>
 *
 * <p><strong>Core Responsibilities:</strong></p>
 * <ul>
 * <li><strong>Provider Management</strong> - Register, organize, and coordinate multiple message providers</li>
 * <li><strong>Code-Provider Mapping</strong> - Associate specific validation codes with specialized providers</li>
 * <li><strong>Message Coordination</strong> - Route message requests to appropriate providers</li>
 * <li><strong>Fallback Handling</strong> - Provide default message generation when specialized providers unavailable</li>
 * <li><strong>Extensibility</strong> - Support dynamic provider registration and customization</li>
 * </ul>
 *
 * <p><strong>Usage Scenarios:</strong></p>
 * <ul>
 * <li><strong>Multi-language Applications</strong> - Different providers for different locales</li>
 * <li><strong>Domain Specialization</strong> - Specialized providers for business vs. technical validations</li>
 * <li><strong>Custom Message Sources</strong> - Providers loading messages from databases, files, or services</li>
 * <li><strong>A/B Testing</strong> - Different message providers for user experience experiments</li>
 * <li><strong>Hierarchical Messages</strong> - General and specific providers with fallback chains</li>
 * </ul>
 *
 * <p><strong>Design Pattern:</strong> This registry implements a combination of the Registry
 * and Strategy patterns, allowing flexible provider selection and message generation strategies
 * while maintaining a clean separation of concerns between validation logic and message presentation.</p>
 *
 * @author Matej Šarić
 * @see ValidationMessageProvider
 * @see DefaultValidationMessageRegistry
 * @see DefaultMessageProvider
 * @since 1.2.3
 */
public interface ValidationMessageRegistry {

    /**
     * Registers a validation message provider with the registry, automatically mapping
     * all supported validation codes to the provider for message generation.
     *
     * <p>This method performs automatic provider registration by querying the provider
     * for all validation codes it supports and creating appropriate mappings. This is
     * the preferred method for registering providers that handle multiple validation types.</p>
     *
     * <p><strong>Registration Process:</strong></p>
     * <ol>
     * <li>Query provider for supported validation codes using {@link ValidationMessageProvider#supports(String)}</li>
     * <li>Create mappings for all supported codes</li>
     * <li>Update internal provider routing tables</li>
     * <li>Preserve any existing specialized mappings for unsupported codes</li>
     * </ol>
     *
     * <p><strong>Provider Precedence:</strong> If multiple providers support the same
     * validation code, the behavior depends on the implementation. Typically, the most
     * recently registered provider takes precedence, but implementations may use different
     * strategies such as provider priority or specialization rules.</p>
     *
     * <p><strong>Example Usage:</strong></p>
     * <pre>{@code
     * ValidationMessageRegistry registry = new DefaultValidationMessageRegistry();
     * ValidationMessageProvider spanishProvider = new SpanishMessageProvider();
     * registry.registerProvider(spanishProvider);
     * }</pre>
     *
     * @param provider the validation message provider to register with the registry
     * @throws NullPointerException     if provider is null
     * @throws IllegalArgumentException if provider cannot be registered due to configuration issues
     */
    void registerProvider(ValidationMessageProvider provider);

    /**
     * Associates a specific validation code with a designated message provider,
     * creating a direct mapping for targeted message generation control.
     *
     * <p>This method provides fine-grained control over provider selection by creating
     * explicit mappings between validation codes and providers. This is useful for
     * specialized message handling, custom business rules, or when different providers
     * should handle different types of validation failures.</p>
     *
     * <p><strong>Mapping Strategy:</strong> This method creates or updates a direct
     * association between the specified code and provider, bypassing any automatic
     * registration logic. The mapping remains in effect until explicitly changed
     * or the provider is re-registered through other means.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Override default provider for specific validation types</li>
     * <li>Route business-critical validations to specialized providers</li>
     * <li>Implement A/B testing for specific error messages</li>
     * <li>Provide custom messages for domain-specific validation codes</li>
     * <li>Handle legacy or deprecated validation codes with specialized providers</li>
     * </ul>
     *
     * <p><strong>Example Usage:</strong></p>
     * <pre>{@code
     * registry.setProviderForCode("business.credit_limit", customBusinessProvider);
     * registry.setProviderForCode("string.email", emailSpecializedProvider);
     * }</pre>
     *
     * @param code     the validation code to associate with the provider
     * @param provider the message provider to handle messages for the specified code
     * @throws NullPointerException     if code or provider is null
     * @throws IllegalArgumentException if code is empty or provider is invalid
     */
    void setProviderForCode(String code, ValidationMessageProvider provider);

    /**
     * Retrieves a localized validation error message for the specified validation failure
     * by routing the request to the appropriate registered message provider.
     *
     * <p>This method serves as the central entry point for message generation in the
     * validation framework, coordinating between multiple providers to deliver the
     * most appropriate error message for the given validation context.</p>
     *
     * <p><strong>Provider Selection Logic:</strong></p>
     * <ol>
     * <li>Check for explicit code-to-provider mappings created via {@link #setProviderForCode}</li>
     * <li>Use registered providers based on their support for the validation code</li>
     * <li>Fall back to default provider if no specialized provider is available</li>
     * <li>Apply any configured provider precedence or selection rules</li>
     * </ol>
     *
     * <p><strong>Message Generation Process:</strong></p>
     * <ol>
     * <li>Select appropriate provider based on validation code</li>
     * <li>Delegate message generation to selected provider</li>
     * <li>Return formatted message with parameter substitution applied</li>
     * <li>Handle any provider errors with fallback strategies</li>
     * </ol>
     *
     * <p><strong>Example Usage:</strong></p>
     * <pre>{@code
     * Map<String, Object> params = Map.of(
     *     "field", "username",
     *     "minLength", 8
     * );
     * String message = registry.getMessage("string.min_length", identifier, params);
     * // Result: Localized message from appropriate provider
     * }</pre>
     *
     * @param code       the validation code identifying the type of validation failure
     * @param identifier the validation identifier providing context about what was validated
     * @param parameters map of parameter names to values for message template substitution
     * @return localized error message generated by the appropriate provider
     * @throws NullPointerException     if code, identifier, or parameters is null
     * @throws IllegalArgumentException if code is empty or invalid
     * @throws RuntimeException         if message generation fails and no fallback is available
     */
    String getMessage(String code, ValidationIdentifier identifier, Map<String, Object> parameters);

    /**
     * Sets the default validation message provider used as a fallback when no
     * specialized provider is available for a particular validation code.
     *
     * <p>This method establishes the default provider that serves as the foundation
     * for message generation when specialized providers are not available or do not
     * support specific validation codes. The default provider typically provides
     * comprehensive coverage of all standard validation scenarios.</p>
     *
     * <p><strong>Fallback Strategy:</strong> When a validation code is requested
     * but no registered provider specifically supports it, the registry falls back
     * to this default provider. This ensures that all validation failures can
     * generate meaningful error messages even in the absence of specialized providers.</p>
     *
     * <p><strong>Registration Side Effects:</strong> Setting a new default provider
     * may trigger automatic registration of the provider for all validation codes
     * it supports, depending on the implementation. This ensures that the default
     * provider is properly integrated into the provider selection mechanism.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Replace default English messages with localized alternatives</li>
     * <li>Implement custom message formatting or styling</li>
     * <li>Switch between different message sources (database, files, services)</li>
     * <li>Provide domain-specific default messages for business applications</li>
     * <li>Enable dynamic message provider switching at runtime</li>
     * </ul>
     *
     * <p><strong>Example Usage:</strong></p>
     * <pre>{@code
     * ValidationMessageProvider spanishProvider = new SpanishMessageProvider();
     * registry.setValidationMessageProvider(spanishProvider);
     * }</pre>
     *
     * @param provider the validation message provider to use as the default fallback
     * @throws NullPointerException     if provider is null
     * @throws IllegalArgumentException if provider cannot serve as a default provider
     */
    void setValidationMessageProvider(ValidationMessageProvider provider);
}