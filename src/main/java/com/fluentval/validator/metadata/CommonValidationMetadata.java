package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Abstract base class for validation metadata related to fundamental object validation constraints.
 * This class serves as the foundation for all validation metadata types that deal with basic object
 * validation scenarios including null checking, equality validation, type validation, identity
 * validation, and custom predicate-based validation, providing essential infrastructure for
 * core validation operations that apply to all object types.
 *
 * <p>CommonValidationMetadata supports fundamental validation patterns:</p>
 * <ul>
 * <li><strong>Null validation</strong> - ensuring objects are null or non-null as required</li>
 * <li><strong>Equality validation</strong> - checking object equality and inequality using equals() method</li>
 * <li><strong>Identity validation</strong> - verifying object identity using reference equality (==)</li>
 * <li><strong>Type validation</strong> - ensuring objects are instances of specific classes or interfaces</li>
 * <li><strong>Predicate validation</strong> - applying custom validation logic through predicates</li>
 * </ul>
 *
 * <p>These validations form the cornerstone of most validation frameworks, providing the basic
 * building blocks upon which more complex validation scenarios are constructed. Each implementation
 * handles specific aspects of object validation while maintaining consistent error messaging and
 * metadata structure for comprehensive validation reporting.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationMetadata
 * @see DefaultValidationCode
 */
public abstract class CommonValidationMetadata extends ValidationMetadata {

    /**
     * Protected constructor for CommonValidationMetadata.
     *
     * <p>This constructor initializes the base metadata with the validation identifier and
     * automatically adds the field identifier to the message parameters for error message
     * template substitution in validation failure scenarios.</p>
     *
     * @param identifier the validation identifier specifying what failed validation
     * @param code the default validation code categorizing the specific common validation type
     */
    protected CommonValidationMetadata(ValidationIdentifier identifier,
                                       DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    /**
     * Validation metadata for non-null object constraints.
     *
     * <p>This class represents validation failures where an object must not be null.
     * It is the most fundamental validation type, ensuring that required objects have
     * actual values rather than null references, which is essential for preventing
     * NullPointerExceptions and ensuring data integrity.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating required form fields and user inputs</li>
     * <li>Ensuring mandatory configuration parameters are provided</li>
     * <li>Checking that dependency injection has properly initialized objects</li>
     * <li>Verifying that method parameters are not null before processing</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies solely on null/non-null
     * checking and inherits field identifier from the parent class for error messaging.</li>
     * </ul>
     */
    public static final class NotNull extends CommonValidationMetadata {

        private NotNull(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_NULL);
            // Field parameter is already added in the parent constructor
        }
    }

    /**
     * Validation metadata for null object constraints.
     *
     * <p>This class represents validation failures where an object must be null.
     * This validation is used in scenarios where certain fields should remain
     * uninitialized or be explicitly cleared under specific conditions, often
     * in state management or conditional validation scenarios.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that optional fields remain unset in certain states</li>
     * <li>Ensuring exclusive field relationships (one field set means another must be null)</li>
     * <li>Checking that temporary or intermediate values are properly cleared</li>
     * <li>Verifying that objects are properly reset or deinitialized</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies solely on null/non-null
     * checking and inherits field identifier from the parent class for error messaging.</li>
     * </ul>
     */
    public static final class MustBeNull extends CommonValidationMetadata {

        private MustBeNull(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.MUST_BE_NULL);
            // Field parameter is already added in the parent constructor
        }
    }

    /**
     * Validation metadata for object equality constraints.
     *
     * <p>This class represents validation failures where an object must be equal to a
     * specific target object using the equals() method for comparison. This validation
     * ensures that objects have expected values and is fundamental for value validation
     * and business rule enforcement.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that user inputs match expected values</li>
     * <li>Ensuring configuration values are set to specific required settings</li>
     * <li>Checking that calculated results match expected outcomes</li>
     * <li>Verifying that object states match required conditions</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>targetObject</strong> - {@code T} - The object that the validated value
     * must be equal to for validation to pass. Equality is determined using the equals()
     * method, so proper equals() implementation is essential. Can be null if the validated
     * object must equal null. The toString() representation is used in error messages.</li>
     * </ul>
     *
     * @param <T> the type of object being validated for equality
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class IsEqual<T> extends CommonValidationMetadata {
        private final T targetObject;

        private IsEqual(ValidationIdentifier identifier, T targetObject) {
            super(identifier, DefaultValidationCode.IS_EQUAL);
            this.targetObject = targetObject;

            // Add message parameter for the target value
            addMessageParameter(MessageParameter.VALUE, targetObject != null ? targetObject.toString() : "null");
        }
    }

    /**
     * Validation metadata for object inequality constraints.
     *
     * <p>This class represents validation failures where an object must not be equal to a
     * specific target object using the equals() method for comparison. This validation
     * ensures that objects do not have prohibited values and helps enforce business rules
     * that exclude certain values or states.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring user inputs don't match prohibited or reserved values</li>
     * <li>Validating that new values differ from current values (change detection)</li>
     * <li>Checking that objects don't equal default or placeholder values</li>
     * <li>Verifying that calculated results don't match error conditions</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>targetObject</strong> - {@code T} - The object that the validated value
     * must not be equal to for validation to pass. Inequality is determined using the
     * equals() method negation. Cannot be null as per factory method validation, ensuring
     * clear identification of prohibited values. The toString() representation is used
     * in error messages.</li>
     * </ul>
     *
     * @param <T> the type of object being validated for inequality
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NotEqual<T> extends CommonValidationMetadata {
        private final T targetObject;

        private NotEqual(ValidationIdentifier identifier, T targetObject) {
            super(identifier, DefaultValidationCode.IS_NOT_EQUAL);
            this.targetObject = targetObject;

            // Add message parameter for the target value
            addMessageParameter(MessageParameter.VALUE, targetObject != null ? targetObject.toString() : "null");
        }
    }

    /**
     * Validation metadata for custom predicate-based constraints.
     *
     * <p>This class represents validation failures where an object must satisfy a custom
     * predicate condition. This is the most flexible validation type, allowing for
     * arbitrary validation logic while maintaining consistent error reporting and
     * metadata structure. It enables complex business rules and custom validation
     * scenarios that don't fit standard validation patterns.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Implementing complex business rules that require custom logic</li>
     * <li>Validating against dynamic or computed conditions</li>
     * <li>Creating composite validations that combine multiple checks</li>
     * <li>Applying domain-specific validation rules unique to business context</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>predicate</strong> - {@code Predicate<T>} - The predicate function that
     * defines the custom validation condition. This predicate is applied to the validated
     * object and must return true for validation to pass. Must not be null and should be
     * designed to handle the expected object types safely.</li>
     * <li><strong>predicateDescription</strong> - {@code String} - Human-readable description
     * of the predicate condition used in error messages to explain what requirement the
     * object failed to meet. Must not be null or blank, providing clear feedback to users
     * about validation failures.</li>
     * </ul>
     *
     * @param <T> the type of object being validated by the predicate
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Satisfies<T> extends CommonValidationMetadata {
        private final Predicate<T> predicate;
        private final String predicateDescription;

        private Satisfies(ValidationIdentifier identifier, Predicate<T> predicate, String predicateDescription) {
            super(identifier, DefaultValidationCode.SATISFIES);
            this.predicate = predicate;
            this.predicateDescription = predicateDescription;

            // Add message parameter for the predicate description
            addMessageParameter(MessageParameter.CONDITION, predicateDescription);
        }
    }

    /**
     * Validation metadata for positive type instance constraints.
     *
     * <p>This class represents validation failures where an object must be an instance of
     * a specific class or interface. This validation ensures type safety and is essential
     * for scenarios where objects must conform to specific type contracts or implement
     * required interfaces for proper processing.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that objects implement required interfaces before casting</li>
     * <li>Ensuring that configuration objects are of expected types</li>
     * <li>Checking that deserialized objects maintain proper type relationships</li>
     * <li>Verifying that dependency injection provides objects of correct types</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>targetClass</strong> - {@code Class<?>} - The class or interface that
     * the validated object must be an instance of for validation to pass. Uses the
     * Class.isInstance() method for type checking, which handles inheritance and interface
     * implementation correctly. Must not be null as per factory method validation. The
     * simple class name is used in error messages for readability.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class InstanceOf extends CommonValidationMetadata {
        private final Class<?> targetClass;

        private InstanceOf(ValidationIdentifier identifier, Class<?> targetClass) {
            super(identifier, DefaultValidationCode.IS_INSTANCE_OF);
            this.targetClass = targetClass;

            // Add message parameter for the class name
            addMessageParameter(MessageParameter.CLASS_NAME, targetClass.getSimpleName());
        }
    }

    /**
     * Validation metadata for negative type instance constraints.
     *
     * <p>This class represents validation failures where an object must not be an instance
     * of a specific class or interface. This validation is used to ensure that objects
     * do not belong to prohibited types or implement interfaces that would cause
     * processing issues or security concerns.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Preventing certain object types from being processed in specific contexts</li>
     * <li>Ensuring that objects don't implement deprecated or dangerous interfaces</li>
     * <li>Validating that objects are not instances of legacy or incompatible types</li>
     * <li>Checking that objects don't belong to restricted or internal implementation classes</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>targetClass</strong> - {@code Class<?>} - The class or interface that
     * the validated object must not be an instance of for validation to pass. Uses the
     * Class.isInstance() method negation for type checking. Must not be null as per
     * factory method validation, ensuring clear identification of prohibited types. The
     * simple class name is used in error messages for readability.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NotInstanceOf extends CommonValidationMetadata {
        private final Class<?> targetClass;

        private NotInstanceOf(ValidationIdentifier identifier, Class<?> targetClass) {
            super(identifier, DefaultValidationCode.IS_NOT_INSTANCE_OF);
            this.targetClass = targetClass;

            // Add message parameter for the class name
            addMessageParameter(MessageParameter.CLASS_NAME, targetClass.getSimpleName());
        }
    }

    /**
     * Validation metadata for positive object identity constraints.
     *
     * <p>This class represents validation failures where an object must be the exact same
     * instance as a target object using reference equality (== operator). Unlike equality
     * validation which uses equals(), this validation checks object identity, ensuring
     * that the validated object is literally the same object reference.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that singleton objects maintain their identity</li>
     * <li>Ensuring that shared references point to the same object instance</li>
     * <li>Checking that caching or pooling returns identical object references</li>
     * <li>Verifying that object passing maintains reference integrity</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>targetObject</strong> - {@code T} - The exact object reference that the
     * validated object must be identical to for validation to pass. Identity is determined
     * using the == operator, not equals(). Can be null if the validated object must be
     * the same null reference. The toString() representation is used in error messages,
     * though identity comparisons are more about reference than value.</li>
     * </ul>
     *
     * @param <T> the type of object being validated for identity
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class SameAs<T> extends CommonValidationMetadata {
        private final T targetObject;

        private SameAs(ValidationIdentifier identifier, T targetObject) {
            super(identifier, DefaultValidationCode.IS_SAME_AS);
            this.targetObject = targetObject;

            // Add reference parameter
            addMessageParameter(MessageParameter.REFERENCE, String.valueOf(targetObject));
        }
    }

    /**
     * Validation metadata for negative object identity constraints.
     *
     * <p>This class represents validation failures where an object must not be the exact
     * same instance as a target object using reference equality (== operator). This
     * validation ensures that objects are different instances, which is important for
     * scenarios where object identity matters for processing or security reasons.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring that objects are properly copied rather than shared references</li>
     * <li>Validating that object creation produces new instances rather than returning cached ones</li>
     * <li>Checking that mutable objects aren't accidentally shared between contexts</li>
     * <li>Verifying that defensive copying has created separate object instances</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>targetObject</strong> - {@code T} - The object reference that the
     * validated object must not be identical to for validation to pass. Identity difference
     * is determined using the != operator. Can be null, allowing validation that objects
     * are not the same null reference. The toString() representation is used in error
     * messages for reference identification.</li>
     * </ul>
     *
     * @param <T> the type of object being validated for identity difference
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NotSameAs<T> extends CommonValidationMetadata {
        private final T targetObject;

        private NotSameAs(ValidationIdentifier identifier, T targetObject) {
            super(identifier, DefaultValidationCode.IS_NOT_SAME_AS);
            this.targetObject = targetObject;

            // Add reference parameter
            addMessageParameter(MessageParameter.REFERENCE, String.valueOf(targetObject));
        }
    }

    // Factory methods

    /**
     * Factory method for creating NotNull validation metadata.
     *
     * <p>Creates metadata for validating that an object is not null.</p>
     *
     * @param identifier the validation identifier
     * @return NotNull metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static NotNull notNull(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new NotNull(identifier);
    }

    /**
     * Factory method for creating MustBeNull validation metadata.
     *
     * <p>Creates metadata for validating that an object must be null.</p>
     *
     * @param identifier the validation identifier
     * @return MustBeNull metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static MustBeNull mustBeNull(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new MustBeNull(identifier);
    }

    /**
     * Factory method for creating IsEqual validation metadata.
     *
     * <p>Creates metadata for validating that an object equals the specified value
     * using the equals() method for comparison.</p>
     *
     * @param <T> the type of object being validated for equality
     * @param identifier the validation identifier
     * @param value the target value that the object must equal (can be null)
     * @return IsEqual metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static <T> IsEqual<T> isEqual(ValidationIdentifier identifier, T value) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new IsEqual<>(identifier, value);
    }

    /**
     * Factory method for creating NotEqual validation metadata.
     *
     * <p>Creates metadata for validating that an object does not equal the specified value
     * using the equals() method for comparison.</p>
     *
     * @param <T> the type of object being validated for inequality
     * @param identifier the validation identifier
     * @param value the target value that the object must not equal (can be null)
     * @return NotEqual metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static <T> NotEqual<T> notEqual(ValidationIdentifier identifier, T value) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new NotEqual<>(identifier, value);
    }

    /**
     * Factory method for creating Satisfies validation metadata.
     *
     * <p>Creates metadata for validating that an object satisfies a custom predicate condition.</p>
     *
     * @param <T> the type of object being validated by the predicate
     * @param identifier the validation identifier
     * @param predicate the predicate function that defines the validation condition
     * @param predicateDescription human-readable description of the condition for error messages
     * @return Satisfies metadata instance
     * @throws NullPointerException if identifier, predicate, or predicateDescription is null
     * @throws IllegalArgumentException if predicateDescription is blank
     */
    public static <T> Satisfies<T> satisfies(ValidationIdentifier identifier,
                                             Predicate<T> predicate,
                                             String predicateDescription) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        Objects.requireNonNull(predicateDescription, "Predicate description cannot be null");

        if (predicateDescription.isBlank()) {
            throw new IllegalArgumentException("Predicate description cannot be blank");
        }

        return new Satisfies<>(identifier, predicate, predicateDescription);
    }

    /**
     * Factory method for creating InstanceOf validation metadata.
     *
     * <p>Creates metadata for validating that an object is an instance of the specified class or interface.</p>
     *
     * @param identifier the validation identifier
     * @param targetClass the class or interface that the object must be an instance of
     * @return InstanceOf metadata instance
     * @throws NullPointerException if identifier or targetClass is null
     */
    public static InstanceOf instanceOf(ValidationIdentifier identifier, Class<?> targetClass) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(targetClass, "Target class cannot be null");
        return new InstanceOf(identifier, targetClass);
    }

    /**
     * Factory method for creating NotInstanceOf validation metadata.
     *
     * <p>Creates metadata for validating that an object is not an instance of the specified class or interface.</p>
     *
     * @param identifier the validation identifier
     * @param targetClass the class or interface that the object must not be an instance of
     * @return NotInstanceOf metadata instance
     * @throws NullPointerException if identifier or targetClass is null
     */
    public static NotInstanceOf notInstanceOf(ValidationIdentifier identifier, Class<?> targetClass) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(targetClass, "Target class cannot be null");
        return new NotInstanceOf(identifier, targetClass);
    }

    /**
     * Factory method for creating SameAs validation metadata.
     *
     * <p>Creates metadata for validating that an object is the exact same instance as the
     * specified reference object using identity comparison (== operator).</p>
     *
     * @param <T> the type of object being validated for identity
     * @param identifier the validation identifier
     * @param reference the target object reference that must be identical (can be null)
     * @return SameAs metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static <T> SameAs<T> sameAs(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new SameAs<>(identifier, reference);
    }

    /**
     * Factory method for creating NotSameAs validation metadata.
     *
     * <p>Creates metadata for validating that an object is not the exact same instance as the
     * specified reference object using identity comparison (!= operator).</p>
     *
     * @param <T> the type of object being validated for identity difference
     * @param identifier the validation identifier
     * @param reference the target object reference that must not be identical (can be null)
     * @return NotSameAs metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static <T> NotSameAs<T> notSameAs(ValidationIdentifier identifier, T reference) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new NotSameAs<>(identifier, reference);
    }
}