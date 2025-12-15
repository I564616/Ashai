package com.apb.storefront.controllers.cms;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.storefront.controllers.ControllerConstants;
import com.sabmiller.facades.customer.SABMCustomerFacade;

import de.hybris.platform.acceleratorcms.model.components.CategoryNavigationComponentModel;
import de.hybris.platform.servicelayer.session.SessionService;


@Controller("CategoryNavigationComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.CategoryNavigationComponent)
public class CategoryNavigationComponentController extends AbstractAcceleratorCMSComponentController<CategoryNavigationComponentModel>
{
	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;
		
 	@Autowired
 	private SABMCustomerFacade sabmCustomerFacade;
 	
	@Resource(name= "sessionService")
	private SessionService sessionService;
	
	@Override
	protected void fillModel(HttpServletRequest request, Model model, CategoryNavigationComponentModel component) {
		
		if (asahiSiteUtil.isSga())
		{
			sabmCustomerFacade.setRestrictedCategoriesInSession();	
		}
	}
}
