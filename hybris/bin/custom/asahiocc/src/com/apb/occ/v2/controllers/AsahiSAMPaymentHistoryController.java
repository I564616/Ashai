/**
 * 
 */
package com.apb.occ.v2.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.apb.facades.sam.data.AsahiSAMInvoiceData;
import com.apb.facades.sam.data.AsahiSAMPaymentData;
import com.apb.facades.sam.payment.history.AsahiSAMPaymentHistoryFacade;

import de.hybris.platform.asahiocc.dto.sam.AsahiSAMInvoiceWsDTO;
import de.hybris.platform.asahiocc.dto.sam.AsahiSAMPaymentHistWsDTO;
import de.hybris.platform.asahiocc.dto.sam.AsahiSAMPaymentWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;


/**
 * The Class AsahiSAMPaymentHistoryController.
 * 
 * @author Kuldeep.Singh1
 */

@RestController
@RequestMapping(value = "/{baseSiteId}/paymentHist")
@ApiVersion("v2")
public class AsahiSAMPaymentHistoryController extends AsahiBaseController
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiSAMPaymentHistoryController.class);

	/** The data mapper. */
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;
	
	/** The asahi SAM payment history facade. */
	@Resource
	private AsahiSAMPaymentHistoryFacade asahiSAMPaymentHistoryFacade;


	/**
	 * Import payment history.
	 *
	 * @param paymentHistory the payment history
	 * @throws WebserviceValidationException            the webservice validation exception
	 */
	@RequestMapping(value = "/importPaymentHist", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importPaymentHistory(@RequestBody final AsahiSAMPaymentHistWsDTO documents) throws WebserviceValidationException
	{
		logger.info("Importing SAM Document into hybris");
		if (CollectionUtils.isNotEmpty(documents.getDocuments()))
		{
			List<AsahiSAMInvoiceWsDTO> samInvoiceList = documents.getDocuments().get(0).getInvoices();
			
			Collections.sort(samInvoiceList, (AsahiSAMInvoiceWsDTO a1, AsahiSAMInvoiceWsDTO a2) -> a1.getPaymentDocIdentifier().compareTo(a2.getPaymentDocIdentifier()) );
			final ArrayList<Object> errorList = new ArrayList<>();
			for (final AsahiSAMInvoiceWsDTO invoice : samInvoiceList)
			{
				logger.info("Importing SAM Document with Number: " + invoice.getDocumentNumber());

				this.asahiSAMPaymentHistoryFacade.importPaymentHistory(this.dataMapper.map(invoice, AsahiSAMInvoiceData.class));

				logger.info("Payment SAM Document Number: " + invoice.getDocumentNumber() + " is imported");
			}
		}
	}
}
