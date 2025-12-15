/**
 *
 */
package com.sabmiller.core.interceptor;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.dao.SabmB2BUnitDao;
import com.sabmiller.core.model.StagingSABMRecommendationModel;
import com.sabmiller.core.recommendation.service.RecommendationService;


/**
 * @author Siddarth
 *
 */
public class StagingSABMRecommendationPrepareInterceptor implements PrepareInterceptor<StagingSABMRecommendationModel>
{

	@Resource
	private RecommendationService recommendationService;

	final static String MANDATORYATTRIBUTEEXCEPTION = "Mandatory Atributes Missing";
	final static String IMPORTEXCEPTION = "Bulk Upload failed";
	final static String INVALIDB2BUNITID = "B2bUnit not found for ID";
	protected boolean isBannerInfoAvailable = false;
	private static final Logger LOG = LoggerFactory.getLogger(StagingSABMRecommendationPrepareInterceptor.class);
	@Resource
	private SabmB2BUnitDao b2bUnitDao;


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.interceptor.PrepareInterceptor#onPrepare(java.lang.Object,
	 * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
	 */
	@Override
	public void onPrepare(final StagingSABMRecommendationModel model, final InterceptorContext ctx) throws InterceptorException
	{
		boolean isBannerInfoAvailable = false;
		List<String> errorList = new ArrayList<String>();
		errorList = doValidation(model, errorList);
		if (!errorList.isEmpty())
		{
			throwException(MANDATORYATTRIBUTEEXCEPTION, errorList.toString());
		}

		if (model.getB2bUnitID() == null
				&& (model.getPrimaryBanner() != null || model.getSubBanner() != null || model.getPriceGroup() != null))
		{
			isBannerInfoAvailable = true;
		}
		else
		{
			final B2BUnitModel b2bUnit = b2bUnitDao.findB2BUnitbyUID(model.getB2bUnitID());
			if (b2bUnit != null)
			{
				model.setB2bUnit(b2bUnitDao.findB2BUnitbyUID(model.getB2bUnitID()));
			}
			else
			{
				throwException(INVALIDB2BUNITID, model.getB2bUnitID());
			}
		}
		try
		{
			recommendationService.updateRecommendationsForBulkUpload(model, isBannerInfoAvailable);
		}
		catch (final Exception e)
		{
			throw new InterceptorException(e.getMessage());
		}

	}

	protected List<String> doValidation(final StagingSABMRecommendationModel model, final List<String> errorList)
	{
		if (model.getRecommendedBy() == null)
		{
			errorList.add("Attribute RECOMMENDEDBY is NULL");
		}
		switch (model.getRecommendationType())
		{
			case PRODUCT:
				if (model.getProductCode() == null)
				{
					errorList.add("Attribute PRODUCTCODE is NULL");
				}
				if (model.getQty() == null)
				{
					errorList.add("Attribute QTY is NULL");
				}
				if (model.getProductUOM() == null)
				{
					errorList.add("Attribute PRODUCTUOM is NULL");
				}
				break;

			case DEAL:
				if (model.getDealCode() == null)
				{
					errorList.add("Attribute DEALCODE is NULL");
				}
				if (model.getDealProducts() == null)
				{
					errorList.add("Attribute DEALPRODUCTS is NULL");
				}
				break;
		}

		if (model.getB2bUnitID() == null)
		{

			if (model.getPrimaryBanner() != null || model.getSubBanner() != null || model.getPriceGroup() != null)
			{
				isBannerInfoAvailable = true;
			}
			else
			{
				errorList.add("Attribute B2BUNIT/PRIMARYBANNER/SUBBANNER/PRICEGROUP is NULL");
			}
		}
		return errorList;
	}

	private void throwException(final String exceptionTypemessage, final String exceptionDescription) throws InterceptorException
	{
		throw new InterceptorException(exceptionTypemessage + "::" + (exceptionDescription != null ? exceptionDescription : ""));
	}

}
