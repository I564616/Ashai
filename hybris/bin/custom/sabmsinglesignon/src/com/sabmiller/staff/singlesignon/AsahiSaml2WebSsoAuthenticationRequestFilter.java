
package com.sabmiller.staff.singlesignon;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.impl.AuthnRequestMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.saml2.core.Saml2ParameterNames;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
//import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationRequestContext;
//import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationRequestFactory;
import org.springframework.security.saml2.provider.service.authentication.Saml2PostAuthenticationRequest;
import org.springframework.security.saml2.provider.service.authentication.Saml2RedirectAuthenticationRequest;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
import org.springframework.security.saml2.provider.service.web.HttpSessionSaml2AuthenticationRequestRepository;
//import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationRequestContextResolver;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationRequestRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sabmiller.staff.singlesignon.constants.SabmsinglesignonConstants;


public class AsahiSaml2WebSsoAuthenticationRequestFilter extends OncePerRequestFilter

{

	//Adding the below static block to fix the SSO logout issue

	static
	{
		final XMLObjectProviderRegistry registry = org.opensaml.core.config.ConfigurationService
				.get(XMLObjectProviderRegistry.class);
		if (null != registry)
		{
			final AuthnRequestMarshaller marshaller = new AuthnRequestMarshaller() {
				@Override
				public Element marshall(final XMLObject object, final Element element) throws MarshallingException
				{
					configureAuthnRequest((AuthnRequest) object);
					return super.marshall(object, element);
				}

				@Override
				public Element marshall(final XMLObject object, final Document document) throws MarshallingException {
					configureAuthnRequest((AuthnRequest) object);
					return super.marshall(object, document);
				}

				private void configureAuthnRequest(final AuthnRequest authnRequest)
				{
					authnRequest.setForceAuthn(Boolean.TRUE);
					authnRequest.setIsPassive(Boolean.FALSE);
				}
			};
			registry.getMarshallerFactory().registerMarshaller(AuthnRequest.DEFAULT_ELEMENT_NAME, marshaller);
		}
	}

//	private final Saml2AuthenticationRequestContextResolver authenticationRequestContextResolver;

//	private Saml2AuthenticationRequestFactory authenticationRequestFactory;

	private Saml2AuthenticationRequestRepository<AbstractSaml2AuthenticationRequest> authenticationRequestRepository = new HttpSessionSaml2AuthenticationRequestRepository();

	protected static final Logger log = LoggerFactory.getLogger(AsahiSaml2WebSsoAuthenticationRequestFilter.class);

	private ConfigurationService configurationService;

	private static final String SSO_URL_AFTER_AUTH = "samlsinglesignon/saml/SSO";


//	/**
//	 * @param authenticationRequestContextResolver
//	 * @param authenticationRequestFactory
//	 */

//	public AsahiSaml2WebSsoAuthenticationRequestFilter(
//			final Saml2AuthenticationRequestContextResolver authenticationRequestContextResolver,
//			final Saml2AuthenticationRequestFactory authenticationRequestFactory)
//	{
//		Assert.notNull(authenticationRequestContextResolver, "authenticationRequestContextResolver cannot be null");
//		Assert.notNull(authenticationRequestFactory, "authenticationRequestFactory cannot be null");
//		this.authenticationRequestContextResolver = authenticationRequestContextResolver;
//		this.authenticationRequestFactory = authenticationRequestFactory;
//	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{

//		if (request.getRequestURL().toString().contains(SSO_URL_AFTER_AUTH))
//		{
//			filterChain.doFilter(request, response);
//			return;
//
//		}
//
//		String asahiRelayState = null;
//		if ((null != request.getRequestURL()
//				&& request.getRequestURL().toString().contains(SabmsinglesignonConstants.ASAHI_STAFF_PORTAL))
//				|| (org.apache.commons.lang3.StringUtils.isNotBlank(request.getParameter("RelayState"))
//						&& request.getParameter("RelayState").contains(SabmsinglesignonConstants.ASAHI_STAFF_PORTAL)))
//		{
//			final Base64.Encoder encoder = Base64.getEncoder();
//			// asahiRelayState = getConfigurationService().getConfiguration().getString("sso.alb.relay.state");
//			asahiRelayState = request.getParameter("RelayState");
//			request.setAttribute("RelayState", encoder.encodeToString(asahiRelayState.getBytes()));
//			log.info("IN AsahiSaml2WebSsoAuthenticationRequestFilter #### Setting RelayStateURL ");
//		}
//		else if (null != request.getRequestURL()
//				&& request.getRequestURL().toString().contains(SabmsinglesignonConstants.CMS_COCKPIT))
//		{
//			request.setAttribute("RelayState", SabmsinglesignonConstants.CMS_COCKPIT);
//			log.info("IN AsahiSaml2WebSsoAuthenticationRequestFilter #### Setting RelayStateURL "
//					+ SabmsinglesignonConstants.CMS_COCKPIT);
//
//		}
//		final Saml2AuthenticationRequestContext context = authenticationRequestContextResolver
//				.resolve(request);
//		final AsahiSaml2AuthenticationRequestContext modifiedContext = new AsahiSaml2AuthenticationRequestContext(
//				context.getRelyingPartyRegistration(), context.getIssuer(),
//				getConfigurationService().getConfiguration().getString("sso.relyingParty.request.url"), context.getRelayState());
//		final RelyingPartyRegistration relyingParty = context.getRelyingPartyRegistration();
//		if (relyingParty.getAssertingPartyDetails().getSingleSignOnServiceBinding() == Saml2MessageBinding.REDIRECT)
//		{
//			sendRedirect(request, response, modifiedContext);
//		}
//		else
//		{
//			sendPost(request, response, modifiedContext);
//		}
	}

//	private void sendRedirect(final HttpServletRequest request, final HttpServletResponse response,
//			final Saml2AuthenticationRequestContext context) throws IOException
//	{
//
//		final Saml2RedirectAuthenticationRequest authenticationRequest = this.authenticationRequestFactory
//				.createRedirectAuthenticationRequest(context);
//		final String relayState = (String) (authenticationRequest.getRelayState() != null ? authenticationRequest.getRelayState()
//				: request.getAttribute("RelayState"));
//		this.authenticationRequestRepository.saveAuthenticationRequest(authenticationRequest, request, response);
//		final UriComponentsBuilder uriBuilder = UriComponentsBuilder
//				.fromUriString(authenticationRequest.getAuthenticationRequestUri());
//		addParameter(Saml2ParameterNames.SAML_REQUEST, authenticationRequest.getSamlRequest(), uriBuilder);
//		addParameter(Saml2ParameterNames.RELAY_STATE, relayState, uriBuilder);
//		addParameter(Saml2ParameterNames.SIG_ALG, authenticationRequest.getSigAlg(), uriBuilder);
//		addParameter(Saml2ParameterNames.SIGNATURE, authenticationRequest.getSignature(), uriBuilder);
//		final String redirectUrl = uriBuilder.build(true).toUriString();
//		log.info("IN AsahiSaml2WebSsoAuthenticationRequestFilter sendRedirect url : " + redirectUrl);
//		response.sendRedirect(redirectUrl);
//		log.info("IN AsahiSaml2WebSsoAuthenticationRequestFilter sendRedirect response : " + response);
//	}

	private void addParameter(final String name, final String value, final UriComponentsBuilder builder)
	{
		Assert.hasText(name, "name cannot be empty or null");
		if (StringUtils.hasText(value))
		{
			builder.queryParam(UriUtils.encode(name, StandardCharsets.ISO_8859_1),
					UriUtils.encode(value, StandardCharsets.ISO_8859_1));
		}
	}

//	private void sendPost(final HttpServletRequest request, final HttpServletResponse response,
//			final Saml2AuthenticationRequestContext context) throws IOException
//	{
//		final Saml2PostAuthenticationRequest authenticationRequest = this.authenticationRequestFactory
//				.createPostAuthenticationRequest(context);
//		this.authenticationRequestRepository.saveAuthenticationRequest(authenticationRequest, request, response);
//		final String html = createSamlPostRequestFormData(authenticationRequest, request);
//		response.setContentType(MediaType.TEXT_HTML_VALUE);
//		response.getWriter().write(html);
//	}

	private String createSamlPostRequestFormData(final Saml2PostAuthenticationRequest authenticationRequest,
			final HttpServletRequest request)
	{
		final String relayState = (String) (authenticationRequest.getRelayState() != null ? authenticationRequest.getRelayState()
				: request.getAttribute("RelayState"));
		final String authenticationRequestUri = authenticationRequest.getAuthenticationRequestUri();
		final String samlRequest = authenticationRequest.getSamlRequest();
		final StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html>\n");
		html.append("<html>\n").append("    <head>\n");
		html.append("        <meta charset=\"utf-8\" />\n");
		html.append("    </head>\n");
		html.append("    <body onload=\"document.forms[0].submit()\">\n");
		html.append("        <noscript>\n");
		html.append("            <p>\n");
		html.append("                <strong>Note:</strong> Since your browser does not support JavaScript,\n");
		html.append("                you must press the Continue button once to proceed.\n");
		html.append("            </p>\n");
		html.append("        </noscript>\n");
		html.append("        \n");
		html.append("        <form action=\"");
		html.append(authenticationRequestUri);
		html.append("\" method=\"post\">\n");
		html.append("            <div>\n");
		html.append("                <input type=\"hidden\" name=\"SAMLRequest\" value=\"");
		html.append(HtmlUtils.htmlEscape(samlRequest));
		html.append("\"/>\n");
		if (StringUtils.hasText(relayState))
		{
			html.append("                <input type=\"hidden\" name=\"RelayState\" value=\"");
			html.append(HtmlUtils.htmlEscape(relayState));
			html.append("\"/>\n");
		}
		html.append("            </div>\n");
		html.append("            <noscript>\n");
		html.append("                <div>\n");
		html.append("                    <input type=\"submit\" value=\"Continue\"/>\n");
		html.append("                </div>\n");
		html.append("            </noscript>\n");
		html.append("        </form>\n");
		html.append("        \n");
		html.append("    </body>\n");
		html.append("</html>");
		return html.toString();
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Use the given {@link Saml2AuthenticationRequestRepository} to save the authentication request
	 *
	 * @param authenticationRequestRepository
	 *           the {@link Saml2AuthenticationRequestRepository} to use
	 * @since 5.6
	 */

	public void setAuthenticationRequestRepository(
			final Saml2AuthenticationRequestRepository<AbstractSaml2AuthenticationRequest> authenticationRequestRepository)
	{
		Assert.notNull(authenticationRequestRepository, "authenticationRequestRepository cannot be null");
		this.authenticationRequestRepository = authenticationRequestRepository;
	}

//	@Deprecated
//	public void setAuthenticationRequestFactory(final Saml2AuthenticationRequestFactory authenticationRequestFactory)
//	{
//		Assert.notNull(authenticationRequestFactory, "authenticationRequestFactory cannot be null");
//		this.authenticationRequestFactory = authenticationRequestFactory;
//	}


 }
