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

/**
 * Utility class providing validation rules for date and time types including LocalDate, LocalDateTime,
 * and other Temporal implementations. This class offers comprehensive validation for date ranges,
 * temporal comparisons, business rules, and calendar-based constraints.
 *
 * <p>All validation rules in this class automatically skip null values, meaning they will
 * pass validation if the input is null. Use in combination with {@code CommonValidationRules.notNull()}
 * if null values should be rejected.</p>
 *
 * <p>Supported temporal types include:</p>
 * <ul>
 * <li>{@link LocalDate} - Date without time</li>
 * <li>{@link LocalDateTime} - Date and time without timezone</li>
 * <li>Other {@link Temporal} types that implement {@link Comparable}</li>
 * </ul>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationRule
 * @see DateTimeValidationMetadata
 * @see Temporal
 */
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

    /**
     * Creates a validation rule that checks if a date/time falls within the specified range (inclusive).
     *
     * <p>This rule validates that the temporal value is between the minimum and maximum dates/times,
     * including the boundary values. The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param min the minimum allowed date/time (inclusive)
     * @param max the maximum allowed date/time (inclusive)
     * @return a ValidationRule that passes if the value is within the specified range
     * @throws NullPointerException if min or max is null
     * @throws IllegalArgumentException if min is after max
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that event date is within conference period
     * LocalDate conferenceStart = LocalDate.of(2024, 6, 1);
     * LocalDate conferenceEnd = LocalDate.of(2024, 6, 15);
     * ValidationRule<LocalDate> dateRule = DateTimeValidationRules.inRange(
     *     conferenceStart, conferenceEnd
     * );
     *
     * ValidationResult result = Validator.of(event)
     *     .property(ValidationIdentifier.ofField("eventDate"), Event::getEventDate)
     *         .validate(dateRule)
     *         .end()
     *     .getResult();
     *
     * // Validate that appointment time is within business hours
     * LocalDateTime businessStart = LocalDateTime.of(2024, 12, 1, 9, 0);
     * LocalDateTime businessEnd = LocalDateTime.of(2024, 12, 1, 17, 0);
     * ValidationRule<LocalDateTime> timeRule = DateTimeValidationRules.inRange(
     *     businessStart, businessEnd
     * );
     *
     * // Validate that booking date is within available period
     * LocalDate availableFrom = LocalDate.now();
     * LocalDate availableUntil = LocalDate.now().plusMonths(6);
     * ValidationRule<LocalDate> bookingRule = DateTimeValidationRules.inRange(
     *     availableFrom, availableUntil
     * );
     * }</pre>
     */
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

    /**
     * Creates a validation rule that checks if a date/time is before the specified reference date/time.
     *
     * <p>This rule validates that the temporal value occurs strictly before the reference value.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param date the reference date/time that the value must be before
     * @return a ValidationRule that passes if the value is before the reference date/time
     * @throws NullPointerException if date is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that registration deadline is before event date
     * ValidationResult result = Validator.of(event)
     *     .property(ValidationIdentifier.ofField("registrationDeadline"), Event::getRegistrationDeadline)
     *         .validate(DateTimeValidationRules.before(event.getEventDate()))
     *         .end()
     *     .getResult();
     *
     * // Validate that birth date is before current date
     * LocalDate today = LocalDate.now();
     * ValidationRule<LocalDate> birthDateRule = DateTimeValidationRules.before(today);
     *
     * ValidationResult personResult = Validator.of(person)
     *     .property(ValidationIdentifier.ofField("birthDate"), Person::getBirthDate)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(birthDateRule)
     *         .end()
     *     .getResult();
     *
     * // Validate that start time is before end time
     * ValidationResult meetingResult = Validator.of(meeting)
     *     .property(ValidationIdentifier.ofField("startTime"), Meeting::getStartTime)
     *         .validate(DateTimeValidationRules.before(meeting.getEndTime()))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> before(final T date) {
        Objects.requireNonNull(date, "Reference date must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isBefore(value, date),
                identifier -> DateTimeValidationMetadata.before(identifier, date)
        );
    }

    /**
     * Creates a validation rule that checks if a date/time is after the specified reference date/time.
     *
     * <p>This rule validates that the temporal value occurs strictly after the reference value.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param date the reference date/time that the value must be after
     * @return a ValidationRule that passes if the value is after the reference date/time
     * @throws NullPointerException if date is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that end date is after start date
     * ValidationResult result = Validator.of(project)
     *     .property(ValidationIdentifier.ofField("endDate"), Project::getEndDate)
     *         .validate(DateTimeValidationRules.after(project.getStartDate()))
     *         .end()
     *     .getResult();
     *
     * // Validate that expiration date is after creation date
     * ValidationResult tokenResult = Validator.of(token)
     *     .property(ValidationIdentifier.ofField("expirationDate"), Token::getExpirationDate)
     *         .validate(DateTimeValidationRules.after(token.getCreationDate()))
     *         .end()
     *     .getResult();
     *
     * // Validate that delivery date is after order date
     * LocalDate minimumDeliveryDate = LocalDate.now().plusDays(1);
     * ValidationRule<LocalDate> deliveryRule = DateTimeValidationRules.after(minimumDeliveryDate);
     *
     * ValidationResult orderResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("deliveryDate"), Order::getDeliveryDate)
     *         .validate(deliveryRule)
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> after(final T date) {
        Objects.requireNonNull(date, "Reference date must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isAfter(value, date),
                identifier -> DateTimeValidationMetadata.after(identifier, date)
        );
    }

    /**
     * Creates a validation rule that checks if a date/time is before or equal to the specified reference date/time.
     *
     * <p>This rule validates that the temporal value occurs before or exactly on the reference value.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param date the reference date/time that the value must be before or equal to
     * @return a ValidationRule that passes if the value is before or equal to the reference date/time
     * @throws NullPointerException if date is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that submission date is not later than deadline
     * LocalDate submissionDeadline = LocalDate.of(2024, 12, 31);
     * ValidationRule<LocalDate> deadlineRule = DateTimeValidationRules.beforeOrEquals(submissionDeadline);
     *
     * ValidationResult result = Validator.of(application)
     *     .property(ValidationIdentifier.ofField("submissionDate"), Application::getSubmissionDate)
     *         .validate(deadlineRule)
     *         .end()
     *     .getResult();
     *
     * // Validate that checkout time is not after maximum allowed time
     * LocalDateTime maxCheckoutTime = LocalDateTime.of(2024, 6, 15, 12, 0);
     * ValidationRule<LocalDateTime> checkoutRule = DateTimeValidationRules.beforeOrEquals(maxCheckoutTime);
     *
     * // Validate that age verification date is not in the future
     * LocalDate today = LocalDate.now();
     * ValidationRule<LocalDate> verificationRule = DateTimeValidationRules.beforeOrEquals(today);
     *
     * ValidationResult verificationResult = Validator.of(document)
     *     .property(ValidationIdentifier.ofField("verificationDate"), Document::getVerificationDate)
     *         .validate(verificationRule)
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> beforeOrEquals(final T date) {
        Objects.requireNonNull(date, "Reference date must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isBeforeOrEquals(value, date),
                identifier -> DateTimeValidationMetadata.beforeOrEquals(identifier, date)
        );
    }

    /**
     * Creates a validation rule that checks if a date/time is after or equal to the specified reference date/time.
     *
     * <p>This rule validates that the temporal value occurs after or exactly on the reference value.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param date the reference date/time that the value must be after or equal to
     * @return a ValidationRule that passes if the value is after or equal to the reference date/time
     * @throws NullPointerException if date is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that event date is not before minimum notice period
     * LocalDate minimumEventDate = LocalDate.now().plusDays(7);
     * ValidationRule<LocalDate> noticeRule = DateTimeValidationRules.afterOrEquals(minimumEventDate);
     *
     * ValidationResult result = Validator.of(event)
     *     .property(ValidationIdentifier.ofField("eventDate"), Event::getEventDate)
     *         .validate(noticeRule)
     *         .end()
     *     .getResult();
     *
     * // Validate that appointment time is not before business opening
     * LocalDateTime businessOpening = LocalDateTime.of(2024, 6, 15, 9, 0);
     * ValidationRule<LocalDateTime> openingRule = DateTimeValidationRules.afterOrEquals(businessOpening);
     *
     * // Validate that contract start date is not before today
     * LocalDate today = LocalDate.now();
     * ValidationRule<LocalDate> startRule = DateTimeValidationRules.afterOrEquals(today);
     *
     * ValidationResult contractResult = Validator.of(contract)
     *     .property(ValidationIdentifier.ofField("startDate"), Contract::getStartDate)
     *         .validate(startRule)
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> afterOrEquals(final T date) {
        Objects.requireNonNull(date, "Reference date must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isAfterOrEquals(value, date),
                identifier -> DateTimeValidationMetadata.afterOrEquals(identifier, date)
        );
    }

    /**
     * Creates a validation rule that checks if a date/time is in the future.
     *
     * <p>This rule validates that the temporal value occurs after the current date/time.
     * The current time is determined when the validation is executed.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @return a ValidationRule that passes if the value is in the future
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that event date is in the future
     * ValidationResult result = Validator.of(event)
     *     .property(ValidationIdentifier.ofField("eventDate"), Event::getEventDate)
     *         .validate(DateTimeValidationRules.future())
     *         .end()
     *     .getResult();
     *
     * // Validate that expiration date is in the future
     * ValidationResult tokenResult = Validator.of(token)
     *     .property(ValidationIdentifier.ofField("expirationDate"), Token::getExpirationDate)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(DateTimeValidationRules.future())
     *         .end()
     *     .getResult();
     *
     * // Validate that appointment time is in the future
     * ValidationResult appointmentResult = Validator.of(appointment)
     *     .property(ValidationIdentifier.ofField("scheduledTime"), Appointment::getScheduledTime)
     *         .validate(DateTimeValidationRules.future())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> future() {
        return createSkipNullRule(
                ValidationFunctions::isFuture,
                DateTimeValidationMetadata::future
        );
    }

    /**
     * Creates a validation rule that checks if a date/time is in the past.
     *
     * <p>This rule validates that the temporal value occurs before the current date/time.
     * The current time is determined when the validation is executed.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @return a ValidationRule that passes if the value is in the past
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that birth date is in the past
     * ValidationResult result = Validator.of(person)
     *     .property(ValidationIdentifier.ofField("birthDate"), Person::getBirthDate)
     *         .validate(DateTimeValidationRules.past())
     *         .end()
     *     .getResult();
     *
     * // Validate that historical event date is in the past
     * ValidationResult eventResult = Validator.of(historicalEvent)
     *     .property(ValidationIdentifier.ofField("occurredDate"), HistoricalEvent::getOccurredDate)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(DateTimeValidationRules.past())
     *         .end()
     *     .getResult();
     *
     * // Validate that graduation date is in the past
     * ValidationResult educationResult = Validator.of(education)
     *     .property(ValidationIdentifier.ofField("graduationDate"), Education::getGraduationDate)
     *         .validate(DateTimeValidationRules.past())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> past() {
        return createSkipNullRule(
                ValidationFunctions::isPast,
                DateTimeValidationMetadata::past
        );
    }

    /**
     * Creates a validation rule that checks if a date/time is in the present or future.
     *
     * <p>This rule validates that the temporal value occurs on or after the current date/time.
     * The current time is determined when the validation is executed.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @return a ValidationRule that passes if the value is in the present or future
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that subscription end date is not in the past
     * ValidationResult result = Validator.of(subscription)
     *     .property(ValidationIdentifier.ofField("endDate"), Subscription::getEndDate)
     *         .validate(DateTimeValidationRules.presentOrFuture())
     *         .end()
     *     .getResult();
     *
     * // Validate that license expiration is not already expired
     * ValidationResult licenseResult = Validator.of(license)
     *     .property(ValidationIdentifier.ofField("expirationDate"), License::getExpirationDate)
     *         .validate(DateTimeValidationRules.presentOrFuture())
     *         .end()
     *     .getResult();
     *
     * // Validate that warranty end date is valid
     * ValidationResult warrantyResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("warrantyEndDate"), Product::getWarrantyEndDate)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(DateTimeValidationRules.presentOrFuture())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> presentOrFuture() {
        return createSkipNullRule(
                ValidationFunctions::isPresentOrFuture,
                DateTimeValidationMetadata::presentOrFuture
        );
    }

    /**
     * Creates a validation rule that checks if a date/time is in the present or past.
     *
     * <p>This rule validates that the temporal value occurs on or before the current date/time.
     * The current time is determined when the validation is executed.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @return a ValidationRule that passes if the value is in the present or past
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that transaction date is not in the future
     * ValidationResult result = Validator.of(transaction)
     *     .property(ValidationIdentifier.ofField("transactionDate"), Transaction::getTransactionDate)
     *         .validate(DateTimeValidationRules.presentOrPast())
     *         .end()
     *     .getResult();
     *
     * // Validate that invoice date is not future-dated
     * ValidationResult invoiceResult = Validator.of(invoice)
     *     .property(ValidationIdentifier.ofField("invoiceDate"), Invoice::getInvoiceDate)
     *         .validate(DateTimeValidationRules.presentOrPast())
     *         .end()
     *     .getResult();
     *
     * // Validate that log entry timestamp is not in the future
     * ValidationResult logResult = Validator.of(logEntry)
     *     .property(ValidationIdentifier.ofField("timestamp"), LogEntry::getTimestamp)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(DateTimeValidationRules.presentOrPast())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> presentOrPast() {
        return createSkipNullRule(
                ValidationFunctions::isPresentOrPast,
                DateTimeValidationMetadata::presentOrPast
        );
    }

    /**
     * Creates a validation rule that checks if a date/time equals the specified reference date/time.
     *
     * <p>This rule validates that the temporal value is exactly equal to the reference value.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param date the reference date/time that the value must equal
     * @return a ValidationRule that passes if the value equals the reference date/time
     * @throws NullPointerException if date is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that release date matches planned date
     * LocalDate plannedReleaseDate = LocalDate.of(2024, 12, 25);
     * ValidationRule<LocalDate> releaseRule = DateTimeValidationRules.equalsDate(plannedReleaseDate);
     *
     * ValidationResult result = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("actualReleaseDate"), Product::getActualReleaseDate)
     *         .validate(releaseRule)
     *         .end()
     *     .getResult();
     *
     * // Validate that meeting time matches scheduled time
     * LocalDateTime scheduledTime = LocalDateTime.of(2024, 6, 15, 14, 30);
     * ValidationRule<LocalDateTime> meetingRule = DateTimeValidationRules.equalsDate(scheduledTime);
     *
     * // Validate that birthday matches expected date
     * LocalDate expectedBirthday = LocalDate.of(1990, 5, 15);
     * ValidationRule<LocalDate> birthdayRule = DateTimeValidationRules.equalsDate(expectedBirthday);
     *
     * ValidationResult personResult = Validator.of(person)
     *     .property(ValidationIdentifier.ofField("birthDate"), Person::getBirthDate)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(birthdayRule)
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> equalsDate(final T date) {
        Objects.requireNonNull(date, "Reference date must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isEqualTo(value, date),
                identifier -> DateTimeValidationMetadata.equalsDate(identifier, date)
        );
    }

    /**
     * Creates a validation rule that checks if a date/time falls on a weekday.
     *
     * <p>This rule validates that the temporal value falls on Monday, Tuesday, Wednesday, Thursday, or Friday.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @return a ValidationRule that passes if the value is on a weekday
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that business meeting is scheduled on a weekday
     * ValidationResult result = Validator.of(meeting)
     *     .property(ValidationIdentifier.ofField("meetingDate"), Meeting::getMeetingDate)
     *         .validate(DateTimeValidationRules.isWeekday())
     *         .end()
     *     .getResult();
     *
     * // Validate that delivery date is on a business day
     * ValidationResult deliveryResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("deliveryDate"), Order::getDeliveryDate)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(DateTimeValidationRules.isWeekday())
     *         .end()
     *     .getResult();
     *
     * // Validate that appointment is scheduled on working days
     * ValidationResult appointmentResult = Validator.of(appointment)
     *     .property(ValidationIdentifier.ofField("appointmentDate"), Appointment::getAppointmentDate)
     *         .validate(DateTimeValidationRules.isWeekday())
     *         .validate(DateTimeValidationRules.future())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isWeekday() {
        return createSkipNullRule(
                ValidationFunctions::isWeekday,
                DateTimeValidationMetadata::isWeekday
        );
    }

    /**
     * Creates a validation rule that checks if a date/time falls on a weekend.
     *
     * <p>This rule validates that the temporal value falls on Saturday or Sunday.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @return a ValidationRule that passes if the value is on a weekend
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that personal event is scheduled on weekend
     * ValidationResult result = Validator.of(personalEvent)
     *     .property(ValidationIdentifier.ofField("eventDate"), PersonalEvent::getEventDate)
     *         .validate(DateTimeValidationRules.isWeekend())
     *         .end()
     *     .getResult();
     *
     * // Validate that leisure activity is on weekend
     * ValidationResult activityResult = Validator.of(activity)
     *     .property(ValidationIdentifier.ofField("activityDate"), Activity::getActivityDate)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(DateTimeValidationRules.isWeekend())
     *         .end()
     *     .getResult();
     *
     * // Validate that family gathering is on weekend days
     * ValidationResult gatheringResult = Validator.of(gathering)
     *     .property(ValidationIdentifier.ofField("gatheringDate"), Gathering::getGatheringDate)
     *         .validate(DateTimeValidationRules.isWeekend())
     *         .validate(DateTimeValidationRules.future())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> isWeekend() {
        return createSkipNullRule(
                ValidationFunctions::isWeekend,
                DateTimeValidationMetadata::isWeekend
        );
    }

    /**
     * Creates a validation rule that checks if a date/time falls within the specified month.
     *
     * <p>This rule validates that the temporal value occurs in the given month, regardless of year.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param month the month that the value must fall within
     * @return a ValidationRule that passes if the value is in the specified month
     * @throws NullPointerException if month is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that holiday event is in December
     * ValidationRule<LocalDate> holidayRule = DateTimeValidationRules.inMonth(Month.DECEMBER);
     *
     * ValidationResult result = Validator.of(holidayEvent)
     *     .property(ValidationIdentifier.ofField("eventDate"), HolidayEvent::getEventDate)
     *         .validate(holidayRule)
     *         .end()
     *     .getResult();
     *
     * // Validate that summer camp is during summer months
     * ValidationResult campResult = Validator.of(summerCamp)
     *     .property(ValidationIdentifier.ofField("startDate"), SummerCamp::getStartDate)
     *         .validate(DateTimeValidationRules.inMonth(Month.JUNE)
     *             .or(DateTimeValidationRules.inMonth(Month.JULY))
     *             .or(DateTimeValidationRules.inMonth(Month.AUGUST)))
     *         .end()
     *     .getResult();
     *
     * // Validate that tax filing is in appropriate month
     * ValidationRule<LocalDateTime> taxRule = DateTimeValidationRules.inMonth(Month.APRIL);
     *
     * ValidationResult taxResult = Validator.of(taxFiling)
     *     .property(ValidationIdentifier.ofField("filingDate"), TaxFiling::getFilingDate)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(taxRule)
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Temporal & Comparable<? super T>> ValidationRule<T> inMonth(final Month month) {
        Objects.requireNonNull(month, "Month must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isInMonth(value, month),
                identifier -> DateTimeValidationMetadata.inMonth(identifier, month)
        );
    }

    /**
     * Creates a validation rule that checks if a date/time falls within the specified year.
     *
     * <p>This rule validates that the temporal value occurs in the given year.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of temporal value to validate
     * @param year the year that the value must fall within (must be non-negative)
     * @return a ValidationRule that passes if the value is in the specified year
     * @throws IllegalArgumentException if year is negative
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that event is scheduled for current year
     * int currentYear = LocalDate.now().getYear();
     * ValidationRule<LocalDate> currentYearRule = DateTimeValidationRules.inYear(currentYear);
     *
     * ValidationResult result = Validator.of(event)
     *     .property(ValidationIdentifier.ofField("eventDate"), Event::getEventDate)
     *         .validate(currentYearRule)
     *         .end()
     *     .getResult();
     *
     * // Validate that historical document is from specific year
     * ValidationRule<LocalDate> historicalRule = DateTimeValidationRules.inYear(1776);
     *
     * ValidationResult documentResult = Validator.of(historicalDocument)
     *     .property(ValidationIdentifier.ofField("creationDate"), HistoricalDocument::getCreationDate)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(historicalRule)
     *         .end()
     *     .getResult();
     *
     * // Validate that financial report is for specific fiscal year
     * ValidationRule<LocalDateTime> fiscalYearRule = DateTimeValidationRules.inYear(2024);
     *
     * ValidationResult reportResult = Validator.of(financialReport)
     *     .property(ValidationIdentifier.ofField("reportDate"), FinancialReport::getReportDate)
     *         .validate(fiscalYearRule)
     *         .end()
     *     .getResult();
     *
     * // Complex validation combining multiple date rules
     * ValidationResult complexResult = Validator.of(conference)
     *     .property(ValidationIdentifier.ofField("conferenceDate"), Conference::getConferenceDate)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(DateTimeValidationRules.inYear(2024))
     *         .validate(DateTimeValidationRules.inMonth(Month.SEPTEMBER))
     *         .validate(DateTimeValidationRules.isWeekday())
     *         .validate(DateTimeValidationRules.future())
     *         .end()
     *     .getResult();
     * }</pre>
     */
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