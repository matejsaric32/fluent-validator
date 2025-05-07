package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class CommonValidationMetadata extends ValidationMetadata {

    protected CommonValidationMetadata(ValidationIdentifier identifier,
                                       DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    public static final class NotNull extends CommonValidationMetadata {
        public NotNull(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_NULL);
            // Field parameter is already added in the parent constructor
        }
    }

    public static final class MustBeNull extends CommonValidationMetadata {
        public MustBeNull(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.MUST_BE_NULL);
            // Field parameter is already added in the parent constructor
        }
    }

    @Getter
    public static final class Equal<T> extends CommonValidationMetadata {
        private final T targetObject;

        public Equal(ValidationIdentifier identifier, T targetObject) {
            super(identifier, DefaultValidationCode.IS_EQUAL);
            this.targetObject = targetObject;

            // Add message parameter for the target value
            addMessageParameter(MessageParameter.VALUE, targetObject != null ? targetObject.toString() : "null");
        }
    }

    @Getter
    public static final class NotEqual<T> extends CommonValidationMetadata {
        private final T targetObject;

        public NotEqual(ValidationIdentifier identifier, T targetObject) {
            super(identifier, DefaultValidationCode.IS_NOT_EQUAL);
            this.targetObject = targetObject;

            // Add message parameter for the target value
            addMessageParameter(MessageParameter.VALUE, targetObject != null ? targetObject.toString() : "null");
        }
    }

    @Getter
    public static final class Satisfies<T> extends CommonValidationMetadata {
        private final Predicate<T> predicate;
        private final String predicateDescription;

        public Satisfies(ValidationIdentifier identifier, Predicate<T> predicate, String predicateDescription) {
            super(identifier, DefaultValidationCode.SATISFIES);
            this.predicate = Objects.requireNonNull(predicate, "Predicate must not be null");
            this.predicateDescription = predicateDescription;

            // Add message parameter for the predicate description
            addMessageParameter(MessageParameter.CONDITION, predicateDescription);
        }
    }

    @Getter
    public static final class InstanceOf extends CommonValidationMetadata {
        private final Class<?> targetClass;

        public InstanceOf(ValidationIdentifier identifier, Class<?> targetClass) {
            super(identifier, DefaultValidationCode.IS_INSTANCE_OF);
            this.targetClass = Objects.requireNonNull(targetClass, "Class must not be null");

            // Add message parameter for the class name
            addMessageParameter(MessageParameter.CLASS_NAME, targetClass.getSimpleName());
        }
    }

    @Getter
    public static final class NotInstanceOf extends CommonValidationMetadata {
        private final Class<?> targetClass;

        public NotInstanceOf(ValidationIdentifier identifier, Class<?> targetClass) {
            super(identifier, DefaultValidationCode.IS_NOT_INSTANCE_OF);
            this.targetClass = Objects.requireNonNull(targetClass, "Class must not be null");

            // Add message parameter for the class name
            addMessageParameter(MessageParameter.CLASS_NAME, targetClass.getSimpleName());
        }
    }

    @Getter
    public static final class SameAs<T> extends CommonValidationMetadata {
        private final T targetObject;

        public SameAs(ValidationIdentifier identifier, T targetObject) {
            super(identifier, DefaultValidationCode.IS_SAME_AS);
            this.targetObject = targetObject;

            // Add reference parameter
            addMessageParameter(MessageParameter.REFERENCE, String.valueOf(targetObject));
        }
    }

    @Getter
    public static final class NotSameAs<T> extends CommonValidationMetadata {
        private final T targetObject;

        public NotSameAs(ValidationIdentifier identifier, T targetObject) {
            super(identifier, DefaultValidationCode.IS_NOT_SAME_AS);
            this.targetObject = targetObject;

            // Add reference parameter
            addMessageParameter(MessageParameter.REFERENCE, String.valueOf(targetObject));
        }
    }

    // Factory methods
    public static NotNull notNull(ValidationIdentifier identifier) {
        return new NotNull(identifier);
    }

    public static MustBeNull mustBeNull(ValidationIdentifier identifier) {
        return new MustBeNull(identifier);
    }

    public static <T> Equal<T> equal(ValidationIdentifier identifier, T value) {
        return new Equal<>(identifier, value);
    }

    public static <T> NotEqual<T> notEqual(ValidationIdentifier identifier, T value) {
        return new NotEqual<>(identifier, value);
    }

    public static <T> Satisfies<T> satisfies(ValidationIdentifier identifier,
                                             Predicate<T> predicate,
                                             String predicateDescription) {
        return new Satisfies<>(identifier, predicate, predicateDescription);
    }

    public static InstanceOf instanceOf(ValidationIdentifier identifier, Class<?> targetClass) {
        return new InstanceOf(identifier, targetClass);
    }

    public static NotInstanceOf notInstanceOf(ValidationIdentifier identifier, Class<?> targetClass) {
        return new NotInstanceOf(identifier, targetClass);
    }

    public static <T> SameAs<T> sameAs(ValidationIdentifier identifier, T reference) {
        return new SameAs<>(identifier, reference);
    }

    public static <T> NotSameAs<T> notSameAs(ValidationIdentifier identifier, T reference) {
        return new NotSameAs<>(identifier, reference);
    }
}