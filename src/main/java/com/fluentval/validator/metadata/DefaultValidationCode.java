package com.fluentval.validator.metadata;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of standard validation codes used throughout the FluentVal validation framework.
 * This enum provides a centralized catalog of all built-in validation types, organized by
 * validation domain and purpose, with each code mapping to a specific validation rule and
 * corresponding error message template.
 *
 * <p>DefaultValidationCode serves as the foundation for validation metadata creation and
 * error message generation, providing consistent categorization and identification of
 * validation failures across the entire validation framework.</p>
 *
 * <p><strong>Code Structure:</strong> Each validation code follows a hierarchical naming convention
 * using dot notation (e.g., "domain.validation_type") to provide clear categorization and
 * support for internationalization and message template organization.</p>
 *
 * <p><strong>Usage:</strong> These codes are primarily used internally by validation metadata
 * classes and message providers, but can also be referenced directly when creating custom
 * validation rules or error handling logic.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationMetadata
 * @see com.fluentval.validator.message.ValidationMessageProvider
 */
@AllArgsConstructor
@Getter
public enum DefaultValidationCode {

    // Common validation codes
    NOT_NULL("common.not_null"),
    MUST_BE_NULL("common.must_be_null"),
    IS_EQUAL("common.is_equal"),
    IS_NOT_EQUAL("common.is_not_equal"),
    SATISFIES("common.satisfies"),
    IS_INSTANCE_OF("common.is_instance_of"),
    IS_NOT_INSTANCE_OF("common.is_not_instance_of"),
    IS_SAME_AS("common.is_same_as"),
    IS_NOT_SAME_AS("common.is_not_same_as"),

    // String validation codes
    NOT_BLANK("string.not_blank"),
    MAX_LENGTH("string.max_length"),
    MIN_LENGTH("string.min_length"),
    EXACT_LENGTH("string.exact_length"),
    MATCHES("string.matches"),
    ONE_OF("string.one_of"),
    ONE_OF_IGNORE_CASE("string.one_of_ignore_case"),
    STARTS_WITH("string.starts_with"),
    ENDS_WITH("string.ends_with"),
    CONTAINS("string.contains"),
    NUMERIC("string.numeric"),
    ALPHANUMERIC("string.alphanumeric"),
    UPPERCASE("string.uppercase"),
    LOWERCASE("string.lowercase"),
    NO_WHITESPACE("string.no_whitespace"),
    NO_LEADING_WHITESPACE("string.no_leading_whitespace"),
    NO_TRAILING_WHITESPACE("string.no_trailing_whitespace"),
    NO_CONSECUTIVE_WHITESPACE("string.no_consecutive_whitespace"),
    TRIMMED("string.trimmed"),
    PROPER_SPACING("string.proper_spacing"),

    // Number validation codes
    MIN("number.min"),
    MAX("number.max"),
    RANGE("number.range"),
    POSITIVE("number.positive"),
    NEGATIVE("number.negative"),
    NOT_ZERO("number.not_zero"),

    // Collection validation codes
    NOT_EMPTY("collection.not_empty"),
    IS_EMPTY("collection.is_empty"),
    MIN_SIZE("collection.min_size"),
    MAX_SIZE("collection.max_size"),
    EXACT_SIZE("collection.exact_size"),
    SIZE_RANGE("collection.size_range"),
    ALL_MATCH("collection.all_match"),
    ANY_MATCH("collection.any_match"),
    NONE_MATCH("collection.none_match"),
    NO_DUPLICATES("collection.no_duplicates"),
    COLLECTION_CONTAINS("collection.contains"),
    DOES_NOT_CONTAIN("collection.does_not_contain"),
    CONTAINS_ALL("collection.contains_all"),
    CONTAINS_NONE("collection.contains_none"),

    // Allowed values validation codes
    ALLOWED_VALUES_CONTAINS("allowed.contains"),
    ALLOWED_VALUES_ONE_OF("allowed.one_of"),
    NOT_CONTAINS("allowed.not_contains"),
    NONE_OF("allowed.none_of"),
    IS_IN_ENUM("allowed.is_in_enum"),
    IN_RANGE("allowed.in_range"),

    // DateTime validation codes
    DATE_TIME_IN_RANGE("datetime.in_range"),
    BEFORE("datetime.before"),
    AFTER("datetime.after"),
    BEFORE_OR_EQUALS("datetime.before_or_equals"),
    AFTER_OR_EQUALS("datetime.after_or_equals"),
    FUTURE("datetime.future"),
    PAST("datetime.past"),
    PRESENT_OR_FUTURE("datetime.present_or_future"),
    PRESENT_OR_PAST("datetime.present_or_past"),
    EQUALS_DATE("datetime.equals"),
    IS_WEEKDAY("datetime.is_weekday"),
    IS_WEEKEND("datetime.is_weekend"),
    IN_MONTH("datetime.in_month"),
    IN_YEAR("datetime.in_year"),

    // Time validation codes
    TIME_IN_RANGE("time.in_range"),
    TIME_BEFORE("time.before"),
    TIME_AFTER("time.after"),
    TIME_BEFORE_OR_EQUALS("time.before_or_equals"),
    TIME_AFTER_OR_EQUALS("time.after_or_equals"),
    TIME_EQUALS("time.equals"),
    IS_MORNING("time.is_morning"),
    IS_AFTERNOON("time.is_afternoon"),
    IS_EVENING("time.is_evening"),
    IS_BUSINESS_HOURS("time.is_business_hours"),
    IS_LUNCH_HOUR("time.is_lunch_hour"),
    HOURS_BETWEEN("time.hours_between"),
    MINUTES_BETWEEN("time.minutes_between"),
    SECONDS_BETWEEN("time.seconds_between"),
    IN_TIME_ZONE("time.in_time_zone");

    private final String code;

}
