package com.apb.storefront.controllers.misc;

import java.text.ParseException;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.facades.checkout.APBCheckoutFacade;
import com.apb.storefront.controllers.ControllerConstants;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.model.ModelService;

@Controller
@RequestMapping(value = "history")
public class ReorderController extends AbstractController{
	
	@Resource(name = "b2bCheckoutFacade")
	private CheckoutFacade b2bCheckoutFacade;
	
	@Resource
	private CartService cartService;
	
	@Resource(name="apbCheckoutFacade")
	private APBCheckoutFacade apbCheckoutFacade;
	
	@Resource
	ModelService modelService;
	
	@Resource
	CartFacade cartFacade;
	
	@Resource
	CloneAbstractOrderStrategy cloneAbstractOrderStrategy;
	
	private static final String CART_REDIRECT = "redirect:/cart";
	
	@PostMapping("/reorder")
	@RequireHardLogIn
	@ResponseBody
	public String showReorderPopup(@RequestParam(value = "orderCode") final String orderCode, final RedirectAttributes redirectModel, Model model) throws CMSItemNotFoundException, InvalidCartException, CommerceCartModificationException, ParseException
	{
		if(CollectionUtils.isNotEmpty(cartService.getSessionCart().getEntries()))
		{
			model.addAttribute("orderCode", orderCode);
			return "true";
		}
		return "false";
	}

	@RequestMapping(value = "/reorder/keepCart", method = { RequestMethod.PUT, RequestMethod.POST })
	@RequireHardLogIn
	public String reorder(@RequestParam(value = "orderCode") final String orderCode, @RequestParam(value = "clear") final boolean clearCart, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException, InvalidCartException, ParseException, CommerceCartModificationException, CommerceCartRestorationException, CommerceCartMergingException
	{
		apbCheckoutFacade.createCartFromOrder(orderCode, clearCart);
		return CART_REDIRECT;
	}

}
