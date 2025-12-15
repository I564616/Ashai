/**
 *
 */
package com.sabmiller.staff.singlesignon;

import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
/*import org.springframework.security.saml.SAMLCredential;*/
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;

import com.hybris.samlssobackoffice.BackofficeSSOService;
import com.sabmiller.staff.singlesignon.constants.SabmsinglesignonConstants;
import com.sabmiller.staff.singlesignon.services.SABMSSOAdditionalRolesProvider;


/**
 * @author dale.bryan.a.mercado
 *
 */
public class CustomSSOService extends BackofficeSSOService
{
	private SABMSSOAdditionalRolesProvider sabmssoAdditionalRolesProvider;

	private ConfigurationService configurationService;

	private UserService customUserService;

	private ModelService customModelService;

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomSSOService.class);


	@Override
	public UserModel getOrCreateSSOUser(final String id, final String name, final Collection<String> roles)
	{
		if (CollectionUtils.isNotEmpty(roles))
		{

			boolean isBackofficeSSO = false;
			final String backofficeSSOEntityId = getConfigurationService().getConfiguration()
					.getString("backoffice.saml.response.audience");
			final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null && auth instanceof Saml2Authentication
					&& auth.getPrincipal() instanceof DefaultSaml2AuthenticatedPrincipal)
			{
				LOGGER.info("Auth not null in CustomSSOService");
				final DefaultSaml2AuthenticatedPrincipal principal = (DefaultSaml2AuthenticatedPrincipal) auth.getPrincipal();
				if (((Saml2Authentication) auth).getSaml2Response().contains(backofficeSSOEntityId))
				{
					isBackofficeSSO = true;
				}
			}

				/*
				 * final String relayStateURL = StringUtils.EMPTY; if (null != relayStateURL) {
				 *
				 * final Decoder decoder = Base64.getDecoder(); final byte[] bytes = decoder.decode(relayStateURL); final
				 * String decodedRelayURL = new String(bytes); if
				 * (decodedRelayURL.contains(SabmsinglesignonConstants.ASAHI_STAFF_PORTAL) &&
				 * !hasRequiredUserAccessRole(roles,
				 * getConfigurationService().getConfiguration().getString("sso.alb.ad.role"))) {
				 * LOGGER.error("User does not have access to ALB Staff Portal"); throw new
				 * AccessDeniedException("User does not have access to ALB Staff Portal"); } }
				 *
				 * else if (((Saml2Authentication) auth).getSaml2Response().contains(backofficeSSOEntityId) &&
				 * !hasRequiredUserAccessRole(roles,
				 * getConfigurationService().getConfiguration().getString("sso.admin.ad.role"))) {
				 * LOGGER.error("User does not have access to Backoffice"); throw new
				 * AccessDeniedException("User does not have access to Backoffice"); }
				 *
				 * else if (!((Saml2Authentication) auth).getSaml2Response().contains(backofficeSSOEntityId) &&
				 * !hasRequiredUserAccessRole(roles,
				 * getConfigurationService().getConfiguration().getString("sso.cub.ad.role"))) {
				 * LOGGER.error("User does not have access to CUB Staff Portal"); throw new
				 * AccessDeniedException("User does not have access to CUB Staff Portal"); }
				 */


			/*}
			else if (!hasRequiredUserAccessRole(roles, getConfigurationService().getConfiguration().getString("sso.alb.ad.role")))
			{
				LOGGER.info("Auth null in CustomSSOService");
				LOGGER.error("User does not have access to ALB Staff Portal");
				throw new AccessDeniedException("User does not have access to ALB Staff Portal");
			}*/


			final Collection<String> hybrisRoles = new ArrayList<String>();
			hybrisRoles.addAll(getSabmssoAdditionalRolesProvider().getAdditionalRoles(id, name));
			final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();

			try
			{
				final UserModel user = getCustomUserService().getUserForUID(id);
				groups.addAll(user.getGroups());
			}
			catch (final UnknownIdentifierException ex)
			{
				LOGGER.info("No user exist for the given UID");
			}
			final UserModel ssoUser = super.getOrCreateSSOUser(id, name, hybrisRoles);
			final Set<PrincipalGroupModel> finalGroups = new HashSet<PrincipalGroupModel>();
			finalGroups.addAll(groups);
			finalGroups.addAll(ssoUser.getGroups());
			ssoUser.setGroups(finalGroups);

			//Remove admingroup else restrictions in Staff Portal would not be  applied
			Set<PrincipalGroupModel> filteredGroups = new HashSet<PrincipalGroupModel>();
			try {
				final PrincipalGroupModel bdegroup = finalGroups.stream()
						.filter(group -> group.getUid().equalsIgnoreCase(SabmsinglesignonConstants.BDE_GROUP)).findFirst().orElse(null);
   			if (null != bdegroup && !isBackofficeSSO)
   			{
   				filteredGroups = finalGroups.stream()
   						.filter(group -> !group.getUid().equalsIgnoreCase(SabmsinglesignonConstants.ADMIN_GROUP))
   						.collect(Collectors.toSet());
   				ssoUser.setGroups(filteredGroups);
   			}
			}
			catch (final Exception ex ) {
				LOGGER.info("UserGroup does not exist");
			}

			getCustomModelService().save(ssoUser);
			return ssoUser;

		}
		return null;
	}



	/**
	 * @param roles
	 * @param application
	 * @return
	 */
	private boolean hasRequiredUserAccessRole(final Collection<String> roles, final String application)
	{
		final String adRole = roles.stream().filter(role -> role.equalsIgnoreCase(application)).findAny().orElse(null);
		if (StringUtils.isNotBlank(adRole))
		{
			return true;
		}
		return false;
	}


	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected SABMSSOAdditionalRolesProvider getSabmssoAdditionalRolesProvider()
	{
		return sabmssoAdditionalRolesProvider;
	}

	public void setSabmssoAdditionalRolesProvider(final SABMSSOAdditionalRolesProvider sabmssoAdditionalRolesProvider)
	{
		this.sabmssoAdditionalRolesProvider = sabmssoAdditionalRolesProvider;
	}

	/**
	 * @return the userService
	 */

	/**
	 * @return the customUserService
	 */
	public UserService getCustomUserService()
	{
		return customUserService;
	}


	/**
	 * @param customUserService
	 *           the customUserService to set
	 */
	public void setCustomUserService(final UserService customUserService)
	{
		this.customUserService = customUserService;
	}


	/**
	 * @return the customModelService
	 */
	public ModelService getCustomModelService()
	{
		return customModelService;
	}


	/**
	 * @param customModelService
	 *           the customModelService to set
	 */
	public void setCustomModelService(final ModelService customModelService)
	{
		this.customModelService = customModelService;
	}


}
