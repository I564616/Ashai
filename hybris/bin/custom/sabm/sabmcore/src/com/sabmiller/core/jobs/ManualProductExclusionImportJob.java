/**
 *
 */
package com.sabmiller.core.jobs;

import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.ImpExMedia;
import de.hybris.platform.impex.jalo.cronjob.ImpExImportCronJob;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SABMManualImportProductExclusionService;


/**
 * @author Biswaranjan Sahu
 *
 */
/**
 * @FetchCustomerCUPJob : Fetch the prices form SAP for last week active user and update in Hybris.
 *
 */
public class ManualProductExclusionImportJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(ManualProductExclusionImportJob.class);
	//private static boolean executed = false;

	@Resource(name = "sabmMManualImportProductExclusionService")
	private SABMManualImportProductExclusionService sabmMManualImportProductExclusionService;

	@Resource(name = "setupImpexService")
	private SetupImpexService setupImpexService;

	final String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

	@Resource(name = "sabmCronJobStatus")
	private SabmCronJobStatus sabmCronJobStatus;

	/**
	 * @return the sabmCronJobStatus
	 */
	public SabmCronJobStatus getSabmCronJobStatus()
	{
		return sabmCronJobStatus;
	}


	/**
	 * @param sabmCronJobStatus
	 *           the sabmCronJobStatus to set
	 */
	public void setSabmCronJobStatus(final SabmCronJobStatus sabmCronJobStatus)
	{
		this.sabmCronJobStatus = sabmCronJobStatus;
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */


	@Override
	public PerformResult perform(final CronJobModel cronJob)

	{
		LOG.info("Inside perform method of Job Name : FetchCustomerCUPJob");
		boolean status = true;
		final int maxThreads = Config.getInt("core.import.productexclusion.maxthreads", 20);
		//cronJob.setMaxThreads(maxThreads);
		if (clearAbortRequestedIfNeeded(cronJob))
		{
			status = false;
			LOG.debug("The job is aborted.");
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}


		try
		{
			File directory = new File("./");
			directory = new File(directory.getAbsolutePath());
			while (directory.getParent() != null)
			{
				if (directory.getPath().endsWith("hybris"))
				{
					break;
				}
				directory = new File(directory.getParent());
			}
			final String soureRawFilePath = Config.getString("core.import.productexclusion.sourcepath",
					"/bin/custom/sabm/sabmcore/resources/sabmcore/import/PE");
			final File soureRawFilefolder = new File(directory.getPath() + soureRawFilePath);
			if (soureRawFilefolder.exists())
			{
				sabmMManualImportProductExclusionService
						.generateImpexFilesForProductExclusion(directory.getPath() + soureRawFilePath);
				LOG.info("Completed Generating All Impex files");
			}

			final String relativeImpexFilesPath = Config.getString("core.import.productexclusion.destinationpath", "/impexfiles");
			final String impexFilesPath = soureRawFilePath + relativeImpexFilesPath;

			final File folder = new File(directory.getPath() + impexFilesPath);
			//System.out.println("folder==>" + folder.getPath());
			if (folder.exists())
			{
				final File[] listOfFiles = folder.listFiles();

				for (int i = 0; i < listOfFiles.length; i++)
				{
					final File file = listOfFiles[i];
					if (file.isFile() && (file.getName().endsWith(".impex") || file.getName().endsWith(".IMPEX")))
					{
						LOG.info("Starting Import File : " + file.getName());
						//importImpexFile(context, "/sabmcore/import/PE" + relativeImpexFilesPath + "/" + file.getName());

						try
						{
							// Creating import media
							final ImpExMedia jobMedia = ImpExManager.getInstance().createImpExMedia(file.getName(), "UTF-8");

							jobMedia.setFieldSeparator(';');
							jobMedia.setQuoteCharacter('\"');
							final InputStream inputstream = new FileInputStream(
									directory.getPath() + impexFilesPath + "/" + file.getName());

							jobMedia.setData(inputstream, file.getName(), "text/impex");
							// create cronjob
							final ImpExImportCronJob dynamicCronJob = ImpExManager.getInstance().createDefaultImpExImportCronJob();
							dynamicCronJob.setEnableCodeExecution(true);
							dynamicCronJob.setJobMedia(jobMedia);
							dynamicCronJob.setMaxThreads(maxThreads);

							ImpExManager.getInstance().importData(dynamicCronJob, true, true);

						}
						catch (final UnsupportedEncodingException e)
						{
							LOG.error("Given encoding is not supported", e);
						}
						//setupImpexService.importImpexFile(impexFilesPath + "/" + file.getName(), true);
						LOG.info("Finished Import File : " + file.getName());
						if (file.delete())
						{
							LOG.info("Deleted IMPEX File : " + file.getName());
						}
						else
						{
							LOG.info("Problem In Deleting IMPEX File : " + file.getName());
						}
					}
				}
				if (folder.listFiles().length <= 0)
				{
					if (folder.delete())
					{
						LOG.info("Deleted generated impex folder : " + folder.getName());
						LOG.info("######### FINISHED ############");
					}
					else
					{
						LOG.info("Problem in Deleting generated impex folder : " + folder.getName());
					}
				}
			}
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			status = false;
			LOG.info("Problem in importing Product Exclusion.");
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}
		finally
		{

			if (!status)
			{
				sabmCronJobStatus.sendJobStatusNotification(cronJob.getCode(), "Aborted", timeStamp);
			}
		}
	}

	@Override
	public boolean isAbortable()
	{
		return true;
	}


}
