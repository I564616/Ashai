package com.apb.storefront.controllers.pages;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apb.storefront.data.LoginValidateInclusionData;
import com.apb.storefront.security.ImpersonateUserLoginStrategy;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.search.restriction.SabmSearchRestrictionService;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.customer.SABMCustomerFacade;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;

@Controller
@Scope("tenant")
public class ImpersonateCustomerController extends AbstractPageController{
	
	private static final Logger LOG = LoggerFactory.getLogger(ImpersonateCustomerController.class.getName());
	
	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;
	
	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;
	
	@Resource(name = "impersonateUserLoginStrategy")
	private ImpersonateUserLoginStrategy impersonateUserLoginStrategy;
	
	
	@Resource(name = "sabmSearchRestrictionService")
	private SabmSearchRestrictionService sabmSearchRestrictionService;
	
	@Resource(name = "sabmCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;
	
	@Resource(name = "cartFacade")
	private SABMCartFacade sabmCartFacade;
	
	@GetMapping("/bdeview")
	public void bdeViewOnlyLogin(@RequestParam(value = "token", required = true) final String token, @RequestParam(value = "landingPage", required = false) final String landingPage, final HttpServletRequest request,
			final HttpServletResponse response,final Model model,final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		try{
			
				UserModel customerModel = customerFacade.validateSecureToken(token);
				if(customerModel instanceof BDECustomerModel){
					final String newSecureToken = customerFacade.getNewSecureToken(customerModel.getUid());
					if(StringUtils.isNotBlank(newSecureToken)){
						
						if(!request.getSession().isNew()){
							//Sleep time 1/2 sec. This is because , i have observed it is throwing long errors in console when invalidating a session if no sleep time.
							Thread.sleep(500);
							request.getSession().invalidate();
						}
						HttpSession session1 = request.getSession();
						 String encodedRedirectUrl = response.encodeRedirectURL(request.getContextPath()+ 
								"/bdelogin?token="+ newSecureToken.toString().replaceAll("\\+", "%2B"));
						if(StringUtils.isNotBlank(landingPage)){
							encodedRedirectUrl = encodedRedirectUrl + "&landingPage=" +landingPage;
						}
						response.sendRedirect(encodedRedirectUrl);
						}	
				}
		}
   	catch (final IllegalArgumentException e)
   	{
   		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "updatePwd.token.invalid");
   	}
   	catch (final TokenInvalidatedException e)
   	{
   		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "updatePwd.token.invalidated");
   	}
		catch (final Exception e)
		{
			LOG.warn("The link used for Staff User Login was invalid.", e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "Invalid Login");
		}

	}
	
	@GetMapping("/bdelogin")
	public String bdeViewLogin(@RequestParam(value = "token", required = true) final String token, @RequestParam(value = "landingPage", required = false) final String landingPage,final HttpServletRequest request,
			final HttpServletResponse response,final Model model,final RedirectAttributes redirectModel){
	
		try{
			sabmSearchRestrictionService.disableSearchRestrictions(); 
			UserModel customerModel = customerFacade.validateSecureToken(token);
			if(customerModel instanceof BDECustomerModel && impersonateUserLoginStrategy.loginAsBDECustomer(customerModel.getUid(), request, response)){
				sabmSearchRestrictionService.enableSearchRestrictions(); 
				sabmCartFacade.removeSessionCart();
				sabmCustomerFacade.setCustomerCreditAndInclusionInSession();
				if(StringUtils.isNotBlank(landingPage)){
					return REDIRECT_PREFIX + landingPage;
				} else {
				return REDIRECT_PREFIX + "/";
				}
			}
			else {
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "Invalid Login");
			}
   	}
   	catch (final IllegalArgumentException e)
   	{
   		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "updatePwd.token.invalid");
   	}
   	catch (final TokenInvalidatedException e)
   	{
   		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "updatePwd.token.invalidated");
   	}
   	catch (final Exception e)
   	{
   		LOG.warn("The link used for Staff User Login was invalid.", e);
   		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "Invalid Login");
   	}
   	return REDIRECT_PREFIX + "/";
   	}

}
