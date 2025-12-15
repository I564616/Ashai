/**
 *
 */
package com.sabmiller.integration.media.services.impl;

import com.sabmiller.integration.media.services.ConversionGroupService;
import de.hybris.platform.mediaconversion.model.ConversionGroupModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The Class DefaultConversionGroupService.
 */
/* @SuppressFBWarnings("NP_NULL_ON_SOME_PATH") */
@SuppressWarnings("NP_NULL_ON_SOME_PATH")
public class DefaultConversionGroupService implements ConversionGroupService
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultConversionGroupService.class);

	/** The conversion group dao. */
	@Resource
	GenericDao<ConversionGroupModel> conversionGroupDao;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.media.services.ConversionGroupService#getConversionGroupByCode(java.lang.String)
	 */
	@Override
	public ConversionGroupModel getConversionGroupByCode(final String code)
	{
		List<ConversionGroupModel> conversionGroupList = null;
		if (StringUtils.isNotEmpty(code))
		{
			final Map<String, Object> paramMap = new HashMap<>();

			paramMap.put("code", code);

			conversionGroupList = conversionGroupDao.find(paramMap);
		}
		else
		{
			LOG.warn("Unable to find ConversionGroup with code null");
		}

		//Throws exception is the result is null/empty or not a single result.
		ServicesUtil.validateIfSingleResult(conversionGroupList, "No conversion group with code: " + code + " can be found.",
				"More than one conversion group with code " + code + " found.");

		return conversionGroupList.get(0);
	}
}
