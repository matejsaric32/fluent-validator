package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Objects;

/**
 * Abstract base class for validation metadata related to time-specific constraints and validations.
 * This class serves as the foundation for all validation metadata types that deal with time-of-day
 * validation scenarios including time range validation, time comparison validation, business hours
 * validation, and time zone validation, providing specialized infrastructure for intraday temporal
 * validation operations that focus on time components rather than full date/time values.
 *
 * <p>TimeValidationMetadata supports various time-specific validation patterns:</p>
 * <ul>
 * <li><strong>Time range validation</strong> - ensuring times fall within specified daily ranges</li>
 * <li><strong>Time comparison validation</strong> - checking temporal relationships for time components</li>
 * <li><strong>Period validation</strong> - validating against predefined time periods (morning, afternoon, evening)</li>
 * <li><strong>Business time validation</strong> - checking against business hours and operational periods</li>
 * <li><strong>Granular time validation</strong> - validating specific time components (hours, minutes, seconds)</li>
 * <li><strong>Time zone validation</strong> - ensuring times are in specific time zones</li>
 * </ul>
 *
 * <p>This validation framework is distinct from DateTimeValidationMetadata as it focuses specifically
 * on time-of-day constraints rather than full temporal validation. It works with Java Time API types
 * that implement {@code Temporal} and {@code Comparable}, extracting and validating time components
 * while providing comprehensive error messaging with properly formatted time representations.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationMetadata
 * @see DefaultValidationCode
 * @see java.time.temporal.Temporal
 * @see DateTimeValidationMetadata
 */
public abstract class TimeValidationMetadata extends ValidationMetadata {

    /**
     * Protected constructor for TimeValidationMetadata.
     *
     * <p>This constructor initializes the base metadata with the validation identifier and
     * automatically adds the field identifier to the message parameters for error message
     * template substitution in time validation failure scenarios.</p>
     *
     * @param identifier the validation identifier specifying what failed validation
     * @param code the default validation code categorizing the specific time validation type
     */
    protected TimeValidationMetadata(ValidationIdentifier identifier,
                                     DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    /**
     * Validation metadata for time range constraints.
     *
     * <p>This class represents validation failures where a time must fall within a
     * specified time range (inclusive). It provides comprehensive daily time range
     * validation focusing on time-of-day components, essential for validating business
     * hours, operational windows, and daily scheduling constraints.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that appointment times fall within office hours</li>
     * <li>Ensuring that system maintenance windows occur during designated periods</li>
     * <li>Checking that delivery times are within service availability ranges</li>
     * <li>Verifying that operational processes execute during allowed time windows</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>min</strong> - {@code T extends Temporal & Comparable<? super T>} - The minimum
     * (earliest) time that the validated value must be on or after for validation to pass. This
     * represents the start of the acceptable time range. Must not be null and must be before or
     * equal to the max time.</li>
     * <li><strong>max</strong> - {@code T extends Temporal & Comparable<? super T>} - The maximum
     * (latest) time that the validated value must be on or before for validation to pass. This
     * represents the end of the acceptable time range. Must not be null and must be after or
     * equal to the min time.</li>
     * </ul>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
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

    /**
     * Validation metadata for time precedence constraints.
     *
     * <p>This class represents validation failures where a time must be before
     * (earlier than) a specified reference time. It provides strict time ordering
     * validation focusing on time-of-day components ensuring that times occur in
     * the correct chronological sequence within a day.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that meeting start times precede end times</li>
     * <li>Ensuring that shift start times are before shift end times</li>
     * <li>Checking that event preparation times occur before event start times</li>
     * <li>Verifying that daily task sequences maintain proper timing order</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>reference</strong> - {@code T extends Temporal & Comparable<? super T>} - The
     * reference time that the validated value must be strictly before (earlier than) for
     * validation to pass. Uses time component comparison to determine chronological precedence.
     * Must not be null as per factory method validation.</li>
     * </ul>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class Before<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        private Before(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_BEFORE);
            this.reference = reference;

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }
    }

    /**
     * Validation metadata for time succession constraints.
     *
     * <p>This class represents validation failures where a time must be after
     * (later than) a specified reference time. It provides strict time ordering
     * validation focusing on time-of-day components ensuring that times occur in
     * the correct chronological sequence within a day.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that closing times follow opening times</li>
     * <li>Ensuring that follow-up call times are after initial contact times</li>
     * <li>Checking that delivery times are after pickup times</li>
     * <li>Verifying that completion times occur after start times</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>reference</strong> - {@code T extends Temporal & Comparable<? super T>} - The
     * reference time that the validated value must be strictly after (later than) for
     * validation to pass. Uses time component comparison to determine chronological succession.
     * Must not be null as per factory method validation.</li>
     * </ul>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class After<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        private After(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_AFTER);
            this.reference = reference;

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }
    }

    /**
     * Validation metadata for time precedence or equality constraints.
     *
     * <p>This class represents validation failures where a time must be before or
     * equal to a specified reference time. It provides inclusive time ordering
     * validation allowing for both chronological precedence and time equality
     * within daily time constraints.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that deadline times don't exceed final cutoff times</li>
     * <li>Ensuring that service times are on or before availability limits</li>
     * <li>Checking that operational times include boundary conditions</li>
     * <li>Verifying that scheduling times allow for current or earlier slots</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>reference</strong> - {@code T extends Temporal & Comparable<? super T>} - The
     * reference time that the validated value must be before or equal to for validation to pass.
     * Uses time component comparison allowing for both precedence and equality. Must not be null
     * as per factory method validation.</li>
     * </ul>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class BeforeOrEquals<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        private BeforeOrEquals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_BEFORE_OR_EQUALS);
            this.reference = reference;

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }
    }

    /**
     * Validation metadata for time succession or equality constraints.
     *
     * <p>This class represents validation failures where a time must be after or
     * equal to a specified reference time. It provides inclusive time ordering
     * validation allowing for both chronological succession and time equality
     * within daily time constraints.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that service times are on or after opening times</li>
     * <li>Ensuring that processing times don't precede initialization times</li>
     * <li>Checking that execution times allow for current or later scheduling</li>
     * <li>Verifying that operational times include start boundary conditions</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>reference</strong> - {@code T extends Temporal & Comparable<? super T>} - The
     * reference time that the validated value must be after or equal to for validation to pass.
     * Uses time component comparison allowing for both succession and equality. Must not be null
     * as per factory method validation.</li>
     * </ul>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class AfterOrEquals<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        private AfterOrEquals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_AFTER_OR_EQUALS);
            this.reference = reference;

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }
    }

    /**
     * Validation metadata for time equality constraints.
     *
     * <p>This class represents validation failures where a time must be exactly equal
     * to a specified reference time. It provides precise time matching validation
     * ensuring that times match specific target values exactly, focusing on time-of-day
     * components for precise scheduling and synchronization requirements.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that synchronized processes execute at identical times</li>
     * <li>Ensuring that scheduled events occur at exact predetermined times</li>
     * <li>Checking that recurring daily tasks maintain precise timing</li>
     * <li>Verifying that time-critical operations align with specific time markers</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>reference</strong> - {@code T extends Temporal & Comparable<? super T>} - The
     * exact time that the validated value must equal for validation to pass. Uses time component
     * comparison for precise equality checking. Must not be null as per factory method validation.</li>
     * </ul>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class Equals<T extends Temporal & Comparable<? super T>> extends TimeValidationMetadata {
        private final T reference;

        private Equals(ValidationIdentifier identifier, T reference) {
            super(identifier, DefaultValidationCode.TIME_EQUALS);
            this.reference = reference;

            // Store raw reference time
            addMessageParameter(MessageParameter.REFERENCE_TIME, String.valueOf(reference));
        }
    }

    /**
     * Validation metadata for morning time period constraints.
     *
     * <p>This class represents validation failures where a time must fall within the
     * morning period (00:00-11:59). It provides predefined time period validation
     * for morning hours, essential for scheduling and business logic that operates
     * on traditional daily time periods.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that morning meetings are scheduled during morning hours</li>
     * <li>Ensuring that morning shift times align with morning periods</li>
     * <li>Checking that breakfast service times occur during morning hours</li>
     * <li>Verifying that morning routine tasks are scheduled appropriately</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation uses predefined morning time
     * constants (00:00-11:59) and inherits field identifier from the parent class for error
     * messaging. The time period and range are automatically added to message parameters.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class IsMorning extends TimeValidationMetadata {
        private IsMorning(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_MORNING);

            // Add time period details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "morning");
            addMessageParameter(MessageParameter.TIME_RANGE, "00:00-11:59");
        }
    }

    /**
     * Validation metadata for afternoon time period constraints.
     *
     * <p>This class represents validation failures where a time must fall within the
     * afternoon period (12:00-17:59). It provides predefined time period validation
     * for afternoon hours, supporting business logic that distinguishes between
     * different parts of the day for scheduling and operational purposes.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that afternoon appointments are scheduled during afternoon hours</li>
     * <li>Ensuring that lunch meetings occur during appropriate afternoon periods</li>
     * <li>Checking that afternoon service hours align with afternoon availability</li>
     * <li>Verifying that afternoon work shifts are properly scheduled</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation uses predefined afternoon time
     * constants (12:00-17:59) and inherits field identifier from the parent class for error
     * messaging. The time period and range are automatically added to message parameters.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class IsAfternoon extends TimeValidationMetadata {
        private IsAfternoon(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_AFTERNOON);

            // Add time period details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "afternoon");
            addMessageParameter(MessageParameter.TIME_RANGE, "12:00-17:59");
        }
    }

    /**
     * Validation metadata for evening time period constraints.
     *
     * <p>This class represents validation failures where a time must fall within the
     * evening period (18:00-23:59). It provides predefined time period validation
     * for evening hours, supporting scheduling and business logic that requires
     * validation against evening operational periods.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that evening events are scheduled during evening hours</li>
     * <li>Ensuring that dinner service times occur during evening periods</li>
     * <li>Checking that evening shift work aligns with evening time ranges</li>
     * <li>Verifying that after-hours activities are properly scheduled</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation uses predefined evening time
     * constants (18:00-23:59) and inherits field identifier from the parent class for error
     * messaging. The time period and range are automatically added to message parameters.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class IsEvening extends TimeValidationMetadata {
        private IsEvening(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_EVENING);

            // Add time period details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "evening");
            addMessageParameter(MessageParameter.TIME_RANGE, "18:00-23:59");
        }
    }

    /**
     * Validation metadata for business hours constraints.
     *
     * <p>This class represents validation failures where a time must fall within
     * standard business hours (08:00-16:00). It provides predefined business time
     * validation essential for validating operational activities, appointments,
     * and services that must occur during standard business operating hours.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that business meetings are scheduled during business hours</li>
     * <li>Ensuring that customer service calls occur within operating hours</li>
     * <li>Checking that business transactions happen during business hours</li>
     * <li>Verifying that office-based activities align with business schedules</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation uses predefined business hours
     * constants (08:00-16:00) and inherits field identifier from the parent class for error
     * messaging. The time period and range are automatically added to message parameters.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class IsBusinessHours extends TimeValidationMetadata {
        private IsBusinessHours(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_BUSINESS_HOURS);

            // Add business hours details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "business hours");
            addMessageParameter(MessageParameter.TIME_RANGE, "08:00-16:00");
        }
    }

    /**
     * Validation metadata for lunch hour constraints.
     *
     * <p>This class represents validation failures where a time must fall within
     * the standard lunch hour period (12:00-13:00). It provides predefined lunch
     * time validation useful for scheduling around lunch breaks and ensuring
     * activities align with standard meal time periods.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that lunch meetings are scheduled during lunch hours</li>
     * <li>Ensuring that lunch break activities occur during appropriate times</li>
     * <li>Checking that meal service aligns with lunch hour periods</li>
     * <li>Verifying that lunch-related scheduling respects lunch time boundaries</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation uses predefined lunch hour
     * constants (12:00-13:00) and inherits field identifier from the parent class for error
     * messaging. The time period and range are automatically added to message parameters.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class IsLunchHour extends TimeValidationMetadata {
        private IsLunchHour(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_LUNCH_HOUR);

            // Add lunch hour details for potential use in messages
            addMessageParameter(MessageParameter.TIME_PERIOD, "lunch hour");
            addMessageParameter(MessageParameter.TIME_RANGE, "12:00-13:00");
        }
    }

    /**
     * Validation metadata for hour range constraints.
     *
     * <p>This class represents validation failures where a time's hour component must
     * fall within a specified range (0-23). It provides granular hour-level validation
     * for scenarios requiring specific hour-based constraints independent of minutes
     * and seconds, useful for hourly scheduling and time-based business rules.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that hourly processes execute within allowed hour ranges</li>
     * <li>Ensuring that time-based rules apply to specific hours of the day</li>
     * <li>Checking that hourly reporting occurs within designated hour windows</li>
     * <li>Verifying that hour-sensitive operations respect hour-based constraints</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>minHour</strong> - {@code int} - The minimum hour (0-23) that the time's
     * hour component must be greater than or equal to for validation to pass. Must be
     * between 0-23 and less than or equal to maxHour as per factory method validation.</li>
     * <li><strong>maxHour</strong> - {@code int} - The maximum hour (0-23) that the time's
     * hour component must be less than or equal to for validation to pass. Must be
     * between 0-23 and greater than or equal to minHour as per factory method validation.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
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

    /**
     * Validation metadata for minute range constraints.
     *
     * <p>This class represents validation failures where a time's minute component must
     * fall within a specified range (0-59). It provides granular minute-level validation
     * for scenarios requiring specific minute-based constraints independent of hours
     * and seconds, useful for minute-precise scheduling and fine-grained time controls.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that minute-precise processes execute within allowed minute ranges</li>
     * <li>Ensuring that minute-based scheduling respects specific minute windows</li>
     * <li>Checking that minute-sensitive operations occur within designated periods</li>
     * <li>Verifying that fine-grained time controls apply appropriate minute constraints</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>minMinute</strong> - {@code int} - The minimum minute (0-59) that the time's
     * minute component must be greater than or equal to for validation to pass. Must be
     * between 0-59 and less than or equal to maxMinute as per factory method validation.</li>
     * <li><strong>maxMinute</strong> - {@code int} - The maximum minute (0-59) that the time's
     * minute component must be less than or equal to for validation to pass. Must be
     * between 0-59 and greater than or equal to minMinute as per factory method validation.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
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

    /**
     * Validation metadata for second range constraints.
     *
     * <p>This class represents validation failures where a time's second component must
     * fall within a specified range (0-59). It provides granular second-level validation
     * for scenarios requiring specific second-based constraints independent of hours
     * and minutes, useful for second-precise timing and high-precision time controls.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that second-precise processes execute within allowed second ranges</li>
     * <li>Ensuring that high-precision timing respects specific second windows</li>
     * <li>Checking that second-sensitive synchronization occurs within designated periods</li>
     * <li>Verifying that precision time controls apply appropriate second constraints</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>minSecond</strong> - {@code int} - The minimum second (0-59) that the time's
     * second component must be greater than or equal to for validation to pass. Must be
     * between 0-59 and less than or equal to maxSecond as per factory method validation.</li>
     * <li><strong>maxSecond</strong> - {@code int} - The maximum second (0-59) that the time's
     * second component must be less than or equal to for validation to pass. Must be
     * between 0-59 and greater than or equal to minSecond as per factory method validation.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
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

    /**
     * Validation metadata for time zone constraints.
     *
     * <p>This class represents validation failures where a time must be in a specific
     * time zone. It provides time zone validation essential for distributed systems,
     * global applications, and scenarios where time zone consistency is critical for
     * business operations and data integrity.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that timestamps are in the expected business time zone</li>
     * <li>Ensuring that scheduled events respect specific regional time zones</li>
     * <li>Checking that time-sensitive operations occur in designated time zones</li>
     * <li>Verifying that global coordination maintains time zone consistency</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>zoneId</strong> - {@code ZoneId} - The specific time zone that the validated
     * time must be in for validation to pass. Uses Java Time API ZoneId for precise time zone
     * identification and validation. Must not be null as per factory method validation.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
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

    /**
     * Factory method for creating InRange validation metadata.
     *
     * <p>Creates metadata for validating that a time falls within the specified time range (inclusive).</p>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     * @param identifier the validation identifier
     * @param min the minimum (earliest) allowed time (inclusive)
     * @param max the maximum (latest) allowed time (inclusive)
     * @return InRange metadata instance
     * @throws NullPointerException if identifier, min, or max is null
     * @throws IllegalArgumentException if min is after max
     */
    public static <T extends Temporal & Comparable<? super T>> InRange<T> inRange(ValidationIdentifier identifier, T min, T max) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(min, "Min value must not be null");
        Objects.requireNonNull(max, "Max value must not be null");

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Min time must be before or equal to max time");
        }

        return new InRange<>(identifier, min, max);
    }

    /**
     * Factory method for creating Before validation metadata.
     *
     * <p>Creates metadata for validating that a time is strictly before the specified reference time.</p>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     * @param identifier the validation identifier
     * @param reference the reference time that the validated value must be before
     * @return Before metadata instance
     * @throws NullPointerException if identifier or reference is null
     */
    public static <T extends Temporal & Comparable<? super T>> Before<T> before(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(reference, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return new Before<>(identifier, reference);
    }

    /**
     * Factory method for creating After validation metadata.
     *
     * <p>Creates metadata for validating that a time is strictly after the specified reference time.</p>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     * @param identifier the validation identifier
     * @param reference the reference time that the validated value must be after
     * @return After metadata instance
     * @throws NullPointerException if identifier or reference is null
     */
    public static <T extends Temporal & Comparable<? super T>> After<T> after(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(reference, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return new After<>(identifier, reference);
    }

    /**
     * Factory method for creating BeforeOrEquals validation metadata.
     *
     * <p>Creates metadata for validating that a time is before or equal to the specified reference time.</p>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     * @param identifier the validation identifier
     * @param reference the reference time that the validated value must be before or equal to
     * @return BeforeOrEquals metadata instance
     * @throws NullPointerException if identifier or reference is null
     */
    public static <T extends Temporal & Comparable<? super T>> BeforeOrEquals<T> beforeOrEquals(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(reference, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return new BeforeOrEquals<>(identifier, reference);
    }

    /**
     * Factory method for creating AfterOrEquals validation metadata.
     *
     * <p>Creates metadata for validating that a time is after or equal to the specified reference time.</p>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     * @param identifier the validation identifier
     * @param reference the reference time that the validated value must be after or equal to
     * @return AfterOrEquals metadata instance
     * @throws NullPointerException if identifier or reference is null
     */
    public static <T extends Temporal & Comparable<? super T>> AfterOrEquals<T> afterOrEquals(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(reference, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return new AfterOrEquals<>(identifier, reference);
    }

    /**
     * Factory method for creating Equals validation metadata.
     *
     * <p>Creates metadata for validating that a time is exactly equal to the specified reference time.</p>
     *
     * @param <T> the temporal type being validated (must implement Temporal and Comparable)
     * @param identifier the validation identifier
     * @param reference the reference time that the validated value must equal exactly
     * @return Equals metadata instance
     * @throws NullPointerException if identifier or reference is null
     */
    public static <T extends Temporal & Comparable<? super T>> Equals<T> equals(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(reference, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return new Equals<>(identifier, reference);
    }

    /**
     * Factory method for creating IsMorning validation metadata.
     *
     * <p>Creates metadata for validating that a time falls within the morning period (00:00-11:59).</p>
     *
     * @param identifier the validation identifier
     * @return IsMorning metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static IsMorning isMorning(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new IsMorning(identifier);
    }

    /**
     * Factory method for creating IsAfternoon validation metadata.
     *
     * <p>Creates metadata for validating that a time falls within the afternoon period (12:00-17:59).</p>
     *
     * @param identifier the validation identifier
     * @return IsAfternoon metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static IsAfternoon isAfternoon(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new IsAfternoon(identifier);
    }

    /**
     * Factory method for creating IsEvening validation metadata.
     *
     * <p>Creates metadata for validating that a time falls within the evening period (18:00-23:59).</p>
     *
     * @param identifier the validation identifier
     * @return IsEvening metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static IsEvening isEvening(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new IsEvening(identifier);
    }

    /**
     * Factory method for creating IsBusinessHours validation metadata.
     *
     * <p>Creates metadata for validating that a time falls within standard business hours (08:00-16:00).</p>
     *
     * @param identifier the validation identifier
     * @return IsBusinessHours metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static IsBusinessHours isBusinessHours(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new IsBusinessHours(identifier);
    }

    /**
     * Factory method for creating IsLunchHour validation metadata.
     *
     * <p>Creates metadata for validating that a time falls within the lunch hour period (12:00-13:00).</p>
     *
     * @param identifier the validation identifier
     * @return IsLunchHour metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static IsLunchHour isLunchHour(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new IsLunchHour(identifier);
    }

    /**
     * Factory method for creating HoursBetween validation metadata.
     *
     * <p>Creates metadata for validating that a time's hour component falls within the specified range.</p>
     *
     * @param identifier the validation identifier
     * @param minHour the minimum hour (0-23, inclusive)
     * @param maxHour the maximum hour (0-23, inclusive)
     * @return HoursBetween metadata instance
     * @throws NullPointerException if identifier is null
     * @throws IllegalArgumentException if minHour or maxHour is outside 0-23 range,
     *                                  or if minHour is greater than maxHour
     */
    public static HoursBetween hoursBetween(ValidationIdentifier identifier, int minHour, int maxHour) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

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

    /**
     * Factory method for creating MinutesBetween validation metadata.
     *
     * <p>Creates metadata for validating that a time's minute component falls within the specified range.</p>
     *
     * @param identifier the validation identifier
     * @param minMinute the minimum minute (0-59, inclusive)
     * @param maxMinute the maximum minute (0-59, inclusive)
     * @return MinutesBetween metadata instance
     * @throws NullPointerException if identifier is null
     * @throws IllegalArgumentException if minMinute or maxMinute is outside 0-59 range,
     *                                  or if minMinute is greater than maxMinute
     */
    public static MinutesBetween minutesBetween(ValidationIdentifier identifier, int minMinute, int maxMinute) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

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

    /**
     * Factory method for creating SecondsBetween validation metadata.
     *
     * <p>Creates metadata for validating that a time's second component falls within the specified range.</p>
     *
     * @param identifier the validation identifier
     * @param minSecond the minimum second (0-59, inclusive)
     * @param maxSecond the maximum second (0-59, inclusive)
     * @return SecondsBetween metadata instance
     * @throws NullPointerException if identifier is null
     * @throws IllegalArgumentException if minSecond or maxSecond is outside 0-59 range,
     *                                  or if minSecond is greater than maxSecond
     */
    public static SecondsBetween secondsBetween(ValidationIdentifier identifier, int minSecond, int maxSecond) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

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

    /**
     * Factory method for creating InTimeZone validation metadata.
     *
     * <p>Creates metadata for validating that a time is in the specified time zone.</p>
     *
     * @param identifier the validation identifier
     * @param zoneId the time zone that the time must be in
     * @return InTimeZone metadata instance
     * @throws NullPointerException if identifier or zoneId is null
     */
    public static InTimeZone inTimeZone(ValidationIdentifier identifier, ZoneId zoneId) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(zoneId, "ZoneId must not be null");

        return new InTimeZone(identifier, zoneId);
    }
}