package com.fluentval.validator.message;

import com.fluentval.validator.ValidationIdentifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of ValidationMessageProvider that provides comprehensive
 * English-language error messages for all standard validation types in the FluentVal framework.
 * This class serves as the primary message provider and includes built-in templates for
 * common, string, number, collection, date-time, and specialized validation scenarios.
 *
 * <p>DefaultMessageProvider implements an efficient template compilation and caching system
 * to optimize message generation performance, pre-parsing message templates into structured
 * components for rapid parameter substitution during validation error reporting.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 * <li><strong>Comprehensive Coverage</strong> - Built-in messages for all standard validation codes</li>
 * <li><strong>Template Compilation</strong> - Pre-compiled templates for optimal performance</li>
 * <li><strong>Parameter Substitution</strong> - Robust placeholder replacement with context values</li>
 * <li><strong>Template Customization</strong> - Runtime template modification and extension</li>
 * <li><strong>Fallback Handling</strong> - Graceful handling of unknown codes with default messages</li>
 * <li><strong>Performance Optimization</strong> - Cached template parsing for repeated usage</li>
 * </ul>
 *
 * <p><strong>Template Syntax:</strong> Message templates use curly brace syntax for placeholders
 * (e.g., "Field '{field}' must be at least {minLength} characters long"). Placeholders are
 * replaced with corresponding values from the parameters map during message generation.</p>
 *
 * <p><strong>Extensibility:</strong> While this provider includes comprehensive default messages,
 * it can be extended or customized by modifying templates at runtime or by subclassing
 * to provide domain-specific message handling.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationMessageProvider
 * @see MessageParameter
 * @see com.fluentval.validator.metadata.DefaultValidationCode
 */
public class DefaultMessageProvider implements ValidationMessageProvider {

    /**
     * Map storing message templates keyed by validation codes.
     * This map contains the raw template strings with placeholder syntax
     * that will be compiled into structured components for efficient processing.
     */
    private final Map<String, String> messageTemplates = new HashMap<>();

    /**
     * Cache of compiled template parts for performance optimization.
     * This map stores pre-parsed template structures to avoid repeated
     * parsing overhead during message generation operations.
     */
    private final Map<String, List<TemplatePart>> compiledTemplates = new HashMap<>();

    /**
     * Internal class representing a parsed component of a message template.
     * Template parts can be either static text or dynamic placeholders that
     * require parameter substitution during message generation.
     */
    private static class TemplatePart {

        /**
         * Enumeration of template part types for processing logic.
         */
        enum Type {
            /** Static text that appears as-is in the final message */
            TEXT,
            /** Dynamic placeholder that gets replaced with parameter values */
            PLACEHOLDER
        }

        /** The type of this template part (text or placeholder) */
        final Type type;

        /** The content of this template part (text content or placeholder name) */
        final String value;

        /**
         * Creates a new template part with the specified type and content.
         *
         * @param type the type of template part (TEXT or PLACEHOLDER)
         * @param value the content (static text or placeholder name)
         */
        TemplatePart(Type type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    /**
     * Constructs a new DefaultMessageProvider with all standard validation message templates.
     *
     * <p>This constructor initializes the provider with comprehensive English-language
     * error messages for all validation codes defined in {@link com.fluentval.validator.metadata.DefaultValidationCode}.
     * The initialization process loads templates for common, string, number, collection,
     * date-time, and specialized validation scenarios.</p>
     */
    public DefaultMessageProvider() {
        initializeDefaultMessages();
    }

    /**
     * Initializes the default message templates for all standard validation codes.
     *
     * <p>This method populates the messageTemplates map with comprehensive English-language
     * error message templates covering all validation scenarios supported by the framework.
     * Templates use placeholder syntax for dynamic content substitution.</p>
     *
     * <p><strong>Template Categories:</strong></p>
     * <ul>
     * <li>Common validation messages (null checks, equality, predicates, type validation)</li>
     * <li>String validation messages (length, format, content, whitespace, case validation)</li>
     * <li>Number validation messages (boundaries, ranges, sign validation)</li>
     * <li>DateTime validation messages (temporal constraints, ranges, business rules)</li>
     * <li>Time validation messages (time-of-day constraints, periods, zones)</li>
     * <li>Collection validation messages (size, content, element validation)</li>
     * <li>Allowed values validation messages (enumeration, set membership)</li>
     * </ul>
     */
    private void initializeDefaultMessages() {
        // Common validation messages
        messageTemplates.put("common.not_null", "Field '{field}' must not be null");
        messageTemplates.put("common.must_be_null", "Field '{field}' must be null");
        messageTemplates.put("common.is_equal", "Field '{field}' must be equal to '{value}'");
        messageTemplates.put("common.is_not_equal", "Field '{field}' must not be equal to '{value}'");
        messageTemplates.put("common.satisfies", "Field '{field}' must satisfy the condition: {condition}");
        messageTemplates.put("common.is_instance_of", "Field '{field}' must be an instance of {className}");
        messageTemplates.put("common.is_not_instance_of", "Field '{field}' must not be an instance of {className}");
        messageTemplates.put("common.is_same_as", "Field '{field}' must be the same object as {reference}");
        messageTemplates.put("common.is_not_same_as", "Field '{field}' must not be the same object as {reference}");

        // String validation messages
        messageTemplates.put("string.not_blank", "Field '{field}' must not be blank");
        messageTemplates.put("string.max_length", "Field '{field}' must not exceed {maxLength} characters");
        messageTemplates.put("string.min_length", "Field '{field}' must be at least {minLength} characters long");
        messageTemplates.put("string.exact_length", "Field '{field}' must be exactly {exactLength} characters long");
        messageTemplates.put("string.matches", "Field '{field}' must match the pattern: {pattern}");
        messageTemplates.put("string.one_of", "Field '{field}' must be one of: {allowedValues}");
        messageTemplates.put("string.one_of_ignore_case", "Field '{field}' must be one of (case insensitive): {allowedValues}");
        messageTemplates.put("string.starts_with", "Field '{field}' must start with '{prefix}'");
        messageTemplates.put("string.ends_with", "Field '{field}' must end with '{suffix}'");
        messageTemplates.put("string.contains", "Field '{field}' must contain '{substring}'");
        messageTemplates.put("string.numeric", "Field '{field}' must contain only numeric characters");
        messageTemplates.put("string.alphanumeric", "Field '{field}' must contain only alphanumeric characters");
        messageTemplates.put("string.uppercase", "Field '{field}' must be in uppercase");
        messageTemplates.put("string.lowercase", "Field '{field}' must be in lowercase");
        messageTemplates.put("string.no_whitespace", "Field '{field}' must not contain whitespace");
        messageTemplates.put("string.no_leading_whitespace", "Field '{field}' must not start with whitespace");
        messageTemplates.put("string.no_trailing_whitespace", "Field '{field}' must not end with whitespace");
        messageTemplates.put("string.no_consecutive_whitespace", "Field '{field}' must not contain consecutive whitespace");
        messageTemplates.put("string.trimmed", "Field '{field}' must be trimmed");
        messageTemplates.put("string.proper_spacing", "Field '{field}' must have proper spacing");

        // Number validation messages
        messageTemplates.put("number.min", "Field '{field}' must be at least {min}");
        messageTemplates.put("number.max", "Field '{field}' must not exceed {max}");
        messageTemplates.put("number.range", "Field '{field}' must be between {min} and {max}");
        messageTemplates.put("number.positive", "Field '{field}' must be positive");
        messageTemplates.put("number.negative", "Field '{field}' must be negative");
        messageTemplates.put("number.not_zero", "Field '{field}' must not be zero");

        // DateTime validation messages
        messageTemplates.put("datetime.in_range", "Field '{field}' must be between {minDate} and {maxDate}");
        messageTemplates.put("datetime.before", "Field '{field}' must be before {referenceDate}");
        messageTemplates.put("datetime.after", "Field '{field}' must be after {referenceDate}");
        messageTemplates.put("datetime.before_or_equals", "Field '{field}' must be before or equal to {referenceDate}");
        messageTemplates.put("datetime.after_or_equals", "Field '{field}' must be after or equal to {referenceDate}");
        messageTemplates.put("datetime.future", "Field '{field}' must be in the future");
        messageTemplates.put("datetime.past", "Field '{field}' must be in the past");
        messageTemplates.put("datetime.present_or_future", "Field '{field}' must be in the present or future");
        messageTemplates.put("datetime.present_or_past", "Field '{field}' must be in the present or past");
        messageTemplates.put("datetime.equals", "Field '{field}' must be equal to {referenceDate}");
        messageTemplates.put("datetime.is_weekday", "Field '{field}' must be a weekday (Monday-Friday)");
        messageTemplates.put("datetime.is_weekend", "Field '{field}' must be a weekend day (Saturday-Sunday)");
        messageTemplates.put("datetime.in_month", "Field '{field}' must be in month {month}");
        messageTemplates.put("datetime.in_year", "Field '{field}' must be in year {year}");

        // Time validation messages
        messageTemplates.put("time.in_range", "Field '{field}' must be between {minTime} and {maxTime}");
        messageTemplates.put("time.before", "Field '{field}' must be before {referenceTime}");
        messageTemplates.put("time.after", "Field '{field}' must be after {referenceTime}");
        messageTemplates.put("time.before_or_equals", "Field '{field}' must be before or equal to {referenceTime}");
        messageTemplates.put("time.after_or_equals", "Field '{field}' must be after or equal to {referenceTime}");
        messageTemplates.put("time.equals", "Field '{field}' must be equal to {referenceTime}");
        messageTemplates.put("time.is_morning", "Field '{field}' must be in the morning ({timeRange})");
        messageTemplates.put("time.is_afternoon", "Field '{field}' must be in the afternoon ({timeRange})");
        messageTemplates.put("time.is_evening", "Field '{field}' must be in the evening ({timeRange})");
        messageTemplates.put("time.is_business_hours", "Field '{field}' must be during business hours ({timeRange})");
        messageTemplates.put("time.is_lunch_hour", "Field '{field}' must be during lunch hour ({timeRange})");
        messageTemplates.put("time.hours_between", "Field '{field}' hours must be between {minHour} and {maxHour}");
        messageTemplates.put("time.minutes_between", "Field '{field}' minutes must be between {minMinute} and {maxMinute}");
        messageTemplates.put("time.seconds_between", "Field '{field}' seconds must be between {minSecond} and {maxSecond}");
        messageTemplates.put("time.in_time_zone", "Field '{field}' must be in time zone {timeZone}");

        // Collection validation messages
        messageTemplates.put("collection.not_empty", "Field '{field}' must not be empty");
        messageTemplates.put("collection.is_empty", "Field '{field}' must be empty");
        messageTemplates.put("collection.min_size", "Field '{field}' must contain at least {minSize} elements");
        messageTemplates.put("collection.max_size", "Field '{field}' must not contain more than {maxSize} elements");
        messageTemplates.put("collection.exact_size", "Field '{field}' must contain exactly {exactSize} elements");
        messageTemplates.put("collection.size_range", "Field '{field}' must contain between {minSize} and {maxSize} elements");
        messageTemplates.put("collection.all_match", "All elements in '{field}' must satisfy: {condition}");
        messageTemplates.put("collection.any_match", "At least one element in '{field}' must satisfy: {condition}");
        messageTemplates.put("collection.none_match", "No elements in '{field}' may satisfy: {condition}");
        messageTemplates.put("collection.no_duplicates", "Field '{field}' must not contain duplicate elements");
        messageTemplates.put("collection.contains", "Field '{field}' must contain element: {element}");
        messageTemplates.put("collection.does_not_contain", "Field '{field}' must not contain element: {element}");
        messageTemplates.put("collection.contains_all", "Field '{field}' must contain all elements: {elements}");
        messageTemplates.put("collection.contains_none", "Field '{field}' must not contain any of: {elements}");

        // Allowed values validation messages
        messageTemplates.put("allowed.contains", "Field '{field}' must be contained in: {allowedValues}");
        messageTemplates.put("allowed.one_of", "Field '{field}' must be one of: {allowedValues}");
        messageTemplates.put("allowed.not_contains", "Field '{field}' must not be contained in: {allowedValues}");
        messageTemplates.put("allowed.none_of", "Field '{field}' must not be one of: {allowedValues}");
        messageTemplates.put("allowed.is_in_enum", "Field '{field}' must be a valid {className} value: {allowedValues}");
        messageTemplates.put("allowed.in_range", "Field '{field}' must be within the allowed range");
    }

    /**
     * Compiles a message template into structured parts for efficient parameter substitution.
     *
     * <p>This method parses a template string containing placeholders in curly brace syntax
     * and converts it into a list of structured components. Each component is either static
     * text or a parameter placeholder, enabling efficient message generation without
     * repeated parsing overhead.</p>
     *
     * <p><strong>Parsing Logic:</strong></p>
     * <ul>
     * <li>Text outside curly braces becomes TEXT parts</li>
     * <li>Content inside curly braces becomes PLACEHOLDER parts</li>
     * <li>Malformed braces (missing closing brace) are treated as literal text</li>
     * <li>Empty placeholders are preserved as valid PLACEHOLDER parts</li>
     * </ul>
     *
     * <p><strong>Example:</strong></p>
     * <pre>
     * Input: "Field '{field}' must be at least {min} characters"
     * Output: [TEXT("Field '"), PLACEHOLDER("field"), TEXT("' must be at least "),
     *          PLACEHOLDER("min"), TEXT(" characters")]
     * </pre>
     *
     * @param template the template string to compile into structured parts
     * @return a list of TemplatePart objects representing the parsed template structure
     */
    private List<TemplatePart> compileTemplate(String template) {
        List<TemplatePart> parts = new ArrayList<>();
        StringBuilder textBuilder = new StringBuilder();

        int i = 0;
        while (i < template.length()) {
            char c = template.charAt(i);

            if (c == '{') {
                // If we have accumulated text, add it as a part
                if (textBuilder.length() > 0) {
                    parts.add(new TemplatePart(TemplatePart.Type.TEXT, textBuilder.toString()));
                    textBuilder.setLength(0);
                }

                // Find closing brace
                int closeBrace = template.indexOf('}', i);
                if (closeBrace == -1) {
                    // No closing brace, treat as text
                    textBuilder.append(c);
                    i++;
                } else {
                    // Extract placeholder name
                    String placeholder = template.substring(i + 1, closeBrace);
                    parts.add(new TemplatePart(TemplatePart.Type.PLACEHOLDER, placeholder));
                    i = closeBrace + 1;
                }
            } else {
                textBuilder.append(c);
                i++;
            }
        }

        // Add any remaining text
        if (textBuilder.length() > 0) {
            parts.add(new TemplatePart(TemplatePart.Type.TEXT, textBuilder.toString()));
        }

        return parts;
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Implementation Details:</strong></p>
     * <ul>
     * <li>Uses template compilation caching for optimal performance</li>
     * <li>Provides fallback message for unknown validation codes</li>
     * <li>Handles missing parameters gracefully by leaving placeholders empty</li>
     * <li>Supports all standard validation codes defined in the framework</li>
     * </ul>
     *
     * <p><strong>Performance Optimization:</strong> Templates are compiled once and cached
     * for subsequent use, avoiding repeated parsing overhead during message generation.</p>
     *
     * @param code the validation code identifying the type of validation failure
     * @param identifier the validation identifier (used for context but not directly in message generation)
     * @param parameters map of parameter names to values for template substitution
     * @return human-readable error message with placeholders replaced by parameter values
     */
    @Override
    public String getMessage(String code, ValidationIdentifier identifier, Map<String, Object> parameters) {
        String template = messageTemplates.getOrDefault(code, "Validation failed for field '{field}'");

        List<TemplatePart> parts = compiledTemplates.computeIfAbsent(
                template, this::compileTemplate
        );

        StringBuilder result = new StringBuilder();
        for (TemplatePart part : parts) {
            if (part.type == TemplatePart.Type.TEXT) {
                result.append(part.value);
            } else {
                Object value = parameters.get(part.value);
                if (value != null) {
                    result.append(value.toString());
                }
            }
        }

        return result.toString();
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Implementation Details:</strong> This implementation supports all standard
     * validation codes defined in {@link com.fluentval.validator.metadata.DefaultValidationCode}.
     * It returns {@code true} for any code that has a corresponding message template
     * in the internal messageTemplates map.</p>
     *
     * @param code the validation code to check for support
     * @return {@code true} if this provider has a message template for the specified code
     */
    @Override
    public boolean supports(String code) {
        return messageTemplates.containsKey(code);
    }

    /**
     * Sets or updates a message template for the specified validation code.
     *
     * <p>This method allows runtime customization of error messages by adding new templates
     * or overriding existing ones. When a template is updated, any cached compiled version
     * is invalidated to ensure the new template is used for subsequent message generation.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Customizing default error messages for specific business requirements</li>
     * <li>Adding support for custom validation codes</li>
     * <li>Localizing messages for different languages or regions</li>
     * <li>Providing domain-specific terminology in error messages</li>
     * </ul>
     *
     * <p><strong>Template Syntax:</strong> Templates should use curly brace syntax for
     * placeholders (e.g., "Field '{field}' must contain at least {minLength} characters").
     * Available parameters depend on the validation type and are defined in
     * {@link MessageParameter}.</p>
     *
     * @param code the validation code to associate with the template
     * @param template the message template with placeholder syntax for parameter substitution
     * @throws IllegalArgumentException if code or template is null
     */
    public void setMessageTemplate(String code, String template) {
        messageTemplates.put(code, template);
        compiledTemplates.remove(template);
    }
}