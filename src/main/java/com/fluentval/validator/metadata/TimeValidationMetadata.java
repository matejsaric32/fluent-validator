package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.Getter;

import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class TimeValidationMetadata extends ValidationMetadata {

    protected TimeValidationMetadata(ValidationIdentifier identifier,
                                     DefaultValidationCode code,
                                     Map<String, String> messageParameters) {
        super(identifier, code.getCode(), messageParameters);

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    @Getter
    public static final class InRange<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T min;
        private final T max;

        public InRange(ValidationIdentifier identifier, T min, T max) {
            super(identifier, DefaultValidationCode.TIME_IN_RANGE, new HashMap<>());
            this.min = Objects.requireNonNull(min, "Min value can't be null");
            this.max = Objects.requireNonNull(max, "Max value can't be null");

            // Store raw time values
            addMessageParameter(MessageParameter.MIN_TIME, String.valueOf(min));
            addMessageParameter(MessageParameter.MAX_TIME, String.valueOf(max));
        }

    }

    @Getter
    public static final class Before<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        public Before(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_BEFORE, new HashMap<>());
            this.reference = Objects.requireNonNull(reference, "Reference can't be null");

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }

    }

    @Getter
    public static final class After<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        public After(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_AFTER, new HashMap<>());
            this.reference = Objects.requireNonNull(reference, "Reference can't be null");

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }

    }

    @Getter
    public static final class BeforeOrEquals<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        public BeforeOrEquals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_BEFORE_OR_EQUALS, new HashMap<>());
            this.reference = Objects.requireNonNull(reference, "Reference can't be null");

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }

    }

    @Getter
    public static final class AfterOrEquals<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        public AfterOrEquals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_AFTER_OR_EQUALS, new HashMap<>());
            this.reference = Objects.requireNonNull(reference, "Reference can't be null");

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }

    }

    @Getter
    public static final class Equals<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        public Equals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_EQUALS, new HashMap<>());
            this.reference = Objects.requireNonNull(reference, "Reference can't be null");

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }

    }

    @Getter
    public static final class IsMorning extends TimeValidationMetadata {

        public IsMorning(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_MORNING, new HashMap<>());

            // Add time period details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "morning");
            addMessageParameter(MessageParameter.TIME_RANGE, "00:00-11:59");
        }
    }

    @Getter
    public static final class IsAfternoon extends TimeValidationMetadata {

        public IsAfternoon(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_AFTERNOON, new HashMap<>());

            // Add time period details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "afternoon");
            addMessageParameter(MessageParameter.TIME_RANGE, "12:00-17:59");
        }
    }

    @Getter
    public static final class IsEvening extends TimeValidationMetadata {

        public IsEvening(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_EVENING, new HashMap<>());

            // Add time period details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "evening");
            addMessageParameter(MessageParameter.TIME_RANGE, "18:00-23:59");
        }
    }

    @Getter
    public static final class IsBusinessHours extends TimeValidationMetadata {

        public IsBusinessHours(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_BUSINESS_HOURS, new HashMap<>());

            // Add business hours details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "business hours");
            addMessageParameter(MessageParameter.TIME_RANGE, "08:00-16:00");
        }
    }

    @Getter
    public static final class IsLunchHour extends TimeValidationMetadata {

        public IsLunchHour(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_LUNCH_HOUR, new HashMap<>());

            // Add lunch hour details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "lunch hour");
            addMessageParameter(MessageParameter.TIME_RANGE, "12:00-13:00");
        }
    }

    @Getter
    public static final class HoursBetween extends TimeValidationMetadata {
        private final int minHour;
        private final int maxHour;

        public HoursBetween(ValidationIdentifier identifier, int minHour, int maxHour) {
            super(identifier, DefaultValidationCode.HOURS_BETWEEN, new HashMap<>());

            if (minHour < 0 || minHour > 23) {
                throw new IllegalArgumentException("Min hour must between 0 - 23");
            }
            if (maxHour < 0 || maxHour > 23) {
                throw new IllegalArgumentException("Max hour must between 0 - 23");
            }
            if (minHour > maxHour) {
                throw new IllegalArgumentException("Min hour must be less or equal max hour");
            }

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

        public MinutesBetween(ValidationIdentifier identifier, int minMinute, int maxMinute) {
            super(identifier, DefaultValidationCode.MINUTES_BETWEEN, new HashMap<>());

            if (minMinute < 0 || minMinute > 59) {
                throw new IllegalArgumentException("Min minutes must between 0 - 59");
            }
            if (maxMinute < 0 || maxMinute > 59) {
                throw new IllegalArgumentException("Max minutes must between 0 - 59");
            }
            if (minMinute > maxMinute) {
                throw new IllegalArgumentException("Min minutes must be less or equal max minute");
            }

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

        public SecondsBetween(ValidationIdentifier identifier, int minSecond, int maxSecond) {
            super(identifier, DefaultValidationCode.SECONDS_BETWEEN, new HashMap<>());

            if (minSecond < 0 || minSecond > 59) {
                throw new IllegalArgumentException("Min seconds must between 0 - 59");
            }
            if (maxSecond < 0 || maxSecond > 59) {
                throw new IllegalArgumentException("Max seconds must between 0 - 59");
            }
            if (minSecond > maxSecond) {
                throw new IllegalArgumentException("Min seconds must be less or equal max minute");
            }

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

        public InTimeZone(ValidationIdentifier identifier, ZoneId zoneId) {
            super(identifier, DefaultValidationCode.IN_TIME_ZONE, new HashMap<>());
            this.zoneId = Objects.requireNonNull(zoneId, "ZoneId can't be null");

            // Add message parameters
            addMessageParameter(MessageParameter.TIME_ZONE, zoneId.toString());
        }
    }

    // Factory methods
    public static <T extends Temporal & Comparable<? super T>> InRange<T> inRange(ValidationIdentifier identifier, T min, T max) {
        return new InRange<>(identifier, min, max);
    }

    public static <T extends Temporal & Comparable<? super T>> Before<T> before(ValidationIdentifier identifier, T reference) {
        return new Before<>(identifier, reference);
    }

    public static <T extends Temporal & Comparable<? super T>> After<T> after(ValidationIdentifier identifier, T reference) {
        return new After<>(identifier, reference);
    }

    public static <T extends Temporal & Comparable<? super T>> BeforeOrEquals<T> beforeOrEquals(ValidationIdentifier identifier, T reference) {
        return new BeforeOrEquals<>(identifier, reference);
    }

    public static <T extends Temporal & Comparable<? super T>> AfterOrEquals<T> afterOrEquals(ValidationIdentifier identifier, T reference) {
        return new AfterOrEquals<>(identifier, reference);
    }

    public static <T extends Temporal & Comparable<? super T>> Equals<T> equals(ValidationIdentifier identifier, T reference) {
        return new Equals<>(identifier, reference);
    }

    public static IsMorning isMorning(ValidationIdentifier identifier) {
        return new IsMorning(identifier);
    }

    public static IsAfternoon isAfternoon(ValidationIdentifier identifier) {
        return new IsAfternoon(identifier);
    }

    public static IsEvening isEvening(ValidationIdentifier identifier) {
        return new IsEvening(identifier);
    }

    public static IsBusinessHours isBusinessHours(ValidationIdentifier identifier) {
        return new IsBusinessHours(identifier);
    }

    public static IsLunchHour isLunchHour(ValidationIdentifier identifier) {
        return new IsLunchHour(identifier);
    }

    public static HoursBetween hoursBetween(ValidationIdentifier identifier, int minHour, int maxHour) {
        return new HoursBetween(identifier, minHour, maxHour);
    }

    public static MinutesBetween minutesBetween(ValidationIdentifier identifier, int minMinute, int maxMinute) {
        return new MinutesBetween(identifier, minMinute, maxMinute);
    }

    public static SecondsBetween secondsBetween(ValidationIdentifier identifier, int minSecond, int maxSecond) {
        return new SecondsBetween(identifier, minSecond, maxSecond);
    }

    public static InTimeZone inTimeZone(ValidationIdentifier identifier, ZoneId zoneId) {
        return new InTimeZone(identifier, zoneId);
    }
}