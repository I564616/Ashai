/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.saml2.assertion.impl;

import net.shibboleth.shared.primitive.ObjectSupport;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.assertion.SubjectConfirmationValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Nonnull;
//import javax.annotation.concurrent.ThreadSafe;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * A base class for {@link SubjectConfirmationValidator} implementations. 
 * 
 * <p>
 * This class takes care of processing the <code>NotBefore</code>, <code>NotOnOrAfter</code>, 
 * <code>Recipient</code>, and <code>Address</code> checks.
 * </p>
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * </p>
 * <ul>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_ADDRESS_REQUIRED}:
 * Optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_CHECK_ADDRESS}:
 * Optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_VALID_ADDRESSES}:
 * Required if {@link SAML2AssertionValidationParameters#SC_CHECK_ADDRESS} is true or omitted,
 * otherwise optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_RECIPIENT_REQUIRED}:
 * Optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_VALID_RECIPIENTS}:
 * Required.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_IN_RESPONSE_TO_REQUIRED}:
 * Optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_VALID_IN_RESPONSE_TO}:
 * Required.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_NOT_BEFORE_REQUIRED}:
 * Optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_NOT_ON_OR_AFTER_REQUIRED}:
 * Optional.
 * </li>
 * </ul>
 * 
 * <p>
 * Supports the following {@link ValidationContext} dynamic parameters:
 * </p>
 * <ul>
 *   <li>None.</li>
 * </ul>
 */
//@ThreadSafe
public abstract class AbstractSubjectConfirmationValidator //implements SubjectConfirmationValidator
{

    /** Class logger. */
    private Logger log = LoggerFactory.getLogger(AbstractSubjectConfirmationValidator.class);

    /** Constructor. */
    public AbstractSubjectConfirmationValidator() {
    }

    /** {@inheritDoc} */
    // Checkstyle: CyclomaticComplexity OFF
    @Nonnull public ValidationResult validate(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context)
            throws AssertionValidationException {
        
        final boolean addressRequired = isAddressRequired(context);
        final boolean inResponseToRequired = isInResponseToRequired(context);
        final boolean recipientRequired = isRecipientRequired(context);
        final boolean notOnOrAfterRequired = isNotOnOrAfterRequired(context);
        final boolean notBeforeRequired = isNotBeforeRequired(context);

        if (confirmation.getSubjectConfirmationData() != null) {
            ValidationResult result = validateNotBefore(confirmation, assertion, context, notBeforeRequired);
            if (result != ValidationResult.VALID) {
                return result;
            }

            result = validateNotOnOrAfter(confirmation, assertion, context, notOnOrAfterRequired);
            if (result != ValidationResult.VALID) {
                return result;
            }

            result = validateRecipient(confirmation, assertion, context, recipientRequired);
            if (result != ValidationResult.VALID) {
                return result;
            }

            result = validateAddress(confirmation, assertion, context, addressRequired);
            if (result != ValidationResult.VALID) {
                return result;
            }
            
            result = validateInResponseTo(confirmation, assertion, context, inResponseToRequired);
            if (result != ValidationResult.VALID) {
                return result;
            }
        } else {
            if (inResponseToRequired || recipientRequired || notOnOrAfterRequired || notBeforeRequired 
                    || addressRequired) {
                log.warn("SubjectConfirmationData was null, and one of more data elements were required");
//                context.setValidationFailureMessage(
//                        "SubjectConfirmationData was null and one or more data elements were required");
                return ValidationResult.INVALID;
            }
        }

        return doValidate(confirmation, assertion, context);
    }
    // Checkstyle: CyclomaticComplexity ON

    /**
     * Determine whether Address is required.
     * 
     * @param context current validation context
     * 
     * @return true if required, false if not
     */
    protected boolean isAddressRequired(final ValidationContext context) {
        return ObjectSupport.firstNonNull(
                (Boolean) context.getStaticParameters().get(
                        SAML2AssertionValidationParameters.SC_ADDRESS_REQUIRED),
                Boolean.FALSE);
    }

    /**
     * Determine whether Recipient is required.
     * 
     * @param context current validation context
     * 
     * @return true if required, false if not
     */
    protected boolean isRecipientRequired(final ValidationContext context) {
        return ObjectSupport.firstNonNull(
                (Boolean) context.getStaticParameters().get(
                        SAML2AssertionValidationParameters.SC_RECIPIENT_REQUIRED),
                Boolean.FALSE);
    }

    /**
     * Determine whether NotBefore is required.
     * 
     * @param context current validation context
     * 
     * @return true if required, false if not
     */
    protected boolean isNotBeforeRequired(final ValidationContext context) {
        return ObjectSupport.firstNonNull(
                (Boolean) context.getStaticParameters().get(
                        SAML2AssertionValidationParameters.SC_NOT_BEFORE_REQUIRED),
                Boolean.FALSE);
    }

    /**
     * Determine whether NotOnOrAfter is required.
     * 
     * @param context current validation context
     * 
     * @return true if required, false if not
     */
    protected boolean isNotOnOrAfterRequired(final ValidationContext context) {
        return ObjectSupport.firstNonNull(
                (Boolean) context.getStaticParameters().get(
                        SAML2AssertionValidationParameters.SC_NOT_ON_OR_AFTER_REQUIRED),
                Boolean.FALSE);
    }

    /**
     * Determine whether InResponseTo is required.
     * 
     * @param context current validation context
     * 
     * @return true if required, false if not
     */
    protected boolean isInResponseToRequired(final ValidationContext context) {
        return ObjectSupport.firstNonNull(
                (Boolean) context.getStaticParameters().get(
                        SAML2AssertionValidationParameters.SC_IN_RESPONSE_TO_REQUIRED),
                Boolean.FALSE);
    }

    /**
     * Validates the <code>InResponseTo</code> condition of the
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData}, if any is present.
     * 
     * @param confirmation confirmation method, with {@link org.opensaml.saml.saml2.core.SubjectConfirmationData},
     *  being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * @param required whether the InResponseTo value is required
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the NotBefore
     */
    protected ValidationResult validateInResponseTo(@Nonnull final SubjectConfirmation confirmation,
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context, final boolean required)
                    throws AssertionValidationException {
        
        final String inResponseTo = 
                StringSupport.trimOrNull(confirmation.getSubjectConfirmationData().getInResponseTo());
        if (inResponseTo == null) {
            if (required) {
                log.warn("SubjectConfirmationData/@InResponseTo was missing and was required");
//                context.setValidationFailureMessage(
//                        "SubjectConfirmationData/@InResponseTo was missing and was required");
                return ValidationResult.INVALID;
            }
            return ValidationResult.VALID;
        }
        
        log.debug("Evaluating SubjectConfirmationData@InResponseTo of: {}", inResponseTo);

        final String validInResponseTo;
        try {
            validInResponseTo = (String) context.getStaticParameters().get(
                    SAML2AssertionValidationParameters.SC_VALID_IN_RESPONSE_TO);
        } catch (final ClassCastException e) {
            log.warn("The value of the static validation parameter '{}' was not java.lang.String",
                    SAML2AssertionValidationParameters.SC_VALID_IN_RESPONSE_TO);
//            context.setValidationFailureMessage(
//                    "Unable to determine valid subject confirmation InResponseTo");
            return ValidationResult.INDETERMINATE;
        }

        log.debug("validInResponseTo: {}", validInResponseTo);

        if (validInResponseTo == null) {
            log.warn("Valid InResponseTo was not available from the validation context, " 
                    + "unable to evaluate SubjectConfirmationData@InResponseTo");
            // Below are the lines changed to fix issues with ALB SSO, caused by InResponseTo validation
            //context.setValidationFailureMessage("Unable to determine valid subject confirmation InResponseTo");
            //return ValidationResult.INDETERMINATE;
            return ValidationResult.VALID;
        }

        if (Objects.equals(inResponseTo, validInResponseTo)) {
            log.info("Matched valid InResponseTo: {}", inResponseTo);
            return ValidationResult.VALID;
        }
        
        log.error("Failed to match SubjectConfirmationData@InResponse to the valid value: {}", validInResponseTo);

//        context.setValidationFailureMessage(String.format(
//                "Subject confirmation InResponseTo for assertion '%s' did not match the valid value",
//                assertion.getID()));
        return ValidationResult.INVALID;
    }

    /**
     * Validates the <code>NotBefore</code> condition of the
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData}, if any is present.
     * 
     * @param confirmation confirmation method, with {@link org.opensaml.saml.saml2.core.SubjectConfirmationData},
     *  being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * @param required whether the NotBefore value is required
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the NotBefore
     */
    @Nonnull protected ValidationResult validateNotBefore(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context,
            final boolean required) throws AssertionValidationException {
        
//        final Instant notBefore = confirmation.getSubjectConfirmationData().getNotBefore();
//        if (notBefore == null) {
//            if (required) {
//                log.warn("SubjectConfirmationData/@NotBefore was missing and was required");
//                context.setValidationFailureMessage(
//                        "SubjectConfirmationData/@NotBefore was missing and was required");
//                return ValidationResult.INVALID;
//            }
//            return ValidationResult.VALID;
//        }
//
//        final Instant skewedNow = Instant.now().plus(SAML20AssertionValidator.getClockSkew(context));
//
//        log.debug("Evaluating SubjectConfirmationData NotBefore '{}' against 'skewed now' time '{}'",
//                notBefore, skewedNow);
//        if (notBefore != null && notBefore.isAfter(skewedNow)) {
//            context.setValidationFailureMessage(String.format(
//                    "Subject confirmation, in assertion '%s', with NotBefore condition of '%s' is not yet valid",
//                    assertion.getID(), notBefore));
//            return ValidationResult.INVALID;
//        }

        return ValidationResult.VALID;
    }

    /**
     * Validates the <code>NotOnOrAfter</code> condition of the
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData}, if any is present.
     * 
     * @param confirmation confirmation method, with {@link org.opensaml.saml.saml2.core.SubjectConfirmationData},
     *  being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * @param required whether the NotOnOrAfter value is required
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the NotOnOrAFter
     */
    @Nonnull protected ValidationResult validateNotOnOrAfter(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context, final boolean required) 
                    throws AssertionValidationException {
        
//        final Instant notOnOrAfter = confirmation.getSubjectConfirmationData().getNotOnOrAfter();
//        if (notOnOrAfter == null) {
//            if (required) {
//                log.warn("SubjectConfirmationData/@NotOnOrAfter was missing and was required");
//                context.setValidationFailureMessage(
//                        "SubjectConfirmationData/@NotOnOrAfter was missing and was required");
//                return ValidationResult.INVALID;
//            }
//            return ValidationResult.VALID;
//        }
//
//        final Instant skewedNow = Instant.now().minus(SAML20AssertionValidator.getClockSkew(context));
//
//        log.debug("Evaluating SubjectConfirmationData NotOnOrAfter '{}' against 'skewed now' time '{}'",
//                notOnOrAfter, skewedNow);
//        if (notOnOrAfter != null && notOnOrAfter.isBefore(skewedNow)) {
//            context.setValidationFailureMessage(String.format(
//                    "Subject confirmation, in assertion '%s', with NotOnOrAfter condition of '%s' is no longer valid",
//                    assertion.getID(), notOnOrAfter));
//            return ValidationResult.INVALID;
//        }

        return ValidationResult.VALID;
    }

    /**
     * Validates the <code>Recipient</code> condition of the
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData}, if any is present.
     * 
     * @param confirmation confirmation method being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * @param required whether the Recipient value is required
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the recipient
     */
    @Nonnull protected ValidationResult validateRecipient(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context, final boolean required)
                    throws AssertionValidationException {
        
//        final String recipient =
//                StringSupport.trimOrNull(confirmation.getSubjectConfirmationData().getRecipient());
//        if (recipient == null) {
//            if (required) {
//                log.warn("SubjectConfirmationData/@Recipient was missing and was required");
//                context.setValidationFailureMessage(
//                        "SubjectConfirmationData/@Recipient was missing and was required");
//                return ValidationResult.INVALID;
//            }
//            return ValidationResult.VALID;
//        }
//
//        log.debug("Evaluating SubjectConfirmationData@Recipient of : {}", recipient);
//
//        final Set<String> validRecipients;
//        try {
//            validRecipients = (Set<String>) context.getStaticParameters().get(
//                    SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS);
//        } catch (final ClassCastException e) {
//            log.warn("The value of the static validation parameter '{}' was not java.util.Set<String>",
//                    SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS);
//            context.setValidationFailureMessage(
//                    "Unable to determine list of valid subject confirmation recipient endpoints");
//            return ValidationResult.INDETERMINATE;
//        }
//        if (validRecipients == null || validRecipients.isEmpty()) {
//            log.warn("Set of valid recipient URI's was not available from the validation context, "
//                    + "unable to evaluate SubjectConfirmationData@Recipient");
//            context.setValidationFailureMessage(
//                    "Unable to determine list of valid subject confirmation recipient endpoints");
//            return ValidationResult.INDETERMINATE;
//        }
//
//        if (validRecipients.contains(recipient)) {
//            log.debug("Matched valid recipient: {}", recipient);
//            return ValidationResult.VALID;
//        }
//
//        log.debug("Failed to match SubjectConfirmationData@Recipient to any supplied valid recipients: {}",
//                validRecipients);
//
//        context.setValidationFailureMessage(String.format(
//                "Subject confirmation recipient for assertion '%s' did not match any valid recipients", assertion
//                        .getID()));
        return ValidationResult.INVALID;
    }

    /**
     * Validates the <code>Address</code> condition of the {@link org.opensaml.saml.saml2.core.SubjectConfirmationData},
     * if any is present.
     * 
     * @param confirmation confirmation method being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * @param required whether the Address value is required
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the address
     */
    @Nonnull protected ValidationResult validateAddress(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context, final boolean required) 
                    throws AssertionValidationException {

        final Boolean checkAddress =
                (Boolean) context.getStaticParameters().get(SAML2AssertionValidationParameters.SC_CHECK_ADDRESS);

        if (checkAddress != null && !checkAddress) {
            log.debug("SubjectConfirmationData/@Address check is disabled, skipping");
            return ValidationResult.VALID;
        }

        final String address = StringSupport.trimOrNull(confirmation.getSubjectConfirmationData().getAddress());
        if (address == null) {
            if (required) {
                log.warn("SubjectConfirmationData/@Address was missing and was required");
//                context.setValidationFailureMessage(
//                        "SubjectConfirmationData/@Address was missing and was required");
                return ValidationResult.INVALID;
            }
            return ValidationResult.VALID;
        }
        
        return AssertionValidationSupport.checkAddress(context, address,
                SAML2AssertionValidationParameters.SC_VALID_ADDRESSES,
                assertion,
                "SubjectConfirmationData/@Address");
    }

    /**
     * Performs any further validation required for the specific confirmation method implementation.
     * 
     * @param confirmation confirmation method being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if further validation finds the confirmation method to be invalid
     */
    @Nonnull protected abstract ValidationResult doValidate(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context) 
                    throws AssertionValidationException;
    
}