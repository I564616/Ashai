/**
 *
 */
/*
 * package com.sabmiller.staff.singlesignon;
 *
 * import de.hybris.platform.servicelayer.config.ConfigurationService;
 *
 * import java.util.Base64;
 *
 * import javax.servlet.http.HttpServletRequest; import javax.servlet.http.HttpServletResponse; import
 * javax.xml.namespace.QName;
 *
 * import org.opensaml.saml2.metadata.IDPSSODescriptor; import org.opensaml.saml2.metadata.SPSSODescriptor; import
 * org.opensaml.saml2.metadata.provider.MetadataProviderException; import
 * org.opensaml.ws.transport.http.HTTPInTransport; import org.slf4j.Logger; import org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Required; import
 * org.springframework.security.saml.context.SAMLContextProviderImpl; import
 * org.springframework.security.saml.context.SAMLMessageContext;
 *
 * import com.sabmiller.staff.singlesignon.constants.SabmsinglesignonConstants;
 *
 *
 *//**
	*
	*/
/*
 * public class SabmSAMLContextProviderImpl extends SAMLContextProviderImpl {
 *
 * protected static final Logger log = LoggerFactory.getLogger(SabmSAMLContextProviderImpl.class);
 *
 * private ConfigurationService configurationService;
 *
 *
 *
 * @Override protected void populateGenericContext(final HttpServletRequest request, final HttpServletResponse response,
 * final SAMLMessageContext context) throws MetadataProviderException {
 *
 * super.populateGenericContext(request, response, context); if (null != request.getRequestURL() &&
 * request.getRequestURL().toString().contains(SabmsinglesignonConstants.ASAHI_STAFF_PORTAL)) { final Base64.Encoder
 * encoder = Base64.getEncoder(); final String asahiRelayState =
 * getConfigurationService().getConfiguration().getString("sso.alb.relay.state");
 * context.setRelayState(encoder.encodeToString(asahiRelayState.getBytes()));
 * log.info("IN SabmSAMLContextProviderImpl #### Setting RelayStateURL "); }
 *
 * }
 *
 *//**
	 * Method tries to load localEntityAlias and localEntityRole from the request path. Path is supposed to be in format:
	 * https(s)://server:port/application/saml/filterName/alias/aliasName/idp|sp?query. In case alias is missing from the
	 * path defaults are used. Otherwise localEntityId and sp or idp localEntityRole is entered into the context.
	 * <p>
	 * In case alias entity id isn't found an exception is raised.
	 *
	 * @param context
	 *           context to populate fields localEntityId and localEntityRole for
	 * @param requestURI
	 *           context path to parse entityId and entityRole from
	 * @param stringBuffer
	 * @throws MetadataProviderException
	 *            in case entityId can't be populated
	 *//*
		 *
		 * protected void populateLocalEntityId(final SAMLMessageContext context, String requestURI, final StringBuffer
		 * requestURL) throws MetadataProviderException {
		 *
		 * String entityId; final HTTPInTransport inTransport = (HTTPInTransport) context.getInboundMessageTransport();
		 *
		 * // Pre-configured entity Id entityId = (String)
		 * inTransport.getAttribute(org.springframework.security.saml.SAMLConstants.LOCAL_ENTITY_ID); if (entityId !=
		 * null) { log.debug("Using protocol specified SP {}", entityId); context.setLocalEntityId(entityId);
		 * context.setLocalEntityRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME); return; }
		 *
		 * if (requestURI == null) { requestURI = ""; }
		 *
		 * final int filterIndex = requestURI.indexOf("/alias/"); if (filterIndex != -1) { // EntityId from URL alias
		 *
		 * String localAlias = requestURI.substring(filterIndex + 7); QName localEntityRole;
		 *
		 * final int entityTypePosition = localAlias.lastIndexOf('/'); if (entityTypePosition != -1) { final String
		 * entityRole = localAlias.substring(entityTypePosition + 1); if ("idp".equalsIgnoreCase(entityRole)) {
		 * localEntityRole = IDPSSODescriptor.DEFAULT_ELEMENT_NAME; } else { localEntityRole =
		 * SPSSODescriptor.DEFAULT_ELEMENT_NAME; } localAlias = localAlias.substring(0, entityTypePosition); } else {
		 * localEntityRole = SPSSODescriptor.DEFAULT_ELEMENT_NAME; }
		 *
		 *
		 * // Populate entityId entityId = metadata.getEntityIdForAlias(localAlias);
		 *
		 * if (entityId == null) { throw new MetadataProviderException("No local entity found for alias " + localAlias +
		 * ", verify your configuration."); } else { log.debug("Using SP {} specified in request with alias {}", entityId,
		 * localAlias); }
		 *
		 * context.setLocalEntityId(entityId); context.setLocalEntityRole(localEntityRole);
		 *
		 * } else { // Defaults
		 *
		 * final String localEntityId = getConfigurationService().getConfiguration().getString("sso.entity.id");
		 * context.setLocalEntityId(localEntityId); context.setLocalEntityRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
		 *
		 * }
		 *
		 * }
		 *
		 *
		 * protected ConfigurationService getConfigurationService() { return configurationService; }
		 *
		 * @Required public void setConfigurationService(final ConfigurationService configurationService) {
		 * this.configurationService = configurationService; }
		 *
		 *
		 * }
		 */