package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.MetadataUtils;
import com.fluentval.validator.metadata.TimeValidationMetadata;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Objects;

import static com.fluentval.validator.rule.ValidationRuleUtils.createSkipNullRule;

/**
 * Utility class providing validation rules for time-based types including LocalTime, OffsetTime,
 * ZonedDateTime, and other Temporal implementations that represent time components. This class offers
 * comprehensive validation for time ranges, business hours, time periods, and temporal constraints.
 *
 * <p>All validation rules in this class automatically skip null values, meaning they will
 * pass validation if the input is null. Use in combination with {@code CommonValidationRules.notNull()}
 * if null values should be rejected.</p>
 *
 * <p>Supported temporal types include:</p>
 * <ul>
 * <li>{@link LocalTime} - Time without date or timezone</li>
 * <li>{@link OffsetTime} - Time with UTC offset</li>
 * <li>{@link ZonedDateTime} - Date and time with timezone (time component extracted)</li>
 * <li>Other {@link Temporal} types that implement {@link Comparable} and contain time information</li>
 * </ul>
 *
 * <p>Time validation categories include:</p>
 * <ul>
 * <li><strong>Range validations</strong> - time ranges, before/after comparisons</li>
 * <li><strong>Business time validations</strong> - business hours, lunch hours</li>
 * <li><strong>Period validations</strong> - morning, afternoon, evening periods</li>
 * <li><strong>Component validations</strong> - hour, minute, second ranges</li>
 * <li><strong>Timezone validations</strong> - timezone-specific constraints</li>
 * </ul>
 *
 * <p>Predefined time periods:</p>
 * <ul>
 * <li><strong>Morning:</strong> 00:00 - 11:59</li>
 * <li><strong>Afternoon:</strong> 12:00 - 17:59</li>
 * <li><strong>Evening:</strong> 18:00 - 23:59</li>
 * <li><strong>Business Hours:</strong> 08:00 - 16:00</li>
 * <li><strong>Lunch Hour:</strong> 12:00 - 13:00</li>
 * </ul>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationRule
 * @see TimeValidationMetadata
 * @see LocalTime
 * @see OffsetTime
 * @see ZonedDateTime
 * @see Temporal
 */
public final class TimeValidationRules {

    // Time period constants
    private static final LocalTime MORNING_START = LocalTime.of(0, 0);
    private static final LocalTime MORNING_END = LocalTime.of(11, 59, 59);
    private static final LocalTime AFTERNOON_START = LocalTime.of(12, 0);
    private static final LocalTime AFTERNOON_END = LocalTime.of(17, 59, 59);
    private static final LocalTime EVENING_START = LocalTime.of(18, 0);
    private static final LocalTime EVENING_END = LocalTime.of(23, 59, 59);

    // Business hours constants
    private static final LocalTime BUSINESS_HOURS_START = LocalTime.of(8, 0);
    private static final LocalTime BUSINESS_HOURS_END = LocalTime.of(16, 0);
    private static final LocalTime LUNCH_BREAK_START = LocalTime.of(12, 0);
    private static final LocalTime LUNCH_BREAK_END = LocalTime.of(13, 0);

    private TimeValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static <T extends Temporal & Comparable<? super T>> boolean isInTimeRange(final T value, final T min, final T max) {
            LocalTime ltValue = getLocalTime(value);
            LocalTime ltMin = getLocalTime(min);
            LocalTime ltMax = getLocalTime(max);

            return !ltValue.isBefore(ltMin) && !ltValue.isAfter(ltMax);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isBefore(final T value, final T time) {
            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            return ltValue.isBefore(ltTime);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isAfter(final T value, final T time) {
            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            return ltValue.isAfter(ltTime);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isBeforeOrEquals(final T value, final T time) {
            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            return !ltValue.isAfter(ltTime);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isAfterOrEquals(final T value, final T time) {
            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            return !ltValue.isBefore(ltTime);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isEqual(final T value, final T time) {
            LocalTime ltValue = getLocalTime(value);
            LocalTime ltTime = getLocalTime(time);

            return ltValue.equals(ltTime);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isMorning(final T value) {
            LocalTime ltValue = getLocalTime(value);
            return !ltValue.isBefore(MORNING_START) && !ltValue.isAfter(MORNING_END);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isAfternoon(final T value) {
            LocalTime ltValue = getLocalTime(value);
            return !ltValue.isBefore(AFTERNOON_START) && !ltValue.isAfter(AFTERNOON_END);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isEvening(final T value) {
            LocalTime ltValue = getLocalTime(value);
            return !ltValue.isBefore(EVENING_START) && !ltValue.isAfter(EVENING_END);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isBusinessHours(final T value) {
            LocalTime ltValue = getLocalTime(value);
            return !ltValue.isBefore(BUSINESS_HOURS_START) && !ltValue.isAfter(BUSINESS_HOURS_END);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isLunchHour(final T value) {
            LocalTime ltValue = getLocalTime(value);
            return !ltValue.isBefore(LUNCH_BREAK_START) && !ltValue.isAfter(LUNCH_BREAK_END);
        }

        static <T extends Temporal & Comparable<? super T>> boolean isHoursBetween(final T value, final int minHour, final int maxHour) {
            int hour = value.get(ChronoField.HOUR_OF_DAY);
            return hour >= minHour && hour <= maxHour;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isMinutesBetween(final T value, final int minMinute, final int maxMinute) {
            int minute = value.get(ChronoField.MINUTE_OF_HOUR);
            return minute >= minMinute && minute <= maxMinute;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isSecondsBetween(final T value, final int minSecond, final int maxSecond) {
            int second = value.get(ChronoField.SECOND_OF_MINUTE);
            return second >= minSecond && second <= maxSecond;
        }

        static <T extends Temporal & Comparable<? super T>> boolean isInTimeZone(final T value, final ZoneId zoneId) {
            ZoneId valueZone = getZoneId(value);
            return valueZone != null && valueZone.equals(zoneId);
        }

        private static ZoneId getZoneId(final Temporal value) {
            if (value instanceof ZonedDateTime zonedDateTime) {
                return zonedDateTime.getZone();
            } else if (value instanceof OffsetTime offsetTime) {
                return ZoneId.ofOffset("", offsetTime.getOffset());
            }

            return null;
        }
    }

    private static LocalTime getLocalTime(final Temporal value) {
        if (value instanceof LocalTime localTime) {
            return localTime;
        } else if (value instanceof OffsetTime offsetTime) {
            return offsetTime.toLocalTime();
        } else if (value instanceof ZonedDateTime zonedDateTime) {
            return zonedDateTime.toLocalTime();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + value.getClass());
    }

    /**
     * Creates a validation rule that checks if a time falls within the specified range (inclusive).
     *
     * <p>This rule validates that the time component is between the minimum and maximum times,
     * including the boundary values. The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param min the minimum allowed time (inclusive)
     * @param max the maximum allowed time (inclusive)
     * @return a ValidationRule that passes if the time is within the specified range
     * @throws NullPointerException if min or max is null
     * @throws IllegalArgumentException if min is after max
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that appointment time is within business hours
     * LocalTime businessStart = LocalTime.of(9, 0);
     * LocalTime businessEnd = LocalTime.of(17, 0);
     * ValidationResult result = Validator.of(appointment)
     *     .property(ValidationIdentifier.ofField("appointmentTime"), Appointment::getAppointmentTime)
     *         .validate(TimeValidationRules.inRange(businessStart, businessEnd))
     *         .end()
     *     .getResult();
     *
     * // Validate that meeting time is during work hours
     * LocalTime workStart = LocalTime.of(8, 30);
     * LocalTime workEnd = LocalTime.of(18, 0);
     * ValidationResult meetingResult = Validator.of(meeting)
     *     .property(ValidationIdentifier.ofField("startTime"), Meeting::getStartTime)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(TimeValidationRules.inRange(workStart, workEnd))
     *         .end()
     *     .getResult();
     *
     * // Validate that store opening time is within allowed range
     * LocalTime earliestOpen = LocalTime.of(6, 0);
     * LocalTime latestOpen = LocalTime.of(10, 0);
     * ValidationResult storeResult = Validator.of(store)
     *     .property(ValidationIdentifier.ofField("openingTime"), Store::getOpeningTime)
     *         .validate(TimeValidationRules.inRange(earliestOpen, latestOpen))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inRange(final T min, final T max) {
        Objects.requireNonNull(min, "Min time must not be null");
        Objects.requireNonNull(max, "Max time must not be null");

        LocalTime ltMin = getLocalTime(min);
        LocalTime ltMax = getLocalTime(max);

        if (ltMin.isAfter(ltMax)) {
            throw new IllegalArgumentException("Min time must be before or equal to max time");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isInTimeRange(value, min, max),
                identifier -> TimeValidationMetadata.inRange(identifier, min, max)
        );
    }

    /**
     * Creates a validation rule that checks if a time is before the specified reference time.
     *
     * <p>This rule validates that the time component occurs strictly before the reference time.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param time the reference time that the value must be before
     * @return a ValidationRule that passes if the time is before the reference time
     * @throws NullPointerException if time is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that checkout time is before closing time
     * LocalTime closingTime = LocalTime.of(22, 0);
     * ValidationResult result = Validator.of(checkout)
     *     .property(ValidationIdentifier.ofField("checkoutTime"), Checkout::getCheckoutTime)
     *         .validate(TimeValidationRules.before(closingTime))
     *         .end()
     *     .getResult();
     *
     * // Validate that meeting start is before end time
     * ValidationResult meetingResult = Validator.of(meeting)
     *     .property(ValidationIdentifier.ofField("startTime"), Meeting::getStartTime)
     *         .validate(TimeValidationRules.before(meeting.getEndTime()))
     *         .end()
     *     .getResult();
     *
     * // Validate that deadline is before business hours end
     * LocalTime endOfBusiness = LocalTime.of(17, 30);
     * ValidationResult deadlineResult = Validator.of(task)
     *     .property(ValidationIdentifier.ofField("deadline"), Task::getDeadline)
     *         .validate(TimeValidationRules.before(endOfBusiness))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> before(final T time) {
        Objects.requireNonNull(time, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return createSkipNullRule(
                value -> ValidationFunctions.isBefore(value, time),
                identifier -> TimeValidationMetadata.before(identifier, time)
        );
    }

    /**
     * Creates a validation rule that checks if a time is after the specified reference time.
     *
     * <p>This rule validates that the time component occurs strictly after the reference time.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param time the reference time that the value must be after
     * @return a ValidationRule that passes if the time is after the reference time
     * @throws NullPointerException if time is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that service time is after opening time
     * LocalTime openingTime = LocalTime.of(8, 0);
     * ValidationResult result = Validator.of(service)
     *     .property(ValidationIdentifier.ofField("serviceTime"), Service::getServiceTime)
     *         .validate(TimeValidationRules.after(openingTime))
     *         .end()
     *     .getResult();
     *
     * // Validate that meeting end is after start time
     * ValidationResult meetingResult = Validator.of(meeting)
     *     .property(ValidationIdentifier.ofField("endTime"), Meeting::getEndTime)
     *         .validate(TimeValidationRules.after(meeting.getStartTime()))
     *         .end()
     *     .getResult();
     *
     * // Validate that break time is after minimum work period
     * LocalTime minimumWork = LocalTime.of(10, 0);
     * ValidationResult breakResult = Validator.of(employee)
     *     .property(ValidationIdentifier.ofField("breakTime"), Employee::getBreakTime)
     *         .validate(TimeValidationRules.after(minimumWork))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> after(final T time) {
        Objects.requireNonNull(time, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return createSkipNullRule(
                value -> ValidationFunctions.isAfter(value, time),
                identifier -> TimeValidationMetadata.after(identifier, time)
        );
    }

    /**
     * Creates a validation rule that checks if a time is before or equal to the specified reference time.
     *
     * <p>This rule validates that the time component occurs before or exactly at the reference time.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param time the reference time that the value must be before or equal to
     * @return a ValidationRule that passes if the time is before or equal to the reference time
     * @throws NullPointerException if time is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that last order time is not later than kitchen closing
     * LocalTime kitchenClose = LocalTime.of(21, 30);
     * ValidationResult result = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("orderTime"), Order::getOrderTime)
     *         .validate(TimeValidationRules.beforeOrEquals(kitchenClose))
     *         .end()
     *     .getResult();
     *
     * // Validate that submission time is not after deadline
     * LocalTime deadline = LocalTime.of(23, 59);
     * ValidationResult submissionResult = Validator.of(submission)
     *     .property(ValidationIdentifier.ofField("submissionTime"), Submission::getSubmissionTime)
     *         .validate(TimeValidationRules.beforeOrEquals(deadline))
     *         .end()
     *     .getResult();
     *
     * // Validate that call time is within support hours
     * LocalTime supportEnd = LocalTime.of(20, 0);
     * ValidationResult callResult = Validator.of(supportCall)
     *     .property(ValidationIdentifier.ofField("callTime"), SupportCall::getCallTime)
     *         .validate(TimeValidationRules.beforeOrEquals(supportEnd))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> beforeOrEquals(final T time) {
        Objects.requireNonNull(time, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return createSkipNullRule(
                value -> ValidationFunctions.isBeforeOrEquals(value, time),
                identifier -> TimeValidationMetadata.beforeOrEquals(identifier, time)
        );
    }

    /**
     * Creates a validation rule that checks if a time is after or equal to the specified reference time.
     *
     * <p>This rule validates that the time component occurs after or exactly at the reference time.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param time the reference time that the value must be after or equal to
     * @return a ValidationRule that passes if the time is after or equal to the reference time
     * @throws NullPointerException if time is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that service time is not earlier than opening
     * LocalTime openingTime = LocalTime.of(9, 0);
     * ValidationResult result = Validator.of(service)
     *     .property(ValidationIdentifier.ofField("serviceTime"), Service::getServiceTime)
     *         .validate(TimeValidationRules.afterOrEquals(openingTime))
     *         .end()
     *     .getResult();
     *
     * // Validate that alarm time is not earlier than minimum
     * LocalTime earliestAlarm = LocalTime.of(6, 0);
     * ValidationResult alarmResult = Validator.of(alarm)
     *     .property(ValidationIdentifier.ofField("alarmTime"), Alarm::getAlarmTime)
     *         .validate(TimeValidationRules.afterOrEquals(earliestAlarm))
     *         .end()
     *     .getResult();
     *
     * // Validate that meeting time is not before earliest allowed
     * LocalTime earliestMeeting = LocalTime.of(8, 30);
     * ValidationResult meetingResult = Validator.of(meeting)
     *     .property(ValidationIdentifier.ofField("startTime"), Meeting::getStartTime)
     *         .validate(TimeValidationRules.afterOrEquals(earliestMeeting))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> afterOrEquals(final T time) {
        Objects.requireNonNull(time, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return createSkipNullRule(
                value -> ValidationFunctions.isAfterOrEquals(value, time),
                identifier -> TimeValidationMetadata.afterOrEquals(identifier, time)
        );
    }

    /**
     * Creates a validation rule that checks if a time equals the specified reference time.
     *
     * <p>This rule validates that the time component is exactly equal to the reference time.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param time the reference time that the value must equal
     * @return a ValidationRule that passes if the time equals the reference time
     * @throws NullPointerException if time is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that synchronized event time matches expected time
     * LocalTime expectedTime = LocalTime.of(12, 0);
     * ValidationResult result = Validator.of(synchronizedEvent)
     *     .property(ValidationIdentifier.ofField("eventTime"), SynchronizedEvent::getEventTime)
     *         .validate(TimeValidationRules.isEquals(expectedTime))
     *         .end()
     *     .getResult();
     *
     * // Validate that daily backup time matches schedule
     * LocalTime backupTime = LocalTime.of(2, 30);
     * ValidationResult backupResult = Validator.of(backup)
     *     .property(ValidationIdentifier.ofField("scheduledTime"), Backup::getScheduledTime)
     *         .validate(TimeValidationRules.isEquals(backupTime))
     *         .end()
     *     .getResult();
     *
     * // Validate that meeting time matches booked slot
     * LocalTime bookedSlot = LocalTime.of(14, 30);
     * ValidationResult slotResult = Validator.of(meeting)
     *     .property(ValidationIdentifier.ofField("meetingTime"), Meeting::getMeetingTime)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(TimeValidationRules.isEquals(bookedSlot))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isEquals(final T time) {
        Objects.requireNonNull(time, MetadataUtils.REFERENCE_TIME_MUST_NOT_BE_NULL_MSG);

        return createSkipNullRule(
                value -> ValidationFunctions.isEqual(value, time),
                identifier -> TimeValidationMetadata.equals(identifier, time)
        );
    }

    /**
     * Creates a validation rule that checks if a time falls within the morning period (00:00 - 11:59).
     *
     * <p>This rule validates that the time component is within the predefined morning period.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @return a ValidationRule that passes if the time is in the morning
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that breakfast service time is in the morning
     * ValidationResult result = Validator.of(restaurant)
     *     .property(ValidationIdentifier.ofField("breakfastTime"), Restaurant::getBreakfastTime)
     *         .validate(TimeValidationRules.isMorning())
     *         .end()
     *     .getResult();
     *
     * // Validate that morning exercise time is appropriate
     * ValidationResult exerciseResult = Validator.of(routine)
     *     .property(ValidationIdentifier.ofField("morningExercise"), Routine::getMorningExercise)
     *         .validate(TimeValidationRules.isMorning())
     *         .end()
     *     .getResult();
     *
     * // Validate that early bird special is during morning hours
     * ValidationResult specialResult = Validator.of(promotion)
     *     .property(ValidationIdentifier.ofField("specialTime"), Promotion::getSpecialTime)
     *         .validate(TimeValidationRules.isMorning())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isMorning() {
        return createSkipNullRule(
                ValidationFunctions::isMorning,
                TimeValidationMetadata::isMorning
        );
    }

    /**
     * Creates a validation rule that checks if a time falls within the afternoon period (12:00 - 17:59).
     *
     * <p>This rule validates that the time component is within the predefined afternoon period.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @return a ValidationRule that passes if the time is in the afternoon
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that lunch meeting is scheduled for afternoon
     * ValidationResult result = Validator.of(meeting)
     *     .property(ValidationIdentifier.ofField("lunchMeetingTime"), Meeting::getLunchMeetingTime)
     *         .validate(TimeValidationRules.isAfternoon())
     *         .end()
     *     .getResult();
     *
     * // Validate that afternoon tea service is properly timed
     * ValidationResult teaResult = Validator.of(service)
     *     .property(ValidationIdentifier.ofField("afternoonTeaTime"), Service::getAfternoonTeaTime)
     *         .validate(TimeValidationRules.isAfternoon())
     *         .end()
     *     .getResult();
     *
     * // Validate that matinee show is in afternoon
     * ValidationResult showResult = Validator.of(show)
     *     .property(ValidationIdentifier.ofField("matineeTime"), Show::getMatineeTime)
     *         .validate(TimeValidationRules.isAfternoon())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isAfternoon() {
        return createSkipNullRule(
                ValidationFunctions::isAfternoon,
                TimeValidationMetadata::isAfternoon
        );
    }

    /**
     * Creates a validation rule that checks if a time falls within the evening period (18:00 - 23:59).
     *
     * <p>This rule validates that the time component is within the predefined evening period.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @return a ValidationRule that passes if the time is in the evening
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that dinner service is in the evening
     * ValidationResult result = Validator.of(restaurant)
     *     .property(ValidationIdentifier.ofField("dinnerTime"), Restaurant::getDinnerTime)
     *         .validate(TimeValidationRules.isEvening())
     *         .end()
     *     .getResult();
     *
     * // Validate that evening event is properly scheduled
     * ValidationResult eventResult = Validator.of(event)
     *     .property(ValidationIdentifier.ofField("eveningEventTime"), Event::getEveningEventTime)
     *         .validate(TimeValidationRules.isEvening())
     *         .end()
     *     .getResult();
     *
     * // Validate that night shift starts in evening
     * ValidationResult shiftResult = Validator.of(shift)
     *     .property(ValidationIdentifier.ofField("nightShiftStart"), Shift::getNightShiftStart)
     *         .validate(TimeValidationRules.isEvening())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isEvening() {
        return createSkipNullRule(
                ValidationFunctions::isEvening,
                TimeValidationMetadata::isEvening
        );
    }

    /**
     * Creates a validation rule that checks if a time falls within business hours (08:00 - 16:00).
     *
     * <p>This rule validates that the time component is within the predefined business hours period.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @return a ValidationRule that passes if the time is during business hours
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that customer service call is during business hours
     * ValidationResult result = Validator.of(serviceCall)
     *     .property(ValidationIdentifier.ofField("callTime"), ServiceCall::getCallTime)
     *         .validate(TimeValidationRules.isBusinessHours())
     *         .end()
     *     .getResult();
     *
     * // Validate that office meeting is during business hours
     * ValidationResult meetingResult = Validator.of(meeting)
     *     .property(ValidationIdentifier.ofField("meetingTime"), Meeting::getMeetingTime)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(TimeValidationRules.isBusinessHours())
     *         .end()
     *     .getResult();
     *
     * // Validate that appointment is scheduled during business hours
     * ValidationResult appointmentResult = Validator.of(appointment)
     *     .property(ValidationIdentifier.ofField("appointmentTime"), Appointment::getAppointmentTime)
     *         .validate(TimeValidationRules.isBusinessHours())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isBusinessHours() {
        return createSkipNullRule(
                ValidationFunctions::isBusinessHours,
                TimeValidationMetadata::isBusinessHours
        );
    }

    /**
     * Creates a validation rule that checks if a time falls within the lunch hour (12:00 - 13:00).
     *
     * <p>This rule validates that the time component is within the predefined lunch hour period.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @return a ValidationRule that passes if the time is during lunch hour
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that lunch break is scheduled during lunch hour
     * ValidationResult result = Validator.of(employee)
     *     .property(ValidationIdentifier.ofField("lunchBreak"), Employee::getLunchBreak)
     *         .validate(TimeValidationRules.isLunchHour())
     *         .end()
     *     .getResult();
     *
     * // Validate that lunch special is served during lunch hour
     * ValidationResult specialResult = Validator.of(restaurant)
     *     .property(ValidationIdentifier.ofField("lunchSpecialTime"), Restaurant::getLunchSpecialTime)
     *         .validate(TimeValidationRules.isLunchHour())
     *         .end()
     *     .getResult();
     *
     * // Validate that cafeteria service is during lunch hour
     * ValidationResult cafeteriaResult = Validator.of(cafeteria)
     *     .property(ValidationIdentifier.ofField("serviceTime"), Cafeteria::getServiceTime)
     *         .validate(TimeValidationRules.isLunchHour())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isLunchHour() {
        return createSkipNullRule(
                ValidationFunctions::isLunchHour,
                TimeValidationMetadata::isLunchHour
        );
    }

    /**
     * Creates a validation rule that checks if the hour component of a time falls within the specified range.
     *
     * <p>This rule validates that the hour component (0-23) is between the minimum and maximum hours (inclusive).
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param minHour the minimum allowed hour (0-23)
     * @param maxHour the maximum allowed hour (0-23)
     * @return a ValidationRule that passes if the hour is within the specified range
     * @throws IllegalArgumentException if minHour or maxHour is not between 0-23, or if minHour > maxHour
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that event time is within evening hours
     * ValidationResult result = Validator.of(event)
     *     .property(ValidationIdentifier.ofField("eventTime"), Event::getEventTime)
     *         .validate(TimeValidationRules.hoursBetween(18, 22)) // 6 PM to 10 PM
     *         .end()
     *     .getResult();
     *
     * // Validate that work shift is within allowed hours
     * ValidationResult shiftResult = Validator.of(workShift)
     *     .property(ValidationIdentifier.ofField("startTime"), WorkShift::getStartTime)
     *         .validate(TimeValidationRules.hoursBetween(6, 22)) // 6 AM to 10 PM
     *         .end()
     *     .getResult();
     *
     * // Validate that store hours are reasonable
     * ValidationResult storeResult = Validator.of(store)
     *     .property(ValidationIdentifier.ofField("openingTime"), Store::getOpeningTime)
     *         .validate(TimeValidationRules.hoursBetween(7, 11)) // 7 AM to 11 AM
     *         .end()
     *     .property(ValidationIdentifier.ofField("closingTime"), Store::getClosingTime)
     *         .validate(TimeValidationRules.hoursBetween(17, 23)) // 5 PM to 11 PM
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> hoursBetween(final int minHour, final int maxHour) {
        if (minHour < 0 || minHour > 23) {
            throw new IllegalArgumentException("Min hour must be between 0 - 23");
        }
        if (maxHour < 0 || maxHour > 23) {
            throw new IllegalArgumentException("Max hour must be between 0 - 23");
        }
        if (minHour > maxHour) {
            throw new IllegalArgumentException("Min hour must be less than or equal to max hour");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isHoursBetween(value, minHour, maxHour),
                identifier -> TimeValidationMetadata.hoursBetween(identifier, minHour, maxHour)
        );
    }

    /**
     * Creates a validation rule that checks if the minute component of a time falls within the specified range.
     *
     * <p>This rule validates that the minute component (0-59) is between the minimum and maximum minutes (inclusive).
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param minMinute the minimum allowed minute (0-59)
     * @param maxMinute the maximum allowed minute (0-59)
     * @return a ValidationRule that passes if the minute is within the specified range
     * @throws IllegalArgumentException if minMinute or maxMinute is not between 0-59, or if minMinute > maxMinute
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that appointment times are on quarter hours
     * ValidationResult result = Validator.of(appointment)
     *     .property(ValidationIdentifier.ofField("appointmentTime"), Appointment::getAppointmentTime)
     *         .validate(TimeValidationRules.minutesBetween(0, 45)) // 00, 15, 30, 45 minutes allowed
     *         .end()
     *     .getResult();
     *
     * // Validate that meeting times are within first half of hour
     * ValidationResult meetingResult = Validator.of(meeting)
     *     .property(ValidationIdentifier.ofField("startTime"), Meeting::getStartTime)
     *         .validate(TimeValidationRules.minutesBetween(0, 30))
     *         .end()
     *     .getResult();
     *
     * // Validate that scheduled maintenance is during low-traffic period
     * ValidationResult maintenanceResult = Validator.of(maintenance)
     *     .property(ValidationIdentifier.ofField("scheduledTime"), Maintenance::getScheduledTime)
     *         .validate(TimeValidationRules.hoursBetween(2, 4)) // 2-4 AM
     *         .validate(TimeValidationRules.minutesBetween(0, 30)) // First 30 minutes of hour
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> minutesBetween(final int minMinute, final int maxMinute) {
        if (minMinute < 0 || minMinute > 59) {
            throw new IllegalArgumentException("Min minute must be between 0 - 59");
        }
        if (maxMinute < 0 || maxMinute > 59) {
            throw new IllegalArgumentException("Max minute must be between 0 - 59");
        }
        if (minMinute > maxMinute) {
            throw new IllegalArgumentException("Min minute must be less than or equal to max minute");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isMinutesBetween(value, minMinute, maxMinute),
                identifier -> TimeValidationMetadata.minutesBetween(identifier, minMinute, maxMinute)
        );
    }

    /**
     * Creates a validation rule that checks if the second component of a time falls within the specified range.
     *
     * <p>This rule validates that the second component (0-59) is between the minimum and maximum seconds (inclusive).
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param minSecond the minimum allowed second (0-59)
     * @param maxSecond the maximum allowed second (0-59)
     * @return a ValidationRule that passes if the second is within the specified range
     * @throws IllegalArgumentException if minSecond or maxSecond is not between 0-59, or if minSecond > maxSecond
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that synchronized events occur within the first 30 seconds
     * ValidationResult result = Validator.of(synchronizedEvent)
     *     .property(ValidationIdentifier.ofField("triggerTime"), SynchronizedEvent::getTriggerTime)
     *         .validate(TimeValidationRules.secondsBetween(0, 30))
     *         .end()
     *     .getResult();
     *
     * // Validate that precise timing measurements are in specific range
     * ValidationResult timingResult = Validator.of(measurement)
     *     .property(ValidationIdentifier.ofField("measurementTime"), Measurement::getMeasurementTime)
     *         .validate(TimeValidationRules.secondsBetween(15, 45))
     *         .end()
     *     .getResult();
     *
     * // Validate that automated process runs at specific seconds
     * ValidationResult processResult = Validator.of(automatedProcess)
     *     .property(ValidationIdentifier.ofField("executionTime"), AutomatedProcess::getExecutionTime)
     *         .validate(TimeValidationRules.minutesBetween(0, 0)) // Exactly at minute mark
     *         .validate(TimeValidationRules.secondsBetween(0, 5)) // Within first 5 seconds
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> secondsBetween(final int minSecond, final int maxSecond) {
        if (minSecond < 0 || minSecond > 59) {
            throw new IllegalArgumentException("Min second must be between 0 - 59");
        }
        if (maxSecond < 0 || maxSecond > 59) {
            throw new IllegalArgumentException("Max second must be between 0 - 59");
        }
        if (minSecond > maxSecond) {
            throw new IllegalArgumentException("Min second must be less than or equal to max second");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isSecondsBetween(value, minSecond, maxSecond),
                identifier -> TimeValidationMetadata.secondsBetween(identifier, minSecond, maxSecond)
        );
    }

    /**
     * Creates a validation rule that checks if a time is in the specified timezone.
     *
     * <p>This rule validates that the temporal value is in the given timezone. This validation
     * only applies to timezone-aware temporal types like ZonedDateTime and OffsetTime.
     * For timezone-unaware types like LocalTime, this validation will always fail.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param zoneId the required timezone
     * @return a ValidationRule that passes if the time is in the specified timezone
     * @throws NullPointerException if zoneId is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that meeting time is in company timezone
     * ZoneId companyZone = ZoneId.of("America/New_York");
     * ValidationResult result = Validator.of(meeting)
     *     .property(ValidationIdentifier.ofField("meetingTime"), Meeting::getMeetingTime)
     *         .validate(TimeValidationRules.inTimeZone(companyZone))
     *         .end()
     *     .getResult();
     *
     * // Validate that scheduled event is in UTC
     * ZoneId utcZone = ZoneId.of("UTC");
     * ValidationResult eventResult = Validator.of(scheduledEvent)
     *     .property(ValidationIdentifier.ofField("eventTime"), ScheduledEvent::getEventTime)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(TimeValidationRules.inTimeZone(utcZone))
     *         .end()
     *     .getResult();
     *
     * // Validate that system event is in server timezone
     * ZoneId serverZone = ZoneId.of("Europe/London");
     * ValidationResult systemResult = Validator.of(systemEvent)
     *     .property(ValidationIdentifier.ofField("timestamp"), SystemEvent::getTimestamp)
     *         .validate(TimeValidationRules.inTimeZone(serverZone))
     *         .end()
     *     .getResult();
     *
     * // Complex validation combining multiple time constraints
     * ValidationResult complexResult = Validator.of(appointment)
     *     .property(ValidationIdentifier.ofField("appointmentTime"), Appointment::getAppointmentTime)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(TimeValidationRules.isBusinessHours())
     *         .validate(TimeValidationRules.hoursBetween(9, 17))
     *         .validate(TimeValidationRules.minutesBetween(0, 45)) // Quarter hour slots
     *         .validate(TimeValidationRules.inTimeZone(ZoneId.of("America/Chicago")))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inTimeZone(final ZoneId zoneId) {
        Objects.requireNonNull(zoneId, "ZoneId must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isInTimeZone(value, zoneId),
                identifier -> TimeValidationMetadata.inTimeZone(identifier, zoneId)
        );
    }
}