package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Abstract base class for validation metadata related to collection constraints and validations.
 * This class serves as the foundation for all validation metadata types that deal with collection
 * size validation, element validation, predicate-based validation, and collection membership
 * constraints, providing common infrastructure for comprehensive collection validation scenarios.
 *
 * <p>CollectionValidationMetadata supports various validation patterns:</p>
 * <ul>
 * <li><strong>Size validation</strong> - ensuring collections meet minimum, maximum, exact, or range size requirements</li>
 * <li><strong>Emptiness validation</strong> - checking whether collections are empty or non-empty</li>
 * <li><strong>Element validation</strong> - verifying presence or absence of specific elements</li>
 * <li><strong>Predicate validation</strong> - applying custom conditions to collection elements</li>
 * <li><strong>Uniqueness validation</strong> - ensuring collections contain no duplicate elements</li>
 * <li><strong>Membership validation</strong> - checking if collections contain all or none of specified elements</li>
 * </ul>
 *
 * <p>Each concrete implementation provides specific metadata for different collection validation scenarios,
 * including constraint values, actual collection state information, and human-readable descriptions
 * for comprehensive error messaging and validation reporting.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationMetadata
 * @see DefaultValidationCode
 */
public abstract class CollectionValidationMetadata extends ValidationMetadata {

    /**
     * Constructs CollectionValidationMetadata with the specified identifier and validation code.
     *
     * <p>This constructor initializes the base metadata with the validation identifier and
     * automatically adds the field identifier to the message parameters for error message
     * template substitution.</p>
     *
     * @param identifier the validation identifier specifying what failed validation
     * @param code the default validation code categorizing the specific collection validation type
     */
    protected CollectionValidationMetadata(ValidationIdentifier identifier,
                                           DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    /**
     * Validation metadata for non-empty collection constraints.
     *
     * <p>This class represents validation failures where a collection must contain at least one element.
     * It is used for ensuring that required collections are not empty and contain meaningful data.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that required lists of items are provided</li>
     * <li>Ensuring user selections contain at least one choice</li>
     * <li>Checking that configuration arrays have values</li>
     * <li>Verifying that result sets contain data</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies solely on the collection's
     * empty/non-empty state and inherits field identifier from the parent class for error messaging.</li>
     * </ul>
     */
    public static final class NotEmpty extends CollectionValidationMetadata {
        private NotEmpty(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_EMPTY);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for empty collection constraints.
     *
     * <p>This class represents validation failures where a collection must be completely empty.
     * It is used for ensuring that certain collections remain uninitialized or cleared of content
     * under specific conditions.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that error lists are empty after successful operations</li>
     * <li>Ensuring temporary collections are cleared</li>
     * <li>Checking that certain optional lists remain unset</li>
     * <li>Verifying cleanup operations have completed</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies solely on the collection's
     * empty/non-empty state and inherits field identifier from the parent class for error messaging.</li>
     * </ul>
     */
    public static final class IsEmpty extends CollectionValidationMetadata {
        private IsEmpty(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_EMPTY);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for minimum collection size constraints.
     *
     * <p>This class represents validation failures where a collection must contain at least
     * a specified minimum number of elements. It provides lower bound size validation with
     * clear constraint specification for error messaging.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring sufficient data points for statistical analysis</li>
     * <li>Validating minimum required selections in forms</li>
     * <li>Checking that batch operations have enough items</li>
     * <li>Verifying minimum participant counts for processes</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>minimumSize</strong> - {@code int} - The minimum number of elements that the
     * collection must contain to pass validation. This value represents the lower bound constraint
     * and is used in error messages to inform users of the requirement. Must be non-negative.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class MinSize extends CollectionValidationMetadata {
        private final int minimumSize;

        private MinSize(ValidationIdentifier identifier, int minimumSize) {
            super(identifier, DefaultValidationCode.MIN_SIZE);
            this.minimumSize = minimumSize;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_SIZE, String.valueOf(minimumSize));
        }
    }

    /**
     * Validation metadata for maximum collection size constraints.
     *
     * <p>This class represents validation failures where a collection must not exceed
     * a specified maximum number of elements. It provides upper bound size validation
     * to prevent collections from becoming too large for processing or storage.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Limiting upload batch sizes to prevent system overload</li>
     * <li>Constraining user selections to manageable numbers</li>
     * <li>Preventing memory issues with large collections</li>
     * <li>Enforcing business rules on maximum quantities</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>maximumSize</strong> - {@code int} - The maximum number of elements that the
     * collection may contain to pass validation. This value represents the upper bound constraint
     * and is used in error messages to inform users of the limitation. Must be non-negative.</li>
     * </ul>
     *
     * <p><strong>Note:</strong> There appears to be a typo in the field name {@code maximunSize}
     * in the original code which should be {@code maximumSize}.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class MaxSize extends CollectionValidationMetadata {
        private final int maximunSize; // Note: Typo in original field name

        private MaxSize(ValidationIdentifier identifier, int maximunSize) {
            super(identifier, DefaultValidationCode.MAX_SIZE);
            this.maximunSize = maximunSize;

            // Add message parameters
            addMessageParameter(MessageParameter.MAX_SIZE, String.valueOf(maximunSize));
        }
    }

    /**
     * Validation metadata for exact collection size constraints.
     *
     * <p>This class represents validation failures where a collection must contain exactly
     * a specified number of elements. It provides precise size validation and includes both
     * the required size and the actual size for detailed error reporting.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating coordinate pairs or triplets (e.g., x,y,z coordinates)</li>
     * <li>Ensuring specific numbers of required fields are provided</li>
     * <li>Checking that arrays match expected dimensions</li>
     * <li>Verifying that configuration sets contain exact numbers of elements</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>requiredSize</strong> - {@code int} - The exact number of elements that the
     * collection must contain to pass validation. This represents the precise size constraint.</li>
     * <li><strong>actualSize</strong> - {@code int} - The actual number of elements found in
     * the collection during validation. This provides context in error messages showing the
     * difference between expected and actual sizes.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ExactSize extends CollectionValidationMetadata {
        private final int requiredSize;
        private final int actualSize;

        private ExactSize(ValidationIdentifier identifier, int requiredSize, int actualSize) {
            super(identifier, DefaultValidationCode.EXACT_SIZE);
            this.requiredSize = requiredSize;
            this.actualSize = actualSize;

            // Add message parameters
            addMessageParameter(MessageParameter.EXACT_SIZE, String.valueOf(requiredSize));
            addMessageParameter(MessageParameter.ACTUAL_SIZE, String.valueOf(actualSize));
        }
    }

    /**
     * Validation metadata for collection size range constraints.
     *
     * <p>This class represents validation failures where a collection must contain a number
     * of elements within a specified range (inclusive). It provides both lower and upper
     * bound validation with detailed size information for comprehensive error reporting.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating reasonable batch sizes for processing</li>
     * <li>Ensuring collections are neither too small nor too large</li>
     * <li>Checking that user selections fall within acceptable ranges</li>
     * <li>Validating data sets for statistical significance</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>minSize</strong> - {@code int} - The minimum number of elements (inclusive)
     * that the collection must contain. Represents the lower bound of the acceptable range.</li>
     * <li><strong>maxSize</strong> - {@code int} - The maximum number of elements (inclusive)
     * that the collection may contain. Represents the upper bound of the acceptable range.</li>
     * <li><strong>actualSize</strong> - {@code int} - The actual number of elements found in
     * the collection during validation. Provides context showing how the actual size relates
     * to the acceptable range.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class SizeRange extends CollectionValidationMetadata {
        private final int minSize;
        private final int maxSize;
        private final int actualSize;

        private SizeRange(ValidationIdentifier identifier, int minSize, int maxSize, int actualSize) {
            super(identifier, DefaultValidationCode.SIZE_RANGE);
            this.minSize = minSize;
            this.maxSize = maxSize;
            this.actualSize = actualSize;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_SIZE, String.valueOf(minSize));
            addMessageParameter(MessageParameter.MAX_SIZE, String.valueOf(maxSize));
            addMessageParameter(MessageParameter.ACTUAL_SIZE, String.valueOf(actualSize));
        }
    }

    /**
     * Validation metadata for universal element predicate constraints.
     *
     * <p>This class represents validation failures where all elements in a collection must
     * satisfy a specific condition. It provides universal quantification validation with
     * custom predicate logic and human-readable condition descriptions.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring all values in a collection meet minimum thresholds</li>
     * <li>Validating that all elements pass specific business rules</li>
     * <li>Checking that all items have required properties</li>
     * <li>Verifying that all entries are properly formatted</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>condition</strong> - {@code Predicate<E>} - The predicate function that
     * defines the condition each element must satisfy. This predicate is applied to every
     * element in the collection during validation.</li>
     * <li><strong>conditionDescription</strong> - {@code String} - Human-readable description
     * of the condition used in error messages to explain what requirement the elements failed
     * to meet. Must not be null or blank.</li>
     * </ul>
     *
     * @param <E> the type of elements in the collection being validated
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AllMatch<E> extends CollectionValidationMetadata {
        private final Predicate<E> condition;
        private final String conditionDescription;

        private AllMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
            super(identifier, DefaultValidationCode.ALL_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    /**
     * Validation metadata for existential element predicate constraints.
     *
     * <p>This class represents validation failures where at least one element in a collection
     * must satisfy a specific condition. It provides existential quantification validation
     * ensuring that collections contain at least one element meeting the specified criteria.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring at least one administrator exists in user lists</li>
     * <li>Validating that collections contain at least one valid item</li>
     * <li>Checking that at least one element meets quality standards</li>
     * <li>Verifying that collections have at least one element with required properties</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>condition</strong> - {@code Predicate<E>} - The predicate function that
     * defines the condition at least one element must satisfy. Applied to collection elements
     * until one matching element is found or all elements are checked.</li>
     * <li><strong>conditionDescription</strong> - {@code String} - Human-readable description
     * of the condition used in error messages to explain what requirement no elements satisfied.
     * Must not be null or blank.</li>
     * </ul>
     *
     * @param <E> the type of elements in the collection being validated
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AnyMatch<E> extends CollectionValidationMetadata {
        private final Predicate<E> condition;
        private final String conditionDescription;

        private AnyMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
            super(identifier, DefaultValidationCode.ANY_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    /**
     * Validation metadata for negative element predicate constraints.
     *
     * <p>This class represents validation failures where no elements in a collection may
     * satisfy a specific condition. It provides negative validation ensuring that collections
     * do not contain any elements meeting the specified (undesirable) criteria.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring no elements contain prohibited content</li>
     * <li>Validating that collections have no invalid or corrupted items</li>
     * <li>Checking that no elements violate security constraints</li>
     * <li>Verifying that collections contain no deprecated or obsolete elements</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>condition</strong> - {@code Predicate<E>} - The predicate function that
     * defines the condition no elements should satisfy. Applied to all collection elements
     * to ensure none match the undesirable criteria.</li>
     * <li><strong>conditionDescription</strong> - {@code String} - Human-readable description
     * of the prohibited condition used in error messages to explain what undesirable property
     * some elements possessed. Must not be null or blank.</li>
     * </ul>
     *
     * @param <E> the type of elements in the collection being validated
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoneMatch<E> extends CollectionValidationMetadata {
        private final Predicate<E> condition;
        private final String conditionDescription;

        private NoneMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
            super(identifier, DefaultValidationCode.NONE_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    /**
     * Validation metadata for collection uniqueness constraints.
     *
     * <p>This class represents validation failures where a collection must contain only
     * unique elements with no duplicates. It ensures data integrity by preventing
     * duplicate values that could cause processing issues or violate business rules.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that ID lists contain no duplicate identifiers</li>
     * <li>Ensuring unique email addresses in mailing lists</li>
     * <li>Checking that sets of values are truly unique</li>
     * <li>Verifying that configuration lists have no repeated entries</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on comparing collection
     * elements for equality and inherits field identifier from the parent class for error
     * messaging. The uniqueness check is performed using the elements' equals() method.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoDuplicates extends CollectionValidationMetadata {
        private NoDuplicates(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_DUPLICATES);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for single element containment constraints.
     *
     * <p>This class represents validation failures where a collection must contain a specific
     * element. It provides membership validation ensuring that required elements are present
     * in collections.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that required options are selected</li>
     * <li>Ensuring that mandatory elements are included in lists</li>
     * <li>Checking that collections contain expected values</li>
     * <li>Verifying that required configurations are present</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>element</strong> - {@code E} - The specific element that must be present
     * in the collection for validation to pass. This element is checked for membership using
     * the collection's contains() method, which relies on the element's equals() implementation.
     * Can be null if null values need to be validated for presence.</li>
     * </ul>
     *
     * @param <E> the type of element being checked for containment
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Contains<E> extends CollectionValidationMetadata {
        private final E element;

        private Contains(ValidationIdentifier identifier, E element) {
            super(identifier, DefaultValidationCode.COLLECTION_CONTAINS);
            this.element = element;

            // Add message parameters
            addMessageParameter(MessageParameter.ELEMENT, element != null ? element.toString() : "null");
        }
    }

    /**
     * Validation metadata for single element exclusion constraints.
     *
     * <p>This class represents validation failures where a collection must not contain a
     * specific element. It provides negative membership validation ensuring that prohibited
     * or unwanted elements are not present in collections.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring banned or inappropriate values are not included</li>
     * <li>Validating that deprecated options are not selected</li>
     * <li>Checking that collections exclude problematic elements</li>
     * <li>Verifying that certain values are filtered out</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>element</strong> - {@code E} - The specific element that must not be present
     * in the collection for validation to pass. This element is checked for absence using the
     * collection's contains() method. The element must not be null as per the factory method
     * validation, ensuring clear identification of prohibited values.</li>
     * </ul>
     *
     * @param <E> the type of element being checked for exclusion
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class DoesNotContain<E> extends CollectionValidationMetadata {
        private final E element;

        private DoesNotContain(ValidationIdentifier identifier, E element) {
            super(identifier, DefaultValidationCode.DOES_NOT_CONTAIN);
            this.element = element;

            // Add message parameters
            addMessageParameter(MessageParameter.ELEMENT, element != null ? element.toString() : "null");
        }
    }

    /**
     * Validation metadata for multiple element containment constraints.
     *
     * <p>This class represents validation failures where a collection must contain all
     * elements from a specified collection. It provides comprehensive membership validation
     * ensuring that collections include all required elements from a reference set.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that all required permissions are granted</li>
     * <li>Ensuring that collections include all mandatory fields</li>
     * <li>Checking that result sets contain all expected items</li>
     * <li>Verifying that collections are supersets of required elements</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>elements</strong> - {@code Collection<E>} - The collection of elements that
     * must all be present in the validated collection. This collection is copied defensively
     * during construction to prevent external modification. All elements in this collection
     * must be found in the target collection for validation to pass, using the containsAll()
     * method semantics.</li>
     * </ul>
     *
     * @param <E> the type of elements being checked for containment
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ContainsAll<E> extends CollectionValidationMetadata {
        private final Collection<E> elements;

        private ContainsAll(ValidationIdentifier identifier, Collection<E> elements) {
            super(identifier, DefaultValidationCode.CONTAINS_ALL);
            this.elements = new ArrayList<>(elements);

            // Add message parameters
            addMessageParameter(MessageParameter.ELEMENTS, elements.toString());
        }
    }

    /**
     * Validation metadata for multiple element exclusion constraints.
     *
     * <p>This class represents validation failures where a collection must not contain any
     * elements from a specified collection. It provides comprehensive exclusion validation
     * ensuring that collections do not include any prohibited elements from a reference set.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring collections exclude all banned or prohibited items</li>
     * <li>Validating that collections contain no deprecated elements</li>
     * <li>Checking that collections are disjoint from blacklisted sets</li>
     * <li>Verifying that collections exclude all problematic values</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>elements</strong> - {@code Collection<E>} - The collection of elements that
     * must not be present in the validated collection. This collection is copied defensively
     * during construction to prevent external modification. None of the elements in this
     * collection should be found in the target collection for validation to pass.</li>
     * </ul>
     *
     * @param <E> the type of elements being checked for exclusion
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ContainsNone<E> extends CollectionValidationMetadata {
        private final Collection<E> elements;

        private ContainsNone(ValidationIdentifier identifier, Collection<E> elements) {
            super(identifier, DefaultValidationCode.CONTAINS_NONE);
            this.elements = new ArrayList<>(elements);

            // Add message parameters
            addMessageParameter(MessageParameter.ELEMENTS, elements.toString());
        }
    }

    // Factory methods

    /**
     * Factory method for creating NotEmpty validation metadata.
     *
     * <p>Creates metadata for validating that a collection contains at least one element.</p>
     *
     * @param identifier the validation identifier
     * @return NotEmpty metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static NotEmpty notEmpty(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new NotEmpty(identifier);
    }

    /**
     * Factory method for creating IsEmpty validation metadata.
     *
     * <p>Creates metadata for validating that a collection is completely empty.</p>
     *
     * @param identifier the validation identifier
     * @return IsEmpty metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static IsEmpty isEmpty(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new IsEmpty(identifier);
    }

    /**
     * Factory method for creating MinSize validation metadata.
     *
     * <p>Creates metadata for validating that a collection contains at least the specified
     * minimum number of elements.</p>
     *
     * @param identifier the validation identifier
     * @param minSize the minimum required number of elements
     * @return MinSize metadata instance
     * @throws NullPointerException if identifier is null
     * @throws IllegalArgumentException if minSize is negative
     */
    public static MinSize minSize(ValidationIdentifier identifier, int minSize) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        if (minSize < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }
        return new MinSize(identifier, minSize);
    }

    /**
     * Factory method for creating MaxSize validation metadata.
     *
     * <p>Creates metadata for validating that a collection does not exceed the specified
     * maximum number of elements.</p>
     *
     * @param identifier the validation identifier
     * @param maxSize the maximum allowed number of elements
     * @return MaxSize metadata instance
     * @throws NullPointerException if identifier is null
     * @throws IllegalArgumentException if maxSize is negative
     */
    public static MaxSize maxSize(ValidationIdentifier identifier, int maxSize) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        if (maxSize < 0) {
            throw new IllegalArgumentException("Maximum size cannot be negative");
        }
        return new MaxSize(identifier, maxSize);
    }

    /**
     * Factory method for creating ExactSize validation metadata.
     *
     * <p>Creates metadata for validating that a collection contains exactly the specified
     * number of elements. This method is typically called during validation with both
     * the required size constraint and the actual size found in the collection.</p>
     *
     * @param identifier the validation identifier
     * @param exactSize the exact required number of elements
     * @param actualSize the actual number of elements found in the collection
     * @return ExactSize metadata instance
     * @throws NullPointerException if identifier is null
     * @throws IllegalArgumentException if exactSize or actualSize is negative
     */
    public static ExactSize exactSize(ValidationIdentifier identifier, int exactSize, int actualSize) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        if (exactSize < 0) {
            throw new IllegalArgumentException("Exact size cannot be negative");
        }
        if (actualSize < 0) {
            throw new IllegalArgumentException("Actual size cannot be negative");
        }
        return new ExactSize(identifier, exactSize, actualSize);
    }

    /**
     * Factory method for creating SizeRange validation metadata.
     *
     * <p>Creates metadata for validating that a collection size falls within the specified
     * range (inclusive). This method is typically called during validation with the range
     * constraints and the actual size found in the collection.</p>
     *
     * @param identifier the validation identifier
     * @param minSize the minimum allowed number of elements (inclusive)
     * @param maxSize the maximum allowed number of elements (inclusive)
     * @param actualSize the actual number of elements found in the collection
     * @return SizeRange metadata instance
     * @throws NullPointerException if identifier is null
     * @throws IllegalArgumentException if minSize is negative, or maxSize is less than minSize,
     *                                  or actualSize is negative
     */
    public static SizeRange sizeRange(ValidationIdentifier identifier, int minSize, int maxSize, int actualSize) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        if (minSize < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }
        if (maxSize < minSize) {
            throw new IllegalArgumentException("Maximum size must be greater than or equal to minimum size");
        }
        if (actualSize < 0) {
            throw new IllegalArgumentException("Actual size cannot be negative");
        }
        return new SizeRange(identifier, minSize, maxSize, actualSize);
    }

    /**
     * Factory method for creating AllMatch validation metadata.
     *
     * <p>Creates metadata for validating that all elements in a collection satisfy
     * the specified predicate condition.</p>
     *
     * @param <E> the type of elements in the collection
     * @param identifier the validation identifier
     * @param condition the predicate that all elements must satisfy
     * @param description human-readable description of the condition for error messages
     * @return AllMatch metadata instance
     * @throws NullPointerException if identifier, condition, or description is null
     * @throws IllegalArgumentException if description is blank
     */
    public static <E> AllMatch<E> allMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AllMatch<>(identifier, condition, description);
    }

    /**
     * Factory method for creating AnyMatch validation metadata.
     *
     * <p>Creates metadata for validating that at least one element in a collection
     * satisfies the specified predicate condition.</p>
     *
     * @param <E> the type of elements in the collection
     * @param identifier the validation identifier
     * @param condition the predicate that at least one element must satisfy
     * @param description human-readable description of the condition for error messages
     * @return AnyMatch metadata instance
     * @throws NullPointerException if identifier, condition, or description is null
     * @throws IllegalArgumentException if description is blank
     */
    public static <E> AnyMatch<E> anyMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AnyMatch<>(identifier, condition, description);
    }

    /**
     * Factory method for creating NoneMatch validation metadata.
     *
     * <p>Creates metadata for validating that no elements in a collection satisfy
     * the specified predicate condition.</p>
     *
     * @param <E> the type of elements in the collection
     * @param identifier the validation identifier
     * @param condition the predicate that no elements should satisfy
     * @param description human-readable description of the prohibited condition for error messages
     * @return NoneMatch metadata instance
     * @throws NullPointerException if identifier, condition, or description is null
     * @throws IllegalArgumentException if description is blank
     */
    public static <E> NoneMatch<E> noneMatch(ValidationIdentifier identifier, Predicate<E> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new NoneMatch<>(identifier, condition, description);
    }

    /**
     * Factory method for creating NoDuplicates validation metadata.
     *
     * <p>Creates metadata for validating that a collection contains only unique elements
     * with no duplicates.</p>
     *
     * @param identifier the validation identifier
     * @return NoDuplicates metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static NoDuplicates noDuplicates(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new NoDuplicates(identifier);
    }

    /**
     * Factory method for creating Contains validation metadata.
     *
     * <p>Creates metadata for validating that a collection contains the specified element.
     * The element can be null if null value containment needs to be validated.</p>
     *
     * @param <E> the type of element being checked for containment
     * @param identifier the validation identifier
     * @param element the element that must be present in the collection
     * @return Contains metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static <E> Contains<E> contains(ValidationIdentifier identifier, E element) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new Contains<>(identifier, element);
    }

    /**
     * Factory method for creating DoesNotContain validation metadata.
     *
     * <p>Creates metadata for validating that a collection does not contain the specified element.</p>
     *
     * @param <E> the type of element being checked for exclusion
     * @param identifier the validation identifier
     * @param element the element that must not be present in the collection
     * @return DoesNotContain metadata instance
     * @throws NullPointerException if identifier or element is null
     */
    public static <E> DoesNotContain<E> doesNotContain(ValidationIdentifier identifier, E element) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(element, "Element must not be null");
        return new DoesNotContain<>(identifier, element);
    }

    /**
     * Factory method for creating ContainsAll validation metadata.
     *
     * <p>Creates metadata for validating that a collection contains all elements
     * from the specified collection.</p>
     *
     * @param <E> the type of elements being checked for containment
     * @param identifier the validation identifier
     * @param elements the collection of elements that must all be present
     * @return ContainsAll metadata instance
     * @throws NullPointerException if identifier or elements is null
     * @throws IllegalArgumentException if elements collection is empty
     */
    public static <E> ContainsAll<E> containsAll(ValidationIdentifier identifier, Collection<E> elements) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(elements, "Elements collection must not be null");
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Elements collection must not be empty");
        }
        return new ContainsAll<>(identifier, elements);
    }

    /**
     * Factory method for creating ContainsNone validation metadata.
     *
     * <p>Creates metadata for validating that a collection contains none of the elements
     * from the specified collection.</p>
     *
     * @param <E> the type of elements being checked for exclusion
     * @param identifier the validation identifier
     * @param elements the collection of elements that must not be present
     * @return ContainsNone metadata instance
     * @throws NullPointerException if identifier or elements is null
     * @throws IllegalArgumentException if elements collection is empty
     */
    public static <E> ContainsNone<E> containsNone(ValidationIdentifier identifier, Collection<E> elements) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(elements, "Elements collection must not be null");
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Elements collection must not be empty");
        }
        return new ContainsNone<>(identifier, elements);
    }
}