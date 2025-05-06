package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.temporal.Temporal;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public abstract class DateTimeValidationMetadata extends ValidationMetadata {

    // Error code constants
    public static final String IN_RANGE_CODE = "VGD01";
    public static final String BEFORE_CODE = "VGD02";
    public static final String AFTER_CODE = "VGD03";
    public static final String BEFORE_OR_EQUALS_CODE = "VGD04";
    public static final String AFTER_OR_EQUALS_CODE = "VGD05";
    public static final String FUTURE_CODE = "VGD06";
    public static final String PAST_CODE = "VGD07";
    public static final String PRESENT_OR_FUTURE_CODE = "VGD08";
    public static final String PRESENT_OR_PAST_CODE = "VGD09";
    public static final String EQUALS_CODE = "VGD10";
    public static final String IS_WEEKDAY_CODE = "VGD11";
    public static final String IS_WEEKEND_CODE = "VGD12";
    public static final String IN_MONTH_CODE = "VGD13";
    public static final String IN_YEAR_CODE = "VGD14";

    // Message templates
    private static final String IN_RANGE_MESSAGE = "Field '%s' is not a valid date, must be between %s and %s.";
    private static final String BEFORE_MESSAGE = "Field '%s' is not a valid date, must be before %s.";
    private static final String AFTER_MESSAGE = "Field '%s' is not a valid date, must be after %s.";
    private static final String BEFORE_OR_EQUALS_MESSAGE = "Field '%s' is not a valid date, must be before or equal to %s.";
    private static final String AFTER_OR_EQUALS_MESSAGE = "Field '%s' is not a valid date, must be after or equal to %s.";
    private static final String FUTURE_MESSAGE = "Field '%s' must be a future date.";
    private static final String PAST_MESSAGE = "Field '%s' must be a past date.";
    private static final String PRESENT_OR_FUTURE_MESSAGE = "Field '%s' must be today or in the future.";
    private static final String PRESENT_OR_PAST_MESSAGE = "Field '%s' must be today or in the past.";
    private static final String EQUALS_MESSAGE = "Field '%s' must be equal to %s.";
    private static final String IS_WEEKDAY_MESSAGE = "Field '%s' must be a weekday (Mon–Fri).";
    private static final String IS_WEEKEND_MESSAGE = "Field '%s' must be a weekend day (Sat–Sun).";
    private static final String IN_MONTH_MESSAGE = "Field '%s' must be in the month of %s.";
    private static final String IN_YEAR_MESSAGE = "Field '%s' must be in the year %s.";

    protected DateTimeValidationMetadata(ValidationIdentifier identifier, String errorCode, String message) {
        super(identifier, errorCode, message);
    }

    @Getter
    public static final class InRange<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T min;
        private final T max;
        private final String minFormatted;
        private final String maxFormatted;

        public InRange(ValidationIdentifier identifier, T min, T max, String minFormatted, String maxFormatted) {
            super(identifier, IN_RANGE_CODE, formatMessage(IN_RANGE_MESSAGE, identifier.value(), minFormatted, maxFormatted));
            this.min = Objects.requireNonNull(min);
            this.max = Objects.requireNonNull(max);
            this.minFormatted = minFormatted;
            this.maxFormatted = maxFormatted;
            if (min.compareTo(max) > 0) throw new IllegalArgumentException("Min must be less than or equal to max");
        }

    }

    @Getter
    public static final class Before<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;
        private final String referenceFormatted;

        public Before(ValidationIdentifier identifier, T reference, String referenceFormatted) {
            super(identifier, BEFORE_CODE, formatMessage(BEFORE_MESSAGE, identifier.value(), referenceFormatted));
            this.reference = Objects.requireNonNull(reference);
            this.referenceFormatted = referenceFormatted;
        }

    }

    @Getter
    public static final class After<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;
        private final String referenceFormatted;

        public After(ValidationIdentifier identifier, T reference, String referenceFormatted) {
            super(identifier, AFTER_CODE, formatMessage(AFTER_MESSAGE, identifier.value(), referenceFormatted));
            this.reference = Objects.requireNonNull(reference);
            this.referenceFormatted = referenceFormatted;
        }

    }

    @Getter
    public static final class BeforeOrEquals<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;
        private final String referenceFormatted;

        public BeforeOrEquals(ValidationIdentifier identifier, T reference, String referenceFormatted) {
            super(identifier, BEFORE_OR_EQUALS_CODE, formatMessage(BEFORE_OR_EQUALS_MESSAGE, identifier.value(), referenceFormatted));
            this.reference = Objects.requireNonNull(reference);
            this.referenceFormatted = referenceFormatted;
        }

    }

    @Getter
    public static final class AfterOrEquals<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;
        private final String referenceFormatted;

        public AfterOrEquals(ValidationIdentifier identifier, T reference, String referenceFormatted) {
            super(identifier, AFTER_OR_EQUALS_CODE, formatMessage(AFTER_OR_EQUALS_MESSAGE, identifier.value(), referenceFormatted));
            this.reference = Objects.requireNonNull(reference);
            this.referenceFormatted = referenceFormatted;
        }

    }

    @Getter
    public static final class Future extends DateTimeValidationMetadata {
        public Future(ValidationIdentifier identifier) {
            super(identifier, FUTURE_CODE, formatMessage(FUTURE_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class Past extends DateTimeValidationMetadata {
        public Past(ValidationIdentifier identifier) {
            super(identifier, PAST_CODE, formatMessage(PAST_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class PresentOrFuture extends DateTimeValidationMetadata {
        public PresentOrFuture(ValidationIdentifier identifier) {
            super(identifier, PRESENT_OR_FUTURE_CODE, formatMessage(PRESENT_OR_FUTURE_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class PresentOrPast extends DateTimeValidationMetadata {
        public PresentOrPast(ValidationIdentifier identifier) {
            super(identifier, PRESENT_OR_PAST_CODE, formatMessage(PRESENT_OR_PAST_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class Equals<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;
        private final String referenceFormatted;

        public Equals(ValidationIdentifier identifier, T reference, String referenceFormatted) {
            super(identifier, EQUALS_CODE, formatMessage(EQUALS_MESSAGE, identifier.value(), referenceFormatted));
            this.reference = Objects.requireNonNull(reference);
            this.referenceFormatted = referenceFormatted;
        }

    }

    @Getter
    public static final class IsWeekday extends DateTimeValidationMetadata {
        private static final Set<DayOfWeek> WEEKDAYS = EnumSet.of(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

        public IsWeekday(ValidationIdentifier identifier) {
            super(identifier, IS_WEEKDAY_CODE, formatMessage(IS_WEEKDAY_MESSAGE, identifier.value()));
        }

        public Set<DayOfWeek> getWeekdays() {
            return EnumSet.copyOf(WEEKDAYS);
        }
    }

    @Getter
    public static final class IsWeekend extends DateTimeValidationMetadata {
        private static final Set<DayOfWeek> WEEKEND_DAYS = EnumSet.of(
            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

        public IsWeekend(ValidationIdentifier identifier) {
            super(identifier, IS_WEEKEND_CODE, formatMessage(IS_WEEKEND_MESSAGE, identifier.value()));
        }

        public Set<DayOfWeek> getWeekendDays() {
            return EnumSet.copyOf(WEEKEND_DAYS);
        }
    }

    @Getter
    public static final class InMonth extends DateTimeValidationMetadata {
        private final Month month;

        public InMonth(ValidationIdentifier identifier, Month month) {
            super(identifier, IN_MONTH_CODE, formatMessage(IN_MONTH_MESSAGE, identifier.value(), month.toString()));
            this.month = Objects.requireNonNull(month);
        }

    }

    @Getter
    public static final class InYear extends DateTimeValidationMetadata {
        private final int year;

        public InYear(ValidationIdentifier identifier, int year) {
            super(identifier, IN_YEAR_CODE, formatMessage(IN_YEAR_MESSAGE, identifier.value(), String.valueOf(year)));
            if (year < 0) throw new IllegalArgumentException("Year cannot be negative");
            this.year = year;
        }

        public int getYear() {
            return year;
        }
    }
}
