package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.temporal.Temporal;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class DateTimeValidationMetadata extends ValidationMetadata {

    protected DateTimeValidationMetadata(ValidationIdentifier identifier,
                                         DefaultValidationCode code,
                                         Map<String, String> messageParameters) {
        super(identifier, code.getCode(), messageParameters);

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    @Getter
    public static final class InRange<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T min;
        private final T max;

        public InRange(ValidationIdentifier identifier, T min, T max) {
            super(identifier, DefaultValidationCode.DATE_TIME_IN_RANGE, new HashMap<>());
            this.min = Objects.requireNonNull(min);
            this.max = Objects.requireNonNull(max);

            if (min.compareTo(max) > 0) {
                throw new IllegalArgumentException("Min must be less than or equal to max");
            }

            // The formatting will be done by the message provider
            addMessageParameter("minDate", String.valueOf(min));
            addMessageParameter("maxDate", String.valueOf(max));
        }

    }

    @Getter
    public static final class Before<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        public Before(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.BEFORE, new HashMap<>());
            this.reference = Objects.requireNonNull(reference);

            addMessageParameter("referenceDate", String.valueOf(reference));
        }
    }

    @Getter
    public static final class After<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        public After(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.AFTER, new HashMap<>());
            this.reference = Objects.requireNonNull(reference);

            // Store the raw reference date
            addMessageParameter("referenceDate", String.valueOf(reference));
        }

        /**
         * Set the formatted representation of the reference date
         */
        public void setFormattedReference(String referenceFormatted) {
            addMessageParameter("referenceDateFormatted", referenceFormatted);
        }
    }

    @Getter
    public static final class BeforeOrEquals<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        public BeforeOrEquals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.BEFORE_OR_EQUALS, new HashMap<>());
            this.reference = Objects.requireNonNull(reference);

            // Store the raw reference date
            addMessageParameter("referenceDate", String.valueOf(reference));
        }

        /**
         * Set the formatted representation of the reference date
         */
        public void setFormattedReference(String referenceFormatted) {
            addMessageParameter("referenceDateFormatted", referenceFormatted);
        }
    }

    @Getter
    public static final class AfterOrEquals<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        public AfterOrEquals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.AFTER_OR_EQUALS, new HashMap<>());
            this.reference = Objects.requireNonNull(reference);

            // Store the raw reference date
            addMessageParameter("referenceDate", String.valueOf(reference));
        }

        /**
         * Set the formatted representation of the reference date
         */
        public void setFormattedReference(String referenceFormatted) {
            addMessageParameter("referenceDateFormatted", referenceFormatted);
        }
    }

    @Getter
    public static final class Future extends DateTimeValidationMetadata {

        public Future(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.FUTURE, new HashMap<>());
            // No additional parameters needed for this validation
        }
    }

    @Getter
    public static final class Past extends DateTimeValidationMetadata {

        public Past(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.PAST, new HashMap<>());
            // No additional parameters needed for this validation
        }
    }

    @Getter
    public static final class PresentOrFuture extends DateTimeValidationMetadata {

        public PresentOrFuture(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.PRESENT_OR_FUTURE, new HashMap<>());
            // No additional parameters needed for this validation
        }
    }

    @Getter
    public static final class PresentOrPast extends DateTimeValidationMetadata {

        public PresentOrPast(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.PRESENT_OR_PAST, new HashMap<>());
            // No additional parameters needed for this validation
        }
    }

    @Getter
    public static final class Equals<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        public Equals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.EQUALS_DATE, new HashMap<>());
            this.reference = Objects.requireNonNull(reference);

            // Store the raw reference date
            addMessageParameter("referenceDate", String.valueOf(reference));
        }

        /**
         * Set the formatted representation of the reference date
         */
        public void setFormattedReference(String referenceFormatted) {
            addMessageParameter("referenceDateFormatted", referenceFormatted);
        }
    }

    @Getter
    public static final class IsWeekday extends DateTimeValidationMetadata {
        private static final Set<DayOfWeek> WEEKDAYS = EnumSet.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

        public IsWeekday(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_WEEKDAY, new HashMap<>());
            // No additional parameters needed for this validation
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
            super(identifier, DefaultValidationCode.IS_WEEKEND, new HashMap<>());
            // No additional parameters needed for this validation
        }

        public Set<DayOfWeek> getWeekendDays() {
            return EnumSet.copyOf(WEEKEND_DAYS);
        }
    }

    @Getter
    public static final class InMonth extends DateTimeValidationMetadata {
        private final Month month;

        public InMonth(ValidationIdentifier identifier, Month month) {
            super(identifier, DefaultValidationCode.IN_MONTH, new HashMap<>());
            this.month = Objects.requireNonNull(month);

            // Add message parameters
            addMessageParameter("month", month.toString());
        }
    }

    @Getter
    public static final class InYear extends DateTimeValidationMetadata {
        private final int year;

        public InYear(ValidationIdentifier identifier, int year) {
            super(identifier, DefaultValidationCode.IN_YEAR, new HashMap<>());
            if (year < 0) throw new IllegalArgumentException("Year cannot be negative");
            this.year = year;

            // Add message parameters
            addMessageParameter("year", String.valueOf(year));
        }
    }

    // Factory methods for easier validation creation

    /**
     * Factory method for creating InRange validation
     */
    public static <T extends Temporal & Comparable<? super T>> InRange<T> inRange(ValidationIdentifier identifier, T min, T max) {
        return new InRange<>(identifier, min, max);
    }

    /**
     * Factory method for creating Before validation
     */
    public static <T extends Temporal & Comparable<? super T>> Before<T> before(ValidationIdentifier identifier, T reference) {
        return new Before<>(identifier, reference);
    }

    /**
     * Factory method for creating After validation
     */
    public static <T extends Temporal & Comparable<? super T>> After<T> after(ValidationIdentifier identifier, T reference) {
        return new After<>(identifier, reference);
    }

    /**
     * Factory method for creating BeforeOrEquals validation
     */
    public static <T extends Temporal & Comparable<? super T>> BeforeOrEquals<T> beforeOrEquals(ValidationIdentifier identifier, T reference) {
        return new BeforeOrEquals<>(identifier, reference);
    }

    /**
     * Factory method for creating AfterOrEquals validation
     */
    public static <T extends Temporal & Comparable<? super T>> AfterOrEquals<T> afterOrEquals(ValidationIdentifier identifier, T reference) {
        return new AfterOrEquals<>(identifier, reference);
    }

    /**
     * Factory method for creating Future validation
     */
    public static Future future(ValidationIdentifier identifier) {
        return new Future(identifier);
    }

    /**
     * Factory method for creating Past validation
     */
    public static Past past(ValidationIdentifier identifier) {
        return new Past(identifier);
    }

    /**
     * Factory method for creating PresentOrFuture validation
     */
    public static PresentOrFuture presentOrFuture(ValidationIdentifier identifier) {
        return new PresentOrFuture(identifier);
    }

    /**
     * Factory method for creating PresentOrPast validation
     */
    public static PresentOrPast presentOrPast(ValidationIdentifier identifier) {
        return new PresentOrPast(identifier);
    }

    /**
     * Factory method for creating Equals validation
     */
    public static <T extends Temporal & Comparable<? super T>> Equals<T> equalsDate(ValidationIdentifier identifier, T reference) {
        return new Equals<>(identifier, reference);
    }

    /**
     * Factory method for creating IsWeekday validation
     */
    public static IsWeekday isWeekday(ValidationIdentifier identifier) {
        return new IsWeekday(identifier);
    }

    /**
     * Factory method for creating IsWeekend validation
     */
    public static IsWeekend isWeekend(ValidationIdentifier identifier) {
        return new IsWeekend(identifier);
    }

    /**
     * Factory method for creating InMonth validation
     */
    public static InMonth inMonth(ValidationIdentifier identifier, Month month) {
        return new InMonth(identifier, month);
    }

    /**
     * Factory method for creating InYear validation
     */
    public static InYear inYear(ValidationIdentifier identifier, int year) {
        return new InYear(identifier, year);
    }
}