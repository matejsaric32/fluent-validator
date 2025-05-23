package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.CommonValidationMetadata;

import java.util.function.Predicate;

import static com.fluentval.validator.rule.ValidationRuleUtils.createRule;
import static com.fluentval.validator.rule.ValidationRuleUtils.createSkipNullRule;

/**
 * Utility class providing fundamental validation rules for common validation scenarios.
 * This class offers essential validation operations including null checks, equality comparisons,
 * type checking, and custom predicate validation.
 *
 * <p>This class contains validation rules that are commonly used across different types of objects
 * and form the foundation for more complex validation scenarios. Most validation chains will
 * use at least one rule from this class.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationRule
 * @see CommonValidationMetadata
 */
public final class CommonValidationRules {

    private CommonValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static boolean isNull(final Object value) {
            return value == null;
        }

        static boolean isNotNull(final Object value) {
            return value != null;
        }

        static <T> boolean isEqual(final T value, final T target) {
            return value != null && value.equals(target);
        }

        static <T> boolean isNotEqual(final T value, final T target) {
            return value != null && !value.equals(target);
        }

        static <T> boolean satisfiesCondition(final T value, final Predicate<T> predicate) {
            return value != null && predicate.test(value);
        }

        static boolean isInstanceOf(final Object value, final Class<?> clazz) {
            return value != null && clazz.isInstance(value);
        }

        static boolean isNotInstanceOf(final Object value, final Class<?> clazz) {
            return value != null && !clazz.isInstance(value);
        }

        static boolean isSameObject(final Object value, final Object target) {
            return value != null && value == target;
        }

        static boolean isNotSameObject(final Object value, final Object target) {
            return value != null && value != target;
        }
    }

    /**
     * Creates a validation rule that checks if a value is not null.
     *
     * <p>This is one of the most fundamental validation rules, ensuring that required
     * values are present. Unlike other rules in this class, this rule does NOT skip null values
     * as its purpose is specifically to validate the presence of a value.</p>
     *
     * @param <T> the type of value to validate
     * @return a ValidationRule that passes if the value is not null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate required user properties
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("name"), User::getName)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         .end()
     *     .getResult();
     *
     * // Validate required configuration objects
     * ValidationResult configResult = Validator.of(application)
     *     .property(ValidationIdentifier.ofField("database"), Application::getDatabase)
     *         .validate(CommonValidationRules.notNull())
     *         .end()
     *     .property(ValidationIdentifier.ofField("security"), Application::getSecurity)
     *         .validate(CommonValidationRules.notNull())
     *         .end()
     *     .getResult();
     *
     * // Validate required business objects
     * ValidationResult orderResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("customer"), Order::getCustomer)
     *         .validate(CommonValidationRules.notNull())
     *         .end()
     *     .property(ValidationIdentifier.ofField("items"), Order::getItems)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(CollectionValidationRules.notEmpty())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T> ValidationRule<T> notNull() {
        return createRule(
                ValidationFunctions::isNotNull,
                CommonValidationMetadata::notNull
        );
    }

    /**
     * Creates a validation rule that checks if a value is null.
     *
     * <p>This rule validates that a value is explicitly null, which can be useful
     * for ensuring optional fields remain unset or for validating state transitions
     * where certain values should be cleared.</p>
     *
     * @param <T> the type of value to validate
     * @return a ValidationRule that passes if the value is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that temporary fields are cleared
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("temporaryToken"), User::getTemporaryToken)
     *         .validate(CommonValidationRules.mustBeNull())
     *         .end()
     *     .property(ValidationIdentifier.ofField("resetCode"), User::getResetCode)
     *         .validate(CommonValidationRules.mustBeNull())
     *         .end()
     *     .getResult();
     *
     * // Validate state transitions
     * ValidationResult stateResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("cancelReason"), Order::getCancelReason)
     *         .validateWhen(o -> order.getStatus() != OrderStatus.CANCELLED,
     *                      CommonValidationRules.mustBeNull())
     *         .end()
     *     .getResult();
     *
     * // Validate that computed fields are not manually set
     * ValidationResult computedResult = Validator.of(invoice)
     *     .property(ValidationIdentifier.ofField("calculatedTotal"), Invoice::getCalculatedTotal)
     *         .validate(CommonValidationRules.mustBeNull()) // Should be computed, not set
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T> ValidationRule<T> mustBeNull() {
        return createRule(
                ValidationFunctions::isNull,
                CommonValidationMetadata::mustBeNull
        );
    }

    /**
     * Creates a validation rule that checks if a value equals the specified object.
     *
     * <p>This rule validates that the value is equal to the reference object using the
     * {@code equals()} method. The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of value to validate
     * @param object the reference object to compare against
     * @return a ValidationRule that passes if the value equals the reference object
     * @throws IllegalArgumentException if object is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate password confirmation
     * String password = user.getPassword();
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("confirmPassword"), User::getConfirmPassword)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(CommonValidationRules.isEqual(password))
     *         .end()
     *     .getResult();
     *
     * // Validate agreement acceptance
     * ValidationResult agreementResult = Validator.of(registration)
     *     .property(ValidationIdentifier.ofField("termsAccepted"), Registration::getTermsAccepted)
     *         .validate(CommonValidationRules.isEqual(Boolean.TRUE))
     *         .end()
     *     .property(ValidationIdentifier.ofField("privacyAccepted"), Registration::getPrivacyAccepted)
     *         .validate(CommonValidationRules.isEqual(Boolean.TRUE))
     *         .end()
     *     .getResult();
     *
     * // Validate expected status
     * ValidationResult statusResult = Validator.of(task)
     *     .property(ValidationIdentifier.ofField("status"), Task::getStatus)
     *         .validate(CommonValidationRules.isEqual(TaskStatus.COMPLETED))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T> ValidationRule<T> isEqual(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("Reference object cannot be null");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isEqual(value, object),
                identifier -> CommonValidationMetadata.isEqual(identifier, object)
        );
    }

    /**
     * Creates a validation rule that checks if a value does not equal the specified object.
     *
     * <p>This rule validates that the value is not equal to the reference object using the
     * {@code equals()} method. The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of value to validate
     * @param object the reference object to compare against
     * @return a ValidationRule that passes if the value does not equal the reference object
     * @throws IllegalArgumentException if object is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that new password is different from current
     * String currentPassword = user.getCurrentPassword();
     * ValidationResult result = Validator.of(passwordChange)
     *     .property(ValidationIdentifier.ofField("newPassword"), PasswordChange::getNewPassword)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(CommonValidationRules.isNotEqual(currentPassword))
     *         .end()
     *     .getResult();
     *
     * // Validate that status has changed
     * TaskStatus previousStatus = task.getPreviousStatus();
     * ValidationResult statusResult = Validator.of(task)
     *     .property(ValidationIdentifier.ofField("currentStatus"), Task::getCurrentStatus)
     *         .validate(CommonValidationRules.isNotEqual(previousStatus))
     *         .end()
     *     .getResult();
     *
     * // Validate that username is not a forbidden value
     * ValidationResult usernameResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
     *         .validate(CommonValidationRules.isNotEqual("admin"))
     *         .validate(CommonValidationRules.isNotEqual("root"))
     *         .validate(CommonValidationRules.isNotEqual("system"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T> ValidationRule<T> isNotEqual(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("Reference object cannot be null");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isNotEqual(value, object),
                identifier -> CommonValidationMetadata.notEqual(identifier, object)
        );
    }

    /**
     * Creates a validation rule that checks if a value satisfies a custom predicate condition.
     *
     * <p>This rule provides maximum flexibility for custom validation logic by accepting
     * a predicate function and a descriptive message. The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of value to validate
     * @param predicate the condition that the value must satisfy
     * @param message a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if the predicate returns true for the value
     * @throws IllegalArgumentException if predicate is null or message is null/blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate business rules
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("age"), User::getAge)
     *         .validate(CommonValidationRules.satisfies(
     *             age -> age >= 18,
     *             "be at least 18 years old"
     *         ))
     *         .end()
     *     .property(ValidationIdentifier.ofField("creditScore"), User::getCreditScore)
     *         .validate(CommonValidationRules.satisfies(
     *             score -> score >= 650,
     *             "have a credit score of at least 650"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate complex date conditions
     * LocalDate today = LocalDate.now();
     * ValidationResult dateResult = Validator.of(event)
     *     .property(ValidationIdentifier.ofField("startDate"), Event::getStartDate)
     *         .validate(CommonValidationRules.satisfies(
     *             date -> date.isAfter(today),
     *             "be in the future"
     *         ))
     *         .end()
     *     .property(ValidationIdentifier.ofField("endDate"), Event::getEndDate)
     *         .validate(CommonValidationRules.satisfies(
     *             date -> date.isAfter(event.getStartDate()),
     *             "be after the start date"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate custom business logic
     * ValidationResult businessResult = Validator.of(loan)
     *     .property(ValidationIdentifier.ofField("amount"), Loan::getAmount)
     *         .validate(CommonValidationRules.satisfies(
     *             amount -> amount.compareTo(calculateMaxLoanAmount(loan.getApplicant())) <= 0,
     *             "not exceed the maximum allowed loan amount"
     *         ))
     *         .end()
     *     .property(ValidationIdentifier.ofField("interestRate"), Loan::getInterestRate)
     *         .validate(CommonValidationRules.satisfies(
     *             rate -> rate.compareTo(getMinimumRate()) >= 0,
     *             "meet the minimum interest rate requirement"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T> ValidationRule<T> satisfies(final Predicate<T> predicate, final String message) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Predicate description cannot be null or empty");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.satisfiesCondition(value, predicate),
                identifier -> CommonValidationMetadata.satisfies(identifier, predicate, message)
        );
    }

    /**
     * Creates a validation rule that checks if a value is an instance of the specified class.
     *
     * <p>This rule validates the runtime type of an object, useful for polymorphic scenarios
     * or when working with generic types. The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of value to validate
     * @param clazz the class that the value must be an instance of
     * @return a ValidationRule that passes if the value is an instance of the specified class
     * @throws IllegalArgumentException if clazz is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate polymorphic types
     * ValidationResult result = Validator.of(shape)
     *     .property(ValidationIdentifier.ofField("shape"), Container::getShape)
     *         .validate(CommonValidationRules.isInstanceOf(Circle.class))
     *         .end()
     *     .getResult();
     *
     * // Validate collection element types
     * ValidationResult collectionResult = Validator.of(document)
     *     .property(ValidationIdentifier.ofField("content"), Document::getContent)
     *         .validate(CommonValidationRules.isInstanceOf(String.class))
     *         .end()
     *     .property(ValidationIdentifier.ofField("metadata"), Document::getMetadata)
     *         .validate(CommonValidationRules.isInstanceOf(Map.class))
     *         .end()
     *     .getResult();
     *
     * // Validate API response types
     * ValidationResult apiResult = Validator.of(apiResponse)
     *     .property(ValidationIdentifier.ofField("data"), ApiResponse::getData)
     *         .validate(CommonValidationRules.isInstanceOf(JsonObject.class))
     *         .end()
     *     .property(ValidationIdentifier.ofField("error"), ApiResponse::getError)
     *         .validateWhen(response -> !response.isSuccess(),
     *                      CommonValidationRules.isInstanceOf(ErrorDetails.class))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T> ValidationRule<T> isInstanceOf(final Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isInstanceOf(value, clazz),
                identifier -> CommonValidationMetadata.instanceOf(identifier, clazz)
        );
    }

    /**
     * Creates a validation rule that checks if a value is not an instance of the specified class.
     *
     * <p>This rule validates that an object is not of a specific runtime type, useful for
     * excluding certain types or ensuring type safety. The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of value to validate
     * @param clazz the class that the value must not be an instance of
     * @return a ValidationRule that passes if the value is not an instance of the specified class
     * @throws IllegalArgumentException if clazz is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that input is not a dangerous type
     * ValidationResult result = Validator.of(userInput)
     *     .property(ValidationIdentifier.ofField("data"), UserInput::getData)
     *         .validate(CommonValidationRules.isNotInstanceOf(Script.class))
     *         .validate(CommonValidationRules.isNotInstanceOf(Executable.class))
     *         .end()
     *     .getResult();
     *
     * // Validate configuration types
     * ValidationResult configResult = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("value"), Config::getValue)
     *         .validate(CommonValidationRules.isNotInstanceOf(Function.class))
     *         .validate(CommonValidationRules.isNotInstanceOf(Runnable.class))
     *         .end()
     *     .getResult();
     *
     * // Validate serialization safety
     * ValidationResult serializationResult = Validator.of(payload)
     *     .property(ValidationIdentifier.ofField("content"), Payload::getContent)
     *         .validate(CommonValidationRules.isNotInstanceOf(Thread.class))
     *         .validate(CommonValidationRules.isNotInstanceOf(Process.class))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T> ValidationRule<T> isNotInstanceOf(final Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isNotInstanceOf(value, clazz),
                identifier -> CommonValidationMetadata.notInstanceOf(identifier, clazz)
        );
    }

    /**
     * Creates a validation rule that checks if a value is the same object reference as the specified object.
     *
     * <p>This rule validates object identity (reference equality) using the {@code ==} operator,
     * not value equality. This is useful for ensuring specific object instances are used.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of value to validate
     * @param object the reference object to compare against
     * @return a ValidationRule that passes if the value is the same object reference
     * @throws IllegalArgumentException if object is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate singleton instances
     * DatabaseConnection expectedConnection = DatabaseConnection.getInstance();
     * ValidationResult result = Validator.of(service)
     *     .property(ValidationIdentifier.ofField("connection"), Service::getConnection)
     *         .validate(CommonValidationRules.isSameAs(expectedConnection))
     *         .end()
     *     .getResult();
     *
     * // Validate configuration instances
     * AppConfig globalConfig = AppConfig.getGlobalInstance();
     * ValidationResult configResult = Validator.of(module)
     *     .property(ValidationIdentifier.ofField("config"), Module::getConfig)
     *         .validate(CommonValidationRules.isSameAs(globalConfig))
     *         .end()
     *     .getResult();
     *
     * // Validate shared resources
     * ExecutorService sharedExecutor = getSharedExecutorService();
     * ValidationResult executorResult = Validator.of(taskRunner)
     *     .property(ValidationIdentifier.ofField("executor"), TaskRunner::getExecutor)
     *         .validate(CommonValidationRules.isSameAs(sharedExecutor))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T> ValidationRule<T> isSameAs(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("Reference object cannot be null");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isSameObject(value, object),
                identifier -> CommonValidationMetadata.sameAs(identifier, object)
        );
    }

    /**
     * Creates a validation rule that checks if a value is not the same object reference as the specified object.
     *
     * <p>This rule validates that objects are different references using the {@code !=} operator,
     * not value inequality. This is useful for ensuring object isolation or preventing
     * unintended reference sharing. The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of value to validate
     * @param object the reference object to compare against
     * @return a ValidationRule that passes if the value is not the same object reference
     * @throws IllegalArgumentException if object is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate object isolation
     * List<String> originalList = getOriginalList();
     * ValidationResult result = Validator.of(processor)
     *     .property(ValidationIdentifier.ofField("workingList"), Processor::getWorkingList)
     *         .validate(CommonValidationRules.isNotSameAs(originalList))
     *         .end()
     *     .getResult();
     *
     * // Validate defensive copying
     * Map<String, Object> sourceMap = getSourceData();
     * ValidationResult copyResult = Validator.of(dataHandler)
     *     .property(ValidationIdentifier.ofField("dataMap"), DataHandler::getDataMap)
     *         .validate(CommonValidationRules.isNotSameAs(sourceMap))
     *         .end()
     *     .getResult();
     *
     * // Validate thread safety
     * StringBuilder sharedBuilder = getSharedStringBuilder();
     * ValidationResult threadResult = Validator.of(worker)
     *     .property(ValidationIdentifier.ofField("builder"), Worker::getBuilder)
     *         .validate(CommonValidationRules.isNotSameAs(sharedBuilder))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T> ValidationRule<T> isNotSameAs(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("Reference object cannot be null");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isNotSameObject(value, object),
                identifier -> CommonValidationMetadata.notSameAs(identifier, object)
        );
    }
}