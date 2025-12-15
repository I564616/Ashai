package com.apb.occ.v2.controllers;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.b2bunit.ApbB2BUnitFacade;
import com.apb.occ.v2.async.service.AsahiAsyncService;
import com.apb.occ.v2.exception.AsahiWebServiceException;
import com.google.gson.Gson;
import de.hybris.platform.apboccaddon.dto.b2bunit.ApbAddressListWsDTO;
import de.hybris.platform.apboccaddon.dto.b2bunit.ApbB2BUnitListWsDTO;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.core.Registry;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.ArrayList;


/**
 * The Class AsahiB2BUnitController.
 *
 * @author Kuldeep.Singh1
 */
@RestController
@RequestMapping(value = "/{baseSiteId}/customers")
@ApiVersion("v2")
public class AsahiB2BUnitController extends AsahiBaseController
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiB2BUnitController.class);

	/** The product facade. */
	@Resource(name = "apbB2BUnitFacade")
	private ApbB2BUnitFacade apbB2BUnitFacade;

	/** The data mapper. */
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	/** The apb address ws DTO validator. */
	@Resource(name = "apbAddressWsDTOValidator")
	private Validator apbAddressWsDTOValidator;

	@Autowired
    private AsahiConfigurationService asahiConfigurationService;

	@Autowired
    private AsahiAsyncService asahiAsyncService;

	/**
	 * Import user.
	 *
	 * @param customer
	 *           the customer
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@PostMapping(value = "/importCustomer", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public ResponseEntity<WebserviceException> importApbB2BUnit(@RequestBody final ApbB2BUnitListWsDTO customer, @PathVariable("baseSiteId") final String siteUid) throws WebserviceValidationException
	{
	    showIncomingData(customer, "customer");
	    if (CollectionUtils.isNotEmpty(customer.getCustomer()))
		{	
		    asahiAsyncService.setDataMapper(dataMapper);
		    asahiAsyncService.setTenant(Registry.getCurrentTenantNoFallback());
			asahiAsyncService.importCustomers(customer.getCustomer(), siteUid);
		}
		else
		{
			return new ResponseEntity<>(new RequestParameterException("Request is Not Valid", RequestParameterException.INVALID), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "/addresses", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public ResponseEntity<WebserviceException> importAddressForApbB2BUnit(@RequestBody final ApbAddressListWsDTO address) throws WebserviceValidationException
	{
		logger.debug("Importing Address into hybris");
		if (CollectionUtils.isNotEmpty(address.getAddress()))
		{
			final ArrayList<Object> errorList = new ArrayList<>();
			for (final AddressWsDTO addressWS : address.getAddress())
			{
				logger.debug("Address Importing with AddressRecordID: " + addressWS.getRecordId());

				final Errors errors = new BeanPropertyBindingResult(addressWS, "addressWS");
				this.getApbAddressWsDTOValidator().validate(addressWS, errors);
				if (errors.hasErrors())
				{
					errorList.add(errors);
				}
				else
				{
					final AddressData addressData = this.dataMapper.map(addressWS, AddressData.class);
					addressData.setAddressInterface(Boolean.valueOf(true));

					this.apbB2BUnitFacade.importApbB2BUnitAddress(addressData);
				}

				logger.debug("Address with AddressRecordID: " + addressWS.getRecordId() + " is imported");
			}
			if (CollectionUtils.isNotEmpty(errorList))
			{
				return new ResponseEntity<>(new AsahiWebServiceException(errorList), HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}


	/**
	 * @return the apbAddressWsDTOValidator
	 */
	public Validator getApbAddressWsDTOValidator()
	{
		return apbAddressWsDTOValidator;
	}

	/**
	 * @param apbAddressWsDTOValidator
	 *           the apbAddressWsDTOValidator to set
	 */
	public void setApbAddressWsDTOValidator(final Validator apbAddressWsDTOValidator)
	{
		this.apbAddressWsDTOValidator = apbAddressWsDTOValidator;
	}
}
