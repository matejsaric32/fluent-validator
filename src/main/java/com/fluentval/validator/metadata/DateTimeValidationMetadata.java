package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.temporal.Temporal;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract base class for validation metadata related to date and time constraints and validations.
 * This class serves as the foundation for all validation metadata types that deal with temporal
 * validation scenarios including date range validation, temporal comparison validation, calendar-based
 * validation, and time period validation, providing comprehensive infrastructure for date/time
 * validation operations using the Java Time API.
 *
 * <p>DateTimeValidationMetadata supports various temporal validation patterns:</p>
 * <ul>
 * <li><strong>Range validation</strong> - ensuring dates/times fall within specified ranges</li>
 * <li><strong>Comparison validation</strong> - checking temporal relationships (before, after, equals)</li>
 * <li><strong>Relative time validation</strong> - validating against current time (past, future, present)</li>
 * <li><strong>Calendar validation</strong> - ensuring dates match specific calendar constraints</li>
 * <li><strong>Weekday/weekend validation</strong> - checking day-of-week requirements</li>
 * <li><strong>Periodic validation</strong> - validating against specific months, years, or time periods</li>
 * </ul>
 *
 * <p>All validations work with Java Time API types that implement {@code Temporal} and {@code Comparable},
 * ensuring type safety and compatibility with modern Java date/time handling. The validation framework
 * leverages the temporal comparison capabilities and provides comprehensive error messaging with
 * properly formatted date/time representations for user feedback.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationMetadata
 * @see DefaultValidationCode
 * @see java.time.temporal.Temporal
 */
public abstract class DateTimeValidationMetadata extends ValidationMetadata {

    /**
     * Protected constructor for DateTimeValidationMetadata.
     *
     * <p>This constructor initializes the base metadata with the validation identifier and
     * automatically adds the field identifier to the message parameters for error message
     * template substitution in temporal validation failure scenarios.</p>
     *
     * @param identifier the validation identifier specifying what failed validation
     * @param code the default validation code categorizing the specific date/time validation type
     */
    protected DateTimeValidationMetadata(ValidationIdentifier identifier,
                                         DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    /**
     * Validation metadata for date/time range constraints.
     *
     * <p>This class represents validation failures where a date/time must fall within a
     * specified range (inclusive). It provides comprehensive range validation with both
     * minimum and maximum temporal boundaries, essential for validating date ranges,
     * business hours, valid periods, and temporal windows.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating appointment dates within available scheduling periods</li>
     * <li>Ensuring event dates fall within conference or business periods</li>
     * <li>Checking that timestamps are within acceptable processing windows</li>
     * <li>Verifying that historical dates fall within expected research periods</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>min</strong> - {@code T extends Temporal & Comparable<? super T>} - The minimum
     * (earliest) date/time that the validated value must be on or after for validation to pass.
     * This represents the lower bound of the acceptable temporal range. Must not be null and
     * must be before or equal to the max value.</li>
     * <li><strong>max</strong> - {@code T extends Temporal & Comparable<? super T>} - The maximum
     * (latest) date/time that the validated value must be on or before for validation to pass.
     * This represents the upper bound of the acceptable temporal range. Must not be null and
     * must be after or equal to the min value.</li>
     * </ul>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
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

    /**
     * Validation metadata for temporal precedence constraints.
     *
     * <p>This class represents validation failures where a date/time must be before
     * (earlier than) a specified reference date/time. It provides strict temporal
     * ordering validation ensuring that dates occur in the correct chronological sequence.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that start dates precede end dates</li>
     * <li>Ensuring submission deadlines are before event dates</li>
     * <li>Checking that historical events maintain chronological order</li>
     * <li>Verifying that preparation times occur before execution times</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>reference</strong> - {@code T extends Temporal & Comparable<? super T>} - The
     * reference date/time that the validated value must be strictly before (earlier than) for
     * validation to pass. Uses temporal comparison to determine chronological precedence. Must
     * not be null as per factory method validation.</li>
     * </ul>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Before<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        private Before(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.BEFORE);
            this.reference = reference;

            // Store raw reference date
            addMessageParameter(MessageParameter.REFERENCE_DATE, String.valueOf(reference));
        }
    }

    /**
     * Validation metadata for temporal succession constraints.
     *
     * <p>This class represents validation failures where a date/time must be after
     * (later than) a specified reference date/time. It provides strict temporal
     * ordering validation ensuring that dates occur in the correct chronological sequence.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that end dates follow start dates</li>
     * <li>Ensuring follow-up appointments are scheduled after initial consultations</li>
     * <li>Checking that delivery dates are after order dates</li>
     * <li>Verifying that review dates occur after creation dates</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>reference</strong> - {@code T extends Temporal & Comparable<? super T>} - The
     * reference date/time that the validated value must be strictly after (later than) for
     * validation to pass. Uses temporal comparison to determine chronological succession. Must
     * not be null as per factory method validation.</li>
     * </ul>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class After<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        private After(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.AFTER);
            this.reference = reference;

            // Store raw reference date
            addMessageParameter(MessageParameter.REFERENCE_DATE, String.valueOf(reference));
        }
    }

    /**
     * Validation metadata for temporal precedence or equality constraints.
     *
     * <p>This class represents validation failures where a date/time must be before or
     * equal to a specified reference date/time. It provides inclusive temporal ordering
     * validation allowing for both chronological precedence and temporal equality.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that due dates don't exceed final deadlines</li>
     * <li>Ensuring that booking dates are on or before availability periods</li>
     * <li>Checking that expiration dates allow for current or past validity</li>
     * <li>Verifying that historical cutoff dates include boundary conditions</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>reference</strong> - {@code T extends Temporal & Comparable<? super T>} - The
     * reference date/time that the validated value must be before or equal to for validation
     * to pass. Uses temporal comparison allowing for both precedence and equality. Must not
     * be null as per factory method validation.</li>
     * </ul>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class BeforeOrEquals<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        private BeforeOrEquals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.BEFORE_OR_EQUALS);
            this.reference = reference;

            // Store raw reference date
            addMessageParameter(MessageParameter.REFERENCE_DATE, String.valueOf(reference));
        }
    }

    /**
     * Validation metadata for temporal succession or equality constraints.
     *
     * <p>This class represents validation failures where a date/time must be after or
     * equal to a specified reference date/time. It provides inclusive temporal ordering
     * validation allowing for both chronological succession and temporal equality.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that effective dates are on or after activation dates</li>
     * <li>Ensuring that renewal dates don't precede original contract dates</li>
     * <li>Checking that payment dates are on or after invoice dates</li>
     * <li>Verifying that publication dates allow for current or future release</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>reference</strong> - {@code T extends Temporal & Comparable<? super T>} - The
     * reference date/time that the validated value must be after or equal to for validation
     * to pass. Uses temporal comparison allowing for both succession and equality. Must not
     * be null as per factory method validation.</li>
     * </ul>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AfterOrEquals<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        private AfterOrEquals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.AFTER_OR_EQUALS);
            this.reference = reference;

            // Store raw reference date
            addMessageParameter(MessageParameter.REFERENCE_DATE, String.valueOf(reference));
        }
    }

    /**
     * Validation metadata for future date/time constraints.
     *
     * <p>This class represents validation failures where a date/time must be in the future
     * relative to the current system time. It provides dynamic temporal validation that
     * automatically compares against the current moment, essential for validating scheduled
     * events, future deadlines, and forward-looking temporal constraints.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that appointment dates are scheduled for future times</li>
     * <li>Ensuring that expiration dates haven't already passed</li>
     * <li>Checking that event dates are planned for the future</li>
     * <li>Verifying that deadlines provide adequate time for completion</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on dynamic comparison with
     * the current system time and inherits field identifier from the parent class for error
     * messaging. The current time is determined at validation execution time.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Future extends DateTimeValidationMetadata {
        private Future(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.FUTURE);
            // No additional parameters needed for this validation
        }
    }

    /**
     * Validation metadata for past date/time constraints.
     *
     * <p>This class represents validation failures where a date/time must be in the past
     * relative to the current system time. It provides dynamic temporal validation for
     * historical data, completed events, and backward-looking temporal constraints.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that birth dates are in the past</li>
     * <li>Ensuring that completion dates represent finished events</li>
     * <li>Checking that historical records maintain temporal consistency</li>
     * <li>Verifying that transaction dates represent past occurrences</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on dynamic comparison with
     * the current system time and inherits field identifier from the parent class for error
     * messaging. The current time is determined at validation execution time.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Past extends DateTimeValidationMetadata {
        private Past(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.PAST);
            // No additional parameters needed for this validation
        }
    }

    /**
     * Validation metadata for present or future date/time constraints.
     *
     * <p>This class represents validation failures where a date/time must be in the present
     * or future relative to the current system time. It provides inclusive temporal validation
     * allowing for both current and future dates, useful for validating effective dates and
     * ongoing validity periods.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that effective dates are currently active or will be active</li>
     * <li>Ensuring that license dates haven't expired</li>
     * <li>Checking that subscription periods are current or future</li>
     * <li>Verifying that warranty dates provide ongoing or future coverage</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on dynamic comparison with
     * the current system time, allowing for equality, and inherits field identifier from the
     * parent class for error messaging. The current time is determined at validation execution time.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class PresentOrFuture extends DateTimeValidationMetadata {
        private PresentOrFuture(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.PRESENT_OR_FUTURE);
            // No additional parameters needed for this validation
        }
    }

    /**
     * Validation metadata for present or past date/time constraints.
     *
     * <p>This class represents validation failures where a date/time must be in the present
     * or past relative to the current system time. It provides inclusive temporal validation
     * allowing for both current and historical dates, useful for validating completed events
     * and historical records that include the current moment.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that log entries represent current or past events</li>
     * <li>Ensuring that completion dates don't represent future occurrences</li>
     * <li>Checking that audit timestamps are current or historical</li>
     * <li>Verifying that reporting periods include up to the current time</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on dynamic comparison with
     * the current system time, allowing for equality, and inherits field identifier from the
     * parent class for error messaging. The current time is determined at validation execution time.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class PresentOrPast extends DateTimeValidationMetadata {
        private PresentOrPast(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.PRESENT_OR_PAST);
            // No additional parameters needed for this validation
        }
    }

    /**
     * Validation metadata for temporal equality constraints.
     *
     * <p>This class represents validation failures where a date/time must be exactly equal
     * to a specified reference date/time. It provides precise temporal matching validation
     * ensuring that dates/times match specific target values exactly.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that anniversaries match specific historical dates</li>
     * <li>Ensuring that scheduled events occur at exact predetermined times</li>
     * <li>Checking that synchronized processes execute at identical timestamps</li>
     * <li>Verifying that recurring events maintain precise temporal alignment</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>reference</strong> - {@code T extends Temporal & Comparable<? super T>} - The
     * exact date/time that the validated value must equal for validation to pass. Uses temporal
     * comparison for precise equality checking. Must not be null as per factory method validation.</li>
     * </ul>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Equals<T extends Temporal & Comparable<? super T>> extends DateTimeValidationMetadata {
        private final T reference;

        private Equals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.EQUALS_DATE);
            this.reference = reference;

            // Store raw reference date
            addMessageParameter(MessageParameter.REFERENCE_DATE, String.valueOf(reference));
        }
    }

    /**
     * Validation metadata for weekday constraints.
     *
     * <p>This class represents validation failures where a date must fall on a weekday
     * (Monday through Friday). It provides calendar-based validation for business day
     * requirements and working day constraints, essential for business logic that
     * operates on standard work schedules.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that meeting dates are scheduled on business days</li>
     * <li>Ensuring that delivery dates avoid weekends</li>
     * <li>Checking that business operations occur during work days</li>
     * <li>Verifying that processing dates align with business calendars</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>WEEKDAYS</strong> - {@code static final Set<DayOfWeek>} - Immutable set
     * containing the weekday constants (Monday through Friday). This set defines what days
     * are considered weekdays for validation purposes and is used for day-of-week checking.</li>
     * </ul>
     *
     * <p><strong>Methods:</strong></p>
     * <ul>
     * <li><strong>getWeekdays()</strong> - Returns a defensive copy of the weekdays set</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
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

    /**
     * Validation metadata for weekend constraints.
     *
     * <p>This class represents validation failures where a date must fall on a weekend
     * (Saturday or Sunday). It provides calendar-based validation for non-business day
     * requirements and leisure day constraints, useful for scheduling that specifically
     * targets weekend periods.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that recreational events are scheduled on weekends</li>
     * <li>Ensuring that maintenance windows occur during non-business days</li>
     * <li>Checking that personal appointments avoid work days</li>
     * <li>Verifying that leisure activities align with weekend availability</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>WEEKEND_DAYS</strong> - {@code static final Set<DayOfWeek>} - Immutable set
     * containing the weekend day constants (Saturday and Sunday). This set defines what days
     * are considered weekend days for validation purposes and is used for day-of-week checking.</li>
     * </ul>
     *
     * <p><strong>Methods:</strong></p>
     * <ul>
     * <li><strong>getWeekendDays()</strong> - Returns a defensive copy of the weekend days set</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
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

    /**
     * Validation metadata for specific month constraints.
     *
     * <p>This class represents validation failures where a date must fall within a
     * specific month. It provides calendar-based validation for seasonal requirements,
     * monthly business rules, and periodic constraints that operate on monthly cycles.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that seasonal events occur in appropriate months</li>
     * <li>Ensuring that quarterly reports align with specific months</li>
     * <li>Checking that annual processes execute in designated months</li>
     * <li>Verifying that holiday-related activities occur in correct months</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>month</strong> - {@code Month} - The specific month that the validated date
     * must fall within for validation to pass. Uses the Java Time API Month enum for type
     * safety and clear month representation. Must not be null as per factory method validation.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class InMonth extends DateTimeValidationMetadata {
        private final Month month;

        private InMonth(ValidationIdentifier identifier, Month month) {
            super(identifier, DefaultValidationCode.IN_MONTH);
            this.month = month;

            // Add message parameters
            addMessageParameter(MessageParameter.MONTH, month.toString());
        }
    }

    /**
     * Validation metadata for specific year constraints.
     *
     * <p>This class represents validation failures where a date must fall within a
     * specific year. It provides calendar-based validation for annual requirements,
     * yearly business rules, and constraints that operate on annual cycles.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that fiscal data aligns with specific fiscal years</li>
     * <li>Ensuring that annual events occur within designated years</li>
     * <li>Checking that historical data falls within expected year ranges</li>
     * <li>Verifying that planning dates target specific operational years</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>year</strong> - {@code int} - The specific year that the validated date
     * must fall within for validation to pass. Must be non-negative as per factory method
     * validation, representing a valid calendar year.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
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

    /**
     * Factory method for creating InRange validation metadata.
     *
     * <p>Creates metadata for validating that a date/time falls within the specified range (inclusive).</p>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     * @param identifier the validation identifier
     * @param min the minimum (earliest) allowed date/time (inclusive)
     * @param max the maximum (latest) allowed date/time (inclusive)
     * @return InRange metadata instance
     * @throws NullPointerException if identifier, min, or max is null
     * @throws IllegalArgumentException if min is after max
     */
    public static <T extends Temporal & Comparable<? super T>> InRange<T> inRange(ValidationIdentifier identifier, T min, T max) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(min, "Min value must not be null");
        Objects.requireNonNull(max, "Max value must not be null");

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Min must be less than or equal to max");
        }

        return new InRange<>(identifier, min, max);
    }

    /**
     * Factory method for creating Before validation metadata.
     *
     * <p>Creates metadata for validating that a date/time is strictly before the specified reference date/time.</p>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     * @param identifier the validation identifier
     * @param reference the reference date/time that the validated value must be before
     * @return Before metadata instance
     * @throws NullPointerException if identifier or reference is null
     */
    public static <T extends Temporal & Comparable<? super T>> Before<T> before(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(reference, MetadataUtils.REFERENCE_DATE_MUST_NOT_BE_NULL_MSG);

        return new Before<>(identifier, reference);
    }

    /**
     * Factory method for creating After validation metadata.
     *
     * <p>Creates metadata for validating that a date/time is strictly after the specified reference date/time.</p>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     * @param identifier the validation identifier
     * @param reference the reference date/time that the validated value must be after
     * @return After metadata instance
     * @throws NullPointerException if identifier or reference is null
     */
    public static <T extends Temporal & Comparable<? super T>> After<T> after(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(reference, MetadataUtils.REFERENCE_DATE_MUST_NOT_BE_NULL_MSG);

        return new After<>(identifier, reference);
    }

    /**
     * Factory method for creating BeforeOrEquals validation metadata.
     *
     * <p>Creates metadata for validating that a date/time is before or equal to the specified reference date/time.</p>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     * @param identifier the validation identifier
     * @param reference the reference date/time that the validated value must be before or equal to
     * @return BeforeOrEquals metadata instance
     * @throws NullPointerException if identifier or reference is null
     */
    public static <T extends Temporal & Comparable<? super T>> BeforeOrEquals<T> beforeOrEquals(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(reference, MetadataUtils.REFERENCE_DATE_MUST_NOT_BE_NULL_MSG);

        return new BeforeOrEquals<>(identifier, reference);
    }

    /**
     * Factory method for creating AfterOrEquals validation metadata.
     *
     * <p>Creates metadata for validating that a date/time is after or equal to the specified reference date/time.</p>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     * @param identifier the validation identifier
     * @param reference the reference date/time that the validated value must be after or equal to
     * @return AfterOrEquals metadata instance
     * @throws NullPointerException if identifier or reference is null
     */
    public static <T extends Temporal & Comparable<? super T>> AfterOrEquals<T> afterOrEquals(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(reference, MetadataUtils.REFERENCE_DATE_MUST_NOT_BE_NULL_MSG);

        return new AfterOrEquals<>(identifier, reference);
    }

    /**
     * Factory method for creating Future validation metadata.
     *
     * <p>Creates metadata for validating that a date/time is in the future relative to the current system time.</p>
     *
     * @param identifier the validation identifier
     * @return Future metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static Future future(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new Future(identifier);
    }

    /**
     * Factory method for creating Past validation metadata.
     *
     * <p>Creates metadata for validating that a date/time is in the past relative to the current system time.</p>
     *
     * @param identifier the validation identifier
     * @return Past metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static Past past(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new Past(identifier);
    }

    /**
     * Factory method for creating PresentOrFuture validation metadata.
     *
     * <p>Creates metadata for validating that a date/time is in the present or future relative to the current system time.</p>
     *
     * @param identifier the validation identifier
     * @return PresentOrFuture metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static PresentOrFuture presentOrFuture(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new PresentOrFuture(identifier);
    }

    /**
     * Factory method for creating PresentOrPast validation metadata.
     *
     * <p>Creates metadata for validating that a date/time is in the present or past relative to the current system time.</p>
     *
     * @param identifier the validation identifier
     * @return PresentOrPast metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static PresentOrPast presentOrPast(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new PresentOrPast(identifier);
    }

    /**
     * Factory method for creating Equals validation metadata.
     *
     * <p>Creates metadata for validating that a date/time is exactly equal to the specified reference date/time.</p>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     * @param identifier the validation identifier
     * @param reference the reference date/time that the validated value must equal exactly
     * @return Equals metadata instance
     * @throws NullPointerException if identifier or reference is null
     */
    public static <T extends Temporal & Comparable<? super T>> Equals<T> equalsDate(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(reference, MetadataUtils.REFERENCE_DATE_MUST_NOT_BE_NULL_MSG);

        return new Equals<>(identifier, reference);
    }

    /**
     * Factory method for creating IsWeekday validation metadata.
     *
     * <p>Creates metadata for validating that a date falls on a weekday (Monday through Friday).</p>
     *
     * @param identifier the validation identifier
     * @return IsWeekday metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static IsWeekday isWeekday(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new IsWeekday(identifier);
    }

    /**
     * Factory method for creating IsWeekend validation metadata.
     *
     * <p>Creates metadata for validating that a date falls on a weekend (Saturday or Sunday).</p>
     *
     * @param identifier the validation identifier
     * @return IsWeekend metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static IsWeekend isWeekend(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new IsWeekend(identifier);
    }

    /**
     * Factory method for creating InMonth validation metadata.
     *
     * <p>Creates metadata for validating that a date falls within the specified month.</p>
     *
     * @param identifier the validation identifier
     * @param month the month that the date must fall within
     * @return InMonth metadata instance
     * @throws NullPointerException if identifier or month is null
     */
    public static InMonth inMonth(ValidationIdentifier identifier, Month month) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(month, "Month must not be null");

        return new InMonth(identifier, month);
    }

    /**
     * Factory method for creating InYear validation metadata.
     *
     * <p>Creates metadata for validating that a date falls within the specified year.</p>
     *
     * @param identifier the validation identifier
     * @param year the year that the date must fall within (must be non-negative)
     * @return InYear metadata instance
     * @throws NullPointerException if identifier is null
     * @throws IllegalArgumentException if year is negative
     */
    public static InYear inYear(ValidationIdentifier identifier, int year) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        if (year < 0) {
            throw new IllegalArgumentException("Year cannot be negative");
        }

        return new InYear(identifier, year);
    }
}