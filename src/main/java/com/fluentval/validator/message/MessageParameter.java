package com.fluentval.validator.message;

import com.fluentval.validator.metadata.ValidationMetadata;

/**
 * Enumeration of standard message parameters used in validation error message templates.
 * This enum provides a centralized catalog of all parameter keys that can be used for
 * message template substitution, organized by validation domain and parameter purpose,
 * enabling consistent and comprehensive error message generation across the validation framework.
 *
 * <p>MessageParameter serves as the foundation for error message parameterization,
 * providing structured access to validation context information that can be embedded
 * in localized error messages through template substitution mechanisms.</p>
 *
 * <p><strong>Parameter Organization:</strong></p>
 * <ul>
 * <li><strong>Common parameters</strong> - Universal parameters applicable to all validation types</li>
 * <li><strong>String validation parameters</strong> - Text-specific parameters for length, pattern, and content validation</li>
 * <li><strong>Number validation parameters</strong> - Numeric constraint parameters for boundaries and ranges</li>
 * <li><strong>Date/Time validation parameters</strong> - Temporal constraint parameters for date and time validation</li>
 * <li><strong>Collection validation parameters</strong> - Container-specific parameters for size and element validation</li>
 * <li><strong>Object validation parameters</strong> - Type and identity validation parameters</li>
 * <li><strong>Time validation parameters</strong> - Time-specific parameters for time-of-day constraints</li>
 * <li><strong>Map validation parameters</strong> - Map-specific parameters for key-value validation</li>
 * <li><strong>Enhanced metadata parameters</strong> - Advanced validation context and control parameters</li>
 * <li><strong>Custom business parameters</strong> - Domain-specific parameters for business rule validation</li>
 * </ul>
 *
 * <p><strong>Usage Pattern:</strong> These parameters are typically used in message templates
 * with placeholder syntax (e.g., "Field '{field}' must be at least {minLength} characters")
 * and are replaced with actual validation context values during error message generation.</p>
 *
 * <p><strong>Extensibility:</strong> While this enum provides comprehensive coverage of
 * standard validation scenarios, custom validation rules can add additional parameters
 * to message templates as needed for specific business requirements.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationMessageProvider
 * @see ValidationMetadata
 */
public enum MessageParameter {
    // Common parameters
    FIELD("field"),                 // The field name/identifier being validated
    VALUE("value"),                 // The actual value being validated

    // String validation parameters
    MAX_LENGTH("maxLength"),        // Maximum allowed length
    MIN_LENGTH("minLength"),        // Minimum required length
    EXACT_LENGTH("exactLength"),    // Exact required length
    PATTERN("pattern"),             // Regex pattern
    PREFIX("prefix"),               // Required string prefix
    SUFFIX("suffix"),               // Required string suffix
    SUBSTRING("substring"),         // Required substring
    ALLOWED_VALUES("allowedValues"), // List of allowed values

    // Allowed/disallowed values parameters
    DISALLOWED_VALUES("disallowedValues"), // List of disallowed values
    RANGE("range"),

    // Number validation parameters
    MIN("min"),                     // Minimum allowed value
    MAX("max"),                     // Maximum allowed value

    // Date/Time validation parameters
    MIN_DATE("minDate"),            // Minimum allowed date
    MAX_DATE("maxDate"),            // Maximum allowed date
    REFERENCE_DATE("referenceDate"), // Reference date for comparisons
    MONTH("month"),                 // Required month
    YEAR("year"),                   // Required year
    WEEKDAYS("weekdays"),                   // Required year
    WEEKEND_DAYS("weekendDays"),                   // Required year

    // Collection validation parameters
    MIN_SIZE("minSize"),            // Minimum collection size
    MAX_SIZE("maxSize"),            // Maximum collection size
    EXACT_SIZE("exactSize"),        // Exact collection size
    ACTUAL_SIZE("actualSize"),      // Actual collection size
    ELEMENT("element"),             // Element for contains checks
    ELEMENTS("elements"),           // Elements for collection operations
    CONDITION("condition"),         // Condition description for predicate checks

    // Object validation parameters
    CLASS_NAME("className"),        // Class name for instance checks
    REFERENCE("reference"),         // Reference object for identity checks

    // Time validation parameters
    MIN_TIME("minTime"),            // Minimum allowed time
    MAX_TIME("maxTime"),            // Maximum allowed time
    REFERENCE_TIME("referenceTime"), // Reference time for comparisons
    MIN_HOUR("minHour"),            // Minimum hour value
    MAX_HOUR("maxHour"),            // Maximum hour value
    MIN_MINUTE("minMinute"),        // Minimum minute value
    MAX_MINUTE("maxMinute"),        // Maximum minute value
    MIN_SECOND("minSecond"),        // Minimum second value
    MAX_SECOND("maxSecond"),        // Maximum second value
    TIME_ZONE("timeZone"),          // Required time zone
    TIME_PERIOD("timePeriod"),      // Time period (morning, afternoon, etc.)
    TIME_RANGE("timeRange"),        // Time range (e.g., "08:00-16:00")
    FORMATTED_MIN_TIME("minTimeFormatted"), // Formatted minimum time
    FORMATTED_MAX_TIME("maxTimeFormatted"), // Formatted maximum time
    FORMATTED_REFERENCE_TIME("referenceTimeFormatted"), // Formatted reference time

    // Map Validations parameters
    KEY("key"),
    KEYS("keys"),

    // Enhanced validation metadata parameters
    SEVERITY("severity"),           // Validation severity (ERROR, WARNING, INFO)
    CATEGORY("category"),           // Domain/business category
    VALIDATION_GROUP("validationGroup"), // Group for related validations
    BLOCKING("blocking"),           // Whether validation failure blocks further processing
    PRIORITY("priority"),           // Priority (1-highest, lower numbers = higher priority)
    VALIDATION_TIME("validationTime"), // When validation was performed
    SOURCE("source"),               // System component/class performing validation
    CUSTOM_ERROR_CODE("customErrorCode"), // Custom error code for the validation
    VALIDATION_CONTEXT("validationContext"), // Contextual information about the validation
    RETRY_COUNT("retryCount"),      // Number of validation retry attempts
    CORRECTIVE_ACTION("correctiveAction"), // Suggested action to correct the issue
    VALIDATION_LEVEL("validationLevel"), // Hierarchical level of validation (FIELD, OBJECT, GROUP)
    DEPENDENT_FIELDS("dependentFields"), // List of fields this validation depends on
    PARENT_VALIDATION("parentValidation"), // Parent validation reference for hierarchical validations
    VALIDATION_PHASE("validationPhase"), // Execution phase of validation (PRE_VALIDATION, MAIN, POST_VALIDATION)
    TRANSACTION_ID("transactionId"), // Transaction ID for correlation
    USER_ID("userId"),              // User ID associated with the validation
    SESSION_ID("sessionId"),        // Session ID for the validation context
    IS_CONDITIONAL("isConditional"), // Whether this is a conditional validation
    CONDITIONAL_EXPRESSION("conditionalExpression"), // Expression for conditional validation

    // Custom business validation parameters
    ENTITY_TYPE("entityType"),      // Type of entity being validated
    ERROR_DETAILS("errorDetails");  // Additional details about the error

    private final String key;

    MessageParameter(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}