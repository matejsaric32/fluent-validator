package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.DateTimeValidationMetadata;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.Temporal;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import static com.fluentval.validator.rule.ValidationRuleUtils.createSkipNullRule;

public final class DateTimeValidationRules {

    private static final Set<DayOfWeek> WEEKDAYS = EnumSet.of(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

    private static final Set<DayOfWeek> WEEKEND_DAYS = EnumSet.of(
            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    private DateTimeValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static <T extends Temporal & Comparable<? super T>> boolean isInRange(final T value, final T min, final T max) {
            return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isBefore(final T value, final T reference) {
            return value.compareTo(reference) < 0;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isAfter(final T value, final T reference) {
            return value.compareTo(reference) > 0;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isBeforeOrEquals(final T value, final T reference) {
            return value.compareTo(reference) <= 0;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isAfterOrEquals(final T value, final T reference) {
            return value.compareTo(reference) >= 0;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isFuture(final T value) {
            T current = getCurrent(value);
            return value.compareTo(current) > 0;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isPast(final T value) {
            T current = getCurrent(value);
            return value.compareTo(current) < 0;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isPresentOrFuture(final T value) {
            T current = getCurrent(value);
            return value.compareTo(current) >= 0;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isPresentOrPast(final T value) {
            T current = getCurrent(value);
            return value.compareTo(current) <= 0;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isEqualTo(final T value, final T reference) {
            return value.compareTo(reference) == 0;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isWeekday(final T value) {
            DayOfWeek dayOfWeek = getDayOfWeek(value);
            return WEEKDAYS.contains(dayOfWeek);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isWeekend(final T value) {
            DayOfWeek dayOfWeek = getDayOfWeek(value);
            return WEEKEND_DAYS.contains(dayOfWeek);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isInMonth(final T value, final Month month) {
            Month valueMonth = getMonth(value);
            return valueMonth == month;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isInYear(final T value, final int year) {
            int valueYear = getYear(value);
            return valueYear == year;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Temporal & Comparable<? super T>> T getCurrent(final T value) {
        if (value instanceof LocalDate) {
            return (T) LocalDate.now();
        } else if (value instanceof LocalDateTime) {
            return (T) LocalDateTime.now();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    private static <T extends Temporal & Comparable<? super T>> DayOfWeek getDayOfWeek(final T value) {
        if (value instanceof LocalDate localDate) {
            return localDate.getDayOfWeek();
        } else if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.getDayOfWeek();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    private static <T extends Temporal & Comparable<? super T>> Month getMonth(final T value) {
        if (value instanceof LocalDate localDate) {
            return localDate.getMonth();
        } else if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.getMonth();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    private static <T extends Temporal & Comparable<? super T>> int getYear(final T value) {
        if (value instanceof LocalDate localDate) {
            return localDate.getYear();
        } else if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.getYear();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inRange(final T min, final T max) {
        Objects.requireNonNull(min, "Min date must not be null");
        Objects.requireNonNull(max, "Max date must not be null");

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Min date must be before or equal to max date");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isInRange(value, min, max),
                identifier -> DateTimeValidationMetadata.inRange(identifier, min, max)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> before(final T date) {
        Objects.requireNonNull(date, "Reference date must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isBefore(value, date),
                identifier -> DateTimeValidationMetadata.before(identifier, date)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> after(final T date) {
        Objects.requireNonNull(date, "Reference date must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isAfter(value, date),
                identifier -> DateTimeValidationMetadata.after(identifier, date)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> beforeOrEquals(final T date) {
        Objects.requireNonNull(date, "Reference date must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isBeforeOrEquals(value, date),
                identifier -> DateTimeValidationMetadata.beforeOrEquals(identifier, date)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> afterOrEquals(final T date) {
        Objects.requireNonNull(date, "Reference date must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isAfterOrEquals(value, date),
                identifier -> DateTimeValidationMetadata.afterOrEquals(identifier, date)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> future() {
        return createSkipNullRule(
                ValidationFunctions::isFuture,
                DateTimeValidationMetadata::future
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> past() {
        return createSkipNullRule(
                ValidationFunctions::isPast,
                DateTimeValidationMetadata::past
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> presentOrFuture() {
        return createSkipNullRule(
                ValidationFunctions::isPresentOrFuture,
                DateTimeValidationMetadata::presentOrFuture
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> presentOrPast() {
        return createSkipNullRule(
                ValidationFunctions::isPresentOrPast,
                DateTimeValidationMetadata::presentOrPast
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> equalsDate(final T date) {
        Objects.requireNonNull(date, "Reference date must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isEqualTo(value, date),
                identifier -> DateTimeValidationMetadata.equalsDate(identifier, date)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isWeekday() {
        return createSkipNullRule(
                ValidationFunctions::isWeekday,
                DateTimeValidationMetadata::isWeekday
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isWeekend() {
        return createSkipNullRule(
                ValidationFunctions::isWeekend,
                DateTimeValidationMetadata::isWeekend
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inMonth(final Month month) {
        Objects.requireNonNull(month, "Month must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isInMonth(value, month),
                identifier -> DateTimeValidationMetadata.inMonth(identifier, month)
        );
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inYear(final int year) {
        if (year < 0) {
            throw new IllegalArgumentException("Year cannot be negative");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isInYear(value, year),
                identifier -> DateTimeValidationMetadata.inYear(identifier, year)
        );
    }
}