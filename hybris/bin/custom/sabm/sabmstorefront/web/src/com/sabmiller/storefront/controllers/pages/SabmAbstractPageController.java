/**
 * 
 */
package com.sabmiller.storefront.controllers.pages;

import jakarta.annotation.Resource;



import com.apb.core.util.AsahiCoreUtil;
import org.springframework.ui.Model;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;

/**
 * @author Varun.Goyal1
 *
 */
public class SabmAbstractPageController extends AbstractPageController
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
