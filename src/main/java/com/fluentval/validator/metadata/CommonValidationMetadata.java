package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class CommonValidationMetadata extends ValidationMetadata {

    protected CommonValidationMetadata(ValidationIdentifier identifier,
                                       String errorCode,
                                       String message) {
        super(identifier, errorCode, message);
    }

    public static final class NotNull extends CommonValidationMetadata {
        private static final String DEFAULT_MESSAGE = "Field '%s' must not be null.";

        public NotNull(ValidationIdentifier identifier, String errorCode) {
            super(identifier, errorCode, formatMessage(DEFAULT_MESSAGE, identifier.value()));
        }

        public NotNull(ValidationIdentifier identifier, String errorCode, String message) {
            super(identifier, errorCode, message);
        }
    }

    public static final class MustBeNull extends CommonValidationMetadata {
        private static final String DEFAULT_MESSAGE = "Field '%s' must be null.";

        public MustBeNull(ValidationIdentifier identifier, String errorCode) {
            super(identifier, errorCode, formatMessage(DEFAULT_MESSAGE, identifier.value()));
        }

        public MustBeNull(ValidationIdentifier identifier, String errorCode, String message) {
            super(identifier, errorCode, message);
        }
    }

    @Getter
    public static final class Equal<T> extends CommonValidationMetadata {
        private static final String DEFAULT_MESSAGE = "Field '%s' must be equal to %s.";
        private final T targetObject;

        public Equal(ValidationIdentifier identifier, String errorCode, T targetObject) {
            super(identifier, errorCode,
                formatMessage(DEFAULT_MESSAGE, identifier.value(), targetObject != null ? targetObject.toString() : "null"));
            this.targetObject = targetObject;
        }

        public Equal(ValidationIdentifier identifier, String errorCode, T targetObject, String message) {
            super(identifier, errorCode, message);
            this.targetObject = targetObject;
        }

    }

    @Getter
    public static final class NotEqual<T> extends CommonValidationMetadata {
        private static final String DEFAULT_MESSAGE = "Field '%s' must not be equal to %s.";
        private final T targetObject;

        public NotEqual(ValidationIdentifier identifier, String errorCode, T targetObject) {
            super(identifier, errorCode,
                formatMessage(DEFAULT_MESSAGE, identifier.value(), targetObject != null ? targetObject.toString() : "null"));
            this.targetObject = targetObject;
        }

        public NotEqual(ValidationIdentifier identifier, String errorCode, T targetObject, String message) {
            super(identifier, errorCode, message);
            this.targetObject = targetObject;
        }

    }

    @Getter
    public static final class Satisfies<T> extends CommonValidationMetadata {
        private static final String DEFAULT_MESSAGE = "Field '%s' must satisfy the condition: %s";
        private final Predicate<T> predicate;
        private final String predicateMessage;

        public Satisfies(ValidationIdentifier identifier, String errorCode,
                         Predicate<T> predicate, String predicateMessage) {
            super(identifier, errorCode,
                formatMessage(DEFAULT_MESSAGE, identifier.value(), predicateMessage));
            this.predicate = Objects.requireNonNull(predicate, "Predicate must not be null");
            this.predicateMessage = predicateMessage;
        }

        public Satisfies(ValidationIdentifier identifier, String errorCode,
                         Predicate<T> predicate, String predicateMessage, String message) {
            super(identifier, errorCode, message);
            this.predicate = Objects.requireNonNull(predicate, "Predicate must not be null");
            this.predicateMessage = predicateMessage;
        }

    }

    @Getter
    public static final class InstanceOf extends CommonValidationMetadata {
        private static final String DEFAULT_MESSAGE = "Field '%s' must be an instance of class %s.";
        private final Class<?> targetClass;

        public InstanceOf(ValidationIdentifier identifier, String errorCode, Class<?> targetClass) {
            super(identifier, errorCode,
                formatMessage(DEFAULT_MESSAGE, identifier.value(), targetClass.getSimpleName()));
            this.targetClass = Objects.requireNonNull(targetClass, "Class must not be null");
        }

        public InstanceOf(ValidationIdentifier identifier, String errorCode, Class<?> targetClass, String message) {
            super(identifier, errorCode, message);
            this.targetClass = Objects.requireNonNull(targetClass, "Class must not be null");
        }

    }

    @Getter
    public static final class NotInstanceOf extends CommonValidationMetadata {
        private static final String DEFAULT_MESSAGE = "Field '%s' must not be an instance of class %s.";
        private final Class<?> targetClass;

        public NotInstanceOf(ValidationIdentifier identifier, String errorCode, Class<?> targetClass) {
            super(identifier, errorCode,
                formatMessage(DEFAULT_MESSAGE, identifier.value(), targetClass.getSimpleName()));
            this.targetClass = Objects.requireNonNull(targetClass, "Class must not be null");
        }

        public NotInstanceOf(ValidationIdentifier identifier, String errorCode, Class<?> targetClass, String message) {
            super(identifier, errorCode, message);
            this.targetClass = Objects.requireNonNull(targetClass, "Class must not be null");
        }

    }

    @Getter
    public static final class SameAs<T> extends CommonValidationMetadata {
        private static final String DEFAULT_MESSAGE = "Field '%s' must be the same object as the reference.";
        private final T targetObject;

        public SameAs(ValidationIdentifier identifier, String errorCode, T targetObject) {
            super(identifier, errorCode,
                formatMessage(DEFAULT_MESSAGE, identifier.value()));
            this.targetObject = targetObject;
        }

        public SameAs(ValidationIdentifier identifier, String errorCode, T targetObject, String message) {
            super(identifier, errorCode, message);
            this.targetObject = targetObject;
        }

    }

    @Getter
    public static final class NotSameAs<T> extends CommonValidationMetadata {
        private static final String DEFAULT_MESSAGE = "Field '%s' must not be the same object as the reference.";
        private final T targetObject;

        public NotSameAs(ValidationIdentifier identifier, String errorCode, T targetObject) {
            super(identifier, errorCode,
                formatMessage(DEFAULT_MESSAGE, identifier.value()));
            this.targetObject = targetObject;
        }

        public NotSameAs(ValidationIdentifier identifier, String errorCode, T targetObject, String message) {
            super(identifier, errorCode, message);
            this.targetObject = targetObject;
        }

    }
}
