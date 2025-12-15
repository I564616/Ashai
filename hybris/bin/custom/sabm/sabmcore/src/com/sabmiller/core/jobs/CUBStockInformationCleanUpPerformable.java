/**
 *
 */
package com.sabmiller.core.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.dao.CUBStockInformationDao;
import com.sabmiller.core.b2b.dao.SabmDeliveryPlantDao;
import com.sabmiller.core.model.CUBStockInformationModel;


/**
 * @author Siddarth
 *
 */
public class CUBStockInformationCleanUpPerformable extends AbstractJobPerformable<CronJobModel>
{

	@Resource
	private ModelService modelService;
	@Resource
	private SabmDeliveryPlantDao sabmDeliveryPlantDao;
	@Resource
	private CUBStockInformationDao cubStockInformationDao;

	private static final Logger LOG = LoggerFactory.getLogger(CUBStockInformationCleanUpPerformable.class);

	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{
		try
		{
			//Delete the current Stock Lines
			final List<CUBStockInformationModel> existingStockLines = cubStockInformationDao.getAllCUBStockLines();
			if (CollectionUtils.isNotEmpty(existingStockLines))
			{
				LOG.info("Deleting existing CUB Stock lines");
				LOG.debug("Deleting Number of CUB Stock lines" + existingStockLines.size());
				modelService.removeAll(existingStockLines);
			}
		}
		catch (final Exception e)
		{
			LOG.error("Error deleting existing CUB Stock lines" + e);
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

}
