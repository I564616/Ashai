package com.apb.facades.category.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.apb.core.service.category.AsahiCategoryService;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.category.ApbCategoryFacade;
import com.apb.facades.constants.ApbFacadesConstants;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import com.apb.core.util.AsahiSiteUtil;

/**
 * The Class ApbCategoryFacadeImpl.
 * 
 * @author Kuldeep.Singh1
 */
public class ApbCategoryFacadeImpl implements ApbCategoryFacade
{
	
	/** The Constant DYNAMICS_ALLOW_CATEGORY_CREATION_WITHOUT_SUPERCATEGORY. */
	private static final String DYNAMICS_ALLOW_CATEGORY_CREATION_WITHOUT_SUPERCATEGORY = "dynamics.allow.category.creation.without.superCategory.apb";
	
	/** The Constant ROOT_CATEGORY_CODE. */
	private static final String ROOT_CATEGORY_CODE = "root.superCategory.code.apb";
	
	/** The Constant CODE_COMPANY_CATALOG_ID. */
	private static final String CODE_COMPANY_CATALOG_ID = ".company.catalog.id";
	
	/** The Constant CODE_COMPANY_CATALOG_VERSION. */
	private static final String CODE_COMPANY_CATALOG_VERSION = ".company.catalog.version.staged";
	

	/** The category service. */
	@Resource(name = "asahiCategoryService")
	private AsahiCategoryService asahiCategoryService;

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	/** The catalog version service. */
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	/** The search restriction service. */
	@Resource(name = "searchRestrictionService")
	private SearchRestrictionService searchRestrictionService;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/** The user service. */
	@Resource
	UserService userService;

	/** The cms site service. */
	@Resource
	CMSSiteService cmsSiteService;
	
	/** The asahi site util. */
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/**
	 * Import category.
	 *
	 * @param categoryData
	 *           the category data
	 */
	@Override
	public void importCategory(CategoryData categoryData)
	{

		List<CategoryModel> superCategories = new ArrayList<CategoryModel>();
		if (StringUtils.isNotEmpty(categoryData.getParentCategoryId()))
		{
			superCategories = this.getSuperCategories(categoryData);
		}
		else
		{
			final boolean checkSuperCategory = this.asahiConfigurationService
					.getBoolean(DYNAMICS_ALLOW_CATEGORY_CREATION_WITHOUT_SUPERCATEGORY, false);

			if (checkSuperCategory)
			{
				superCategories = this.getRootCategory(categoryData.getCompanyCode());
			}
		}
		if (CollectionUtils.isNotEmpty(superCategories))
		{
			// Fetching Category based on code
			CategoryModel existingCategory = this.asahiCategoryService
					.getCategoryForCode(this.catalogVersionService
							.getCatalogVersion(this.asahiSiteUtil.getCatalogId(categoryData.getCompanyCode()),
									ApbFacadesConstants.STAGED_VERSION), categoryData.getCode());

			if (null != existingCategory)
			{
				//update existing category if not create new
				this.updateExistingCategory(categoryData, existingCategory, superCategories);
			}
			else
			{
				//create new category
				this.createNewCategory(categoryData, superCategories);
			}

		}
	}

	/**
	 * Gets the root category.
	 *
	 * @param companyCode the company code
	 * @return the root category
	 */
	private List<CategoryModel> getRootCategory(String companyCode)
	{
		final String rootCategory = this.asahiConfigurationService.getString(ROOT_CATEGORY_CODE, "1");
		
		CategoryModel superCategory = this.asahiCategoryService
				.getCategoryForCode(this.catalogVersionService
						.getCatalogVersion(this.asahiSiteUtil.getCatalogId(companyCode),
								ApbFacadesConstants.STAGED_VERSION), rootCategory);
		return Collections.singletonList(superCategory);
	}

	/**
	 * Gets the super categories.
	 *
	 * @param categoryData
	 *           the category data
	 * @return the super categories
	 */
	private List<CategoryModel> getSuperCategories(CategoryData categoryData)
	{

		// Fetching Super Categories based on code
		CategoryModel superCategory = this.asahiCategoryService
				.getCategoryForCode(this.catalogVersionService
						.getCatalogVersion(this.asahiSiteUtil.getCatalogId(categoryData.getCompanyCode()),
								ApbFacadesConstants.STAGED_VERSION), categoryData.getParentCategoryId());
		//update existing super category if not create new
		if (null != superCategory)
		{
			return Collections.singletonList(superCategory);
		}
		else
		{
			//create new super category
			List<CategoryModel> newSuperCategories = new ArrayList<CategoryModel>();
			CategoryModel newSupercategory = this.modelService.create(CategoryModel.class);
			newSupercategory.setCode(categoryData.getParentCategoryId());
			newSupercategory.setName(categoryData.getParentCategoryName());
			PrincipalModel customerGroup = userService.getUserGroupForUID(ApbFacadesConstants.CUSTOMER_GROUP_UID);
			newSupercategory.setAllowedPrincipals(Collections.singletonList(customerGroup));
			newSuperCategories.add(newSupercategory);
			newSupercategory.setCatalogVersion(this.catalogVersionService
					.getCatalogVersion(this.asahiSiteUtil.getCatalogId(categoryData.getCompanyCode()),
							ApbFacadesConstants.STAGED_VERSION));
			
			//saving new super category
			this.modelService.save(newSupercategory);
			return newSuperCategories;
		}
	}

	/**
	 * Creates the new category.
	 *
	 * @param categoryData
	 *           the category data
	 * @param superCategories
	 */
	private void createNewCategory(CategoryData categoryData, List<CategoryModel> superCategories)
	{
		CategoryModel newCategory = this.modelService.create(CategoryModel.class);
		newCategory.setCode(categoryData.getCode());
		newCategory.setName(categoryData.getCategoryName());
		newCategory.setSupercategories(superCategories);
		newCategory.setCompanyCode(categoryData.getCompanyCode());

		newCategory.setCatalogVersion(this.catalogVersionService
				.getCatalogVersion(this.asahiSiteUtil.getCatalogId(categoryData.getCompanyCode()),
						ApbFacadesConstants.STAGED_VERSION));
		
		PrincipalModel customerGroup = this.userService.getUserGroupForUID(ApbFacadesConstants.CUSTOMER_GROUP_UID);
		newCategory.setAllowedPrincipals(Collections.singletonList(customerGroup));
		//saving new category

		this.modelService.save(newCategory);
	}

	/**
	 * Update existing category.
	 *
	 * @param categoryData
	 *           the category data
	 * @param existingCategory
	 *           the existing category
	 * @param superCategories
	 */
	private void updateExistingCategory(CategoryData categoryData, CategoryModel existingCategory,
			List<CategoryModel> superCategories)
	{
		existingCategory.setName(categoryData.getCategoryName());
		existingCategory.setSupercategories(superCategories);
		existingCategory.setCompanyCode(categoryData.getCompanyCode());
		//saving existing category
		this.modelService.save(existingCategory);
	}
}
