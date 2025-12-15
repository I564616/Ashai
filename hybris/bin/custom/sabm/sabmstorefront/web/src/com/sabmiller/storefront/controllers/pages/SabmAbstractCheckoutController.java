/**
 * 
 */
package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;

import jakarta.annotation.Resource;

import org.springframework.ui.Model;

import com.apb.core.util.AsahiCoreUtil;

import de.hybris.platform.cms2.model.pages.ContentPageModel;

/**
 * @author Varun.Goyal1
 *
 */
public class SabmAbstractCheckoutController extends AbstractCheckoutController
{

	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@Override
	protected void storeCmsPageInModel(final Model model, final AbstractPageModel cmsPage)
	{
		if (model != null && cmsPage != null)
		{
			model.addAttribute(CMS_PAGE_MODEL, cmsPage);
			if (cmsPage instanceof ContentPageModel)
			{
				storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()));
			}
		}
		
		model.addAttribute("isNAPGroup",asahiCoreUtil.isNAPUser());
	}
	
}
