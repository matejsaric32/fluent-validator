package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.NumberValidationMetadata;

import java.util.Objects;

/**
 * Utility class providing validation rules for numeric types including Integer, Long, Double, Float,
 * Short, Byte, BigDecimal, and other Number implementations. This class offers comprehensive validation
 * for numeric ranges, comparisons, sign constraints, and mathematical relationships.
 *
 * <p>All validation rules in this class automatically skip null values, meaning they will
 * pass validation if the input is null. Use in combination with {@code CommonValidationRules.notNull()}
 * if null values should be rejected.</p>
 *
 * <p>Supported numeric types include:</p>
 * <ul>
 * <li>{@link Integer} - 32-bit signed integers</li>
 * <li>{@link Long} - 64-bit signed integers</li>
 * <li>{@link Double} - Double-precision floating-point numbers</li>
 * <li>{@link Float} - Single-precision floating-point numbers</li>
 * <li>{@link Short} - 16-bit signed integers</li>
 * <li>{@link Byte} - 8-bit signed integers</li>
 * <li>{@link java.math.BigDecimal} - Arbitrary-precision decimal numbers</li>
 * <li>{@link java.math.BigInteger} - Arbitrary-precision integers</li>
 * <li>Other {@link Number} types that implement {@link Comparable}</li>
 * </ul>
 *
 * <p>Common validation patterns include:</p>
 * <ul>
 * <li>Range validations (minimum, maximum, between)</li>
 * <li>Sign validations (positive, negative, non-zero)</li>
 * <li>Business rule validations (age limits, price ranges, quantities)</li>
 * <li>Mathematical constraints (percentages, ratios, scores)</li>
 * </ul>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationRule
 * @see NumberValidationMetadata
 * @see Number
 * @see Comparable
 */
public final class NumberValidationRules {

    private NumberValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static <T extends Number & Comparable<T>> boolean isGreaterThanOrEqualTo(final T value, final T min) {
            return value.compareTo(min) >= 0;
        }

        static <T extends Number & Comparable<T>> boolean isLessThanOrEqualTo(final T value, final T max) {
            return value.compareTo(max) <= 0;
        }

        static <T extends Number & Comparable<T>> boolean isInRange(final T value, final T min, final T max) {
            return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
        }

        static <T extends Number & Comparable<T>> boolean isPositive(final T value) {
            return value.compareTo(getZero(value)) > 0;
        }

        static <T extends Number & Comparable<T>> boolean isNegative(final T value) {
            return value.compareTo(getZero(value)) < 0;
        }

        static <T extends Number & Comparable<T>> boolean isNotZero(final T value) {
            return value.compareTo(getZero(value)) != 0;
        }

        @SuppressWarnings("unchecked")
        static <T extends Number & Comparable<T>> T getZero(final T value) {
            if (value instanceof Integer) {
                return (T) Integer.valueOf(0);
            } else if (value instanceof Long) {
                return (T) Long.valueOf(0L);
            } else if (value instanceof Double) {
                return (T) Double.valueOf(0.0);
            } else if (value instanceof Float) {
                return (T) Float.valueOf(0.0f);
            } else if (value instanceof Short) {
                return (T) Short.valueOf((short) 0);
            } else if (value instanceof Byte) {
                return (T) Byte.valueOf((byte) 0);
            } else {
                // Default fallback
                return (T) Integer.valueOf(0);
            }
        }
    }

    /**
     * Creates a validation rule that checks if a number is greater than or equal to the specified minimum value.
     *
     * <p>This rule validates that the numeric value is at least as large as the minimum threshold.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of number to validate
     * @param min the minimum allowed value (inclusive)
     * @return a ValidationRule that passes if the value is >= min
     * @throws NullPointerException if min is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that age is at least 18
     * ValidationResult result = Validator.of(person)
     *     .property(ValidationIdentifier.ofField("age"), Person::getAge)
     *         .validate(NumberValidationRules.min(18))
     *         .end()
     *     .getResult();
     *
     * // Validate that price is at least $0.01
     * ValidationResult priceResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("price"), Product::getPrice)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(NumberValidationRules.min(new BigDecimal("0.01")))
     *         .end()
     *     .getResult();
     *
     * // Validate that quantity is at least 1
     * ValidationResult quantityResult = Validator.of(orderItem)
     *     .property(ValidationIdentifier.ofField("quantity"), OrderItem::getQuantity)
     *         .validate(NumberValidationRules.min(1))
     *         .end()
     *     .getResult();
     *
     * // Validate that score is at least minimum passing grade
     * ValidationResult scoreResult = Validator.of(exam)
     *     .property(ValidationIdentifier.ofField("score"), Exam::getScore)
     *         .validate(NumberValidationRules.min(60.0))
     *         .validate(NumberValidationRules.max(100.0))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Number & Comparable<T>> ValidationRule<T> min(final T min) {
        Objects.requireNonNull(min, "Minimum value must not be null");

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isGreaterThanOrEqualTo(value, min)) {
                result.addFailure(
                        new ValidationResult.Failure(
                                NumberValidationMetadata.min(identifier, min)
                        )
                );
            }
        };
    }

    /**
     * Creates a validation rule that checks if a number is less than or equal to the specified maximum value.
     *
     * <p>This rule validates that the numeric value does not exceed the maximum threshold.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of number to validate
     * @param max the maximum allowed value (inclusive)
     * @return a ValidationRule that passes if the value is <= max
     * @throws NullPointerException if max is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that age does not exceed reasonable limit
     * ValidationResult result = Validator.of(person)
     *     .property(ValidationIdentifier.ofField("age"), Person::getAge)
     *         .validate(NumberValidationRules.max(120))
     *         .end()
     *     .getResult();
     *
     * // Validate that discount percentage is not more than 100%
     * ValidationResult discountResult = Validator.of(coupon)
     *     .property(ValidationIdentifier.ofField("discountPercentage"), Coupon::getDiscountPercentage)
     *         .validate(NumberValidationRules.max(100.0))
     *         .end()
     *     .getResult();
     *
     * // Validate that file size doesn't exceed limit
     * ValidationResult fileSizeResult = Validator.of(uploadedFile)
     *     .property(ValidationIdentifier.ofField("sizeInBytes"), UploadedFile::getSizeInBytes)
     *         .validate(NumberValidationRules.max(10_000_000L)) // 10MB limit
     *         .end()
     *     .getResult();
     *
     * // Validate that rating is within scale
     * ValidationResult ratingResult = Validator.of(review)
     *     .property(ValidationIdentifier.ofField("rating"), Review::getRating)
     *         .validate(NumberValidationRules.min(1))
     *         .validate(NumberValidationRules.max(5))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Number & Comparable<T>> ValidationRule<T> max(final T max) {
        Objects.requireNonNull(max, "Maximum value must not be null");

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isLessThanOrEqualTo(value, max)) {
                result.addFailure(
                        new ValidationResult.Failure(
                                NumberValidationMetadata.max(identifier, max)
                        )
                );
            }
        };
    }

    /**
     * Creates a validation rule that checks if a number falls within the specified range (inclusive).
     *
     * <p>This rule validates that the numeric value is between the minimum and maximum values,
     * including the boundary values. The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of number to validate
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     * @return a ValidationRule that passes if the value is between min and max (inclusive)
     * @throws NullPointerException if min or max is null
     * @throws IllegalArgumentException if min is greater than max
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that age is within reasonable range
     * ValidationResult result = Validator.of(person)
     *     .property(ValidationIdentifier.ofField("age"), Person::getAge)
     *         .validate(NumberValidationRules.range(0, 150))
     *         .end()
     *     .getResult();
     *
     * // Validate that temperature is within acceptable range
     * ValidationResult tempResult = Validator.of(sensor)
     *     .property(ValidationIdentifier.ofField("temperature"), Sensor::getTemperature)
     *         .validate(NumberValidationRules.range(-40.0, 85.0))
     *         .end()
     *     .getResult();
     *
     * // Validate that percentage is between 0 and 100
     * ValidationResult percentResult = Validator.of(progress)
     *     .property(ValidationIdentifier.ofField("completionPercentage"), Progress::getCompletionPercentage)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(NumberValidationRules.range(0.0, 100.0))
     *         .end()
     *     .getResult();
     *
     * // Validate that port number is in valid range
     * ValidationResult portResult = Validator.of(serverConfig)
     *     .property(ValidationIdentifier.ofField("port"), ServerConfig::getPort)
     *         .validate(NumberValidationRules.range(1024, 65535))
     *         .end()
     *     .getResult();
     *
     * // Validate that price is within product category range
     * BigDecimal minPrice = new BigDecimal("9.99");
     * BigDecimal maxPrice = new BigDecimal("999.99");
     * ValidationResult priceRangeResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("price"), Product::getPrice)
     *         .validate(NumberValidationRules.range(minPrice, maxPrice))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Number & Comparable<T>> ValidationRule<T> range(final T min, final T max) {
        Objects.requireNonNull(min, "Minimum value must not be null");
        Objects.requireNonNull(max, "Maximum value must not be null");

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Minimum value must be less than or equal to maximum value");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isInRange(value, min, max)) {
                result.addFailure(
                        new ValidationResult.Failure(
                                NumberValidationMetadata.range(identifier, min, max)
                        )
                );
            }
        };
    }

    /**
     * Creates a validation rule that checks if a number is positive (greater than zero).
     *
     * <p>This rule validates that the numeric value is strictly greater than zero.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of number to validate
     * @return a ValidationRule that passes if the value is > 0
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that quantity is positive
     * ValidationResult result = Validator.of(orderItem)
     *     .property(ValidationIdentifier.ofField("quantity"), OrderItem::getQuantity)
     *         .validate(NumberValidationRules.positive())
     *         .end()
     *     .getResult();
     *
     * // Validate that price is positive
     * ValidationResult priceResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("price"), Product::getPrice)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(NumberValidationRules.positive())
     *         .end()
     *     .getResult();
     *
     * // Validate that distance is positive
     * ValidationResult distanceResult = Validator.of(route)
     *     .property(ValidationIdentifier.ofField("distance"), Route::getDistance)
     *         .validate(NumberValidationRules.positive())
     *         .end()
     *     .getResult();
     *
     * // Validate that duration is positive
     * ValidationResult durationResult = Validator.of(task)
     *     .property(ValidationIdentifier.ofField("estimatedHours"), Task::getEstimatedHours)
     *         .validate(NumberValidationRules.positive())
     *         .end()
     *     .getResult();
     *
     * // Validate that interest rate is positive
     * ValidationResult interestResult = Validator.of(loan)
     *     .property(ValidationIdentifier.ofField("interestRate"), Loan::getInterestRate)
     *         .validate(NumberValidationRules.positive())
     *         .validate(NumberValidationRules.max(new BigDecimal("0.30"))) // Max 30%
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Number & Comparable<T>> ValidationRule<T> positive() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isPositive(value)) {
                result.addFailure(
                        new ValidationResult.Failure(
                                NumberValidationMetadata.positive(identifier)
                        )
                );
            }
        };
    }

    /**
     * Creates a validation rule that checks if a number is negative (less than zero).
     *
     * <p>This rule validates that the numeric value is strictly less than zero.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of number to validate
     * @return a ValidationRule that passes if the value is < 0
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that temperature is below freezing
     * ValidationResult result = Validator.of(weather)
     *     .property(ValidationIdentifier.ofField("temperature"), Weather::getTemperature)
     *         .validate(NumberValidationRules.negative())
     *         .end()
     *     .getResult();
     *
     * // Validate that debt amount is negative (representing owed money)
     * ValidationResult debtResult = Validator.of(account)
     *     .property(ValidationIdentifier.ofField("balance"), Account::getBalance)
     *         .validate(NumberValidationRules.negative())
     *         .end()
     *     .getResult();
     *
     * // Validate that altitude is below sea level
     * ValidationResult altitudeResult = Validator.of(location)
     *     .property(ValidationIdentifier.ofField("altitude"), Location::getAltitude)
     *         .validate(NumberValidationRules.negative())
     *         .end()
     *     .getResult();
     *
     * // Validate that loss value is negative
     * ValidationResult lossResult = Validator.of(financialReport)
     *     .property(ValidationIdentifier.ofField("netIncome"), FinancialReport::getNetIncome)
     *         .validate(NumberValidationRules.negative())
     *         .end()
     *     .getResult();
     *
     * // Validate that coordinate is in southern hemisphere
     * ValidationResult coordinateResult = Validator.of(gpsLocation)
     *     .property(ValidationIdentifier.ofField("latitude"), GpsLocation::getLatitude)
     *         .validate(NumberValidationRules.negative())
     *         .validate(NumberValidationRules.range(-90.0, 0.0))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Number & Comparable<T>> ValidationRule<T> negative() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isNegative(value)) {
                result.addFailure(
                        new ValidationResult.Failure(
                                NumberValidationMetadata.negative(identifier)
                        )
                );
            }
        };
    }

    /**
     * Creates a validation rule that checks if a number is not equal to zero.
     *
     * <p>This rule validates that the numeric value is either positive or negative, but not zero.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of number to validate
     * @return a ValidationRule that passes if the value is != 0
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that divisor is not zero to prevent division by zero
     * ValidationResult result = Validator.of(calculation)
     *     .property(ValidationIdentifier.ofField("divisor"), Calculation::getDivisor)
     *         .validate(NumberValidationRules.notZero())
     *         .end()
     *     .getResult();
     *
     * // Validate that scaling factor is not zero
     * ValidationResult scaleResult = Validator.of(transformation)
     *     .property(ValidationIdentifier.ofField("scaleFactor"), Transformation::getScaleFactor)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(NumberValidationRules.notZero())
     *         .end()
     *     .getResult();
     *
     * // Validate that quantity ordered is not zero
     * ValidationResult quantityResult = Validator.of(orderItem)
     *     .property(ValidationIdentifier.ofField("quantity"), OrderItem::getQuantity)
     *         .validate(NumberValidationRules.notZero())
     *         .end()
     *     .getResult();
     *
     * // Validate that rate is not zero for calculations
     * ValidationResult rateResult = Validator.of(interestCalculation)
     *     .property(ValidationIdentifier.ofField("rate"), InterestCalculation::getRate)
     *         .validate(NumberValidationRules.notZero())
     *         .end()
     *     .getResult();
     *
     * // Validate that weight is not zero for shipping
     * ValidationResult weightResult = Validator.of(package)
     *     .property(ValidationIdentifier.ofField("weight"), Package::getWeight)
     *         .validate(NumberValidationRules.notZero())
     *         .validate(NumberValidationRules.positive())
     *         .end()
     *     .getResult();
     *
     * // Complex validation with multiple numeric constraints
     * ValidationResult complexResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("price"), Product::getPrice)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(NumberValidationRules.positive())
     *         .validate(NumberValidationRules.min(new BigDecimal("0.01")))
     *         .validate(NumberValidationRules.max(new BigDecimal("99999.99")))
     *         .end()
     *     .property(ValidationIdentifier.ofField("weight"), Product::getWeight)
     *         .validate(NumberValidationRules.positive())
     *         .validate(NumberValidationRules.max(1000.0))
     *         .end()
     *     .property(ValidationIdentifier.ofField("stockQuantity"), Product::getStockQuantity)
     *         .validate(NumberValidationRules.range(0, 10000))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Number & Comparable<T>> ValidationRule<T> notZero() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isNotZero(value)) {
                result.addFailure(
                        new ValidationResult.Failure(
                                NumberValidationMetadata.notZero(identifier)
                        )
                );
            }
        };
    }
}