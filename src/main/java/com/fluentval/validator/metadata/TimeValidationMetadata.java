package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import lombok.Getter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Objects;

public abstract class TimeValidationMetadata extends ValidationMetadata {

    // Error code constants
    public static final String IN_RANGE_CODE = "VGT01";
    public static final String BEFORE_CODE = "VGT02";
    public static final String AFTER_CODE = "VGT03";
    public static final String BEFORE_OR_EQUALS_CODE = "VGT04";
    public static final String AFTER_OR_EQUALS_CODE = "VGT05";
    public static final String EQUALS_CODE = "VGT06";
    public static final String IS_MORNING_CODE = "VGT07";
    public static final String IS_AFTERNOON_CODE = "VGT08";
    public static final String IS_EVENING_CODE = "VGT09";
    public static final String IS_BUSINESS_HOURS_CODE = "VGT10";
    public static final String IS_LUNCH_HOUR_CODE = "VGT11";
    public static final String HOURS_BETWEEN_CODE = "VGT12";
    public static final String MINUTES_BETWEEN_CODE = "VGT13";
    public static final String SECONDS_BETWEEN_CODE = "VGT14";
    public static final String IN_TIME_ZONE_CODE = "VGT15";

    // Message templates
    private static final String IN_RANGE_MESSAGE = "Field '%s' is not a valid time, must be between %s and %s.";
    private static final String BEFORE_MESSAGE = "Field '%s' is not a valid time, must be before %s.";
    private static final String AFTER_MESSAGE = "Field '%s' is not a valid time, must be after %s.";
    private static final String BEFORE_OR_EQUALS_MESSAGE = "Field '%s' is not a valid time, must be before or equal to %s.";
    private static final String AFTER_OR_EQUALS_MESSAGE = "Field '%s' is not a valid time, must be after or equal to %s.";
    private static final String EQUALS_MESSAGE = "Field '%s' is not a valid time, must be equal to %s.";
    private static final String IS_MORNING_MESSAGE = "Field '%s' is not a valid time, must be in the morning (00:00-11:59).";
    private static final String IS_AFTERNOON_MESSAGE = "Field '%s' is not a valid time, must be in the afternoon (12:00-17:59).";
    private static final String IS_EVENING_MESSAGE = "Field '%s' is not a valid time, must be in the evening (18:00-23:59).";
    private static final String IS_BUSINESS_HOURS_MESSAGE = "Field '%s' is not a valid time, must be within business hours (08:00-16:00).";
    private static final String IS_LUNCH_HOUR_MESSAGE = "Field '%s' is not a valid time, must be during lunch break (12:00-13:00).";
    private static final String HOURS_BETWEEN_MESSAGE = "Field '%s' is not a valid time, hour must be between %s and %s.";
    private static final String MINUTES_BETWEEN_MESSAGE = "Field '%s' is not a valid time, minutes must be between %s and %s.";
    private static final String SECONDS_BETWEEN_MESSAGE = "Field '%s' is not a valid time, seconds must be between %s and %s.";
    private static final String IN_TIME_ZONE_MESSAGE = "Field '%s' is not a valid time, must be in time zone '%s'.";

    protected TimeValidationMetadata(ValidationIdentifier identifier,
                                     String errorCode,
                                     String message) {
        super(identifier, errorCode, message);
    }

    @Getter
    public static final class InRange<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T min;
        private final T max;
        private final String minFormatted;
        private final String maxFormatted;

        public InRange(ValidationIdentifier identifier, T min, T max, String minFormatted, String maxFormatted) {
            super(identifier, IN_RANGE_CODE,
                    formatMessage(IN_RANGE_MESSAGE, identifier.value(), minFormatted, maxFormatted));

            this.min = Objects.requireNonNull(min, "Min value can't be null");
            this.max = Objects.requireNonNull(max, "Max value can't be null");
            this.minFormatted = minFormatted;
            this.maxFormatted = maxFormatted;
        }

    }

    @Getter
    public static final class Before<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;
        private final String referenceFormatted;

        public Before(ValidationIdentifier identifier, T reference, String referenceFormatted) {
            super(identifier, BEFORE_CODE,
                    formatMessage(BEFORE_MESSAGE, identifier.value(), referenceFormatted));

            this.reference = Objects.requireNonNull(reference, "Reference formated can't be null");
            this.referenceFormatted = referenceFormatted;
        }

    }

    @Getter
    public static final class After<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;
        private final String referenceFormatted;

        public After(ValidationIdentifier identifier, T reference, String referenceFormatted) {
            super(identifier, AFTER_CODE,
                    formatMessage(AFTER_MESSAGE, identifier.value(), referenceFormatted));

            this.reference = Objects.requireNonNull(reference, "Reference formated can't be null");
            this.referenceFormatted = referenceFormatted;
        }

    }

    @Getter
    public static final class BeforeOrEquals<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;
        private final String referenceFormatted;

        public BeforeOrEquals(ValidationIdentifier identifier, T reference, String referenceFormatted) {
            super(identifier, BEFORE_OR_EQUALS_CODE,
                    formatMessage(BEFORE_OR_EQUALS_MESSAGE, identifier.value(), referenceFormatted));

            this.reference = Objects.requireNonNull(reference, "Reference formated can't be null");
            this.referenceFormatted = referenceFormatted;
        }

    }

    @Getter
    public static final class AfterOrEquals<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;
        private final String referenceFormatted;

        public AfterOrEquals(ValidationIdentifier identifier, T reference, String referenceFormatted) {
            super(identifier, AFTER_OR_EQUALS_CODE,
                    formatMessage(AFTER_OR_EQUALS_MESSAGE, identifier.value(), referenceFormatted));

            this.reference = Objects.requireNonNull(reference, "Reference formated can't be null");
            this.referenceFormatted = referenceFormatted;
        }

    }

    @Getter
    public static final class Equals<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;
        private final String referenceFormatted;

        public Equals(ValidationIdentifier identifier, T reference, String referenceFormatted) {
            super(identifier, EQUALS_CODE,
                    formatMessage(EQUALS_MESSAGE, identifier.value(), referenceFormatted));

            this.reference = Objects.requireNonNull(reference, "Reference formated can't be null");
            this.referenceFormatted = referenceFormatted;
        }

    }

    @Getter
    public static final class IsMorning extends TimeValidationMetadata {
        private static final LocalTime MORNING_START = LocalTime.of(0, 0);
        private static final LocalTime MORNING_END = LocalTime.of(11, 59, 59);

        public IsMorning(ValidationIdentifier identifier) {
            super(identifier, IS_MORNING_CODE,
                    formatMessage(IS_MORNING_MESSAGE, identifier.value()));
        }

    }

    @Getter
    public static final class IsAfternoon extends TimeValidationMetadata {
        private static final LocalTime AFTERNOON_START = LocalTime.of(12, 0);
        private static final LocalTime AFTERNOON_END = LocalTime.of(17, 59, 59);

        public IsAfternoon(ValidationIdentifier identifier) {
            super(identifier, IS_AFTERNOON_CODE,
                    formatMessage(IS_AFTERNOON_MESSAGE, identifier.value()));
        }

    }

    @Getter
    public static final class IsEvening extends TimeValidationMetadata {
        private static final LocalTime EVENING_START = LocalTime.of(18, 0);
        private static final LocalTime EVENING_END = LocalTime.of(23, 59, 59);

        public IsEvening(ValidationIdentifier identifier) {
            super(identifier, IS_EVENING_CODE,
                    formatMessage(IS_EVENING_MESSAGE, identifier.value()));
        }

    }

    @Getter
    public static final class IsBusinessHours extends TimeValidationMetadata {
        private static final LocalTime BUSINESS_HOURS_START = LocalTime.of(8, 0);
        private static final LocalTime BUSINESS_HOURS_END = LocalTime.of(16, 0);

        public IsBusinessHours(ValidationIdentifier identifier) {
            super(identifier, IS_BUSINESS_HOURS_CODE,
                    formatMessage(IS_BUSINESS_HOURS_MESSAGE, identifier.value()));
        }

    }

    @Getter
    public static final class IsLunchHour extends TimeValidationMetadata {
        private static final LocalTime LUNCH_BREAK_START = LocalTime.of(12, 0);
        private static final LocalTime LUNCH_BREAK_END = LocalTime.of(13, 0);

        public IsLunchHour(ValidationIdentifier identifier) {
            super(identifier, IS_LUNCH_HOUR_CODE,
                    formatMessage(IS_LUNCH_HOUR_MESSAGE, identifier.value()));
        }

    }

    @Getter
    public static final class HoursBetween extends TimeValidationMetadata {
        private final int minHour;
        private final int maxHour;

        public HoursBetween(ValidationIdentifier identifier, int minHour, int maxHour) {
            super(identifier, HOURS_BETWEEN_CODE,
                    formatMessage(HOURS_BETWEEN_MESSAGE, identifier.value(),
                            String.valueOf(minHour), String.valueOf(maxHour)));

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
        }

    }

    @Getter
    public static final class MinutesBetween extends TimeValidationMetadata {
        private final int minMinute;
        private final int maxMinute;

        public MinutesBetween(ValidationIdentifier identifier, int minMinute, int maxMinute) {
            super(identifier, MINUTES_BETWEEN_CODE,
                    formatMessage(MINUTES_BETWEEN_MESSAGE, identifier.value(),
                            String.valueOf(minMinute), String.valueOf(maxMinute)));

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
        }

    }

    @Getter
    public static final class SecondsBetween extends TimeValidationMetadata {
        private final int minSecond;
        private final int maxSecond;

        public SecondsBetween(ValidationIdentifier identifier, int minSecond, int maxSecond) {
            super(identifier, SECONDS_BETWEEN_CODE,
                    formatMessage(SECONDS_BETWEEN_MESSAGE, identifier.value(),
                            String.valueOf(minSecond), String.valueOf(maxSecond)));

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
        }

    }

    @Getter
    public static final class InTimeZone extends TimeValidationMetadata {
        private final ZoneId zoneId;

        public InTimeZone(ValidationIdentifier identifier, ZoneId zoneId) {
            super(identifier, IN_TIME_ZONE_CODE,
                    formatMessage(IN_TIME_ZONE_MESSAGE, identifier.value(), zoneId.toString()));

            this.zoneId = Objects.requireNonNull(zoneId, "ZoneId can't be null");
        }

    }
}