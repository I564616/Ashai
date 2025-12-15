package com.sabmiller.staff.singlesignon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
/**
 * Custom SamlObjectsFactory for Asahi.
 * This class is responsible for injecting a custom ResponseValidator into authenticationProvider
 */
public class AsahiSamlObjectsFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AsahiSamlObjectsFactory.class);

    public static AuthenticationProvider getAuthenticationProvider(final AuthenticationProvider authenticationProvider) {
        LOG.info("Injecting Asahi specific ResponseValidator to authenticationProvider");

        if (authenticationProvider instanceof OpenSaml4AuthenticationProvider openSaml4AuthenticationProvider) {
            openSaml4AuthenticationProvider.setResponseValidator(new AsahiResponseValidator());

            return openSaml4AuthenticationProvider;
        }

        return authenticationProvider;
    }
}
