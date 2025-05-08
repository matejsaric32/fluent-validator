package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.MetadataUtils;
import com.fluentval.validator.metadata.TimeValidationMetadata;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Objects;

import static com.fluentval.validator.rule.ValidationRuleUtils.createSkipNullRule;

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

    // Pure validation functions
    private static class ValidationFunctions {
        static <T extends Temporal & Comparable<? super T>> boolean isInTimeRange(final T value, final T min, final T max) {
            LocalTime ltValue = getLocalTime(value);
            LocalTime ltMin = getLocalTime(min);
            LocalTime ltMax = getLocalTime(max);

            return !ltValue.isBefore(ltMin) && !ltValue.isAfter(ltMax);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isBefore(final T value, final T time) {
            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            return ltValue.isBefore(ltTime);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isAfter(final T value, final T time) {
            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            return ltValue.isAfter(ltTime);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isBeforeOrEquals(final T value, final T time) {
            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            return !ltValue.isAfter(ltTime);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isAfterOrEquals(final T value, final T time) {
            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            return !ltValue.isBefore(ltTime);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isEqual(final T value, final T time) {
            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            return ltValue.equals(ltTime);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isMorning(final T value) {
            LocalTime ltValue = getLocalTime(value);
            return !ltValue.isBefore(MORNING_START) && !ltValue.isAfter(MORNING_END);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isAfternoon(final T value) {
            LocalTime ltValue = getLocalTime(value);
            return !ltValue.isBefore(AFTERNOON_START) && !ltValue.isAfter(AFTERNOON_END);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isEvening(final T value) {
            LocalTime ltValue = getLocalTime(value);
            return !ltValue.isBefore(EVENING_START) && !ltValue.isAfter(EVENING_END);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isBusinessHours(final T value) {
            LocalTime ltValue = getLocalTime(value);
            return !ltValue.isBefore(BUSINESS_HOURS_START) && !ltValue.isAfter(BUSINESS_HOURS_END);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isLunchHour(final T value) {
            LocalTime ltValue = getLocalTime(value);
            return !ltValue.isBefore(LUNCH_BREAK_START) && !ltValue.isAfter(LUNCH_BREAK_END);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isHoursBetween(final T value, final int minHour, final int maxHour) {
            int hour = value.get(ChronoField.HOUR_OF_DAY);
            return hour >= minHour && hour <= maxHour;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isMinutesBetween(final T value, final int minMinute, final int maxMinute) {
            int minute = value.get(ChronoField.MINUTE_OF_HOUR);
            return minute >= minMinute && minute <= maxMinute;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isSecondsBetween(final T value, final int minSecond, final int maxSecond) {
            int second = value.get(ChronoField.SECOND_OF_MINUTE);
            return second >= minSecond && second <= maxSecond;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isInTimeZone(final T value, final ZoneId zoneId) {
            ZoneId valueZone = getZoneId(value);
            return valueZone != null && valueZone.equals(zoneId);
        }

        private static ZoneId getZoneId(final Temporal value) {
            if (value instanceof ZonedDateTime zonedDateTime) {
                return zonedDateTime.getZone();
            } else if (value instanceof OffsetTime offsetTime) {
                return ZoneId.ofOffset("", offsetTime.getOffset());
            }

            return null;
        }
    }

    private static LocalTime getLocalTime(final Temporal value) {
        if (value instanceof LocalTime localTime) {
            return localTime;
        } else if (value instanceof OffsetTime offsetTime) {
            return offsetTime.toLocalTime();
        } else if (value instanceof ZonedDateTime zonedDateTime) {
            return zonedDateTime.toLocalTime();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inRange(final T min, final T max) {
        Objects.requireNonNull(min, "Min time must not be null");
        Objects.requireNonNull(max, "Max time must not be null");

        LocalTime ltMin = getLocalTime(min);
        LocalTime ltMax = getLocalTime(max);

        if (ltMin.isAfter(ltMax)) {
            throw new IllegalArgumentException("Min time must be before or equal to max time");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isInTimeRange(value, min, max),
                identifier -> TimeValidationMetadata.inRange(identifier, min, max)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> before(final T time) {
        Objects.requireNonNull(time, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return createSkipNullRule(
                value -> ValidationFunctions.isBefore(value, time),
                identifier -> TimeValidationMetadata.before(identifier, time)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> after(final T time) {
        Objects.requireNonNull(time, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return createSkipNullRule(
                value -> ValidationFunctions.isAfter(value, time),
                identifier -> TimeValidationMetadata.after(identifier, time)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> beforeOrEquals(final T time) {
        Objects.requireNonNull(time, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return createSkipNullRule(
                value -> ValidationFunctions.isBeforeOrEquals(value, time),
                identifier -> TimeValidationMetadata.beforeOrEquals(identifier, time)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> afterOrEquals(final T time) {
        Objects.requireNonNull(time, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return createSkipNullRule(
                value -> ValidationFunctions.isAfterOrEquals(value, time),
                identifier -> TimeValidationMetadata.afterOrEquals(identifier, time)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isEquals(final T time) {
        Objects.requireNonNull(time, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return createSkipNullRule(
                value -> ValidationFunctions.isEqual(value, time),
                identifier -> TimeValidationMetadata.equals(identifier, time)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isMorning() {
        return createSkipNullRule(
                ValidationFunctions::isMorning,
                TimeValidationMetadata::isMorning
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isAfternoon() {
        return createSkipNullRule(
                ValidationFunctions::isAfternoon,
                TimeValidationMetadata::isAfternoon
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isEvening() {
        return createSkipNullRule(
                ValidationFunctions::isEvening,
                TimeValidationMetadata::isEvening
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isBusinessHours() {
        return createSkipNullRule(
                ValidationFunctions::isBusinessHours,
                TimeValidationMetadata::isBusinessHours
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isLunchHour() {
        return createSkipNullRule(
                ValidationFunctions::isLunchHour,
                TimeValidationMetadata::isLunchHour
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> hoursBetween(final int minHour, final int maxHour) {
        if (minHour < 0 || minHour > 23) {
            throw new IllegalArgumentException("Min hour must be between 0 - 23");
        }
        if (maxHour < 0 || maxHour > 23) {
            throw new IllegalArgumentException("Max hour must be between 0 - 23");
        }
        if (minHour > maxHour) {
            throw new IllegalArgumentException("Min hour must be less than or equal to max hour");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isHoursBetween(value, minHour, maxHour),
                identifier -> TimeValidationMetadata.hoursBetween(identifier, minHour, maxHour)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> minutesBetween(final int minMinute, final int maxMinute) {
        if (minMinute < 0 || minMinute > 59) {
            throw new IllegalArgumentException("Min minute must be between 0 - 59");
        }
        if (maxMinute < 0 || maxMinute > 59) {
            throw new IllegalArgumentException("Max minute must be between 0 - 59");
        }
        if (minMinute > maxMinute) {
            throw new IllegalArgumentException("Min minute must be less than or equal to max minute");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isMinutesBetween(value, minMinute, maxMinute),
                identifier -> TimeValidationMetadata.minutesBetween(identifier, minMinute, maxMinute)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> secondsBetween(final int minSecond, final int maxSecond) {
        if (minSecond < 0 || minSecond > 59) {
            throw new IllegalArgumentException("Min second must be between 0 - 59");
        }
        if (maxSecond < 0 || maxSecond > 59) {
            throw new IllegalArgumentException("Max second must be between 0 - 59");
        }
        if (minSecond > maxSecond) {
            throw new IllegalArgumentException("Min second must be less than or equal to max second");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isSecondsBetween(value, minSecond, maxSecond),
                identifier -> TimeValidationMetadata.secondsBetween(identifier, minSecond, maxSecond)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inTimeZone(final ZoneId zoneId) {
        Objects.requireNonNull(zoneId, "ZoneId must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isInTimeZone(value, zoneId),
                identifier -> TimeValidationMetadata.inTimeZone(identifier, zoneId)
        );
    }
}