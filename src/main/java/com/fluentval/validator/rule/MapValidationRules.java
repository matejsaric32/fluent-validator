package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.MapValidationMetadata;
import com.fluentval.validator.metadata.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.fluentval.validator.rule.ValidationRuleUtils.createSkipNullRule;

/**
 * Utility class providing validation rules for Map types including HashMap, TreeMap, LinkedHashMap,
 * and other Map implementations. This class offers comprehensive validation for map size, key/value
 * constraints, entry matching, and structural requirements.
 *
 * <p>All validation rules in this class automatically skip null values, meaning they will
 * pass validation if the input map is null. Use in combination with {@code CommonValidationRules.notNull()}
 * if null maps should be rejected.</p>
 *
 * <p>Supported map operations include:</p>
 * <ul>
 * <li>Size-based validations (empty, size ranges, exact sizes)</li>
 * <li>Key-based validations (presence, absence, pattern matching)</li>
 * <li>Value-based validations (presence, absence, pattern matching)</li>
 * <li>Entry-based validations (key-value pair constraints)</li>
 * <li>Predicate matching for keys, values, and entries</li>
 * </ul>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationRule
 * @see MapValidationMetadata
 * @see Map
 */
public final class MapValidationRules {

    private MapValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static <K, V> boolean isNotEmpty(final Map<K, V> map) {
            return !map.isEmpty();
        }

        static <K, V> boolean isEmpty(final Map<K, V> map) {
            return map.isEmpty();
        }

        static <K, V> boolean hasMinimumSize(final Map<K, V> map, final int minSize) {
            return map.size() >= minSize;
        }

        static <K, V> boolean hasMaximumSize(final Map<K, V> map, final int maxSize) {
            return map.size() <= maxSize;
        }

        static <K, V> boolean hasExactSize(final Map<K, V> map, final int exactSize) {
            return map.size() == exactSize;
        }

        static <K, V> boolean isInSizeRange(final Map<K, V> map, final int minSize, final int maxSize) {
            return map.size() >= minSize && map.size() <= maxSize;
        }

        static <K, V> boolean containsKey(final Map<K, V> map, final K key) {
            return map.containsKey(key);
        }

        static <K, V> boolean doesNotContainKey(final Map<K, V> map, final K key) {
            return !map.containsKey(key);
        }

        static <K, V> boolean containsAllKeys(final Map<K, V> map, final Collection<K> keys) {
            return map.keySet().containsAll(keys);
        }

        static <K, V> boolean containsValue(final Map<K, V> map, final V value) {
            return map.containsValue(value);
        }

        static <K, V> boolean doesNotContainValue(final Map<K, V> map, final V value) {
            return !map.containsValue(value);
        }

        static <K, V> boolean allKeysMatch(final Map<K, V> map, final Predicate<K> condition) {
            return map.keySet().stream().allMatch(condition);
        }

        static <K, V> boolean allValuesMatch(final Map<K, V> map, final Predicate<V> condition) {
            return map.values().stream().allMatch(condition);
        }

        static <K, V> boolean allEntriesMatch(final Map<K, V> map, final BiPredicate<K, V> condition) {
            return map.entrySet().stream().allMatch(entry -> condition.test(entry.getKey(), entry.getValue()));
        }

        static <K, V> boolean anyKeyMatches(final Map<K, V> map, final Predicate<K> condition) {
            return !map.isEmpty() && map.keySet().stream().anyMatch(condition);
        }

        static <K, V> boolean anyValueMatches(final Map<K, V> map, final Predicate<V> condition) {
            return !map.isEmpty() && map.values().stream().anyMatch(condition);
        }

        static <K, V> boolean anyEntryMatches(final Map<K, V> map, final BiPredicate<K, V> condition) {
            return !map.isEmpty() && map.entrySet().stream().anyMatch(entry -> condition.test(entry.getKey(), entry.getValue()));
        }

        static <K, V> boolean noKeyMatches(final Map<K, V> map, final Predicate<K> condition) {
            return map.keySet().stream().noneMatch(condition);
        }

        static <K, V> boolean noValueMatches(final Map<K, V> map, final Predicate<V> condition) {
            return map.values().stream().noneMatch(condition);
        }

        static <K, V> boolean noEntryMatches(final Map<K, V> map, final BiPredicate<K, V> condition) {
            return map.entrySet().stream().noneMatch(entry -> condition.test(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Creates a validation rule that checks if a map is not empty.
     *
     * <p>This rule validates that the map contains at least one key-value pair.
     * The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @return a ValidationRule that passes if the map is not empty
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration map has settings
     * ValidationResult result = Validator.of(application)
     *     .property(ValidationIdentifier.ofField("settings"), Application::getSettings)
     *         .validate(MapValidationRules.notEmpty())
     *         .end()
     *     .getResult();
     *
     * // Validate that user has assigned permissions
     * ValidationResult userResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("permissions"), User::getPermissions)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(MapValidationRules.notEmpty())
     *         .end()
     *     .getResult();
     *
     * // Validate that order has line items
     * ValidationResult orderResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("lineItems"), Order::getLineItems)
     *         .validate(MapValidationRules.notEmpty())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> notEmpty() {
        return createSkipNullRule(
                ValidationFunctions::isNotEmpty,
                MapValidationMetadata::notEmpty
        );
    }

    /**
     * Creates a validation rule that checks if a map is empty.
     *
     * <p>This rule validates that the map contains no key-value pairs.
     * The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @return a ValidationRule that passes if the map is empty
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that error map is empty (no errors)
     * ValidationResult result = Validator.of(response)
     *     .property(ValidationIdentifier.ofField("errors"), Response::getErrors)
     *         .validate(MapValidationRules.isEmpty())
     *         .end()
     *     .getResult();
     *
     * // Validate that cache is cleared
     * ValidationResult cacheResult = Validator.of(system)
     *     .property(ValidationIdentifier.ofField("cache"), System::getCache)
     *         .validate(MapValidationRules.isEmpty())
     *         .end()
     *     .getResult();
     *
     * // Validate that temporary data is cleaned up
     * ValidationResult cleanupResult = Validator.of(session)
     *     .property(ValidationIdentifier.ofField("tempData"), Session::getTempData)
     *         .validate(MapValidationRules.isEmpty())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> isEmpty() {
        return createSkipNullRule(
                ValidationFunctions::isEmpty,
                MapValidationMetadata::isEmpty
        );
    }

    /**
     * Creates a validation rule that checks if a map has at least the specified minimum size.
     *
     * <p>This rule validates that the map contains at least the minimum number of key-value pairs.
     * The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param min the minimum required size (must be non-negative)
     * @return a ValidationRule that passes if the map size is >= min
     * @throws IllegalArgumentException if min is negative
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration has minimum required settings
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.minSize(5))
     *         .end()
     *     .getResult();
     *
     * // Validate that product has minimum attributes
     * ValidationResult productResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("attributes"), Product::getAttributes)
     *         .validate(MapValidationRules.minSize(3))
     *         .end()
     *     .getResult();
     *
     * // Validate that report has minimum data points
     * ValidationResult reportResult = Validator.of(report)
     *     .property(ValidationIdentifier.ofField("dataPoints"), Report::getDataPoints)
     *         .validate(MapValidationRules.minSize(10))
     *         .validate(MapValidationRules.maxSize(1000))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> minSize(final int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.hasMinimumSize(map, min),
                identifier -> MapValidationMetadata.minSize(identifier, min)
        );
    }

    /**
     * Creates a validation rule that checks if a map does not exceed the specified maximum size.
     *
     * <p>This rule validates that the map contains at most the maximum number of key-value pairs.
     * The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param max the maximum allowed size (must be non-negative)
     * @return a ValidationRule that passes if the map size is <= max
     * @throws IllegalArgumentException if max is negative
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that user preferences don't exceed limit
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("preferences"), User::getPreferences)
     *         .validate(MapValidationRules.maxSize(50))
     *         .end()
     *     .getResult();
     *
     * // Validate that request headers don't exceed maximum
     * ValidationResult requestResult = Validator.of(httpRequest)
     *     .property(ValidationIdentifier.ofField("headers"), HttpRequest::getHeaders)
     *         .validate(MapValidationRules.maxSize(100))
     *         .end()
     *     .getResult();
     *
     * // Validate that metadata size is controlled
     * ValidationResult metadataResult = Validator.of(document)
     *     .property(ValidationIdentifier.ofField("metadata"), Document::getMetadata)
     *         .validate(MapValidationRules.maxSize(25))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> maxSize(final int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Maximum size cannot be negative");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.hasMaximumSize(map, max),
                identifier -> MapValidationMetadata.maxSize(identifier, max)
        );
    }

    /**
     * Creates a validation rule that checks if a map has exactly the specified size.
     *
     * <p>This rule validates that the map contains exactly the required number of key-value pairs.
     * The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param size the exact required size (must be non-negative)
     * @return a ValidationRule that passes if the map size equals the specified size
     * @throws IllegalArgumentException if size is negative
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that RGB color map has exactly 3 components
     * ValidationResult result = Validator.of(color)
     *     .property(ValidationIdentifier.ofField("components"), Color::getComponents)
     *         .validate(MapValidationRules.exactSize(3))
     *         .end()
     *     .getResult();
     *
     * // Validate that coordinate has exactly 2 dimensions
     * ValidationResult coordResult = Validator.of(coordinate)
     *     .property(ValidationIdentifier.ofField("dimensions"), Coordinate::getDimensions)
     *         .validate(MapValidationRules.exactSize(2))
     *         .end()
     *     .getResult();
     *
     * // Validate that form has exactly required fields
     * ValidationResult formResult = Validator.of(form)
     *     .property(ValidationIdentifier.ofField("requiredFields"), Form::getRequiredFields)
     *         .validate(MapValidationRules.exactSize(5))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> exactSize(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.hasExactSize(map, size),
                identifier -> MapValidationMetadata.exactSize(identifier, size, size)
        );
    }

    /**
     * Creates a validation rule that checks if a map size falls within the specified range.
     *
     * <p>This rule validates that the map size is between the minimum and maximum values (inclusive).
     * The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param min the minimum required size (must be non-negative)
     * @param max the maximum allowed size (must be non-negative and >= min)
     * @return a ValidationRule that passes if the map size is between min and max (inclusive)
     * @throws IllegalArgumentException if min or max is negative, or if min > max
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration has appropriate number of settings
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.sizeRange(5, 25))
     *         .end()
     *     .getResult();
     *
     * // Validate that survey has reasonable number of questions
     * ValidationResult surveyResult = Validator.of(survey)
     *     .property(ValidationIdentifier.ofField("questions"), Survey::getQuestions)
     *         .validate(MapValidationRules.sizeRange(3, 20))
     *         .end()
     *     .getResult();
     *
     * // Validate that cache has controlled size
     * ValidationResult cacheResult = Validator.of(cache)
     *     .property(ValidationIdentifier.ofField("entries"), Cache::getEntries)
     *         .validate(MapValidationRules.sizeRange(0, 1000))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> sizeRange(final int min, final int max) {
        if (min < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }

        if (max < 0) {
            throw new IllegalArgumentException("Maximum size cannot be negative");
        }

        if (min > max) {
            throw new IllegalArgumentException("Minimum size cannot be greater than maximum size");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isInSizeRange(value, min, max)) {
                result.addFailure(new ValidationResult.Failure(
                        MapValidationMetadata.sizeRange(identifier, min, max, value.size())
                ));
            }
        };
    }

    /**
     * Creates a validation rule that checks if a map contains the specified key.
     *
     * <p>This rule validates that the map includes the given key based on the
     * {@code equals()} method. The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param key the key that must be present in the map
     * @return a ValidationRule that passes if the map contains the specified key
     * @throws NullPointerException if key is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration contains required settings
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.containsKey("database.url"))
     *         .validate(MapValidationRules.containsKey("security.key"))
     *         .end()
     *     .getResult();
     *
     * // Validate that HTTP request has required headers
     * ValidationResult requestResult = Validator.of(httpRequest)
     *     .property(ValidationIdentifier.ofField("headers"), HttpRequest::getHeaders)
     *         .validate(MapValidationRules.containsKey("Content-Type"))
     *         .validate(MapValidationRules.containsKey("Authorization"))
     *         .end()
     *     .getResult();
     *
     * // Validate that product has essential attributes
     * ValidationResult productResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("attributes"), Product::getAttributes)
     *         .validate(MapValidationRules.containsKey("name"))
     *         .validate(MapValidationRules.containsKey("price"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> containsKey(K key) {
        Objects.requireNonNull(key, "Key cannot be null");

        return createSkipNullRule(
                map -> ValidationFunctions.containsKey(map, key),
                identifier -> MapValidationMetadata.containsKey(identifier, key)
        );
    }

    /**
     * Creates a validation rule that checks if a map does not contain the specified key.
     *
     * <p>This rule validates that the map excludes the given key based on the
     * {@code equals()} method. The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param key the key that must not be present in the map
     * @return a ValidationRule that passes if the map does not contain the specified key
     * @throws NullPointerException if key is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration doesn't contain deprecated settings
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.doesNotContainKey("deprecated.setting"))
     *         .validate(MapValidationRules.doesNotContainKey("old.api.key"))
     *         .end()
     *     .getResult();
     *
     * // Validate that user preferences don't contain forbidden options
     * ValidationResult userResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("preferences"), User::getPreferences)
     *         .validate(MapValidationRules.doesNotContainKey("admin.mode"))
     *         .validate(MapValidationRules.doesNotContainKey("debug.enabled"))
     *         .end()
     *     .getResult();
     *
     * // Validate that request doesn't contain sensitive headers
     * ValidationResult requestResult = Validator.of(httpRequest)
     *     .property(ValidationIdentifier.ofField("headers"), HttpRequest::getHeaders)
     *         .validate(MapValidationRules.doesNotContainKey("X-Internal-Token"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> doesNotContainKey(K key) {
        Objects.requireNonNull(key, "Key cannot be null");

        return createSkipNullRule(
                map -> ValidationFunctions.doesNotContainKey(map, key),
                identifier -> MapValidationMetadata.doesNotContainKey(identifier, key)
        );
    }

    /**
     * Creates a validation rule that checks if a map contains all keys from the specified collection.
     *
     * <p>This rule validates that the map includes every key from the specified collection
     * based on the {@code equals()} method. The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param keys the collection of keys that must all be present
     * @return a ValidationRule that passes if the map contains all specified keys
     * @throws NullPointerException if keys is null
     * @throws IllegalArgumentException if keys is empty
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration has all required settings
     * List<String> requiredSettings = List.of(
     *     "database.url", "database.username", "security.key", "logging.level"
     * );
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.containsAllKeys(requiredSettings))
     *         .end()
     *     .getResult();
     *
     * // Validate that product has all mandatory attributes
     * Set<String> mandatoryAttributes = Set.of("name", "price", "category", "description");
     * ValidationResult productResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("attributes"), Product::getAttributes)
     *         .validate(MapValidationRules.containsAllKeys(mandatoryAttributes))
     *         .end()
     *     .getResult();
     *
     * // Validate that API response has all expected fields
     * List<String> expectedFields = List.of("status", "data", "timestamp", "version");
     * ValidationResult apiResult = Validator.of(apiResponse)
     *     .property(ValidationIdentifier.ofField("fields"), ApiResponse::getFields)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(MapValidationRules.containsAllKeys(expectedFields))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> containsAllKeys(Collection<K> keys) {
        Objects.requireNonNull(keys, "Keys collection cannot be null");

        if (keys.isEmpty()) {
            throw new IllegalArgumentException("Keys collection cannot be empty");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.containsAllKeys(map, keys),
                identifier -> MapValidationMetadata.containsAllKeys(identifier, keys)
        );
    }

    /**
     * Creates a validation rule that checks if a map contains the specified value.
     *
     * <p>This rule validates that the map includes the given value based on the
     * {@code equals()} method. The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param value the value that must be present in the map
     * @return a ValidationRule that passes if the map contains the specified value
     * @throws NullPointerException if value is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration has required values
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.containsValue("production"))
     *         .validate(MapValidationRules.containsValue("enabled"))
     *         .end()
     *     .getResult();
     *
     * // Validate that user roles include specific permissions
     * ValidationResult roleResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("rolePermissions"), User::getRolePermissions)
     *         .validate(MapValidationRules.containsValue(Permission.READ))
     *         .end()
     *     .getResult();
     *
     * // Validate that inventory has specific status
     * ValidationResult inventoryResult = Validator.of(inventory)
     *     .property(ValidationIdentifier.ofField("itemStatuses"), Inventory::getItemStatuses)
     *         .validate(MapValidationRules.containsValue(ItemStatus.AVAILABLE))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> containsValue(V value) {
        Objects.requireNonNull(value, "Value cannot be null");

        return createSkipNullRule(
                map -> ValidationFunctions.containsValue(map, value),
                identifier -> MapValidationMetadata.containsValue(identifier, value)
        );
    }

    /**
     * Creates a validation rule that checks if a map does not contain the specified value.
     *
     * <p>This rule validates that the map excludes the given value based on the
     * {@code equals()} method. The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param value the value that must not be present in the map
     * @return a ValidationRule that passes if the map does not contain the specified value
     * @throws NullPointerException if value is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration doesn't have forbidden values
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.doesNotContainValue("debug"))
     *         .validate(MapValidationRules.doesNotContainValue("unsafe"))
     *         .end()
     *     .getResult();
     *
     * // Validate that user permissions don't include dangerous ones
     * ValidationResult userResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("permissions"), User::getPermissions)
     *         .validate(MapValidationRules.doesNotContainValue(Permission.DELETE_ALL))
     *         .validate(MapValidationRules.doesNotContainValue(Permission.SYSTEM_ADMIN))
     *         .end()
     *     .getResult();
     *
     * // Validate that order statuses don't include invalid ones
     * ValidationResult orderResult = Validator.of(orderBatch)
     *     .property(ValidationIdentifier.ofField("orderStatuses"), OrderBatch::getOrderStatuses)
     *         .validate(MapValidationRules.doesNotContainValue(OrderStatus.CORRUPTED))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> doesNotContainValue(V value) {
        Objects.requireNonNull(value, "Value cannot be null");

        return createSkipNullRule(
                map -> ValidationFunctions.doesNotContainValue(map, value),
                identifier -> MapValidationMetadata.doesNotContainValue(identifier, value)
        );
    }

    /**
     * Creates a validation rule that checks if all keys in a map match the specified condition.
     *
     * <p>This rule validates that every key in the map satisfies the given predicate.
     * The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param condition the predicate that all keys must satisfy
     * @param conditionDescription a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if all keys match the condition
     * @throws NullPointerException if condition is null
     * @throws IllegalArgumentException if conditionDescription is null or blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that all configuration keys follow naming convention
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.allKeysMatch(
     *             key -> key.contains(".") && key.length() > 3,
     *             "follow dot notation and be longer than 3 characters"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that all HTTP header names are uppercase
     * ValidationResult headerResult = Validator.of(httpRequest)
     *     .property(ValidationIdentifier.ofField("headers"), HttpRequest::getHeaders)
     *         .validate(MapValidationRules.allKeysMatch(
     *             headerName -> headerName.equals(headerName.toUpperCase()),
     *             "be uppercase"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that all product attribute keys are non-empty
     * ValidationResult productResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("attributes"), Product::getAttributes)
     *         .validate(MapValidationRules.allKeysMatch(
     *             key -> key != null && !key.trim().isEmpty(),
     *             "be non-empty strings"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> allKeysMatch(Predicate<K> condition, String conditionDescription) {
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.allKeysMatch(map, condition),
                identifier -> MapValidationMetadata.allKeysMatch(identifier, condition, conditionDescription)
        );
    }

    /**
     * Creates a validation rule that checks if all values in a map match the specified condition.
     *
     * <p>This rule validates that every value in the map satisfies the given predicate.
     * The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param condition the predicate that all values must satisfy
     * @param conditionDescription a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if all values match the condition
     * @throws NullPointerException if condition is null
     * @throws IllegalArgumentException if conditionDescription is null or blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that all configuration values are non-empty
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.allValuesMatch(
     *             value -> value != null && !value.toString().trim().isEmpty(),
     *             "be non-empty"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that all prices are positive
     * ValidationResult priceResult = Validator.of(catalog)
     *     .property(ValidationIdentifier.ofField("itemPrices"), Catalog::getItemPrices)
     *         .validate(MapValidationRules.allValuesMatch(
     *             price -> price.compareTo(BigDecimal.ZERO) > 0,
     *             "be positive amounts"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that all user permissions are valid
     * ValidationResult permissionResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("rolePermissions"), User::getRolePermissions)
     *         .validate(MapValidationRules.allValuesMatch(
     *             permission -> permission != null && permission.isValid(),
     *             "be valid permissions"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> allValuesMatch(Predicate<V> condition, String conditionDescription) {
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.allValuesMatch(map, condition),
                identifier -> MapValidationMetadata.allValuesMatch(identifier, condition, conditionDescription)
        );
    }

    /**
     * Creates a validation rule that checks if all entries in a map match the specified condition.
     *
     * <p>This rule validates that every key-value pair in the map satisfies the given BiPredicate.
     * The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param condition the BiPredicate that all key-value pairs must satisfy
     * @param conditionDescription a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if all entries match the condition
     * @throws NullPointerException if condition is null
     * @throws IllegalArgumentException if conditionDescription is null or blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that all configuration entries have valid key-value relationships
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.allEntriesMatch(
     *             (key, value) -> key != null && value != null && key.length() <= value.toString().length(),
     *             "have keys shorter than or equal to their values"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that all product categories match their pricing tiers
     * ValidationResult categoryResult = Validator.of(catalog)
     *     .property(ValidationIdentifier.ofField("categoryPricing"), Catalog::getCategoryPricing)
     *         .validate(MapValidationRules.allEntriesMatch(
     *             (category, price) ->
     *                 category.getPriceCategory().isCompatibleWith(price),
     *             "have compatible category and price relationships"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that all user role assignments are consistent
     * ValidationResult roleResult = Validator.of(system)
     *     .property(ValidationIdentifier.ofField("userRoles"), System::getUserRoles)
     *         .validate(MapValidationRules.allEntriesMatch(
     *             (userId, role) -> userId > 0 && role.isValidForUser(userId),
     *             "have valid user-role assignments"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> allEntriesMatch(BiPredicate<K, V> condition, String conditionDescription) {
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.allEntriesMatch(map, condition),
                identifier -> MapValidationMetadata.allEntriesMatch(identifier, condition, conditionDescription)
        );
    }

    /**
     * Creates a validation rule that checks if at least one key in a map matches the specified condition.
     *
     * <p>This rule validates that at least one key in the map satisfies the given predicate.
     * For empty maps, this rule will fail. The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param condition the predicate that at least one key must satisfy
     * @param conditionDescription a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if at least one key matches the condition
     * @throws NullPointerException if condition is null
     * @throws IllegalArgumentException if conditionDescription is null or blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration has at least one debug setting
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.anyKeyMatches(
     *             key -> key.startsWith("debug."),
     *             "be a debug setting"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that headers include at least one authentication header
     * ValidationResult headerResult = Validator.of(httpRequest)
     *     .property(ValidationIdentifier.ofField("headers"), HttpRequest::getHeaders)
     *         .validate(MapValidationRules.anyKeyMatches(
     *             headerName -> headerName.toLowerCase().contains("auth"),
     *             "be an authentication header"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that product has at least one priority attribute
     * ValidationResult productResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("attributes"), Product::getAttributes)
     *         .validate(MapValidationRules.anyKeyMatches(
     *             key -> List.of("priority", "importance", "ranking").contains(key),
     *             "be a priority-related attribute"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> anyKeyMatches(Predicate<K> condition, String conditionDescription) {
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.anyKeyMatches(map, condition),
                identifier -> MapValidationMetadata.anyKeyMatches(identifier, condition, conditionDescription)
        );
    }

    /**
     * Creates a validation rule that checks if at least one value in a map matches the specified condition.
     *
     * <p>This rule validates that at least one value in the map satisfies the given predicate.
     * For empty maps, this rule will fail. The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param condition the predicate that at least one value must satisfy
     * @param conditionDescription a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if at least one value matches the condition
     * @throws NullPointerException if condition is null
     * @throws IllegalArgumentException if conditionDescription is null or blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration has at least one enabled feature
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("features"), Config::getFeatures)
     *         .validate(MapValidationRules.anyValueMatches(
     *             value -> "enabled".equals(value) || Boolean.TRUE.equals(value),
     *             "be enabled"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that order has at least one item in stock
     * ValidationResult orderResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("itemQuantities"), Order::getItemQuantities)
     *         .validate(MapValidationRules.anyValueMatches(
     *             quantity -> quantity > 0,
     *             "be in stock (quantity > 0)"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that user has at least one active permission
     * ValidationResult userResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("permissions"), User::getPermissions)
     *         .validate(MapValidationRules.anyValueMatches(
     *             permission -> permission.isActive(),
     *             "be active"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> anyValueMatches(Predicate<V> condition, String conditionDescription) {
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.anyValueMatches(map, condition),
                identifier -> MapValidationMetadata.anyValueMatches(identifier, condition, conditionDescription)
        );
    }

    /**
     * Creates a validation rule that checks if at least one entry in a map matches the specified condition.
     *
     * <p>This rule validates that at least one key-value pair in the map satisfies the given BiPredicate.
     * For empty maps, this rule will fail. The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param condition the BiPredicate that at least one key-value pair must satisfy
     * @param conditionDescription a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if at least one entry matches the condition
     * @throws NullPointerException if condition is null
     * @throws IllegalArgumentException if conditionDescription is null or blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration has at least one high-priority setting
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.anyEntryMatches(
     *             (key, value) -> key.contains("priority") && "high".equals(value),
     *             "be a high-priority setting"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that catalog has at least one discounted item
     * ValidationResult catalogResult = Validator.of(catalog)
     *     .property(ValidationIdentifier.ofField("itemPrices"), Catalog::getItemPrices)
     *         .validate(MapValidationRules.anyEntryMatches(
     *             (itemName, price) ->
     *                 itemName.contains("sale") || price.compareTo(new BigDecimal("50")) < 0,
     *             "be a discounted item"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that system has at least one admin user assignment
     * ValidationResult systemResult = Validator.of(system)
     *     .property(ValidationIdentifier.ofField("userRoles"), System::getUserRoles)
     *         .validate(MapValidationRules.anyEntryMatches(
     *             (userId, role) -> role.isAdmin() && userId > 0,
     *             "be an admin user assignment"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> anyEntryMatches(BiPredicate<K, V> condition, String conditionDescription) {
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.anyEntryMatches(map, condition),
                identifier -> MapValidationMetadata.anyEntryMatches(identifier, condition, conditionDescription)
        );
    }

    /**
     * Creates a validation rule that checks if no keys in a map match the specified condition.
     *
     * <p>This rule validates that none of the keys in the map satisfy the given predicate.
     * The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param condition the predicate that no keys should satisfy
     * @param conditionDescription a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if no keys match the condition
     * @throws NullPointerException if condition is null
     * @throws IllegalArgumentException if conditionDescription is null or blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration has no deprecated keys
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.noKeyMatches(
     *             key -> key.contains("deprecated") || key.startsWith("old."),
     *             "be deprecated"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that HTTP headers have no forbidden names
     * ValidationResult headerResult = Validator.of(httpRequest)
     *     .property(ValidationIdentifier.ofField("headers"), HttpRequest::getHeaders)
     *         .validate(MapValidationRules.noKeyMatches(
     *             headerName -> List.of("X-Internal", "X-Debug", "X-Admin").contains(headerName),
     *             "be internal or debug headers"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that product attributes have no empty keys
     * ValidationResult productResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("attributes"), Product::getAttributes)
     *         .validate(MapValidationRules.noKeyMatches(
     *             key -> key == null || key.trim().isEmpty(),
     *             "be null or empty"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> noKeyMatches(Predicate<K> condition, String conditionDescription) {
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.noKeyMatches(map, condition),
                identifier -> MapValidationMetadata.noKeyMatches(identifier, condition, conditionDescription)
        );
    }

    /**
     * Creates a validation rule that checks if no values in a map match the specified condition.
     *
     * <p>This rule validates that none of the values in the map satisfy the given predicate.
     * The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param condition the predicate that no values should satisfy
     * @param conditionDescription a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if no values match the condition
     * @throws NullPointerException if condition is null
     * @throws IllegalArgumentException if conditionDescription is null or blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration has no null or empty values
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.noValueMatches(
     *             value -> value == null || value.toString().trim().isEmpty(),
     *             "be null or empty"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that prices have no negative values
     * ValidationResult priceResult = Validator.of(catalog)
     *     .property(ValidationIdentifier.ofField("itemPrices"), Catalog::getItemPrices)
     *         .validate(MapValidationRules.noValueMatches(
     *             price -> price.compareTo(BigDecimal.ZERO) < 0,
     *             "be negative"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that user permissions have no invalid states
     * ValidationResult permissionResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("permissions"), User::getPermissions)
     *         .validate(MapValidationRules.noValueMatches(
     *             permission -> permission == null || !permission.isValid(),
     *             "be null or invalid"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> noValueMatches(Predicate<V> condition, String conditionDescription) {
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.noValueMatches(map, condition),
                identifier -> MapValidationMetadata.noValueMatches(identifier, condition, conditionDescription)
        );
    }

    /**
     * Creates a validation rule that checks if no entries in a map match the specified condition.
     *
     * <p>This rule validates that none of the key-value pairs in the map satisfy the given BiPredicate.
     * The rule automatically skips validation for null maps.</p>
     *
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @param condition the BiPredicate that no key-value pairs should satisfy
     * @param conditionDescription a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if no entries match the condition
     * @throws NullPointerException if condition is null
     * @throws IllegalArgumentException if conditionDescription is null or blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that configuration has no invalid key-value combinations
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settings"), Config::getSettings)
     *         .validate(MapValidationRules.noEntryMatches(
     *             (key, value) -> key.contains("secure") && value.toString().contains("debug"),
     *             "combine secure keys with debug values"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that catalog has no inconsistent pricing
     * ValidationResult catalogResult = Validator.of(catalog)
     *     .property(ValidationIdentifier.ofField("itemPrices"), Catalog::getItemPrices)
     *         .validate(MapValidationRules.noEntryMatches(
     *             (itemName, price) ->
     *                 itemName.contains("premium") && price.compareTo(new BigDecimal("10")) < 0,
     *             "have premium items with low prices"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that system has no conflicting user role assignments
     * ValidationResult systemResult = Validator.of(system)
     *     .property(ValidationIdentifier.ofField("userRoles"), System::getUserRoles)
     *         .validate(MapValidationRules.noEntryMatches(
     *             (userId, role) -> userId <= 0 || role.isGuest() && role.hasAdminPrivileges(),
     *             "have invalid user IDs or conflicting guest-admin roles"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Complex validation combining multiple map rules
     * ValidationResult complexResult = Validator.of(application)
     *     .property(ValidationIdentifier.ofField("configuration"), Application::getConfiguration)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(MapValidationRules.notEmpty())
     *         .validate(MapValidationRules.sizeRange(5, 50))
     *         .validate(MapValidationRules.containsKey("environment"))
     *         .validate(MapValidationRules.containsKey("database.url"))
     *         .validate(MapValidationRules.allValuesMatch(
     *             value -> value != null && !value.toString().trim().isEmpty(),
     *             "be non-empty"
     *         ))
     *         .validate(MapValidationRules.noKeyMatches(
     *             key -> key.contains("password") || key.contains("secret"),
     *             "contain sensitive information in keys"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <K, V> ValidationRule<Map<K, V>> noEntryMatches(BiPredicate<K, V> condition, String conditionDescription) {
        Objects.requireNonNull(condition, MetadataUtils.CONDITION_PREDICATE_MUST_NOT_BE_NULL_MSG);

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                map -> ValidationFunctions.noEntryMatches(map, condition),
                identifier -> MapValidationMetadata.noEntryMatches(identifier, condition, conditionDescription)
        );
    }
}