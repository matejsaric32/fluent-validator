package com.fluentval.validator.message;

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
    
    // Number validation parameters
    MIN("min"),                     // Minimum allowed value
    MAX("max"),                     // Maximum allowed value
    
    // Date/Time validation parameters
    MIN_DATE("minDate"),            // Minimum allowed date
    MAX_DATE("maxDate"),            // Maximum allowed date
    REFERENCE_DATE("referenceDate"), // Reference date for comparisons
    MONTH("month"),                 // Required month
    YEAR("year"),                   // Required year
    
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
    TIME_ZONE("timeZone"),          // Required time zone
    MIN_HOUR("minHour"),            // Minimum hour value
    MAX_HOUR("maxHour"),            // Maximum hour value
    MIN_MINUTE("minMinute"),        // Minimum minute value
    MAX_MINUTE("maxMinute"),        // Maximum minute value
    MIN_SECOND("minSecond"),        // Minimum second value
    MAX_SECOND("maxSecond"),        // Maximum second value
    
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