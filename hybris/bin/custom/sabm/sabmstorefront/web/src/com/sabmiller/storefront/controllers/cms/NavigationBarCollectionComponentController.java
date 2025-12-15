/**
 *
 */
package com.sabmiller.storefront.controllers.cms;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.storefront.controllers.ControllerConstants;
import de.hybris.platform.acceleratorcms.model.components.NavigationBarCollectionComponentModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * @author ross.hengjun.zhu
 *
 */
@Controller("NavigationBarCollectionComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.NavigationBarCollectionComponent)
public class NavigationBarCollectionComponentController
		extends AbstractCMSComponentController<NavigationBarCollectionComponentModel>
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.storefront.controllers.cms.AbstractCMSComponentController#fillModel(javax.servlet.http.
	 * HttpServletRequest, org.springframework.ui.Model,
	 * de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)
	 */
	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final NavigationBarCollectionComponentModel component)
	{
		/*
		 * Fill the component attribute
		 */
		for (final String property : getCmsComponentService().getEditorProperties(component))
		{
			try
			{
				final Object value = modelService.getAttributeValue(component, property);
				model.addAttribute(property, value);
			}
			catch (final AttributeNotSupportedException ignore)
			{
			}
		}

		
	}

	

}
