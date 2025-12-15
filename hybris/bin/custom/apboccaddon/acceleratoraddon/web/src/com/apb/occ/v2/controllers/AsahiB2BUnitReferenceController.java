/**
 * 
 */
package com.apb.occ.v2.controllers;

import de.hybris.platform.apboccaddon.dto.customer.AccountGroupsListWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.AccountGroupsWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.AccountTypeListWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.AccountTypeWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.BannerGroupsListWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.BannerGroupsWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.ChannelListWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.ChannelWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.LicenceClassListWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.LicenceClassWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.LicenseTypesListWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.LicenseTypesWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.SubChannelListWsDTO;
import de.hybris.platform.apboccaddon.dto.customer.SubChannelWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.apb.facades.b2bunit.AsahiB2BUnitReferenceFacade;
import com.apb.facades.b2bunit.data.AccountGroupsData;
import com.apb.facades.b2bunit.data.AccountTypeData;
import com.apb.facades.b2bunit.data.BannerGroupsData;
import com.apb.facades.b2bunit.data.ChannelData;
import com.apb.facades.b2bunit.data.LicenceClassData;
import com.apb.facades.b2bunit.data.LicenseTypesData;
import com.apb.facades.b2bunit.data.SubChannelData;


/**
 * The Class AsahiB2BUnitReferenceController.
 * 
 * @author Kuldeep.Singh1
 */
@RestController
@RequestMapping(value = "/{baseSiteId}/customersReference")
@ApiVersion("v2")
public class AsahiB2BUnitReferenceController
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiB2BUnitReferenceController.class);

	/** The data mapper. */
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	/** The asahi B 2 B unit reference facade. */
	@Resource(name = "asahiB2BUnitReferenceFacade")
	private AsahiB2BUnitReferenceFacade asahiB2BUnitReferenceFacade;
	
	private static final String INVALID_REQUEST_DISCLAIMER = "Request is Not Valid";

	/**
	 * Import account groups.
	 * 
	 * @param accountGroup
	 *           the account group
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importAccountGroup", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importAccountGroups(@RequestBody final AccountGroupsListWsDTO accountGroup) throws WebserviceValidationException
	{
		logger.debug("Importing accountGroups into hybris");
		if (CollectionUtils.isNotEmpty(accountGroup.getAccountGroup()))
		{
			for (final AccountGroupsWsDTO accountGroupWS : accountGroup.getAccountGroup())
			{
				logger.debug("Importing accountGroup with code: " + accountGroupWS.getCode());

				this.asahiB2BUnitReferenceFacade.importAccountGroup(this.dataMapper.map(accountGroupWS, AccountGroupsData.class));

				logger.debug("accountGroup with code: " + accountGroupWS.getCode() + " is imported");
			}
		}
		else
		{
			throw new RequestParameterException(INVALID_REQUEST_DISCLAIMER, RequestParameterException.INVALID);
		}
	}

	/**
	 * Import account types.
	 * 
	 * @param accountType
	 *           the account type
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importAccountType", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importAccountTypes(@RequestBody final AccountTypeListWsDTO accountType) throws WebserviceValidationException
	{
		logger.debug("Importing AccountTypes into hybris");
		if (CollectionUtils.isNotEmpty(accountType.getAccountType()))
		{
			for (final AccountTypeWsDTO accountTypeWS : accountType.getAccountType())
			{
				logger.debug("Importing AccountType with code: " + accountTypeWS.getCode());

				this.asahiB2BUnitReferenceFacade.importAccountType(this.dataMapper.map(accountTypeWS, AccountTypeData.class));

				logger.debug("AccountType with code: " + accountTypeWS.getCode() + " is imported");
			}
		}
		else
		{
			throw new RequestParameterException(INVALID_REQUEST_DISCLAIMER, RequestParameterException.INVALID);
		}
	}

	/**
	 * Import banner groups.
	 * 
	 * @param bannerGroup
	 *           the banner group
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importBannerGroup", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importBannerGroups(@RequestBody final BannerGroupsListWsDTO bannerGroup) throws WebserviceValidationException
	{
		logger.debug("Importing BannerGroups into hybris");
		if (CollectionUtils.isNotEmpty(bannerGroup.getBannerGroup()))
		{
			for (final BannerGroupsWsDTO bannerGroupWS : bannerGroup.getBannerGroup())
			{
				logger.debug("Importing BannerGroup with code: " + bannerGroupWS.getCode());

				this.asahiB2BUnitReferenceFacade.importBannerGroup(this.dataMapper.map(bannerGroupWS, BannerGroupsData.class));

				logger.debug("BannerGroup with code: " + bannerGroupWS.getCode() + " is imported");
			}
		}
		else
		{
			throw new RequestParameterException(INVALID_REQUEST_DISCLAIMER, RequestParameterException.INVALID);
		}
	}

	/**
	 * Import account groups.
	 * 
	 * @param subChannel
	 *           the sub channel
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importSubChannel", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importSubChannel(@RequestBody final SubChannelListWsDTO subChannel) throws WebserviceValidationException
	{
		logger.debug("Importing SubChannels into hybris");
		if (CollectionUtils.isNotEmpty(subChannel.getSubChannel()))
		{
			for (final SubChannelWsDTO subChannelWS : subChannel.getSubChannel())
			{
				logger.debug("Importing SubChannel with code: " + subChannelWS.getCode());

				this.asahiB2BUnitReferenceFacade.importSubChannel(this.dataMapper.map(subChannelWS, SubChannelData.class));

				logger.debug("SubChannel with code: " + subChannelWS.getCode() + " is imported");
			}
		}
		else
		{
			throw new RequestParameterException(INVALID_REQUEST_DISCLAIMER, RequestParameterException.INVALID);
		}
	}

	/**
	 * Import channel.
	 * 
	 * @param channel
	 *           the channel
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importChannel", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importChannel(@RequestBody final ChannelListWsDTO channel) throws WebserviceValidationException
	{
		logger.debug("Importing Channels into hybris");
		if (CollectionUtils.isNotEmpty(channel.getChannel()))
		{
			for (final ChannelWsDTO channelWS : channel.getChannel())
			{
				logger.debug("Importing Channel with code: " + channelWS.getCode());

				this.asahiB2BUnitReferenceFacade.importChannel(this.dataMapper.map(channelWS, ChannelData.class));

				logger.debug("Channel with code: " + channelWS.getCode() + " is imported");
			}
		}
		else
		{
			throw new RequestParameterException(INVALID_REQUEST_DISCLAIMER, RequestParameterException.INVALID);
		}
	}

	/**
	 * Import license types.
	 * 
	 * @param licenseType
	 *           the license type
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importLicenseType", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importLicenseTypes(@RequestBody final LicenseTypesListWsDTO licenseType) throws WebserviceValidationException
	{
		logger.debug("Importing LicenseTypes into hybris");
		if (CollectionUtils.isNotEmpty(licenseType.getLicenseType()))
		{
			for (final LicenseTypesWsDTO licenseTypeWS : licenseType.getLicenseType())
			{
				logger.debug("Importing LicenseType with code: " + licenseTypeWS.getCode());

				this.asahiB2BUnitReferenceFacade.importLicenseType(this.dataMapper.map(licenseTypeWS, LicenseTypesData.class));

				logger.debug("LicenseType with code: " + licenseTypeWS.getCode() + " is imported");
			}
		}
		else
		{
			throw new RequestParameterException(INVALID_REQUEST_DISCLAIMER, RequestParameterException.INVALID);
		}
	}

	/**
	 * Import licence class.
	 * 
	 * @param licenseClass
	 *           the license class
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importLicenceClass", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importLicenceClass(@RequestBody final LicenceClassListWsDTO licenseClass) throws WebserviceValidationException
	{
		logger.debug("Importing LicenceClass into hybris");
		if (CollectionUtils.isNotEmpty(licenseClass.getLicenseClass()))
		{
			for (final LicenceClassWsDTO licenseClassWS : licenseClass.getLicenseClass())
			{
				logger.debug("Importing LicenceClass with code: " + licenseClassWS.getCode());

				this.asahiB2BUnitReferenceFacade.importLicenceClass(this.dataMapper.map(licenseClassWS, LicenceClassData.class));

				logger.debug("LicenceClass with code: " + licenseClassWS.getCode() + " is imported");
			}
		}
		else
		{
			throw new RequestParameterException(INVALID_REQUEST_DISCLAIMER, RequestParameterException.INVALID);
		}
	}

}
