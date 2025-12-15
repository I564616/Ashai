/**
 * 
 */
package com.apb.occ.v2.controllers;

import java.util.ArrayList;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.facades.sam.invoice.AsahiSAMInvoiceFacade;
import com.apb.occ.v2.exception.AsahiWebServiceException;
import com.apb.occ.v2.validators.AsahiSAMInvoiceWsDTOValidator;

import de.hybris.platform.apboccaddon.dto.sam.AsahiSAMInvoiceWsDTO;
import de.hybris.platform.apboccaddon.dto.sam.AsahiSAMInvoicesWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;


/**
 * The Class AsahiSAMInvoiceController.
 * 
 * @author Kuldeep.Singh1
 */

@RestController
@RequestMapping(value = "/{baseSiteId}/invoice")
@ApiVersion("v2")
public class AsahiSAMInvoiceController extends AsahiBaseController
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiSAMInvoiceController.class);

	/** The data mapper. */
	@Resource
	private DataMapper dataMapper;

	/** The asahi SAM invoice ws DTO validator. */
	@Resource(name = "asahiSAMInvoiceWsDTOValidator")
	private AsahiSAMInvoiceWsDTOValidator asahiSAMInvoiceWsDTOValidator;
	
	/** The asahi SAM invoice facade. */
	@Resource
	private AsahiSAMInvoiceFacade asahiSAMInvoiceFacade;


	/**
	 * Gets the asahi SAM invoice ws DTO validator.
	 *
	 * @return the asahi SAM invoice ws DTO validator
	 */
	public AsahiSAMInvoiceWsDTOValidator getAsahiSAMInvoiceWsDTOValidator() {
		return asahiSAMInvoiceWsDTOValidator;
	}


	/**
	 * Sets the asahi SAM invoice ws DTO validator.
	 *
	 * @param asahiSAMInvoiceWsDTOValidator the new asahi SAM invoice ws DTO validator
	 */
	public void setAsahiSAMInvoiceWsDTOValidator(
			AsahiSAMInvoiceWsDTOValidator asahiSAMInvoiceWsDTOValidator) {
		this.asahiSAMInvoiceWsDTOValidator = asahiSAMInvoiceWsDTOValidator;
	}


	/**
	 * Import invoices.
	 *
	 * @param invoices the invoices
	 * @throws WebserviceValidationException            the webservice validation exception
	 */
	@PostMapping(value = "/importInvoices", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importInvoices(@RequestBody final AsahiSAMInvoicesWsDTO invoices) throws WebserviceValidationException
	{
		logger.info("Importing invoices into hybris");

		if (CollectionUtils.isNotEmpty(invoices.getInvoices()))
		{
			final ArrayList<Object> errorList = new ArrayList<>();
			for (final AsahiSAMInvoiceWsDTO invoice : invoices.getInvoices())
			{
				logger.info("Importing invoice with document number: " + invoice.getDocumentNumber());
				
				final Errors errors = new BeanPropertyBindingResult(invoice, "invoice");
				this.getAsahiSAMInvoiceWsDTOValidator().validate(invoice, errors);
				if (errors.hasErrors())
				{
					errorList.add(errors);
				}
				else
				{
					this.asahiSAMInvoiceFacade.importInvoice(this.dataMapper.map(invoice, AsahiSAMInvoiceData.class));
				}

				logger.info("Invoice with document number: " + invoice.getDocumentNumber() + " is imported");
			}
			if (CollectionUtils.isNotEmpty(errorList))
			{
				throw new AsahiWebServiceException(errorList);
			}
		}
	}
}
