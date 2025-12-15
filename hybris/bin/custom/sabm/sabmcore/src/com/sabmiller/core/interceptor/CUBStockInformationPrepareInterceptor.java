/**
 *
 */
package com.sabmiller.core.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.CUBStockStatus;
import com.sabmiller.core.model.CUBStockInformationModel;


/**
 * @author Siddarth
 *
 */
public class CUBStockInformationPrepareInterceptor implements PrepareInterceptor<CUBStockInformationModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(CUBStockInformationPrepareInterceptor.class);

	@Override
	public void onPrepare(final CUBStockInformationModel model, final InterceptorContext context) throws InterceptorException
	{
		if (StringUtils.isNotEmpty(model.getStockString()))
		{
			if (model.getStockString().equalsIgnoreCase(SabmCoreConstants.OUTOFSTOCK))
			{
				LOG.debug("Setting Stock status as:" + CUBStockStatus.OUTOFSTOCK);
				model.setStockStatus(CUBStockStatus.OUTOFSTOCK);
			}
			else if (model.getStockString().equalsIgnoreCase(SabmCoreConstants.LOWSTOCK))
			{
				LOG.debug("Setting Stock status as:" + CUBStockStatus.LOWSTOCK);
				model.setStockStatus(CUBStockStatus.LOWSTOCK);
			}
		}
	}

}