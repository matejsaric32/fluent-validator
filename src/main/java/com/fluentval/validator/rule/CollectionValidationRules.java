package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.CollectionValidationMetadata;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import static com.fluentval.validator.rule.ValidationRuleUtils.createSkipNullRule;

/**
 * Utility class providing validation rules for Collection types including Lists, Sets, and other Collection implementations.
 * This class offers comprehensive validation for collection size, content, element matching, and structural constraints.
 *
 * <p>All validation rules in this class automatically skip null values, meaning they will
 * pass validation if the input collection is null. Use in combination with {@code CommonValidationRules.notNull()}
 * if null collections should be rejected.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationRule
 * @see CollectionValidationMetadata
 */
public final class CollectionValidationRules {

    private CollectionValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static <T extends Collection<?>> boolean isNotEmpty(final T collection) {
            return !collection.isEmpty();
        }

        static <T extends Collection<?>> boolean isEmpty(final T collection) {
            return collection.isEmpty();
        }

        static <T extends Collection<?>> boolean hasMinimumSize(final T collection, final int minSize) {
            return collection.size() >= minSize;
        }

        static <T extends Collection<?>> boolean hasMaximumSize(final T collection, final int maxSize) {
            return collection.size() <= maxSize;
        }

        static <T extends Collection<?>> boolean hasExactSize(final T collection, final int exactSize) {
            return collection.size() == exactSize;
        }

        static <T extends Collection<?>> boolean isInSizeRange(final T collection, final int minSize, final int maxSize) {
            return collection.size() >= minSize && collection.size() <= maxSize;
        }

        static <T extends Collection<E>, E> boolean allElementsMatch(final T collection, final Predicate<E> condition) {
            return collection.stream().allMatch(condition);
        }

        static <T extends Collection<E>, E> boolean anyElementMatches(final T collection, final Predicate<E> condition) {
            return !collection.isEmpty() && collection.stream().anyMatch(condition);
        }

        static <T extends Collection<E>, E> boolean noElementMatches(final T collection, final Predicate<E> condition) {
            return collection.stream().noneMatch(condition);
        }

        static <T extends Collection<?>> boolean hasNoDuplicates(final T collection) {
            return collection.stream().distinct().count() == collection.size();
        }

        static <T extends Collection<E>, E> boolean containsElement(final T collection, final E element) {
            return collection.contains(element);
        }

        static <T extends Collection<E>, E> boolean doesNotContainElement(final T collection, final E element) {
            return !collection.contains(element);
        }

        static <T extends Collection<E>, E> boolean containsAllElements(final T collection, final Collection<E> elements) {
            return collection.containsAll(elements);
        }

        static <T extends Collection<E>, E> boolean containsNoElements(final T collection, final Collection<E> elements) {
            return collection.stream().noneMatch(elements::contains);
        }
    }

    /**
     * Creates a validation rule that checks if a collection is not empty.
     *
     * <p>This rule validates that the collection contains at least one element.
     * The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @return a ValidationRule that passes if the collection is not empty
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that a shopping cart has items
     * ValidationResult result = Validator.of(shoppingCart)
     *     .property(ValidationIdentifier.ofField("items"), ShoppingCart::getItems)
     *         .validate(CollectionValidationRules.notEmpty())
     *         .end()
     *     .getResult();
     *
     * // Validate that a user has assigned roles
     * ValidationResult userResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("roles"), User::getRoles)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(CollectionValidationRules.notEmpty())
     *         .end()
     *     .getResult();
     *
     * // Validate that a form has required fields
     * ValidationResult formResult = Validator.of(form)
     *     .property(ValidationIdentifier.ofField("requiredFields"), Form::getRequiredFields)
     *         .validate(CollectionValidationRules.notEmpty())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<?>> ValidationRule<T> notEmpty() {
        return createSkipNullRule(
                ValidationFunctions::isNotEmpty,
                CollectionValidationMetadata::notEmpty
        );
    }

    /**
     * Creates a validation rule that checks if a collection is empty.
     *
     * <p>This rule validates that the collection contains no elements.
     * The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @return a ValidationRule that passes if the collection is empty
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that error list is empty (no errors)
     * ValidationResult result = Validator.of(response)
     *     .property(ValidationIdentifier.ofField("errors"), Response::getErrors)
     *         .validate(CollectionValidationRules.isEmpty())
     *         .end()
     *     .getResult();
     *
     * // Validate that pending tasks are cleared
     * ValidationResult taskResult = Validator.of(project)
     *     .property(ValidationIdentifier.ofField("pendingTasks"), Project::getPendingTasks)
     *         .validate(CollectionValidationRules.isEmpty())
     *         .end()
     *     .getResult();
     *
     * // Validate that temporary files are cleaned up
     * ValidationResult cleanupResult = Validator.of(system)
     *     .property(ValidationIdentifier.ofField("tempFiles"), System::getTempFiles)
     *         .validate(CollectionValidationRules.isEmpty())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<?>> ValidationRule<T> isEmpty() {
        return createSkipNullRule(
                ValidationFunctions::isEmpty,
                CollectionValidationMetadata::isEmpty
        );
    }

    /**
     * Creates a validation rule that checks if a collection has at least the specified minimum size.
     *
     * <p>This rule validates that the collection contains at least the minimum number of elements.
     * The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @param min the minimum required size (must be non-negative)
     * @return a ValidationRule that passes if the collection size is >= min
     * @throws IllegalArgumentException if min is negative
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that a team has at least 3 members
     * ValidationResult result = Validator.of(team)
     *     .property(ValidationIdentifier.ofField("members"), Team::getMembers)
     *         .validate(CollectionValidationRules.minSize(3))
     *         .end()
     *     .getResult();
     *
     * // Validate that a survey has minimum responses
     * ValidationResult surveyResult = Validator.of(survey)
     *     .property(ValidationIdentifier.ofField("responses"), Survey::getResponses)
     *         .validate(CollectionValidationRules.minSize(10))
     *         .end()
     *     .getResult();
     *
     * // Validate that an order has required items
     * ValidationResult orderResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("items"), Order::getItems)
     *         .validate(CollectionValidationRules.minSize(1))
     *         .validate(CollectionValidationRules.maxSize(50))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<?>> ValidationRule<T> minSize(final int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.hasMinimumSize(collection, min),
                identifier -> CollectionValidationMetadata.minSize(identifier, min)
        );
    }

    /**
     * Creates a validation rule that checks if a collection does not exceed the specified maximum size.
     *
     * <p>This rule validates that the collection contains at most the maximum number of elements.
     * The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @param max the maximum allowed size (must be non-negative)
     * @return a ValidationRule that passes if the collection size is <= max
     * @throws IllegalArgumentException if max is negative
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that a playlist doesn't exceed maximum songs
     * ValidationResult result = Validator.of(playlist)
     *     .property(ValidationIdentifier.ofField("songs"), Playlist::getSongs)
     *         .validate(CollectionValidationRules.maxSize(1000))
     *         .end()
     *     .getResult();
     *
     * // Validate that file attachments don't exceed limit
     * ValidationResult emailResult = Validator.of(email)
     *     .property(ValidationIdentifier.ofField("attachments"), Email::getAttachments)
     *         .validate(CollectionValidationRules.maxSize(10))
     *         .end()
     *     .getResult();
     *
     * // Validate that search results are limited
     * ValidationResult searchResult = Validator.of(searchResponse)
     *     .property(ValidationIdentifier.ofField("results"), SearchResponse::getResults)
     *         .validate(CollectionValidationRules.maxSize(100))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<?>> ValidationRule<T> maxSize(final int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Maximum size cannot be negative");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.hasMaximumSize(collection, max),
                identifier -> CollectionValidationMetadata.maxSize(identifier, max)
        );
    }

    /**
     * Creates a validation rule that checks if a collection has exactly the specified size.
     *
     * <p>This rule validates that the collection contains exactly the required number of elements.
     * The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @param size the exact required size (must be non-negative)
     * @return a ValidationRule that passes if the collection size equals the specified size
     * @throws IllegalArgumentException if size is negative
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that RGB color has exactly 3 components
     * ValidationResult result = Validator.of(color)
     *     .property(ValidationIdentifier.ofField("components"), Color::getComponents)
     *         .validate(CollectionValidationRules.exactSize(3))
     *         .end()
     *     .getResult();
     *
     * // Validate that a coordinate has exactly 2 values (x, y)
     * ValidationResult coordResult = Validator.of(coordinate)
     *     .property(ValidationIdentifier.ofField("values"), Coordinate::getValues)
     *         .validate(CollectionValidationRules.exactSize(2))
     *         .end()
     *     .getResult();
     *
     * // Validate that a dice roll has exactly 6 possible outcomes
     * ValidationResult diceResult = Validator.of(dice)
     *     .property(ValidationIdentifier.ofField("faces"), Dice::getFaces)
     *         .validate(CollectionValidationRules.exactSize(6))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<?>> ValidationRule<T> exactSize(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.hasExactSize(collection, size),
                identifier -> CollectionValidationMetadata.exactSize(identifier, size, size)
        );
    }

    /**
     * Creates a validation rule that checks if a collection size falls within the specified range.
     *
     * <p>This rule validates that the collection size is between the minimum and maximum values (inclusive).
     * The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @param min the minimum required size (must be non-negative)
     * @param max the maximum allowed size (must be non-negative and >= min)
     * @return a ValidationRule that passes if the collection size is between min and max (inclusive)
     * @throws IllegalArgumentException if min or max is negative, or if min > max
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that a team has appropriate number of members
     * ValidationResult result = Validator.of(team)
     *     .property(ValidationIdentifier.ofField("members"), Team::getMembers)
     *         .validate(CollectionValidationRules.sizeRange(5, 12))
     *         .end()
     *     .getResult();
     *
     * // Validate that a poll has reasonable number of options
     * ValidationResult pollResult = Validator.of(poll)
     *     .property(ValidationIdentifier.ofField("options"), Poll::getOptions)
     *         .validate(CollectionValidationRules.sizeRange(2, 10))
     *         .end()
     *     .getResult();
     *
     * // Validate that a batch job processes appropriate number of items
     * ValidationResult batchResult = Validator.of(batchJob)
     *     .property(ValidationIdentifier.ofField("items"), BatchJob::getItems)
     *         .validate(CollectionValidationRules.sizeRange(1, 1000))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<?>> ValidationRule<T> sizeRange(final int min, final int max) {
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
                        CollectionValidationMetadata.sizeRange(identifier, min, max, value.size())
                ));
            }
        };
    }

    /**
     * Creates a validation rule that checks if all elements in a collection match the specified condition.
     *
     * <p>This rule validates that every element in the collection satisfies the given predicate.
     * The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @param <E> the type of elements in the collection
     * @param condition the predicate that all elements must satisfy
     * @param conditionDescription a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if all elements match the condition
     * @throws NullPointerException if condition is null
     * @throws IllegalArgumentException if conditionDescription is null or blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that all ages are positive
     * ValidationResult result = Validator.of(group)
     *     .property(ValidationIdentifier.ofField("ages"), Group::getAges)
     *         .validate(CollectionValidationRules.allMatch(
     *             age -> age > 0,
     *             "be positive"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that all emails are valid format
     * ValidationResult emailResult = Validator.of(mailingList)
     *     .property(ValidationIdentifier.ofField("emails"), MailingList::getEmails)
     *         .validate(CollectionValidationRules.allMatch(
     *             email -> email.contains("@") && email.contains("."),
     *             "be valid email format"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that all prices are within range
     * ValidationResult priceResult = Validator.of(catalog)
     *     .property(ValidationIdentifier.ofField("prices"), Catalog::getPrices)
     *         .validate(CollectionValidationRules.allMatch(
     *             price -> price.compareTo(BigDecimal.ZERO) > 0 &&
     *                      price.compareTo(new BigDecimal("10000")) <= 0,
     *             "be between $0.01 and $10,000"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<E>, E> ValidationRule<T> allMatch(
            final Predicate<E> condition, final String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.allElementsMatch(collection, condition),
                identifier -> CollectionValidationMetadata.allMatch(identifier, condition, conditionDescription)
        );
    }

    /**
     * Creates a validation rule that checks if at least one element in a collection matches the specified condition.
     *
     * <p>This rule validates that at least one element in the collection satisfies the given predicate.
     * For empty collections, this rule will fail. The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @param <E> the type of elements in the collection
     * @param condition the predicate that at least one element must satisfy
     * @param conditionDescription a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if at least one element matches the condition
     * @throws NullPointerException if condition is null
     * @throws IllegalArgumentException if conditionDescription is null or blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that at least one team member is a senior
     * ValidationResult result = Validator.of(team)
     *     .property(ValidationIdentifier.ofField("members"), Team::getMembers)
     *         .validate(CollectionValidationRules.anyMatch(
     *             member -> member.getLevel() == Level.SENIOR,
     *             "be a senior level member"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that at least one product is in stock
     * ValidationResult stockResult = Validator.of(inventory)
     *     .property(ValidationIdentifier.ofField("products"), Inventory::getProducts)
     *         .validate(CollectionValidationRules.anyMatch(
     *             product -> product.getQuantity() > 0,
     *             "be in stock"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that at least one payment method is active
     * ValidationResult paymentResult = Validator.of(account)
     *     .property(ValidationIdentifier.ofField("paymentMethods"), Account::getPaymentMethods)
     *         .validate(CollectionValidationRules.anyMatch(
     *             method -> method.isActive(),
     *             "be active"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<E>, E> ValidationRule<T> anyMatch(
            final Predicate<E> condition, final String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.anyElementMatches(collection, condition),
                identifier -> CollectionValidationMetadata.anyMatch(identifier, condition, conditionDescription)
        );
    }

    /**
     * Creates a validation rule that checks if no elements in a collection match the specified condition.
     *
     * <p>This rule validates that none of the elements in the collection satisfy the given predicate.
     * The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @param <E> the type of elements in the collection
     * @param condition the predicate that no elements should satisfy
     * @param conditionDescription a human-readable description of the condition for error messages
     * @return a ValidationRule that passes if no elements match the condition
     * @throws NullPointerException if condition is null
     * @throws IllegalArgumentException if conditionDescription is null or blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that no users are banned
     * ValidationResult result = Validator.of(userList)
     *     .property(ValidationIdentifier.ofField("users"), UserList::getUsers)
     *         .validate(CollectionValidationRules.noneMatch(
     *             user -> user.getStatus() == UserStatus.BANNED,
     *             "be banned"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that no products are expired
     * ValidationResult productResult = Validator.of(catalog)
     *     .property(ValidationIdentifier.ofField("products"), Catalog::getProducts)
     *         .validate(CollectionValidationRules.noneMatch(
     *             product -> product.getExpirationDate().isBefore(LocalDate.now()),
     *             "be expired"
     *         ))
     *         .end()
     *     .getResult();
     *
     * // Validate that no orders have negative amounts
     * ValidationResult orderResult = Validator.of(batch)
     *     .property(ValidationIdentifier.ofField("orders"), Batch::getOrders)
     *         .validate(CollectionValidationRules.noneMatch(
     *             order -> order.getTotal().compareTo(BigDecimal.ZERO) < 0,
     *             "have negative total"
     *         ))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<E>, E> ValidationRule<T> noneMatch(
            final Predicate<E> condition, final String conditionDescription) {
        Objects.requireNonNull(condition, "Condition predicate cannot be null");

        if (conditionDescription == null || conditionDescription.isBlank()) {
            throw new IllegalArgumentException("Condition description cannot be null or blank");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.noElementMatches(collection, condition),
                identifier -> CollectionValidationMetadata.noneMatch(identifier, condition, conditionDescription)
        );
    }

    /**
     * Creates a validation rule that checks if a collection contains no duplicate elements.
     *
     * <p>This rule validates that all elements in the collection are unique based on their
     * {@code equals()} method. The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @return a ValidationRule that passes if the collection has no duplicate elements
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that user IDs are unique
     * ValidationResult result = Validator.of(userGroup)
     *     .property(ValidationIdentifier.ofField("userIds"), UserGroup::getUserIds)
     *         .validate(CollectionValidationRules.noDuplicates())
     *         .end()
     *     .getResult();
     *
     * // Validate that email addresses are unique
     * ValidationResult emailResult = Validator.of(newsletter)
     *     .property(ValidationIdentifier.ofField("subscribers"), Newsletter::getSubscribers)
     *         .validate(CollectionValidationRules.noDuplicates())
     *         .end()
     *     .getResult();
     *
     * // Validate that product codes are unique
     * ValidationResult productResult = Validator.of(inventory)
     *     .property(ValidationIdentifier.ofField("productCodes"), Inventory::getProductCodes)
     *         .validate(CollectionValidationRules.noDuplicates())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<?>> ValidationRule<T> noDuplicates() {
        return createSkipNullRule(
                ValidationFunctions::hasNoDuplicates,
                CollectionValidationMetadata::noDuplicates
        );
    }

    /**
     * Creates a validation rule that checks if a collection contains the specified element.
     *
     * <p>This rule validates that the collection includes the given element based on the
     * {@code equals()} method. The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @param <E> the type of element to search for
     * @param element the element that must be present in the collection
     * @return a ValidationRule that passes if the collection contains the specified element
     * @throws NullPointerException if element is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that required permissions are present
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("permissions"), User::getPermissions)
     *         .validate(CollectionValidationRules.contains(Permission.READ))
     *         .validate(CollectionValidationRules.contains(Permission.WRITE))
     *         .end()
     *     .getResult();
     *
     * // Validate that essential features are enabled
     * ValidationResult featureResult = Validator.of(application)
     *     .property(ValidationIdentifier.ofField("enabledFeatures"), Application::getEnabledFeatures)
     *         .validate(CollectionValidationRules.contains("USER_AUTHENTICATION"))
     *         .validate(CollectionValidationRules.contains("DATA_ENCRYPTION"))
     *         .end()
     *     .getResult();
     *
     * // Validate that required tags are present
     * ValidationResult tagResult = Validator.of(article)
     *     .property(ValidationIdentifier.ofField("tags"), Article::getTags)
     *         .validate(CollectionValidationRules.contains("published"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<E>, E> ValidationRule<T> contains(final E element) {
        Objects.requireNonNull(element, "Element cannot be null");

        return createSkipNullRule(
                collection -> ValidationFunctions.containsElement(collection, element),
                identifier -> CollectionValidationMetadata.contains(identifier, element)
        );
    }

    /**
     * Creates a validation rule that checks if a collection does not contain the specified element.
     *
     * <p>This rule validates that the collection excludes the given element based on the
     * {@code equals()} method. The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @param <E> the type of element to check for absence
     * @param element the element that must not be present in the collection
     * @return a ValidationRule that passes if the collection does not contain the specified element
     * @throws NullPointerException if element is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that forbidden permissions are not present
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("permissions"), User::getPermissions)
     *         .validate(CollectionValidationRules.doesNotContain(Permission.ADMIN))
     *         .validate(CollectionValidationRules.doesNotContain(Permission.DELETE_ALL))
     *         .end()
     *     .getResult();
     *
     * // Validate that banned words are not present
     * ValidationResult contentResult = Validator.of(comment)
     *     .property(ValidationIdentifier.ofField("words"), Comment::getWords)
     *         .validate(CollectionValidationRules.doesNotContain("spam"))
     *         .validate(CollectionValidationRules.doesNotContain("inappropriate"))
     *         .end()
     *     .getResult();
     *
     * // Validate that dangerous features are disabled
     * ValidationResult safetyResult = Validator.of(system)
     *     .property(ValidationIdentifier.ofField("activeFeatures"), System::getActiveFeatures)
     *         .validate(CollectionValidationRules.doesNotContain("DEBUG_MODE"))
     *         .validate(CollectionValidationRules.doesNotContain("UNSAFE_OPERATIONS"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<E>, E> ValidationRule<T> doesNotContain(final E element) {
        Objects.requireNonNull(element, "Element cannot be null");

        return createSkipNullRule(
                collection -> ValidationFunctions.doesNotContainElement(collection, element),
                identifier -> CollectionValidationMetadata.doesNotContain(identifier, element)
        );
    }

    /**
     * Creates a validation rule that checks if a collection contains all elements from another collection.
     *
     * <p>This rule validates that the collection includes every element from the specified collection
     * based on the {@code equals()} method. The rule automatically skips validation for null collections.</p>
     *
     * Creates a validation rule that checks if a collection contains all elements from another collection.
     *
     * <p>This rule validates that the collection includes every element from the specified collection
     * based on the {@code equals()} method. The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @param <E> the type of elements in the collections
     * @param elements the collection of elements that must all be present
     * @return a ValidationRule that passes if the collection contains all specified elements
     * @throws NullPointerException if elements is null
     * @throws IllegalArgumentException if elements is empty
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that user has all required permissions
     * Set<Permission> requiredPermissions = Set.of(
     *     Permission.READ, Permission.WRITE, Permission.EXECUTE
     * );
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("permissions"), User::getPermissions)
     *         .validate(CollectionValidationRules.containsAll(requiredPermissions))
     *         .end()
     *     .getResult();
     *
     * // Validate that configuration has all mandatory settings
     * List<String> mandatorySettings = List.of(
     *     "database.url", "security.key", "logging.level"
     * );
     * ValidationResult configResult = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("settingKeys"), Config::getSettingKeys)
     *         .validate(CollectionValidationRules.containsAll(mandatorySettings))
     *         .end()
     *     .getResult();
     *
     * // Validate that order contains all requested items
     * List<String> requestedItems = List.of("item1", "item2", "item3");
     * ValidationResult orderResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("itemIds"), Order::getItemIds)
     *         .validate(CollectionValidationRules.containsAll(requestedItems))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<E>, E> ValidationRule<T> containsAll(final Collection<E> elements) {
        Objects.requireNonNull(elements, "Elements collection cannot be null");

        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Elements collection cannot be empty");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.containsAllElements(collection, elements),
                identifier -> CollectionValidationMetadata.containsAll(identifier, elements)
        );
    }

    /**
     * Creates a validation rule that checks if a collection contains none of the elements from another collection.
     *
     * <p>This rule validates that the collection excludes all elements from the specified collection
     * based on the {@code equals()} method. The rule automatically skips validation for null collections.</p>
     *
     * @param <T> the type of collection to validate
     * @param <E> the type of elements in the collections
     * @param elements the collection of elements that must not be present
     * @return a ValidationRule that passes if the collection contains none of the specified elements
     * @throws NullPointerException if elements is null
     * @throws IllegalArgumentException if elements is empty
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that user doesn't have any forbidden permissions
     * Set<Permission> forbiddenPermissions = Set.of(
     *     Permission.DELETE_SYSTEM, Permission.MODIFY_SECURITY, Permission.ACCESS_ADMIN
     * );
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("permissions"), User::getPermissions)
     *         .validate(CollectionValidationRules.containsNone(forbiddenPermissions))
     *         .end()
     *     .getResult();
     *
     * // Validate that content doesn't contain banned words
     * List<String> bannedWords = List.of("spam", "offensive", "inappropriate");
     * ValidationResult contentResult = Validator.of(article)
     *     .property(ValidationIdentifier.ofField("words"), Article::getWords)
     *         .validate(CollectionValidationRules.containsNone(bannedWords))
     *         .end()
     *     .getResult();
     *
     * // Validate that system doesn't have dangerous features enabled
     * Set<String> dangerousFeatures = Set.of(
     *     "UNSAFE_MODE", "DEBUG_IN_PRODUCTION", "DISABLE_SECURITY"
     * );
     * ValidationResult systemResult = Validator.of(system)
     *     .property(ValidationIdentifier.ofField("enabledFeatures"), System::getEnabledFeatures)
     *         .validate(CollectionValidationRules.containsNone(dangerousFeatures))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T extends Collection<E>, E> ValidationRule<T> containsNone(final Collection<E> elements) {
        Objects.requireNonNull(elements, "Elements collection cannot be null");

        if (elements.isEmpty()) {
            throw new IllegalArgumentException("Elements collection cannot be empty");
        }

        return createSkipNullRule(
                collection -> ValidationFunctions.containsNoElements(collection, elements),
                identifier -> CollectionValidationMetadata.containsNone(identifier, elements)
        );
    }
}