/**
 * 
 */
package com.sabmiller.storefront.controllers.cms;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabm.core.model.cms.components.DealHeaderComponentModel;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.storefront.controllers.ControllerConstants;

/**
 * @author meghaaga
 *
 */
/**
 * Controller for CMS DealHeaderComponent.
 */
@Controller("DealHeaderComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.DealHeaderComponent)
public class DealHeaderComponentController extends AbstractCMSComponentController<DealHeaderComponentModel>
{
	@Resource(name = "sessionService")
	private SessionService sessionService;
	@Resource(name = "sabmDealsSearchFacade")
	private SABMDealsSearchFacade sabmDealsSearchFacade;
	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;
	
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Override
	protected void fillModel(HttpServletRequest request, Model model, DealHeaderComponentModel component)
	{
		// YTODO Auto-generated method stub
		model.addAttribute("dealHeaderComponent",component.getDealComponent().getUid());
		/*
		 * Fill the "SABMC-908 Mega Menu - Deals" Link relevant attributes.
		 */
		final Date currentDeliveryDate = (Date) sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);
		if (currentDeliveryDate == null)
		{
			return;
		}

		final Calendar currentDeliveryCalendar = Calendar.getInstance();
		currentDeliveryCalendar.setTime(currentDeliveryDate);



		final List<DealJson> deals = executeSafe(() -> sabmDealsSearchFacade.searchDeals(currentDeliveryDate, Boolean.TRUE), null);

		if (deals == null)
		{
			model.addAttribute("showMenu", false);
			// this means something might have happened when iterating through deals, probably a clash with delete on the deals import
			return;
		}

		int dealCount  = deals.size();
		//Start : Loggers for deal issue 0000314859
		LOG.info("Deal Size in DealHeaderComponentController=>" + dealCount);

		//End : Loggers for deal issue 0000314859
		if (dealCount == 0)
		{
			final boolean hasUpcoming = executeSafe(()->sabmDealsSearchFacade.hasUpcomingDeals(),false);

			if (hasUpcoming)
			{
				model.addAttribute("showMenu", true);
				model.addAttribute("showCircle", false);
			}
			else
			{
				model.addAttribute("showMenu", false);
			}

		}
		else
		{
			model.addAttribute("showMenu", true);
			model.addAttribute("showCircle", true);
			model.addAttribute("dealQuantity", dealCount);

			// Set play animation mark
			final Boolean playAnimation = sessionService.getAttribute(SabmCoreConstants.SESSION_DEAL_ANIMATION);
			if (playAnimation != null && playAnimation.booleanValue())
			{
				model.addAttribute("playAnimation", false);
			}
			else
			{
				model.addAttribute("playAnimation", true);
				sessionService.setAttribute(SabmCoreConstants.SESSION_DEAL_ANIMATION, true);
			}
		}
		


	}

	protected <T> T executeSafe(final Callable<T> callable, final T defaultForError){
		try {
			return callable.call();
		}catch (Throwable e){
			LOG.warn("An error occurred invoking callable ",e);
		}
		return defaultForError;
	}

		
	}


