/**
 *
 */
package com.sabmiller.staff.singlesignon;

//import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationRequestContext;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;


/**
 * @author Saumya.Mittal1
 *
 */
public class AsahiSaml2AuthenticationRequestContext //extends Saml2AuthenticationRequestContext
{

	/**
	 * @param relyingPartyRegistration
	 * @param issuer
	 * @param assertionConsumerServiceUrl
	 * @param relayState
	 */
	public AsahiSaml2AuthenticationRequestContext(final RelyingPartyRegistration relyingPartyRegistration, final String issuer,
			final String assertionConsumerServiceUrl, final String relayState)
	{
		//super(relyingPartyRegistration, issuer, assertionConsumerServiceUrl, relayState);
	}

}
