/**
 * 
 */
package com.sabmiller.storefront.controllers.cms;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabm.core.model.cms.components.SupportHeaderComponentModel;
import com.sabmiller.storefront.controllers.ControllerConstants;

/**
 * @author meghaaga
 *
 */
@Controller("SupportHeaderComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.SupportHeaderComponent)
public class SupportHeaderComponentController extends AbstractCMSComponentController<SupportHeaderComponentModel>
{

	@Override
	protected void fillModel(HttpServletRequest request, Model model, SupportHeaderComponentModel component)
	{
		// YTODO Auto-generated method stub
		model.addAttribute("supportHeaderComponent", component.getSupportComponent().getUid());
		
	}

}
