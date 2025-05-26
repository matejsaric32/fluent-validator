package com.fluentval.validator.message;

import com.fluentval.validator.ValidationIdentifier;

import java.util.Map;

/**
 * Interface for providing localized validation error messages based on validation codes.
 * This interface defines the contract for message providers that generate human-readable
 * error messages from validation failure contexts, supporting internationalization,
 * customization, and template-based message generation.
 *
 * <p>ValidationMessageProvider serves as the foundation for the validation framework's
 * message generation system, enabling flexible message customization, localization
 * support, and context-aware error message creation.</p>
 *
 * <p><strong>Core Responsibilities:</strong></p>
 * <ul>
 * <li><strong>Message Generation</strong> - Convert validation codes and context into readable messages</li>
 * <li><strong>Template Processing</strong> - Support parameter substitution in message templates</li>
 * <li><strong>Code Support</strong> - Determine which validation codes can be handled</li>
 * <li><strong>Localization</strong> - Enable multiple language support for error messages</li>
 * <li><strong>Customization</strong> - Allow domain-specific message customization</li>
 * </ul>
 *
 * <p><strong>Implementation Strategy:</strong> Implementations typically maintain a mapping
 * between validation codes and message templates, using parameter substitution to create
 * context-specific error messages. Templates use placeholder syntax (e.g., "{field}", "{min}")
 * that gets replaced with actual values from the validation context.</p>
 *
 * <p><strong>Usage Pattern:</strong> This interface is primarily used by the validation
 * framework internally, but can also be used directly for custom message generation
 * or testing purposes.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see DefaultMessageProvider
 * @see MessageParameter
 * @see com.fluentval.validator.metadata.ValidationMetadata
 */
public interface ValidationMessageProvider {

    /**
     * Generates a localized error message for the specified validation failure.
     *
     * <p>This method combines a validation code with contextual parameters to produce
     * a human-readable error message. The implementation should use template substitution
     * to replace placeholders in message templates with actual values from the parameters map.</p>
     *
     * <p><strong>Template Processing:</strong> Message templates typically contain placeholders
     * in the format "{parameterName}" which should be replaced with corresponding values
     * from the parameters map. If a parameter is not found, implementations may choose
     * to leave the placeholder as-is, replace it with a default value, or handle it
     * according to their specific strategy.</p>
     *
     * <p><strong>Example Usage:</strong></p>
     * <pre>{@code
     * Map<String, Object> params = Map.of(
     *     "field", "username",
     *     "minLength", 8
     * );
     * String message = provider.getMessage("string.min_length", identifier, params);
     * // Result: "Field 'username' must be at least 8 characters long"
     * }</pre>
     *
     * @param code the validation code identifying the type of validation failure
     *             (e.g., "string.min_length", "number.max", "common.not_null")
     * @param identifier the validation identifier providing context about what was validated
     *                   (field name, path, or other identifier information)
     * @param parameters a map of parameter names to values for template substitution
     *                   (e.g., field names, constraint values, actual values)
     * @return a human-readable error message describing the validation failure,
     *         with template placeholders replaced by actual parameter values
     * @throws IllegalArgumentException if the code is null or invalid
     * @throws NullPointerException if identifier or parameters is null
     */
    String getMessage(String code, ValidationIdentifier identifier, Map<String, Object> parameters);

    /**
     * Determines whether this provider can handle the specified validation code.
     *
     * <p>This method allows the validation framework to determine which provider
     * should be used for a particular validation code, enabling provider chaining,
     * fallback mechanisms, and specialized message handling for different validation types.</p>
     *
     * <p><strong>Provider Selection:</strong> In systems with multiple message providers,
     * this method is used to select the most appropriate provider for each validation code.
     * Providers can specialize in certain domains (e.g., business rules, technical validations)
     * or languages (e.g., English, Spanish, French).</p>
     *
     * <p><strong>Implementation Note:</strong> Implementations should return {@code true}
     * only for codes they can definitively handle with meaningful messages. Returning
     * {@code true} for unsupported codes may result in poor error messages or exceptions.</p>
     *
     * @param code the validation code to check for support
     *             (e.g., "string.min_length", "number.max", "common.not_null")
     * @return {@code true} if this provider can generate meaningful messages for the specified code,
     *         {@code false} otherwise
     * @throws IllegalArgumentException if the code is null
     */
    boolean supports(String code);
}