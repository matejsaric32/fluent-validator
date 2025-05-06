package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.TimeValidationMetadata;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.function.Function;

public final class TimeValidationRules {

    // Time period constants
    private static final LocalTime MORNING_START = LocalTime.of(0, 0);
    private static final LocalTime MORNING_END = LocalTime.of(11, 59, 59);
    private static final LocalTime AFTERNOON_START = LocalTime.of(12, 0);
    private static final LocalTime AFTERNOON_END = LocalTime.of(17, 59, 59);
    private static final LocalTime EVENING_START = LocalTime.of(18, 0);
    private static final LocalTime EVENING_END = LocalTime.of(23, 59, 59);

    // Business hours constants
    private static final LocalTime BUSINESS_HOURS_START = LocalTime.of(8, 0);
    private static final LocalTime BUSINESS_HOURS_END = LocalTime.of(16, 0);
    private static final LocalTime LUNCH_BREAK_START = LocalTime.of(12, 0);
    private static final LocalTime LUNCH_BREAK_END = LocalTime.of(13, 0);

    private TimeValidationRules() {
        // Utility class
    }

    private static <T extends Temporal & Comparable<? super T>> Function<T, String> getFormatter(T value) {
        if (value instanceof LocalTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
            return (T v) -> ((LocalTime) v).format(formatter);
        } else if (value instanceof OffsetTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_TIME;
            return (T v) -> ((OffsetTime) v).format(formatter);
        } else if (value instanceof ZonedDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
            return (T v) -> ((ZonedDateTime) v).format(formatter);
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    private static LocalTime getLocalTime(Temporal value) {
        if (value instanceof LocalTime localTime) {
            return localTime;
        } else if (value instanceof OffsetTime offsetTime) {
            return offsetTime.toLocalTime();
        } else if (value instanceof ZonedDateTime zonedDateTime) {
            return zonedDateTime.toLocalTime();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    private static ZoneId getZoneId(Temporal value) {
        if (value instanceof ZonedDateTime zonedDateTime) {
            return zonedDateTime.getZone();
        } else if (value instanceof OffsetTime offsetTime) {
            return ZoneId.ofOffset("", offsetTime.getOffset());
        }

        return null;
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inRange(T min, T max) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            LocalTime ltValue = getLocalTime(value);
            LocalTime ltMin = getLocalTime(min);
            LocalTime ltMax = getLocalTime(max);

            if (ltValue.isBefore(ltMin) || ltValue.isAfter(ltMax)) {
                Function<T, String> formatter = getFormatter(value);
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.InRange<>(
                            identifier, min, max, formatter.apply(min), formatter.apply(max)
                        )
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> before(T time) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            if (!ltValue.isBefore(ltTime)) {
                Function<T, String> formatter = getFormatter(value);
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.Before<>(
                            identifier, time, formatter.apply(time)
                        )
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> after(T time) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            if (!ltValue.isAfter(ltTime)) {
                Function<T, String> formatter = getFormatter(value);
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.After<>(
                            identifier, time, formatter.apply(time)
                        )
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> beforeOrEquals(T time) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            if (ltValue.isAfter(ltTime)) {
                Function<T, String> formatter = getFormatter(value);
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.BeforeOrEquals<>(
                            identifier, time, formatter.apply(time)
                        )
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> afterOrEquals(T time) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            if (ltValue.isBefore(ltTime)) {
                Function<T, String> formatter = getFormatter(value);
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.AfterOrEquals<>(
                            identifier, time, formatter.apply(time)
                        )
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> equals(T time) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            if (!ltValue.equals(ltTime)) {
                Function<T, String> formatter = getFormatter(value);
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.Equals<>(
                            identifier, time, formatter.apply(time)
                        )
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isMorning() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            LocalTime ltValue = getLocalTime(value);

            if (ltValue.isBefore(MORNING_START) || ltValue.isAfter(MORNING_END)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.IsMorning(identifier)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isAfternoon() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            LocalTime ltValue = getLocalTime(value);

            if (ltValue.isBefore(AFTERNOON_START) || ltValue.isAfter(AFTERNOON_END)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.IsAfternoon(identifier)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isEvening() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            LocalTime ltValue = getLocalTime(value);

            if (ltValue.isBefore(EVENING_START) || ltValue.isAfter(EVENING_END)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.IsEvening(identifier)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isBusinessHours() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            LocalTime ltValue = getLocalTime(value);

            if (ltValue.isBefore(BUSINESS_HOURS_START) || ltValue.isAfter(BUSINESS_HOURS_END)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.IsBusinessHours(identifier)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isLunchHour() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            LocalTime ltValue = getLocalTime(value);

            if (ltValue.isBefore(LUNCH_BREAK_START) || ltValue.isAfter(LUNCH_BREAK_END)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.IsLunchHour(identifier)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> hoursBetween(int minHour, int maxHour) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            int hour = value.get(ChronoField.HOUR_OF_DAY);

            if (hour < minHour || hour > maxHour) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.HoursBetween(identifier, minHour, maxHour)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> minutesBetween(int minMinute, int maxMinute) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            int minute = value.get(ChronoField.MINUTE_OF_HOUR);

            if (minute < minMinute || minute > maxMinute) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.MinutesBetween(identifier, minMinute, maxMinute)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> secondsBetween(int minSecond, int maxSecond) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            int second = value.get(ChronoField.SECOND_OF_MINUTE);

            if (second < minSecond || second > maxSecond) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.SecondsBetween(identifier, minSecond, maxSecond)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inTimeZone(ZoneId zoneId) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            ZoneId valueZone = getZoneId(value);

            if (valueZone == null || !valueZone.equals(zoneId)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new TimeValidationMetadata.InTimeZone(identifier, zoneId)
                    )
                );
            }
        };
    }
}