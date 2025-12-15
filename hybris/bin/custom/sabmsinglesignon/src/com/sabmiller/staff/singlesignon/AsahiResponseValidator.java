package com.sabmiller.staff.singlesignon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.saml2.core.Saml2Error;
import org.springframework.security.saml2.core.Saml2ResponseValidatorResult;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider.ResponseToken;

/**
 * Custom ResponseValidator for Asahi.
 * This class removed invalid_in_response_to error from list of SAML errors.
 */
final class AsahiResponseValidator implements Converter<ResponseToken, Saml2ResponseValidatorResult> {
    private static final Logger LOG = LoggerFactory.getLogger(AsahiResponseValidator.class);

    private final Converter<ResponseToken, Saml2ResponseValidatorResult> delegate = OpenSaml4AuthenticationProvider.createDefaultResponseValidator();

    @Override
    public Saml2ResponseValidatorResult convert(final ResponseToken responseToken) {
        Saml2ResponseValidatorResult result = this.delegate.convert(responseToken);

        LOG.info("Removing invalid_in_response_to from SAML errors");

        final List<Saml2Error> errors = result.getErrors().stream()
                .filter((error) -> !error.getErrorCode().equals("invalid_in_response_to"))
                .collect(Collectors.toList());

        return Saml2ResponseValidatorResult.failure(errors);
    }
}

