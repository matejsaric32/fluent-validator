package com.fluentval.validator;


import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ValidationIdentifier {
    private static final byte TYPE_PATH = 1;
    private static final byte TYPE_INDEX = 2;
    private static final byte TYPE_CUSTOM = 3;
    private static final byte TYPE_FIELD = 4;

    private final String value;
    private final byte type;

    private ValidationIdentifier(String value, byte type) {
        this.value = value;
        this.type = type;
    }

    public static ValidationIdentifier ofPath(String value) {
        return new ValidationIdentifier(value, TYPE_PATH);
    }

    public static ValidationIdentifier ofIndex(String value) {
        return new ValidationIdentifier(value, TYPE_INDEX);
    }

    public static ValidationIdentifier ofCustom(String value) {
        return new ValidationIdentifier(value, TYPE_CUSTOM);
    }

    public static ValidationIdentifier ofField(String value) {
        return new ValidationIdentifier(value, TYPE_FIELD);
    }

    public String value() {
        return value;
    }

    public byte type() {
        return type;
    }
}