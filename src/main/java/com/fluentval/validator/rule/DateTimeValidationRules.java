package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.DateTimeValidationMetadata;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

public final class DateTimeValidationRules {

    private static final Set<DayOfWeek> WEEKDAYS = EnumSet.of(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

    private static final Set<DayOfWeek> WEEKEND_DAYS = EnumSet.of(
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    private DateTimeValidationRules() {
        // Utility class
    }

    @SuppressWarnings("unchecked")
    private static <T extends Temporal & Comparable<? super T>> T getCurrent(T value) {
        if (value instanceof LocalDate) {
            return (T) LocalDate.now();
        } else if (value instanceof LocalDateTime) {
            return (T) LocalDateTime.now();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    private static <T extends Temporal & Comparable<? super T>> Function<T, String> getFormatter(T value) {
        if (value instanceof LocalDate) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            return (T v) -> ((LocalDate) v).format(dateFormatter);
        } else if (value instanceof LocalDateTime) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            return (T v) -> ((LocalDateTime) v).format(dateTimeFormatter);
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    private static <T extends Temporal & Comparable<? super T>> DayOfWeek getDayOfWeek(T value) {
        if (value instanceof LocalDate localDate) {
            return localDate.getDayOfWeek();
        } else if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.getDayOfWeek();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    private static <T extends Temporal & Comparable<? super T>> Month getMonth(T value) {
        if (value instanceof LocalDate localDate) {
            return localDate.getMonth();
        } else if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.getMonth();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    private static <T extends Temporal & Comparable<? super T>> int getYear(T value) {
        if (value instanceof LocalDate localDate) {
            return localDate.getYear();
        } else if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.getYear();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inRange(T min, T max) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
                Function<T, String> formatter = getFormatter(value);
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.InRange<>(
                            identifier, min, max, formatter.apply(min), formatter.apply(max)
                        )
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> before(T date) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.compareTo(date) >= 0) {
                Function<T, String> formatter = getFormatter(value);
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.Before<>(
                            identifier, date, formatter.apply(date)
                        )
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> after(T date) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.compareTo(date) <= 0) {
                Function<T, String> formatter = getFormatter(value);
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.After<>(
                            identifier, date, formatter.apply(date)
                        )
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> beforeOrEquals(T date) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.compareTo(date) > 0) {
                Function<T, String> formatter = getFormatter(value);
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.BeforeOrEquals<>(
                            identifier, date, formatter.apply(date)
                        )
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> afterOrEquals(T date) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.compareTo(date) < 0) {
                Function<T, String> formatter = getFormatter(value);
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.AfterOrEquals<>(
                            identifier, date, formatter.apply(date)
                        )
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> future() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            T current = getCurrent(value);
            if (value.compareTo(current) <= 0) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.Future(identifier)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> past() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            T current = getCurrent(value);
            if (value.compareTo(current) >= 0) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.Past(identifier)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> presentOrFuture() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            T current = getCurrent(value);
            if (value.compareTo(current) < 0) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.PresentOrFuture(identifier)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> presentOrPast() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            T current = getCurrent(value);
            if (value.compareTo(current) > 0) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.PresentOrPast(identifier)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> equalsDate(T date) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.compareTo(date) != 0) {
                Function<T, String> formatter = getFormatter(value);
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.Equals<>(
                            identifier, date, formatter.apply(date)
                        )
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isWeekday() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            DayOfWeek dayOfWeek = getDayOfWeek(value);
            if (!WEEKDAYS.contains(dayOfWeek)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.IsWeekday(identifier)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isWeekend() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            DayOfWeek dayOfWeek = getDayOfWeek(value);
            if (!WEEKEND_DAYS.contains(dayOfWeek)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.IsWeekend(identifier)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inMonth(Month month) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            Month valueMonth = getMonth(value);
            if (valueMonth != month) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.InMonth(identifier, month)
                    )
                );
            }
        };
    }

    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inYear(int year) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            int valueYear = getYear(value);
            if (valueYear != year) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new DateTimeValidationMetadata.InYear(identifier, year)
                    )
                );
            }
        };
    }
}