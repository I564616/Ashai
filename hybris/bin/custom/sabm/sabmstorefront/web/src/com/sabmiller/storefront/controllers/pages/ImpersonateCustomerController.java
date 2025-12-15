/**
 *
 */
package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.storefront.form.ImpersonateCustomerForm;
import com.sabmiller.storefront.security.ImpersonateUserLoginStrategy;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;


/**
 * Controller impersonate a user
 */
@Controller
@Scope("tenant")
public class ImpersonateCustomerController extends SabmAbstractPageController
{

	private static final Logger LOG = LoggerFactory.getLogger(ImpersonateCustomerController.class.getName());

	/** The Constant Personal Assistance Search Page. */
	private static final String PASEARCH_CMS_PAGE = "personalAssistanceSearch";

	@Resource(name = "impersonateUserLoginStrategy")
	private ImpersonateUserLoginStrategy impersonateUserLoginStrategy;
	
	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;
	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;

	@PostMapping("/impersonate")
	@RequireHardLogIn
	public String impersonateCustomer(@Valid final ImpersonateCustomerForm impersonateCustomerForm, final HttpServletRequest request, final HttpServletResponse response)
	{
		// Fix as per INC0486066-Orders placed with incorrect ship to and sold to information
		/*CartModel cartModel = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_CART);
		if(cartModel != null && cartModel.getEntries().size() > 0){
			sessionService.removeAttribute(SabmCoreConstants.SESSION_ATTR_CART);
		}*/
		sessionService.removeAttribute(SabmCoreConstants.SESSION_ATTR_CART);
		impersonateUserLoginStrategy.loginAsCustomer(impersonateCustomerForm.getUid(),impersonateCustomerForm.getUnit(), request, response);
		if(StringUtils.isNotBlank(impersonateCustomerForm.getLandingPage())){
		return REDIRECT_PREFIX + "/"+impersonateCustomerForm.getLandingPage();
		} else {
			return REDIRECT_PREFIX + "/";
		}
	}
	
	@GetMapping("/impersonate/change")
	@RequireHardLogIn
	public void changeImpersonateCustomer( final HttpServletRequest request,
			final HttpServletResponse response)
	{	
		try
		{
		UserModel impersonatedUser = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_IMPERSONATE_PA);
		if(null!= impersonatedUser){
			final String newSecureToken = customerFacade.getNewSecureToken(impersonatedUser.getUid());
			if(StringUtils.isNotBlank(newSecureToken)){
				HttpSession session = request.getSession(false);
				if(null != session){
					//Sleep time 1/2 sec. This is because , i have observed it is throwing long errors in console when invalidating a session if no sleep time.
					Thread.sleep(500);
				//	session.invalidate();	
				}				
				final String encodedRedirectUrl = response.encodeRedirectURL(request.getContextPath() + 
						"/paSearch/relogin?token="+ newSecureToken.toString().replaceAll("\\+", "%2B"));
				response.sendRedirect(encodedRedirectUrl);
				}			
			}		
		}
		catch (Exception e)
		{
			LOG.error("Exception in changeImpersonateCustomer method "+ e);
		}
	}
	
	@GetMapping("/paSearch/relogin")
	public String reLogin(@RequestParam(value = "token",required = true) final String token, final HttpServletRequest request,
			final HttpServletResponse response,final Model model,final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		try{
				UserModel user = customerFacade.validateSecureToken(token);
				if(user instanceof EmployeeModel && impersonateUserLoginStrategy.loginAsEmployee(request, response, user.getUid())){
					return REDIRECT_PREFIX + "/";
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
			LOG.warn("The link used to access the update page was invalid.", e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "Invalid Login");
		}
		return REDIRECT_PREFIX + "/";
	}
	
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
						 String encodedRedirectUrl = response.encodeRedirectURL(request.getContextPath() + 
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
			LOG.warn("The link used to access the update page was invalid.", e);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "Invalid Login");
		}

	}
	
	@GetMapping("/bdelogin")
	public String bdeViewLogin(@RequestParam(value = "token", required = true) final String token, @RequestParam(value = "landingPage", required = false) final String landingPage,final HttpServletRequest request,
			final HttpServletResponse response,final Model model,final RedirectAttributes redirectModel){
	
		try{
			UserModel customerModel = customerFacade.validateSecureToken(token);
			if(customerModel instanceof BDECustomerModel && impersonateUserLoginStrategy.loginAsBDECustomer(customerModel.getUid(), request, response)){
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
   		LOG.warn("The link used to access the update page was invalid.", e);
   		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "Invalid Login");
   	}
   	return REDIRECT_PREFIX + "/";
   	}

}
