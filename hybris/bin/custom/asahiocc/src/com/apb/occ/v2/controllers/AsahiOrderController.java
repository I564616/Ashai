/**
 * 
 */
package com.apb.occ.v2.controllers;

import de.hybris.platform.asahiocc.dto.order.OrderListWsDTO;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import com.apb.occ.v2.exception.AsahiWebServiceException;
import com.apb.occ.v2.validators.AsahiOrderWsDTOValidator;
import com.sabmiller.facades.order.SABMOrderFacade;


/**
 * The Class AsahiOrderController.
 * 
 * @author Kuldeep.Singh1
 */

@RestController
@RequestMapping(value = "/{baseSiteId}/order")
@ApiVersion("v2")
public class AsahiOrderController extends AsahiBaseController
{

	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiOrderController.class);

	/** The data mapper. */
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	/** The asahi order facade. */
	@Resource(name = "orderFacade")
	private SABMOrderFacade sabmOrderFacade;

	/** The asahi order ws DTO validator. */
	@Resource(name = "asahiOrderWsDTOValidator")
	private AsahiOrderWsDTOValidator asahiOrderWsDTOValidator;

	@Autowired
    private ImpersonationService impersonationService;

    @Autowired
    private BaseSiteService baseSiteService;

	/**
	 * Import orders.
	 * 
	 * @param salesOrder
	 *           the sales order
	 * @throws WebserviceValidationException
	 *            the webservice validation exception
	 */
	@RequestMapping(value = "/importOrders", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseBody
	public void importOrders(@RequestBody final OrderListWsDTO salesOrder, @PathVariable("baseSiteId") final String siteUid) throws WebserviceValidationException
	{
	    showIncomingData(salesOrder, "order");
		logger.debug("Importing Orders into hybris");

		if (CollectionUtils.isNotEmpty(salesOrder.getSalesOrder()))
		{
			final ArrayList<Object> errorList = new ArrayList<Object>();
			for (final OrderWsDTO order : salesOrder.getSalesOrder())
			{
				logger.debug("Importing Order with Code: " + order.getCode());

				final Errors errors = new BeanPropertyBindingResult(order, "order");
				this.asahiOrderWsDTOValidator.validate(order, errors);
				if (errors.hasErrors())
				{
					errorList.add(errors);
				}
				else
				{
                    ImpersonationContext ctx = new ImpersonationContext();
                    ctx.setSite(baseSiteService.getBaseSiteForUID(siteUid));
					order.setOrderTotalCDL(order.getOrderCDL());
					this.sabmOrderFacade.importOrder(this.dataMapper.map(order, OrderData.class), siteUid);
				}	

				logger.debug("Order with code: " + order.getCode() + " is imported");
			}
			if (CollectionUtils.isNotEmpty(errorList))
			{
				throw new AsahiWebServiceException(errorList);
			}
		}
	}
}
