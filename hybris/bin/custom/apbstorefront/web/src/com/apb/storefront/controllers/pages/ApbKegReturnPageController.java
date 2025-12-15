package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.apb.facades.kegreturn.data.KegSizeData;
import com.apb.facades.user.ApbUserFacade;
import com.apb.facades.user.data.ApbCompanyData;
import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.controllers.ControllerConstants;
import com.apb.storefront.forms.ApbCompanyDetailsForm;
import com.apb.storefront.forms.ApbKegReturnForm;
import com.apb.storefront.util.ApbKegReturnUtil;


/**
 * @author C5252631
 *
 *         Keg Return Controller for sending Email to Customer and Customer Care Team
 *
 */
@Controller
@RequestMapping(value = "/keg-return")
public class ApbKegReturnPageController extends ApbAbstractPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbKegReturnPageController.class);

	@Resource(name = "b2bCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;

	@Resource(name = "apbUserFacade")
	private ApbUserFacade apbUserFacade;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "apbKegReturnValidator")
	private Validator apbKegReturnValidator;

	@Resource(name = "apbKegReturnUtil")
	private ApbKegReturnUtil apbKegReturnUtil;

	/**
	 * @param apbKegReturnForm
	 * @param model
	 * @param bindingResult
	 * @param redirectModel
	 * @param request
	 * @return kegReturn
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping
	public String kegReturn(final ApbKegReturnForm apbKegReturnForm, final Model model, final BindingResult bindingResult,
			final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		model.addAttribute(new ApbCompanyDetailsForm());
		storeCmsPageInModel(model, getContentPageForLabelOrId(ApbStoreFrontContants.KEG_RETURN));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ApbStoreFrontContants.KEG_RETURN));
		updatePageTitle(model, getContentPageForLabelOrId(ApbStoreFrontContants.KEG_RETURN));
		final Breadcrumb companyDetails = new Breadcrumb("#",
				getMessageSource().getMessage("header.link.keg.return", null, getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(companyDetails));
		final ApbCompanyData companyData = sabmCustomerFacade.getB2BCustomerData();
		model.addAttribute(ApbStoreFrontContants.KEG_DATA, companyData);
		model.addAttribute(ApbStoreFrontContants.APB_KEG_RETURN_FORM, apbKegReturnForm);
		if ((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
		{
			model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		}
		return ControllerConstants.Views.Pages.Account.KegReturnPage;
	}

	/**
	 * @param apbKegReturnForm
	 * @param model
	 * @param bindingResult
	 * @param redirectModel
	 * @param request
	 * @return kegReturnRequest
	 * @throws CMSItemNotFoundException
	 */
	@PostMapping("/keg-return-request")
	public String kegReturnRequest(final ApbKegReturnForm apbKegReturnForm, final Model model, final BindingResult bindingResult,
			final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ApbStoreFrontContants.KEG_RETURN));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ApbStoreFrontContants.KEG_RETURN));
		updatePageTitle(model, getContentPageForLabelOrId(ApbStoreFrontContants.KEG_RETURN));
		final Breadcrumb companyDetails = new Breadcrumb("#",
		getMessageSource().getMessage("header.link.keg.return", null, getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(companyDetails));
		apbKegReturnValidator.validate(apbKegReturnForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			model.addAttribute(apbKegReturnForm);
			GlobalMessages.addErrorMessage(model, ApbStoreFrontContants.FORM_GLOBAL_ERROR);
			model.addAttribute("errors", bindingResult.getFieldErrors());
			return ControllerConstants.Views.Pages.Account.KegReturnPage;
		}
		sabmCustomerFacade.sendKegReturnEmail(apbKegReturnUtil.setKegReturnData(apbKegReturnForm));
		return REDIRECT_PREFIX + "/kegReturnConfirmation";
	}

	/**
	 * get all delivery address from db on the basis of logged in customer
	 *
	 * @return pickAddress
	 */
	@ModelAttribute("pickupAddress")
	public List<AddressData> getPickupAddress()
	{
		return apbUserFacade.getB2BUnitAddressesForUser(userService.getCurrentUser());
	}

	/**
	 * get keg Size based on active cmssite
	 *
	 * @return kegSizes
	 */
	@ModelAttribute("kegSizes")
	public List<KegSizeData> getKegSizes()
	{
		return apbUserFacade.getKegSizes(getCmsSiteService().getCurrentSite());
	}

	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
	}
}
