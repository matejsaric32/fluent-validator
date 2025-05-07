package com.fluentval.validator.message;

import com.fluentval.validator.ValidationIdentifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultMessageProvider implements ValidationMessageProvider {
    private final Map<String, String> messageTemplates = new HashMap<>();
    private final Map<String, List<TemplatePart>> compiledTemplates = new HashMap<>();

    private static class TemplatePart {
        enum Type { TEXT, PLACEHOLDER }

        final Type type;
        final String value;

        TemplatePart(Type type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    public DefaultMessageProvider() {
        initializeDefaultMessages();
    }

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

    @Override
    public boolean supports(String code) {
        return messageTemplates.containsKey(code);
    }

    public void setMessageTemplate(String code, String template) {
        messageTemplates.put(code, template);
        compiledTemplates.remove(template);
    }
}