package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.temporal.Temporal;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public abstract class DateTimeValidationMetadata extends ValidationMetadata {

    protected DateTimeValidationMetadata(ValidationIdentifier identifier,
                                         DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    @Getter
    public static final class InRange<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T min;
        private final T max;

        private InRange(ValidationIdentifier identifier, T min, T max) {
            super(identifier, DefaultValidationCode.DATE_TIME_IN_RANGE);
            this.min = min;
            this.max = max;

            // Store raw date values
            addMessageParameter(MessageParameter.MIN_DATE, String.valueOf(min));
            addMessageParameter(MessageParameter.MAX_DATE, String.valueOf(max));
        }

    }

    @Getter
    public static final class Before<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        private Before(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.BEFORE);
            this.reference = reference;

            // Store raw reference date
            addMessageParameter(MessageParameter.REFERENCE_DATE, String.valueOf(reference));
        }

    }

    @Getter
    public static final class After<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        private After(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.AFTER);
            this.reference = reference;

            // Store raw reference date
            addMessageParameter(MessageParameter.REFERENCE_DATE, String.valueOf(reference));
        }

    }

    @Getter
    public static final class BeforeOrEquals<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        private BeforeOrEquals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.BEFORE_OR_EQUALS);
            this.reference = reference;

            // Store raw reference date
            addMessageParameter(MessageParameter.REFERENCE_DATE, String.valueOf(reference));
        }

    }

    @Getter
    public static final class AfterOrEquals<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        private AfterOrEquals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.AFTER_OR_EQUALS);
            this.reference = reference;

            // Store raw reference date
            addMessageParameter(MessageParameter.REFERENCE_DATE, String.valueOf(reference));
        }

    }

    @Getter
    public static final class Future extends DateTimeValidationMetadata {
        private Future(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.FUTURE);
            // No additional parameters needed for this validation
        }
    }

    @Getter
    public static final class Past extends DateTimeValidationMetadata {
        private Past(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.PAST);
            // No additional parameters needed for this validation
        }
    }

    @Getter
    public static final class PresentOrFuture extends DateTimeValidationMetadata {
        private PresentOrFuture(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.PRESENT_OR_FUTURE);
            // No additional parameters needed for this validation
        }
    }

    @Getter
    public static final class PresentOrPast extends DateTimeValidationMetadata {
        private PresentOrPast(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.PRESENT_OR_PAST);
            // No additional parameters needed for this validation
        }
    }

    @Getter
    public static final class Equals<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        private Equals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.EQUALS_DATE);
            this.reference = reference;

            // Store raw reference date
            addMessageParameter(MessageParameter.REFERENCE_DATE, String.valueOf(reference));
        }

    }

    @Getter
    public static final class IsWeekday extends DateTimeValidationMetadata {
        private static final Set<DayOfWeek> WEEKDAYS = EnumSet.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

        private IsWeekday(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_WEEKDAY);

            // Add weekdays as a parameter for potential use in messages
            addMessageParameter(MessageParameter.WEEKDAYS, "Monday-Friday");
        }

        public Set<DayOfWeek> getWeekdays() {
            return EnumSet.copyOf(WEEKDAYS);
        }
    }

    @Getter
    public static final class IsWeekend extends DateTimeValidationMetadata {
        private static final Set<DayOfWeek> WEEKEND_DAYS = EnumSet.of(
                DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

        private IsWeekend(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_WEEKEND);

            // Add weekend days as a parameter for potential use in messages
            addMessageParameter(MessageParameter.WEEKEND_DAYS, "Saturday-Sunday");
        }

        public Set<DayOfWeek> getWeekendDays() {
            return EnumSet.copyOf(WEEKEND_DAYS);
        }
    }

    @Getter
    public static final class InMonth extends DateTimeValidationMetadata {
        private final Month month;

        private InMonth(ValidationIdentifier identifier, Month month) {
            super(identifier, DefaultValidationCode.IN_MONTH);
            this.month = month;

            // Add message parameters
            addMessageParameter(MessageParameter.MONTH, month.toString());
        }
    }

    @Getter
    public static final class InYear extends DateTimeValidationMetadata {
        private final int year;

        private InYear(ValidationIdentifier identifier, int year) {
            super(identifier, DefaultValidationCode.IN_YEAR);
            this.year = year;

            // Add message parameters
            addMessageParameter(MessageParameter.YEAR, String.valueOf(year));
        }
    }

    // Factory methods
    public static <T extends Temporal & Comparable<? super T>> InRange<T> inRange(ValidationIdentifier identifier, T min, T max) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(min, "Min value must not be null");
        Objects.requireNonNull(max, "Max value must not be null");

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Min must be less than or equal to max");
        }

        return new InRange<>(identifier, min, max);
    }

    public static <T extends Temporal & Comparable<? super T>> Before<T> before(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(reference, "Reference date must not be null");

        return new Before<>(identifier, reference);
    }

    public static <T extends Temporal & Comparable<? super T>> After<T> after(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(reference, "Reference date must not be null");

        return new After<>(identifier, reference);
    }

    public static <T extends Temporal & Comparable<? super T>> BeforeOrEquals<T> beforeOrEquals(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(reference, "Reference date must not be null");

        return new BeforeOrEquals<>(identifier, reference);
    }

    public static <T extends Temporal & Comparable<? super T>> AfterOrEquals<T> afterOrEquals(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(reference, "Reference date must not be null");

        return new AfterOrEquals<>(identifier, reference);
    }

    public static Future future(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new Future(identifier);
    }

    public static Past past(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new Past(identifier);
    }

    public static PresentOrFuture presentOrFuture(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new PresentOrFuture(identifier);
    }

    public static PresentOrPast presentOrPast(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new PresentOrPast(identifier);
    }

    public static <T extends Temporal & Comparable<? super T>> Equals<T> equalsDate(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(reference, "Reference date must not be null");

        return new Equals<>(identifier, reference);
    }

    public static IsWeekday isWeekday(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new IsWeekday(identifier);
    }

    public static IsWeekend isWeekend(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new IsWeekend(identifier);
    }

    public static InMonth inMonth(ValidationIdentifier identifier, Month month) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(month, "Month must not be null");

        return new InMonth(identifier, month);
    }

    public static InYear inYear(ValidationIdentifier identifier, int year) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        if (year < 0) {
            throw new IllegalArgumentException("Year cannot be negative");
        }

        return new InYear(identifier, year);
    }
}