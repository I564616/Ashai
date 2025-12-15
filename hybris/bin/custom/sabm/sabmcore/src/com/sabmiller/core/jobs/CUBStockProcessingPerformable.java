/**
 *
 */
package com.sabmiller.core.jobs;

import com.sabmiller.integration.salesforce.SabmCSVUtils;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.sabmiller.commons.email.service.SabmSFTPService;
import com.sabmiller.core.b2b.dao.CUBStockInformationDao;
import com.sabmiller.core.b2b.dao.SabmDeliveryPlantDao;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.CUBStockStatus;
import com.sabmiller.core.model.CUBStockInformationModel;
import com.sabmiller.core.model.CUBStockProcessingCronJobModel;
import com.sabmiller.core.model.PlantModel;


/**
 * @author Siddarth
 *
 */
public class CUBStockProcessingPerformable extends AbstractJobPerformable<CUBStockProcessingCronJobModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(CUBStockProcessingPerformable.class);
	private static final char DEFAULT_SEPARATOR = ',';

	private static final String FAILEDMESSAGE = "CUB Stock Processing failed";
	@Resource
	private ModelService modelService;
	@Resource
	private SabmDeliveryPlantDao sabmDeliveryPlantDao;
	@Resource
	private CUBStockInformationDao cubStockInformationDao;
	@Resource
	private MediaService mediaService;
	@Resource
	private SabmSFTPService sabmSFTPService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public PerformResult perform(final CUBStockProcessingCronJobModel cronJob)
	{
		CSVReader csvReader = null;
		boolean SFTPFileExists=false;
		java.io.File SFTPfile=null;
		LOG.info("CUB Stock Processing started");
		try
		{
			//Reading the CSV file
			if (!cronJob.getFallbackRequired())
			{
				SFTPfile=sabmSFTPService.getCSVFile(cronJob.getStockFileName());
				csvReader = new CSVReader(SFTPfile,
						CSVConstants.HYBRIS_ENCODING);
				SFTPFileExists=true;
			}
			else
			{
				csvReader = new CSVReader(mediaService.getStreamFromMedia(cronJob.getStockFile()), CSVConstants.HYBRIS_ENCODING);
			}

			//Delete the current Stock Lines
			final List<CUBStockInformationModel> existingStockLines = cubStockInformationDao.getAllCUBStockLines();
			if (CollectionUtils.isNotEmpty(existingStockLines))
			{
				modelService.removeAll(existingStockLines);
			}

			//Parsing the CSV and storing fresh Stock Feed
			while (csvReader.readNextLine())
			{
				final String currentLine = csvReader.getSourceLine();
				final List<String> splittedValues = Arrays.asList(StringUtils.split(currentLine, DEFAULT_SEPARATOR));
				if (CollectionUtils.isNotEmpty(splittedValues))
				{
					if (csvReader.getCurrentLineNumber() != 1 && !storeContents(splittedValues))
					{
						skipLineException(csvReader.getCurrentLineNumber());
					}
				}
				else
				{
					skipLineException(csvReader.getCurrentLineNumber());
				}
			}
			LOG.info("CUB Stock Processing successful!");
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final JSchException je)
		{
			LOG.error(FAILEDMESSAGE + " due to :", je.getMessage());
		}
		catch (final SftpException se)
		{
			LOG.error(FAILEDMESSAGE + " due to :", se.getMessage());
		}
		catch (final Exception e)
		{
			LOG.error(FAILEDMESSAGE + " due to :", e.getMessage());
		}
		finally
		{
			SabmCSVUtils.closeReaderQuietly(csvReader);
			if(SFTPFileExists){
				FileUtils.deleteQuietly(SFTPfile);
			}
		}
		return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
	}

	private void skipLineException(final int lineNumber)
	{
		LOG.error("Due to Invalid or insufficient information. Skipping line: " + lineNumber);
	}

	private boolean storeContents(final List<String> splittedValues)
	{
		if (splittedValues.size() == 4)
		{
			final String plantID = splittedValues.get(0);
			final String productCode = splittedValues.get(1);
			final String stockStatus = splittedValues.get(3);
			if (StringUtils.isNotEmpty(plantID) && StringUtils.isNotEmpty(productCode) && StringUtils.isNotEmpty(stockStatus))
			{
				final PlantModel plantModel = sabmDeliveryPlantDao.lookupPlant(plantID);
				if (plantModel == null)
				{
					LOG.error("Plant not found with ID: " + plantID);
					return false;
				}
				final CUBStockInformationModel stockInfo = modelService.create(CUBStockInformationModel.class);
				stockInfo.setPlant(plantModel);
				stockInfo.setProductCode(productCode);
				if (stockStatus.equalsIgnoreCase(SabmCoreConstants.OUTOFSTOCK))
				{
					stockInfo.setStockStatus(CUBStockStatus.OUTOFSTOCK);
				}
				else if (stockStatus.equalsIgnoreCase(SabmCoreConstants.LOWSTOCK))
				{
					stockInfo.setStockStatus(CUBStockStatus.LOWSTOCK);
				}
				else
				{
					LOG.error("Invalid Stock Flag: " + stockStatus);
					return false;
				}
				modelService.save(stockInfo);
				return true;
			}
		}
		return false;
	}


}
