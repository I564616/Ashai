/**
 *
 */
package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabmiller.facades.customer.SABMCustomerFacade;

import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;

/**
 * Contoller for any server side interaction on the header, example - changing the B2BUnits from the header action will
 * be handled by this controller
 *
 * @author joshua.a.antony
 */
@Controller
@Scope("tenant")
@RequestMapping("/header")
public class HeaderPageController extends SabmAbstractPageController
{
	protected static final Logger LOG = LoggerFactory.getLogger(HeaderPageController.class);

	private static final String B2BUNIT_GUID_PATH_VARIABLE_PATTERN = "{b2bUnitGUID:.*}";

	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;


	/**
	 * Handles selection of B2BUnit from the header.
	 */
	@GetMapping("/b2bunit/" + B2BUNIT_GUID_PATH_VARIABLE_PATTERN)
	public String changeB2BUnit(@PathVariable("b2bUnitGUID") final String b2bUnitId, final HttpServletRequest request)
	{
		customerFacade.changeB2BUnit(b2bUnitId);

		//Redirect to the page from which the B2BUnit was selected in the header
		return REDIRECT_PREFIX + request.getHeader("Referer");
	}


}
