package com.apb.storefront.controllers.pages;

import java.util.Collections;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.apb.storefront.constant.ApbStoreFrontContants;
import com.apb.storefront.controllers.ControllerConstants;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;


/**
 * The Class AsahiFooterPageController.
 */
@Controller
public class AsahiFooterPageController extends ApbAbstractPageController
{
	/**
	 * @param contactUsForm
	 * @param model
	 * @param bindingResult
	 * @param redirectModel
	 * @param request
	 * @return contactUsPage jsp
	 * @throws CMSItemNotFoundException
	 */
	@GetMapping("/termsAndLegal")
	public String getTermsAndLegal(final Model model,final HttpServletRequest request) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ApbStoreFrontContants.ASAHI_TERMS_AND_LEGAL_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ApbStoreFrontContants.ASAHI_TERMS_AND_LEGAL_PAGE));
		updatePageTitle(model, getContentPageForLabelOrId(ApbStoreFrontContants.ASAHI_TERMS_AND_LEGAL_PAGE));
		
		final Breadcrumb contactUsBreadcrumb = new Breadcrumb("#",
				getMessageSource().getMessage("terms.and.legal.page.title", null, getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(contactUsBreadcrumb));
		 if((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
             model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		return ControllerConstants.Views.Pages.Account.TermsAndLegalPage;
	}

	/**
	 * Gets the about us.
	 *
	 * @param model the model
	 * @param request the request
	 * @return contactUsPage jsp
	 * @throws CMSItemNotFoundException the CMS item not found exception
	 */
	@GetMapping("/aboutUs")
	public String getAboutUs(final Model model,final HttpServletRequest request) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ApbStoreFrontContants.ASAHI_ABOUT_US_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ApbStoreFrontContants.ASAHI_ABOUT_US_PAGE));
		updatePageTitle(model, getContentPageForLabelOrId(ApbStoreFrontContants.ASAHI_ABOUT_US_PAGE));
		
		final Breadcrumb contactUsBreadcrumb = new Breadcrumb("#",
				getMessageSource().getMessage("about.us.page.title", null, getI18nService().getCurrentLocale()), null);
		model.addAttribute("breadcrumbs", Collections.singletonList(contactUsBreadcrumb));
		 if((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
             model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		return ControllerConstants.Views.Pages.Account.AboutUsPage;
	}
	

    /**
     * Gets the promotion winners list.
     *
     * @param model the model
     * @param request the request
     * @return contactUsPage jsp
     * @throws CMSItemNotFoundException the CMS item not found exception
     */
    @GetMapping("/promotion-winners")
    public String getPromotionWinners(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
    {
        storeCmsPageInModel(model, getContentPageForLabelOrId(ApbStoreFrontContants.ALB_PROMOTION_WINNERS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ApbStoreFrontContants.ALB_PROMOTION_WINNERS_PAGE));
        updatePageTitle(model, getContentPageForLabelOrId(ApbStoreFrontContants.ALB_PROMOTION_WINNERS_PAGE));

        final Breadcrumb contactUsBreadcrumb = new Breadcrumb("#",
                getMessageSource().getMessage("promotion.winners.page.title", null, getI18nService().getCurrentLocale()), null);
        model.addAttribute("breadcrumbs", Collections.singletonList(contactUsBreadcrumb));
        if((getContentPageForLabelOrId(null)).getBackgroundImage() != null)
            model.addAttribute("media", (getContentPageForLabelOrId(null)).getBackgroundImage().getURL());
		return ControllerConstants.Views.Pages.Account.promotionWinnersPage;
    }
	
	/**
	 * Page Title of cms page
	 *
	 * @param model
	 * @param cmsPage
	 */
	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
	}

}
