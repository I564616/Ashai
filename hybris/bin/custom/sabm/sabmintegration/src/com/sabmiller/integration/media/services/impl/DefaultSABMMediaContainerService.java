/**
 *
 */
package com.sabmiller.integration.media.services.impl;

import com.sabmiller.integration.media.dao.SABMMediaContainerDao;
import com.sabmiller.integration.media.services.SABMMediaContainerService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.impl.DefaultMediaContainerService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.util.List;



/**
 * The Class DefaultSABMMediaContainerService.
 */
/* @SuppressFBWarnings("NP_NULL_ON_SOME_PATH") */
@SuppressWarnings("NP_NULL_ON_SOME_PATH")
public class DefaultSABMMediaContainerService extends DefaultMediaContainerService implements SABMMediaContainerService
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMMediaContainerService.class);

	/** The sabm media container dao. */
	@Resource
	private SABMMediaContainerDao mediaContainerDao;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.integration.media.services.SABMMediaContainerService#getMediaContainerForQualifier(java.lang.String,
	 * de.hybris.platform.catalog.jalo.CatalogVersion)
	 */
	@Override
	public MediaContainerModel getMediaContainerForQualifier(final String qualifier, final CatalogVersionModel catalog)
			throws UnknownIdentifierException, AmbiguousIdentifierException
	{
		List<MediaContainerModel> containerList = null;

		if (StringUtils.isNotEmpty(qualifier) && catalog != null)
		{
			containerList = mediaContainerDao.findMediaContainersByQualifier(catalog, qualifier);
		}
		else
		{
			LOG.warn("Unable to find MediaContainer with qualifier or catalogVersion null");
		}

		//Throws exception is the result is null/empty or not a single result.
		ServicesUtil.validateIfSingleResult(containerList,
				"No media container with qualifier: " + qualifier + "and catalogversion: " + catalog + " can be found.",
				"More than one media container with qualifier " + qualifier + "and catalogversion: " + catalog + " found.");

		return containerList.get(0);
	}

}
