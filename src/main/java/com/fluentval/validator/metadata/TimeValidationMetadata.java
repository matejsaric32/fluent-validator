package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.Getter;

import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Objects;

public abstract class TimeValidationMetadata extends ValidationMetadata {

    protected TimeValidationMetadata(ValidationIdentifier identifier,
                                     DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    @Getter
    public static final class InRange<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T min;
        private final T max;

        private InRange(ValidationIdentifier identifier, T min, T max) {
            super(identifier, DefaultValidationCode.TIME_IN_RANGE);
            this.min = min;
            this.max = max;

            // Store raw time values
            addMessageParameter(MessageParameter.MIN_TIME, String.valueOf(min));
            addMessageParameter(MessageParameter.MAX_TIME, String.valueOf(max));
        }
    }

    @Getter
    public static final class Before<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        private Before(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_BEFORE);
            this.reference = reference;

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }
    }

    @Getter
    public static final class After<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        private After(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_AFTER);
            this.reference = reference;

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }
    }

    @Getter
    public static final class BeforeOrEquals<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        private BeforeOrEquals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_BEFORE_OR_EQUALS);
            this.reference = reference;

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }
    }

    @Getter
    public static final class AfterOrEquals<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        private AfterOrEquals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_AFTER_OR_EQUALS);
            this.reference = reference;

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }
    }

    @Getter
    public static final class Equals<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        private Equals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_EQUALS);
            this.reference = reference;

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }
    }

    @Getter
    public static final class IsMorning extends TimeValidationMetadata {
        private IsMorning(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_MORNING);

            // Add time period details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "morning");
            addMessageParameter(MessageParameter.TIME_RANGE, "00:00-11:59");
        }
    }

    @Getter
    public static final class IsAfternoon extends TimeValidationMetadata {
        private IsAfternoon(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_AFTERNOON);

            // Add time period details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "afternoon");
            addMessageParameter(MessageParameter.TIME_RANGE, "12:00-17:59");
        }
    }

    @Getter
    public static final class IsEvening extends TimeValidationMetadata {
        private IsEvening(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_EVENING);

            // Add time period details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "evening");
            addMessageParameter(MessageParameter.TIME_RANGE, "18:00-23:59");
        }
    }

    @Getter
    public static final class IsBusinessHours extends TimeValidationMetadata {
        private IsBusinessHours(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_BUSINESS_HOURS);

            // Add business hours details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "business hours");
            addMessageParameter(MessageParameter.TIME_RANGE, "08:00-16:00");
        }
    }

    @Getter
    public static final class IsLunchHour extends TimeValidationMetadata {
        private IsLunchHour(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_LUNCH_HOUR);

            // Add lunch hour details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "lunch hour");
            addMessageParameter(MessageParameter.TIME_RANGE, "12:00-13:00");
        }
    }

    @Getter
    public static final class HoursBetween extends TimeValidationMetadata {
        private final int minHour;
        private final int maxHour;

        private HoursBetween(ValidationIdentifier identifier, int minHour, int maxHour) {
            super(identifier, DefaultValidationCode.HOURS_BETWEEN);
            this.minHour = minHour;
            this.maxHour = maxHour;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_HOUR, String.valueOf(minHour));
            addMessageParameter(MessageParameter.MAX_HOUR, String.valueOf(maxHour));
        }
    }

    @Getter
    public static final class MinutesBetween extends TimeValidationMetadata {
        private final int minMinute;
        private final int maxMinute;

        private MinutesBetween(ValidationIdentifier identifier, int minMinute, int maxMinute) {
            super(identifier, DefaultValidationCode.MINUTES_BETWEEN);
            this.minMinute = minMinute;
            this.maxMinute = maxMinute;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_MINUTE, String.valueOf(minMinute));
            addMessageParameter(MessageParameter.MAX_MINUTE, String.valueOf(maxMinute));
        }
    }

    @Getter
    public static final class SecondsBetween extends TimeValidationMetadata {
        private final int minSecond;
        private final int maxSecond;

        private SecondsBetween(ValidationIdentifier identifier, int minSecond, int maxSecond) {
            super(identifier, DefaultValidationCode.SECONDS_BETWEEN);
            this.minSecond = minSecond;
            this.maxSecond = maxSecond;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_SECOND, String.valueOf(minSecond));
            addMessageParameter(MessageParameter.MAX_SECOND, String.valueOf(maxSecond));
        }
    }

    @Getter
    public static final class InTimeZone extends TimeValidationMetadata {
        private final ZoneId zoneId;

        private InTimeZone(ValidationIdentifier identifier, ZoneId zoneId) {
            super(identifier, DefaultValidationCode.IN_TIME_ZONE);
            this.zoneId = zoneId;

            // Add message parameters
            addMessageParameter(MessageParameter.TIME_ZONE, zoneId.toString());
        }
    }

    // Factory methods
    public static <T extends Temporal & Comparable<? super T>> InRange<T> inRange(ValidationIdentifier identifier, T min, T max) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(min, "Min value must not be null");
        Objects.requireNonNull(max, "Max value must not be null");

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Min time must be before or equal to max time");
        }

        return new InRange<>(identifier, min, max);
    }

    public static <T extends Temporal & Comparable<? super T>> Before<T> before(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(reference, "Reference time must not be null");

        return new Before<>(identifier, reference);
    }

    public static <T extends Temporal & Comparable<? super T>> After<T> after(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(reference, "Reference time must not be null");

        return new After<>(identifier, reference);
    }

    public static <T extends Temporal & Comparable<? super T>> BeforeOrEquals<T> beforeOrEquals(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(reference, "Reference time must not be null");

        return new BeforeOrEquals<>(identifier, reference);
    }

    public static <T extends Temporal & Comparable<? super T>> AfterOrEquals<T> afterOrEquals(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(reference, "Reference time must not be null");

        return new AfterOrEquals<>(identifier, reference);
    }

    public static <T extends Temporal & Comparable<? super T>> Equals<T> equals(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(reference, "Reference time must not be null");

        return new Equals<>(identifier, reference);
    }

    public static IsMorning isMorning(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new IsMorning(identifier);
    }

    public static IsAfternoon isAfternoon(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new IsAfternoon(identifier);
    }

    public static IsEvening isEvening(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new IsEvening(identifier);
    }

    public static IsBusinessHours isBusinessHours(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new IsBusinessHours(identifier);
    }

    public static IsLunchHour isLunchHour(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new IsLunchHour(identifier);
    }

    public static HoursBetween hoursBetween(ValidationIdentifier identifier, int minHour, int maxHour) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        if (minHour < 0 || minHour > 23) {
            throw new IllegalArgumentException("Min hour must be between 0 - 23");
        }
        if (maxHour < 0 || maxHour > 23) {
            throw new IllegalArgumentException("Max hour must be between 0 - 23");
        }
        if (minHour > maxHour) {
            throw new IllegalArgumentException("Min hour must be less than or equal to max hour");
        }

        return new HoursBetween(identifier, minHour, maxHour);
    }

    public static MinutesBetween minutesBetween(ValidationIdentifier identifier, int minMinute, int maxMinute) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        if (minMinute < 0 || minMinute > 59) {
            throw new IllegalArgumentException("Min minute must be between 0 - 59");
        }
        if (maxMinute < 0 || maxMinute > 59) {
            throw new IllegalArgumentException("Max minute must be between 0 - 59");
        }
        if (minMinute > maxMinute) {
            throw new IllegalArgumentException("Min minute must be less than or equal to max minute");
        }

        return new MinutesBetween(identifier, minMinute, maxMinute);
    }

    public static SecondsBetween secondsBetween(ValidationIdentifier identifier, int minSecond, int maxSecond) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        if (minSecond < 0 || minSecond > 59) {
            throw new IllegalArgumentException("Min second must be between 0 - 59");
        }
        if (maxSecond < 0 || maxSecond > 59) {
            throw new IllegalArgumentException("Max second must be between 0 - 59");
        }
        if (minSecond > maxSecond) {
            throw new IllegalArgumentException("Min second must be less than or equal to max second");
        }

        return new SecondsBetween(identifier, minSecond, maxSecond);
    }

    public static InTimeZone inTimeZone(ValidationIdentifier identifier, ZoneId zoneId) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(zoneId, "ZoneId must not be null");

        return new InTimeZone(identifier, zoneId);
    }
}