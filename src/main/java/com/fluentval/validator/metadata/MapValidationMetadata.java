package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Abstract base class for validation metadata related to map constraints and validations.
 * This class serves as the foundation for all validation metadata types that deal with map
 * size validation, key-value validation, predicate-based validation, and map membership
 * constraints, providing common infrastructure for comprehensive map validation scenarios.
 *
 * <p>MapValidationMetadata supports various validation patterns:</p>
 * <ul>
 * <li><strong>Size validation</strong> - ensuring maps meet minimum, maximum, exact, or range size requirements</li>
 * <li><strong>Emptiness validation</strong> - checking whether maps are empty or non-empty</li>
 * <li><strong>Key validation</strong> - verifying presence or absence of specific keys</li>
 * <li><strong>Value validation</strong> - checking presence or absence of specific values</li>
 * <li><strong>Predicate validation</strong> - applying custom conditions to map keys, values, or entries</li>
 * <li><strong>Membership validation</strong> - checking if maps contain all specified keys or values</li>
 * </ul>
 *
 * <p>Each concrete implementation provides specific metadata for different map validation scenarios,
 * including constraint values, actual map state information, and human-readable descriptions
 * for comprehensive error messaging and validation reporting.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationMetadata
 * @see DefaultValidationCode
 */
public abstract class MapValidationMetadata extends ValidationMetadata {

    /**
     * Constructs MapValidationMetadata with the specified identifier and validation code.
     *
     * <p>This constructor initializes the base metadata with the validation identifier and
     * automatically adds the field identifier to the message parameters for error message
     * template substitution.</p>
     *
     * @param identifier the validation identifier specifying what failed validation
     * @param code the default validation code categorizing the specific map validation type
     */
    protected MapValidationMetadata(ValidationIdentifier identifier,
                                    DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    /**
     * Validation metadata for non-empty map constraints.
     *
     * <p>This class represents validation failures where a map must contain at least one key-value pair.
     * It is used for ensuring that required maps are not empty and contain meaningful data.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that configuration maps have been properly initialized</li>
     * <li>Ensuring user preference maps contain at least one setting</li>
     * <li>Checking that data lookup maps have entries</li>
     * <li>Verifying that result maps contain processed data</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies solely on the map's
     * empty/non-empty state and inherits field identifier from the parent class for error messaging.</li>
     * </ul>
     */
    public static final class NotEmpty extends MapValidationMetadata {
        private NotEmpty(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_EMPTY);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for empty map constraints.
     *
     * <p>This class represents validation failures where a map must be completely empty.
     * It is used for ensuring that certain maps remain uninitialized or cleared of content
     * under specific conditions.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that error maps are empty after successful operations</li>
     * <li>Ensuring temporary caches are cleared</li>
     * <li>Checking that certain optional configuration maps remain unset</li>
     * <li>Verifying cleanup operations have completed successfully</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies solely on the map's
     * empty/non-empty state and inherits field identifier from the parent class for error messaging.</li>
     * </ul>
     */
    public static final class IsEmpty extends MapValidationMetadata {
        private IsEmpty(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.IS_EMPTY);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for minimum map size constraints.
     *
     * <p>This class represents validation failures where a map must contain at least
     * a specified minimum number of key-value pairs. It provides lower bound size validation with
     * clear constraint specification for error messaging.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring sufficient configuration properties are provided</li>
     * <li>Validating minimum required data entries in lookup tables</li>
     * <li>Checking that batch processing maps have enough items</li>
     * <li>Verifying minimum parameter counts for operations</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>minimumSize</strong> - {@code int} - The minimum number of key-value pairs that the
     * map must contain to pass validation. This value represents the lower bound constraint
     * and is used in error messages to inform users of the requirement. Must be non-negative.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class MinSize extends MapValidationMetadata {
        private final int minimumSize;

        private MinSize(ValidationIdentifier identifier, int minimumSize) {
            super(identifier, DefaultValidationCode.MIN_SIZE);
            this.minimumSize = minimumSize;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_SIZE, String.valueOf(minimumSize));
        }
    }

    /**
     * Validation metadata for maximum map size constraints.
     *
     * <p>This class represents validation failures where a map must not exceed
     * a specified maximum number of key-value pairs. It provides upper bound size validation
     * to prevent maps from becoming too large for processing or memory consumption.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Limiting configuration map sizes to prevent system overload</li>
     * <li>Constraining cache sizes to manageable numbers</li>
     * <li>Preventing memory issues with large lookup tables</li>
     * <li>Enforcing business rules on maximum parameter counts</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>maximumSize</strong> - {@code int} - The maximum number of key-value pairs that the
     * map may contain to pass validation. This value represents the upper bound constraint
     * and is used in error messages to inform users of the limitation. Must be non-negative.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class MaxSize extends MapValidationMetadata {
        private final int maximumSize;

        private MaxSize(ValidationIdentifier identifier, int maximumSize) {
            super(identifier, DefaultValidationCode.MAX_SIZE);
            this.maximumSize = maximumSize;

            // Add message parameters
            addMessageParameter(MessageParameter.MAX_SIZE, String.valueOf(maximumSize));
        }
    }

    /**
     * Validation metadata for exact map size constraints.
     *
     * <p>This class represents validation failures where a map must contain exactly
     * a specified number of key-value pairs. It provides precise size validation and includes both
     * the required size and the actual size for detailed error reporting.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating coordinate maps (e.g., x,y,z coordinate mappings)</li>
     * <li>Ensuring specific numbers of required configuration parameters are provided</li>
     * <li>Checking that transformation maps match expected dimensions</li>
     * <li>Verifying that lookup tables contain exact numbers of entries</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>requiredSize</strong> - {@code int} - The exact number of key-value pairs that the
     * map must contain to pass validation. This represents the precise size constraint.</li>
     * <li><strong>actualSize</strong> - {@code int} - The actual number of key-value pairs found in
     * the map during validation. This provides context in error messages showing the
     * difference between expected and actual sizes.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ExactSize extends MapValidationMetadata {
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
     * Validation metadata for map size range constraints.
     *
     * <p>This class represents validation failures where a map must contain a number
     * of key-value pairs within a specified range (inclusive). It provides both lower and upper
     * bound validation with detailed size information for comprehensive error reporting.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating reasonable configuration parameter counts</li>
     * <li>Ensuring maps are neither too sparse nor too dense</li>
     * <li>Checking that user preference maps fall within acceptable ranges</li>
     * <li>Validating data transformation maps for optimal processing</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>minSize</strong> - {@code int} - The minimum number of key-value pairs (inclusive)
     * that the map must contain. Represents the lower bound of the acceptable range.</li>
     * <li><strong>maxSize</strong> - {@code int} - The maximum number of key-value pairs (inclusive)
     * that the map may contain. Represents the upper bound of the acceptable range.</li>
     * <li><strong>actualSize</strong> - {@code int} - The actual number of key-value pairs found in
     * the map during validation. Provides context showing how the actual size relates
     * to the acceptable range.</li>
     * </ul>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class SizeRange extends MapValidationMetadata {
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
     * Validation metadata for single key containment constraints.
     *
     * <p>This class represents validation failures where a map must contain a specific
     * key. It provides key membership validation ensuring that required keys are present
     * in maps.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that required configuration keys are present</li>
     * <li>Ensuring that mandatory parameter keys are included in request maps</li>
     * <li>Checking that maps contain expected lookup keys</li>
     * <li>Verifying that required metadata keys are present</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>key</strong> - {@code K} - The specific key that must be present
     * in the map for validation to pass. This key is checked for membership using
     * the map's containsKey() method, which relies on the key's equals() implementation.
     * Can be null if null keys need to be validated for presence.</li>
     * </ul>
     *
     * @param <K> the type of key being checked for containment
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ContainsKey<K> extends MapValidationMetadata {
        private final K key;

        private ContainsKey(ValidationIdentifier identifier, K key) {
            super(identifier, DefaultValidationCode.COLLECTION_CONTAINS);
            this.key = key;

            // Add message parameters
            addMessageParameter(MessageParameter.KEY, key != null ? key.toString() : "null");
        }
    }

    /**
     * Validation metadata for single key exclusion constraints.
     *
     * <p>This class represents validation failures where a map must not contain a
     * specific key. It provides negative key membership validation ensuring that prohibited
     * or unwanted keys are not present in maps.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring deprecated configuration keys are not used</li>
     * <li>Validating that sensitive or restricted keys are not included</li>
     * <li>Checking that maps exclude problematic parameter keys</li>
     * <li>Verifying that certain keys are filtered out during processing</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>key</strong> - {@code K} - The specific key that must not be present
     * in the map for validation to pass. This key is checked for absence using the
     * map's containsKey() method. The key must not be null as per the factory method
     * validation, ensuring clear identification of prohibited keys.</li>
     * </ul>
     *
     * @param <K> the type of key being checked for exclusion
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class DoesNotContainKey<K> extends MapValidationMetadata {
        private final K key;

        private DoesNotContainKey(ValidationIdentifier identifier, K key) {
            super(identifier, DefaultValidationCode.DOES_NOT_CONTAIN);
            this.key = key;

            // Add message parameters
            addMessageParameter(MessageParameter.KEY, key != null ? key.toString() : "null");
        }
    }

    /**
     * Validation metadata for multiple key containment constraints.
     *
     * <p>This class represents validation failures where a map must contain all
     * keys from a specified collection. It provides comprehensive key membership validation
     * ensuring that maps include all required keys from a reference set.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that all required configuration parameters are present</li>
     * <li>Ensuring that maps include all mandatory field keys</li>
     * <li>Checking that result maps contain all expected data keys</li>
     * <li>Verifying that maps are supersets of required key collections</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>keys</strong> - {@code Collection<K>} - The collection of keys that
     * must all be present in the validated map. This collection is copied defensively
     * during construction to prevent external modification. All keys in this collection
     * must be found in the target map for validation to pass, using the map's keySet()
     * containsAll() method semantics.</li>
     * </ul>
     *
     * @param <K> the type of keys being checked for containment
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ContainsAllKeys<K> extends MapValidationMetadata {
        private final Collection<K> keys;

        private ContainsAllKeys(ValidationIdentifier identifier, Collection<K> keys) {
            super(identifier, DefaultValidationCode.CONTAINS_ALL);
            this.keys = new ArrayList<>(keys);

            // Add message parameters
            addMessageParameter(MessageParameter.KEYS, keys.toString());
        }
    }

    /**
     * Validation metadata for single value containment constraints.
     *
     * <p>This class represents validation failures where a map must contain a specific
     * value. It provides value membership validation ensuring that required values are present
     * in maps.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that required configuration values are set</li>
     * <li>Ensuring that mandatory data values are included in maps</li>
     * <li>Checking that maps contain expected result values</li>
     * <li>Verifying that required status or state values are present</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>value</strong> - {@code V} - The specific value that must be present
     * in the map for validation to pass. This value is checked for membership using
     * the map's containsValue() method, which relies on the value's equals() implementation.
     * Can be null if null values need to be validated for presence.</li>
     * </ul>
     *
     * @param <V> the type of value being checked for containment
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ContainsValue<V> extends MapValidationMetadata {
        private final V value;

        private ContainsValue(ValidationIdentifier identifier, V value) {
            super(identifier, DefaultValidationCode.COLLECTION_CONTAINS);
            this.value = value;

            // Add message parameters
            addMessageParameter(MessageParameter.VALUE, value != null ? value.toString() : "null");
        }
    }

    /**
     * Validation metadata for single value exclusion constraints.
     *
     * <p>This class represents validation failures where a map must not contain a
     * specific value. It provides negative value membership validation ensuring that prohibited
     * or unwanted values are not present in maps.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring banned or inappropriate values are not stored</li>
     * <li>Validating that deprecated configuration values are not used</li>
     * <li>Checking that maps exclude problematic data values</li>
     * <li>Verifying that certain values are filtered out during processing</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>value</strong> - {@code V} - The specific value that must not be present
     * in the map for validation to pass. This value is checked for absence using the
     * map's containsValue() method. The value must not be null as per the factory method
     * validation, ensuring clear identification of prohibited values.</li>
     * </ul>
     *
     * @param <V> the type of value being checked for exclusion
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class DoesNotContainValue<V> extends MapValidationMetadata {
        private final V value;

        private DoesNotContainValue(ValidationIdentifier identifier, V value) {
            super(identifier, DefaultValidationCode.DOES_NOT_CONTAIN);
            this.value = value;

            // Add message parameters
            addMessageParameter(MessageParameter.VALUE, value != null ? value.toString() : "null");
        }
    }

    /**
     * Validation metadata for universal key predicate constraints.
     *
     * <p>This class represents validation failures where all keys in a map must
     * satisfy a specific condition. It provides universal quantification validation with
     * custom predicate logic and human-readable condition descriptions for map keys.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring all keys in a map meet naming conventions</li>
     * <li>Validating that all parameter keys pass format checks</li>
     * <li>Checking that all configuration keys have required properties</li>
     * <li>Verifying that all lookup keys are properly structured</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>condition</strong> - {@code Predicate<K>} - The predicate function that
     * defines the condition each key must satisfy. This predicate is applied to every
     * key in the map during validation.</li>
     * <li><strong>conditionDescription</strong> - {@code String} - Human-readable description
     * of the condition used in error messages to explain what requirement the keys failed
     * to meet. Must not be null or blank.</li>
     * </ul>
     *
     * @param <K> the type of keys in the map being validated
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AllKeysMatch<K> extends MapValidationMetadata {
        private final Predicate<K> condition;
        private final String conditionDescription;

        private AllKeysMatch(ValidationIdentifier identifier, Predicate<K> condition, String description) {
            super(identifier, DefaultValidationCode.ALL_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    /**
     * Validation metadata for universal value predicate constraints.
     *
     * <p>This class represents validation failures where all values in a map must
     * satisfy a specific condition. It provides universal quantification validation with
     * custom predicate logic and human-readable condition descriptions for map values.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring all values in a map meet minimum quality standards</li>
     * <li>Validating that all configuration values pass validation rules</li>
     * <li>Checking that all data values have required properties</li>
     * <li>Verifying that all result values are properly formatted</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>condition</strong> - {@code Predicate<V>} - The predicate function that
     * defines the condition each value must satisfy. This predicate is applied to every
     * value in the map during validation.</li>
     * <li><strong>conditionDescription</strong> - {@code String} - Human-readable description
     * of the condition used in error messages to explain what requirement the values failed
     * to meet. Must not be null or blank.</li>
     * </ul>
     *
     * @param <V> the type of values in the map being validated
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AllValuesMatch<V> extends MapValidationMetadata {
        private final Predicate<V> condition;
        private final String conditionDescription;

        private AllValuesMatch(ValidationIdentifier identifier, Predicate<V> condition, String description) {
            super(identifier, DefaultValidationCode.ALL_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    /**
     * Validation metadata for universal entry predicate constraints.
     *
     * <p>This class represents validation failures where all key-value pairs (entries) in a map must
     * satisfy a specific condition. It provides universal quantification validation with
     * custom bi-predicate logic that can examine both keys and values together.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring all key-value pairs maintain consistency relationships</li>
     * <li>Validating that all entries pass complex business rules</li>
     * <li>Checking that all configuration entries have valid key-value combinations</li>
     * <li>Verifying that all data entries maintain referential integrity</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>condition</strong> - {@code BiPredicate<K, V>} - The bi-predicate function that
     * defines the condition each key-value pair must satisfy. This predicate is applied to every
     * entry in the map during validation, receiving both key and value as parameters.</li>
     * <li><strong>conditionDescription</strong> - {@code String} - Human-readable description
     * of the condition used in error messages to explain what requirement the entries failed
     * to meet. Must not be null or blank.</li>
     * </ul>
     *
     * @param <K> the type of keys in the map being validated
     * @param <V> the type of values in the map being validated
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AllEntriesMatch<K, V> extends MapValidationMetadata {
        private final BiPredicate<K, V> condition;
        private final String conditionDescription;

        private AllEntriesMatch(ValidationIdentifier identifier, BiPredicate<K, V> condition, String description) {
            super(identifier, DefaultValidationCode.ALL_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    /**
     * Validation metadata for existential key predicate constraints.
     *
     * <p>This class represents validation failures where at least one key in a map
     * must satisfy a specific condition. It provides existential quantification validation
     * ensuring that maps contain at least one key meeting the specified criteria.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring at least one administrative key exists in permission maps</li>
     * <li>Validating that maps contain at least one valid identifier key</li>
     * <li>Checking that at least one key meets search criteria</li>
     * <li>Verifying that maps have at least one key with required properties</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>condition</strong> - {@code Predicate<K>} - The predicate function that
     * defines the condition at least one key must satisfy. Applied to map keys
     * until one matching key is found or all keys are checked.</li>
     * <li><strong>conditionDescription</strong> - {@code String} - Human-readable description
     * of the condition used in error messages to explain what requirement no keys satisfied.
     * Must not be null or blank.</li>
     * </ul>
     *
     * @param <K> the type of keys in the map being validated
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AnyKeyMatches<K> extends MapValidationMetadata {
        private final Predicate<K> condition;
        private final String conditionDescription;

        private AnyKeyMatches(ValidationIdentifier identifier, Predicate<K> condition, String description) {
            super(identifier, DefaultValidationCode.ANY_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    /**
     * Validation metadata for existential value predicate constraints.
     *
     * <p>This class represents validation failures where at least one value in a map
     * must satisfy a specific condition. It provides existential quantification validation
     * ensuring that maps contain at least one value meeting the specified criteria.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring at least one value meets minimum quality standards</li>
     * <li>Validating that maps contain at least one valid data value</li>
     * <li>Checking that at least one value passes business rules</li>
     * <li>Verifying that maps have at least one value with required properties</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>condition</strong> - {@code Predicate<V>} - The predicate function that
     * defines the condition at least one value must satisfy. Applied to map values
     * until one matching value is found or all values are checked.</li>
     * <li><strong>conditionDescription</strong> - {@code String} - Human-readable description
     * of the condition used in error messages to explain what requirement no values satisfied.
     * Must not be null or blank.</li>
     * </ul>
     *
     * @param <V> the type of values in the map being validated
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AnyValueMatches<V> extends MapValidationMetadata {
        private final Predicate<V> condition;
        private final String conditionDescription;

        private AnyValueMatches(ValidationIdentifier identifier, Predicate<V> condition, String description) {
            super(identifier, DefaultValidationCode.ANY_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    /**
     * Validation metadata for non-empty map constraints.
     *
     * <p>This class represents validation failures where a map must contain at least one key-value pair.
     * It is used for ensuring that required maps are not empty and contain meaningful data.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that configuration maps have been properly initialized</li>
     * <li>Ensuring user preference maps contain at least one setting</li>
     * <li>Checking that data lookup maps have entries</li>
     * <li>Verifying that result maps contain processed data</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>condition</strong> - {@code BiPredicate<K, V>} - The bi-predicate function that
     * defines the condition at least one key-value pair must satisfy. Applied to map entries
     * until one matching entry is found or all entries are checked.</li>
     * <li><strong>conditionDescription</strong> - {@code String} - Human-readable description
     * of the condition used in error messages to explain what requirement no entries satisfied.
     * Must not be null or blank.</li>
     * </ul>
     *
     * @param <K> the type of keys in the map being validated
     * @param <V> the type of values in the map being validated
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class AnyEntryMatches<K, V> extends MapValidationMetadata {
        private final BiPredicate<K, V> condition;
        private final String conditionDescription;

        private AnyEntryMatches(ValidationIdentifier identifier, BiPredicate<K, V> condition, String description) {
            super(identifier, DefaultValidationCode.ANY_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    /**
     * Validation metadata for negative key predicate constraints.
     *
     * <p>This class represents validation failures where no keys in a map may
     * satisfy a specific condition. It provides negative validation ensuring that maps
     * do not contain any keys meeting the specified (undesirable) criteria.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring no keys contain prohibited patterns or characters</li>
     * <li>Validating that maps have no deprecated or obsolete keys</li>
     * <li>Checking that no keys violate naming conventions</li>
     * <li>Verifying that maps contain no restricted or sensitive keys</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>condition</strong> - {@code Predicate<K>} - The predicate function that
     * defines the condition no keys should satisfy. Applied to all map keys
     * to ensure none match the undesirable criteria.</li>
     * <li><strong>conditionDescription</strong> - {@code String} - Human-readable description
     * of the prohibited condition used in error messages to explain what undesirable property
     * some keys possessed. Must not be null or blank.</li>
     * </ul>
     *
     * @param <K> the type of keys in the map being validated
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoKeyMatches<K> extends MapValidationMetadata {
        private final Predicate<K> condition;
        private final String conditionDescription;

        private NoKeyMatches(ValidationIdentifier identifier, Predicate<K> condition, String description) {
            super(identifier, DefaultValidationCode.NONE_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    /**
     * Validation metadata for negative value predicate constraints.
     *
     * <p>This class represents validation failures where no values in a map may
     * satisfy a specific condition. It provides negative validation ensuring that maps
     * do not contain any values meeting the specified (undesirable) criteria.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring no values contain prohibited content or data</li>
     * <li>Validating that maps have no invalid or corrupted values</li>
     * <li>Checking that no values violate security constraints</li>
     * <li>Verifying that maps contain no deprecated or obsolete values</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>condition</strong> - {@code Predicate<V>} - The predicate function that
     * defines the condition no values should satisfy. Applied to all map values
     * to ensure none match the undesirable criteria.</li>
     * <li><strong>conditionDescription</strong> - {@code String} - Human-readable description
     * of the prohibited condition used in error messages to explain what undesirable property
     * some values possessed. Must not be null or blank.</li>
     * </ul>
     *
     * @param <V> the type of values in the map being validated
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoValueMatches<V> extends MapValidationMetadata {
        private final Predicate<V> condition;
        private final String conditionDescription;

        private NoValueMatches(ValidationIdentifier identifier, Predicate<V> condition, String description) {
            super(identifier, DefaultValidationCode.NONE_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    /**
     * Validation metadata for negative entry predicate constraints.
     *
     * <p>This class represents validation failures where no key-value pairs (entries) in a map may
     * satisfy a specific condition. It provides negative validation ensuring that maps
     * do not contain any entries meeting the specified (undesirable) criteria.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Ensuring no entries violate consistency rules or constraints</li>
     * <li>Validating that maps have no invalid key-value combinations</li>
     * <li>Checking that no entries contain prohibited data relationships</li>
     * <li>Verifying that maps contain no deprecated or obsolete entry patterns</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>condition</strong> - {@code BiPredicate<K, V>} - The bi-predicate function that
     * defines the condition no key-value pairs should satisfy. Applied to all map entries
     * to ensure none match the undesirable criteria.</li>
     * <li><strong>conditionDescription</strong> - {@code String} - Human-readable description
     * of the prohibited condition used in error messages to explain what undesirable property
     * some entries possessed. Must not be null or blank.</li>
     * </ul>
     *
     * @param <K> the type of keys in the map being validated
     * @param <V> the type of values in the map being validated
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoEntryMatches<K, V> extends MapValidationMetadata {
        private final BiPredicate<K, V> condition;
        private final String conditionDescription;

        private NoEntryMatches(ValidationIdentifier identifier, BiPredicate<K, V> condition, String description) {
            super(identifier, DefaultValidationCode.NONE_MATCH);
            this.condition = condition;
            this.conditionDescription = description;

            // Add message parameters
            addMessageParameter(MessageParameter.CONDITION, description);
        }
    }

    // Factory methods

    /**
     * Factory method for creating NotEmpty validation metadata.
     *
     * <p>Creates metadata for validating that a map contains at least one key-value pair.</p>
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
     * <p>Creates metadata for validating that a map is completely empty.</p>
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
     * <p>Creates metadata for validating that a map contains at least the specified
     * minimum number of key-value pairs.</p>
     *
     * @param identifier the validation identifier
     * @param minSize the minimum required number of key-value pairs
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
     * <p>Creates metadata for validating that a map does not exceed the specified
     * maximum number of key-value pairs.</p>
     *
     * @param identifier the validation identifier
     * @param maxSize the maximum allowed number of key-value pairs
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
     * <p>Creates metadata for validating that a map contains exactly the specified
     * number of key-value pairs. This method is typically called during validation with both
     * the required size constraint and the actual size found in the map.</p>
     *
     * @param identifier the validation identifier
     * @param exactSize the exact required number of key-value pairs
     * @param actualSize the actual number of key-value pairs found in the map
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
     * <p>Creates metadata for validating that a map size falls within the specified
     * range (inclusive). This method is typically called during validation with the range
     * constraints and the actual size found in the map.</p>
     *
     * @param identifier the validation identifier
     * @param minSize the minimum allowed number of key-value pairs (inclusive)
     * @param maxSize the maximum allowed number of key-value pairs (inclusive)
     * @param actualSize the actual number of key-value pairs found in the map
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
     * Factory method for creating ContainsKey validation metadata.
     *
     * <p>Creates metadata for validating that a map contains the specified key.
     * The key can be null if null key containment needs to be validated.</p>
     *
     * @param <K> the type of key being checked for containment
     * @param identifier the validation identifier
     * @param key the key that must be present in the map
     * @return ContainsKey metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static <K> ContainsKey<K> containsKey(ValidationIdentifier identifier, K key) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new ContainsKey<>(identifier, key);
    }

    /**
     * Factory method for creating DoesNotContainKey validation metadata.
     *
     * <p>Creates metadata for validating that a map does not contain the specified key.</p>
     *
     * @param <K> the type of key being checked for exclusion
     * @param identifier the validation identifier
     * @param key the key that must not be present in the map
     * @return DoesNotContainKey metadata instance
     * @throws NullPointerException if identifier or key is null
     */
    public static <K> DoesNotContainKey<K> doesNotContainKey(ValidationIdentifier identifier, K key) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(key, "Key must not be null");
        return new DoesNotContainKey<>(identifier, key);
    }

    /**
     * Factory method for creating ContainsAllKeys validation metadata.
     *
     * <p>Creates metadata for validating that a map contains all keys
     * from the specified collection.</p>
     *
     * @param <K> the type of keys being checked for containment
     * @param identifier the validation identifier
     * @param keys the collection of keys that must all be present
     * @return ContainsAllKeys metadata instance
     * @throws NullPointerException if identifier or keys is null
     * @throws IllegalArgumentException if keys collection is empty
     */
    public static <K> ContainsAllKeys<K> containsAllKeys(ValidationIdentifier identifier, Collection<K> keys) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(keys, "Keys collection must not be null");
        if (keys.isEmpty()) {
            throw new IllegalArgumentException("Keys collection must not be empty");
        }
        return new ContainsAllKeys<>(identifier, keys);
    }

    /**
     * Factory method for creating ContainsValue validation metadata.
     *
     * <p>Creates metadata for validating that a map contains the specified value.
     * The value can be null if null value containment needs to be validated.</p>
     *
     * @param <V> the type of value being checked for containment
     * @param identifier the validation identifier
     * @param value the value that must be present in the map
     * @return ContainsValue metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static <V> ContainsValue<V> containsValue(ValidationIdentifier identifier, V value) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        return new ContainsValue<>(identifier, value);
    }

    /**
     * Factory method for creating DoesNotContainValue validation metadata.
     *
     * <p>Creates metadata for validating that a map does not contain the specified value.</p>
     *
     * @param <V> the type of value being checked for exclusion
     * @param identifier the validation identifier
     * @param value the value that must not be present in the map
     * @return DoesNotContainValue metadata instance
     * @throws NullPointerException if identifier or value is null
     */
    public static <V> DoesNotContainValue<V> doesNotContainValue(ValidationIdentifier identifier, V value) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(value, "Value must not be null");
        return new DoesNotContainValue<>(identifier, value);
    }

    /**
     * Factory method for creating AllKeysMatch validation metadata.
     *
     * <p>Creates metadata for validating that all keys in a map satisfy
     * the specified predicate condition.</p>
     *
     * @param <K> the type of keys in the map
     * @param identifier the validation identifier
     * @param condition the predicate that all keys must satisfy
     * @param description human-readable description of the condition for error messages
     * @return AllKeysMatch metadata instance
     * @throws NullPointerException if identifier, condition, or description is null
     * @throws IllegalArgumentException if description is blank
     */
    public static <K> AllKeysMatch<K> allKeysMatch(ValidationIdentifier identifier, Predicate<K> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AllKeysMatch<>(identifier, condition, description);
    }

    /**
     * Factory method for creating AllValuesMatch validation metadata.
     *
     * <p>Creates metadata for validating that all values in a map satisfy
     * the specified predicate condition.</p>
     *
     * @param <V> the type of values in the map
     * @param identifier the validation identifier
     * @param condition the predicate that all values must satisfy
     * @param description human-readable description of the condition for error messages
     * @return AllValuesMatch metadata instance
     * @throws NullPointerException if identifier, condition, or description is null
     * @throws IllegalArgumentException if description is blank
     */
    public static <V> AllValuesMatch<V> allValuesMatch(ValidationIdentifier identifier, Predicate<V> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AllValuesMatch<>(identifier, condition, description);
    }

    /**
     * Factory method for creating AllEntriesMatch validation metadata.
     *
     * <p>Creates metadata for validating that all key-value pairs (entries) in a map satisfy
     * the specified bi-predicate condition.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param identifier the validation identifier
     * @param condition the bi-predicate that all entries must satisfy
     * @param description human-readable description of the condition for error messages
     * @return AllEntriesMatch metadata instance
     * @throws NullPointerException if identifier, condition, or description is null
     * @throws IllegalArgumentException if description is blank
     */
    public static <K, V> AllEntriesMatch<K, V> allEntriesMatch(ValidationIdentifier identifier, BiPredicate<K, V> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AllEntriesMatch<>(identifier, condition, description);
    }

    /**
     * Factory method for creating AnyKeyMatches validation metadata.
     *
     * <p>Creates metadata for validating that at least one key in a map
     * satisfies the specified predicate condition.</p>
     *
     * @param <K> the type of keys in the map
     * @param identifier the validation identifier
     * @param condition the predicate that at least one key must satisfy
     * @param description human-readable description of the condition for error messages
     * @return AnyKeyMatches metadata instance
     * @throws NullPointerException if identifier, condition, or description is null
     * @throws IllegalArgumentException if description is blank
     */
    public static <K> AnyKeyMatches<K> anyKeyMatches(ValidationIdentifier identifier, Predicate<K> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AnyKeyMatches<>(identifier, condition, description);
    }

    /**
     * Factory method for creating AnyValueMatches validation metadata.
     *
     * <p>Creates metadata for validating that at least one value in a map
     * satisfies the specified predicate condition.</p>
     *
     * @param <V> the type of values in the map
     * @param identifier the validation identifier
     * @param condition the predicate that at least one value must satisfy
     * @param description human-readable description of the condition for error messages
     * @return AnyValueMatches metadata instance
     * @throws NullPointerException if identifier, condition, or description is null
     * @throws IllegalArgumentException if description is blank
     */
    public static <V> AnyValueMatches<V> anyValueMatches(ValidationIdentifier identifier, Predicate<V> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AnyValueMatches<>(identifier, condition, description);
    }

    /**
     * Factory method for creating AnyEntryMatches validation metadata.
     *
     * <p>Creates metadata for validating that at least one key-value pair (entry) in a map
     * satisfies the specified bi-predicate condition.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param identifier the validation identifier
     * @param condition the bi-predicate that at least one entry must satisfy
     * @param description human-readable description of the condition for error messages
     * @return AnyEntryMatches metadata instance
     * @throws NullPointerException if identifier, condition, or description is null
     * @throws IllegalArgumentException if description is blank
     */
    public static <K, V> AnyEntryMatches<K, V> anyEntryMatches(ValidationIdentifier identifier, BiPredicate<K, V> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new AnyEntryMatches<>(identifier, condition, description);
    }

    /**
     * Factory method for creating NoKeyMatches validation metadata.
     *
     * <p>Creates metadata for validating that no keys in a map satisfy
     * the specified predicate condition.</p>
     *
     * @param <K> the type of keys in the map
     * @param identifier the validation identifier
     * @param condition the predicate that no keys should satisfy
     * @param description human-readable description of the prohibited condition for error messages
     * @return NoKeyMatches metadata instance
     * @throws NullPointerException if identifier, condition, or description is null
     * @throws IllegalArgumentException if description is blank
     */
    public static <K> NoKeyMatches<K> noKeyMatches(ValidationIdentifier identifier, Predicate<K> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new NoKeyMatches<>(identifier, condition, description);
    }

    /**
     * Factory method for creating NoValueMatches validation metadata.
     *
     * <p>Creates metadata for validating that no values in a map satisfy
     * the specified predicate condition.</p>
     *
     * @param <V> the type of values in the map
     * @param identifier the validation identifier
     * @param condition the predicate that no values should satisfy
     * @param description human-readable description of the prohibited condition for error messages
     * @return NoValueMatches metadata instance
     * @throws NullPointerException if identifier, condition, or description is null
     * @throws IllegalArgumentException if description is blank
     */
    public static <V> NoValueMatches<V> noValueMatches(ValidationIdentifier identifier, Predicate<V> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new NoValueMatches<>(identifier, condition, description);
    }

    /**
     * Factory method for creating NoEntryMatches validation metadata.
     *
     * <p>Creates metadata for validating that no key-value pairs (entries) in a map satisfy
     * the specified bi-predicate condition.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param identifier the validation identifier
     * @param condition the bi-predicate that no entries should satisfy
     * @param description human-readable description of the prohibited condition for error messages
     * @return NoEntryMatches metadata instance
     * @throws NullPointerException if identifier, condition, or description is null
     * @throws IllegalArgumentException if description is blank
     */
    public static <K, V> NoEntryMatches<K, V> noEntryMatches(ValidationIdentifier identifier, BiPredicate<K, V> condition, String description) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(description, MetadataUtils.CONDITION_DESCRIPTION_MUST_NOT_BE_NULL_MSG);
        if (description.isBlank()) {
            throw new IllegalArgumentException("Condition description must not be blank");
        }
        return new NoEntryMatches<>(identifier, condition, description);
    }
}