package com.apb.storefront.breadcrumb.impl;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.SearchBreadcrumbBuilder;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;


public class APBSearchBreadcrumbBuilder extends SearchBreadcrumbBuilder
{

	private static final String LAST_LINK_CLASS = "active";
	private static Logger LOG = LoggerFactory.getLogger(APBSearchBreadcrumbBuilder.class);

	@Resource(name = "messageSource")
	private MessageSource messageSource;

	@Resource(name = "i18nService")
	private I18NService i18nService;





	public I18NService getI18nService()
	{
		return i18nService;
	}

	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	/**
	 * @return the message source
	 */
	public MessageSource getMessageSource()
	{
		return messageSource;
	}

	/**
	 * @param messageSource
	 */
	public void setMessageSource(final MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}

	@Override
	protected void createBreadcrumbCategoryHierarchyPath(final String categoryCode, final boolean emptyBreadcrumbs,
			final List<Breadcrumb> breadcrumbs)
	{
		// Create category hierarchy path for breadcrumb
		final List<Breadcrumb> categoryBreadcrumbs = new ArrayList<>();
		final Collection<CategoryModel> categoryModels = new ArrayList<>();
		final CategoryModel lastCategoryModel = getCommerceCategoryService().getCategoryForCode(categoryCode);
		categoryModels.addAll(lastCategoryModel.getSupercategories());
		/* ACP-55 START */
		if (!lastCategoryModel.isHideInBreadcrumb())
		{
			categoryBreadcrumbs.add(getCategoryBreadcrumb(lastCategoryModel, !emptyBreadcrumbs ? LAST_LINK_CLASS : ""));
		}
		/* ACP-55 END */

		while (!categoryModels.isEmpty())
		{
			final CategoryModel categoryModel = categoryModels.iterator().next();

			if (!(categoryModel instanceof ClassificationClassModel))
			{
				if ((null != categoryModel))
				{

					if (!(categoryModel.isHideInBreadcrumb()))
					{
						categoryBreadcrumbs.add(getCategoryBreadcrumb(categoryModel));
					}
					categoryModels.clear();
					categoryModels.addAll(categoryModel.getSupercategories());
				}
			}
			else
			{
				categoryModels.remove(categoryModel);
			}
		}
		Collections.reverse(categoryBreadcrumbs);
		breadcrumbs.addAll(categoryBreadcrumbs);
	}

	@Override
	public List<Breadcrumb> getBreadcrumbs(final String categoryCode, final String searchText, final boolean emptyBreadcrumbs)
	{
		final List<Breadcrumb> breadcrumbs = new ArrayList<>();
		final String searchTextNew = getMessageSource().getMessage("search.result.page.breadcrumb.message", new Object[]
		{ searchText }, getI18nService().getCurrentLocale());

		if (categoryCode == null)
		{
			final Breadcrumb breadcrumb = new Breadcrumb("/search?text=" + getEncodedUrl(searchText), searchTextNew,
					emptyBreadcrumbs ? LAST_LINK_CLASS : "");
			breadcrumbs.add(breadcrumb);
		}
		else
		{
			createBreadcrumbCategoryHierarchyPath(categoryCode, emptyBreadcrumbs, breadcrumbs);
		}
		return breadcrumbs;
	}

}
