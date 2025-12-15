package com.sabmiller.storefront.controllers.cms;

import de.hybris.platform.cms2lib.model.components.RotatingImagesComponentModel;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.sabmiller.storefront.controllers.ControllerConstants;

@Controller("RotatingImagesComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.RotatingImagesComponent)
public class RotatingImagesComponentController extends AbstractCMSComponentController<RotatingImagesComponentModel> {

    @Override
   protected void fillModel(final HttpServletRequest request, final Model model, final RotatingImagesComponentModel component) {


   }
}
