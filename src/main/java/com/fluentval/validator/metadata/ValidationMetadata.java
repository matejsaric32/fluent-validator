package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Abstract base class that encapsulates metadata information about validation failures.
 * This class serves as the foundation for all validation metadata types in the framework,
 * providing essential information needed for error reporting, message generation, and
 * validation result processing.
 *
 * <p>ValidationMetadata contains both core validation information (identifier, error code)
 * and enhanced metadata fields that support advanced validation scenarios including
 * severity levels, categorization, grouping, and audit trail information.</p>
 *
 * <p>This class is designed to be extended by specific validation metadata implementations
 * that provide additional context for particular validation types (string, number, collection, etc.).</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationIdentifier
 * @see MessageParameter
 * @see ValidationRuleBuilder
 */
@Data
@EqualsAndHashCode
@ToString
public abstract class ValidationMetadata {

    /**
     * Enumeration defining the severity levels for validation failures.
     *
     * <p>Severity levels allow for differentiated handling of validation results,
     * enabling systems to treat different types of validation failures with
     * appropriate urgency and response mechanisms.</p>
     */
    public enum ValidationSeverity {
        /** Critical validation failure that must be addressed immediately */
        ERROR,
        /** Important validation issue that should be addressed but may not block processing */
        WARNING,
        /** Informational validation notice for awareness purposes */
        INFO
    }

    /**
     * The validation identifier that specifies which field, property, or element failed validation.
     *
     * <p>This identifier provides context about what was being validated, enabling precise
     * error reporting and allowing validation results to be mapped back to specific
     * input elements or business objects.</p>
     *
     * <p><strong>Immutable:</strong> Set during construction and cannot be changed.</p>
     */
    private final ValidationIdentifier identifier;

    /**
     * The primary error code that categorizes the type of validation failure.
     *
     * <p>Error codes provide a standardized way to identify validation failure types,
     * enabling consistent error handling, internationalization support, and
     * programmatic error processing. Common examples include "string.max_length",
     * "number.min", "common.not_null".</p>
     *
     * <p><strong>Immutable:</strong> Set during construction and cannot be changed.</p>
     */
    private final String errorCode;

    /**
     * Dynamic parameters used for message template substitution and error context.
     *
     * <p>This map contains key-value pairs that are used to populate message templates
     * with specific validation context information. For example, a max length validation
     * might include parameters for "maxLength" and "actualLength" values that get
     * substituted into error message templates.</p>
     *
     * <p><strong>Mutable:</strong> Parameters can be added throughout the metadata lifecycle
     * to provide rich context for error messages.</p>
     */
    private final Map<String, Object> messageParameters = new HashMap<>();

    /**
     * The severity level of this validation failure.
     *
     * <p>Severity enables differentiated processing of validation results, allowing
     * systems to handle critical errors differently from warnings or informational
     * messages. This supports scenarios where some validation failures are blocking
     * while others are advisory.</p>
     *
     * <p><strong>Default:</strong> {@link ValidationSeverity#ERROR}</p>
     * <p><strong>Mutable:</strong> Can be changed via {@link #setSeverity(ValidationSeverity)}</p>
     */
    private ValidationSeverity severity = ValidationSeverity.ERROR;

    /**
     * Optional category classification for grouping related validation failures.
     *
     * <p>Categories provide a way to organize validation failures into logical groups,
     * supporting use cases like showing all "security" related errors together or
     * filtering validation results by business domain. Examples might include
     * "SECURITY", "BUSINESS_RULES", "DATA_FORMAT".</p>
     *
     * <p><strong>Default:</strong> null (no category assigned)</p>
     * <p><strong>Mutable:</strong> Can be set via {@link #setCategory(String)}</p>
     */
    private String category;

    /**
     * Optional validation group identifier for organizing related validations.
     *
     * <p>Validation groups enable conditional validation scenarios where different
     * sets of validations are applied based on context. For example, "CREATE" vs
     * "UPDATE" operations might have different validation requirements, or different
     * user roles might have different validation rules.</p>
     *
     * <p><strong>Default:</strong> null (no group assigned)</p>
     * <p><strong>Mutable:</strong> Can be set via setter or enrichment methods</p>
     */
    private String validationGroup;

    /**
     * Flag indicating whether this validation failure should block further processing.
     *
     * <p>Blocking validations halt processing when they fail, while non-blocking
     * validations allow processing to continue even with failures. This enables
     * scenarios where some validations are advisory while others are mandatory
     * for business operation continuity.</p>
     *
     * <p><strong>Examples:</strong></p>
     * <ul>
     * <li><strong>Blocking:</strong> Required field validation, security constraints</li>
     * <li><strong>Non-blocking:</strong> Performance recommendations, optional formatting</li>
     * </ul>
     *
     * <p><strong>Default:</strong> true (validation failures block processing)</p>
     * <p><strong>Mutable:</strong> Can be changed via setter or enrichment methods</p>
     */
    private boolean blocking = true;

    /**
     * Timestamp recording when this validation was performed.
     *
     * <p>The validation time provides audit trail information and enables
     * time-based analysis of validation patterns. This can be useful for
     * performance monitoring, debugging temporal validation issues, and
     * compliance reporting that requires timestamp information.</p>
     *
     * <p><strong>Default:</strong> Current system time at object creation</p>
     * <p><strong>Mutable:</strong> Can be changed via setter or enrichment methods</p>
     */
    private Instant validationTime = Instant.now();

    /**
     * Optional identifier of the system component or class that performed the validation.
     *
     * <p>Source information helps with debugging and audit trails by identifying
     * which part of the system generated the validation failure. This might be
     * a class name, service identifier, or other system component reference.</p>
     *
     * <p><strong>Examples:</strong> "UserService", "PaymentValidator", "OrderController"</p>
     *
     * <p><strong>Default:</strong> null (no source specified)</p>
     * <p><strong>Mutable:</strong> Can be set via setter or enrichment methods</p>
     */
    private String source;

    /**
     * Optional secondary error code for providing additional error context.
     *
     * <p>Additional error codes support scenarios where a single validation failure
     * might have multiple relevant error classifications, or where legacy systems
     * require specific error code formats while maintaining modern error handling.</p>
     *
     * <p><strong>Default:</strong> null (no additional error code)</p>
     * <p><strong>Mutable:</strong> Can be set via setter or enrichment methods</p>
     */
    private String additionalErrorCode;

    /**
     * Constructs ValidationMetadata with required core information.
     *
     * <p>This constructor initializes the immutable core fields and automatically
     * adds the field identifier to the message parameters for use in error message
     * template substitution.</p>
     *
     * @param identifier the validation identifier specifying what failed validation
     * @param errorCode the primary error code categorizing the validation failure
     * @throws NullPointerException if identifier or errorCode is null
     */
    protected ValidationMetadata(ValidationIdentifier identifier, String errorCode) {
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
        this.errorCode = Objects.requireNonNull(errorCode, "ErrorCode cannot be null");
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    /**
     * Sets the validation severity and updates message parameters accordingly.
     *
     * <p>This method not only updates the severity field but also automatically
     * adds the severity information to the message parameters, making it available
     * for error message template substitution.</p>
     *
     * @param severity the validation severity level
     * @return this ValidationMetadata instance for method chaining
     * @throws NullPointerException if severity is null
     */
    public ValidationMetadata setSeverity(ValidationSeverity severity) {
        this.severity = Objects.requireNonNull(severity, "Severity cannot be null");
        addMessageParameter(MessageParameter.SEVERITY, severity.name());
        return this;
    }

    /**
     * Sets the validation category and updates message parameters if non-null.
     *
     * <p>This method updates the category field and, if the category is not null,
     * automatically adds it to the message parameters for potential use in
     * error message templates.</p>
     *
     * @param category the validation category, or null to clear
     * @return this ValidationMetadata instance for method chaining
     */
    public ValidationMetadata setCategory(String category) {
        this.category = category;
        if (category != null) {
            addMessageParameter(MessageParameter.CATEGORY, category);
        }
        return this;
    }

    /**
     * Adds a parameter to the message parameters map using a string key.
     *
     * <p>Message parameters are used for template substitution in error messages,
     * allowing dynamic content to be inserted into localized message templates.
     * This method provides the basic mechanism for adding context information
     * that will be available during message generation.</p>
     *
     * @param key the parameter key for template substitution
     * @param message the parameter value to substitute
     */
    protected void addMessageParameter(String key, Object message) {
        messageParameters.put(key, message);
    }

    /**
     * Adds a parameter to the message parameters map using a MessageParameter enum.
     *
     * <p>This method provides a type-safe way to add well-known message parameters
     * by using the MessageParameter enumeration, which helps prevent typos and
     * ensures consistent parameter naming across the validation framework.</p>
     *
     * @param key the MessageParameter enum value providing the parameter key
     * @param message the parameter value to substitute
     */
    protected void addMessageParameter(MessageParameter key, Object message) {
        addMessageParameter(key.getKey(), message);
    }

    /**
     * Applies an enrichment function to this metadata instance for customization.
     *
     * <p>This method enables flexible post-construction customization of validation
     * metadata by accepting a consumer function that can modify any mutable fields.
     * This supports scenarios where validation metadata needs to be augmented with
     * additional context information after creation.</p>
     *
     * <p>Common enrichment operations include:</p>
     * <ul>
     * <li>Setting validation groups based on runtime context</li>
     * <li>Adding source information from calling components</li>
     * <li>Adjusting severity based on business rules</li>
     * <li>Adding custom message parameters for specialized templates</li>
     * </ul>
     *
     * @param enricher a consumer function that customizes this metadata instance
     * @return this ValidationMetadata instance for method chaining
     * @throws NullPointerException if enricher is null
     */
    public ValidationMetadata enrich(Consumer<ValidationMetadata> enricher) {
        Objects.requireNonNull(enricher, "Enricher cannot be null");
        enricher.accept(this);
        return this;
    }
}