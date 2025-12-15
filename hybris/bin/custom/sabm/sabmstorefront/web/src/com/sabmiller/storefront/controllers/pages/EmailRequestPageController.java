/**
 *
 */
package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

import jakarta.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sabmiller.facades.email.SABMEmailFacade;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;


/**
 *
 */
@Controller
@Scope("tenant")
public class EmailRequestPageController extends SabmAbstractPageController
{
	private static final String SRES_CMS_PAGE = "serviceRequestEmailSent";

	@Resource(name = "emailFacade")
	private SABMEmailFacade emailFacade;
	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder homeBreadcrumbBuilder;

	@PostMapping("/sendRequestEmail")
	public String sendServiceRequestEmail(@RequestParam("key") final String requestKey,
			@RequestParam("type") final String requestType, @RequestParam("text") final String text, final Model model)
			throws CMSItemNotFoundException
	{
		emailFacade.sendServiceRequestEmail(requestKey, requestType, text);

		storeCmsPageInModel(model, getContentPageForLabelOrId(SRES_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SRES_CMS_PAGE));
		model.addAttribute("breadcrumbs", homeBreadcrumbBuilder.getBreadcrumbs("text.service.request.title"));
		model.addAttribute("pageType", SABMWebConstants.PageType.EMAIL_REQUEST.name());

		return getViewForPage(model);
	}

	/**
	 * @author yuxiao.wang
	 * @param subject
	 * @param message
	 * @param model
	 * @return authenticatedContactUsMsgSent
	 * @throws CMSItemNotFoundException
	 */
	//	@RequestMapping(value = "/sendContactUsEmail", method = RequestMethod.POST)
	//	public String sendContactUsEmail(@RequestParam(value = "subject", required = true) final String subject,
	//			@RequestParam(value = "message") final String message, final Model model) throws CMSItemNotFoundException
	//	{
	//		//Send a contact us email
	//		emailFacade.sendContactUsEmail(subject, message);
	//
	//		//Display the authenticatedContactUsMsgSent page
	//		storeCmsPageInModel(model, getContentPageForLabelOrId(SCUS_CMS_PAGE));
	//		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SCUS_CMS_PAGE));
	//		model.addAttribute("breadcrumbs", homeBreadcrumbBuilder.getBreadcrumbs("text.authenticated.contact.us.title"));
	//		return getViewForPage(model);
	//	}

}
