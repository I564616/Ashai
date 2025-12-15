package com.sabmiller.core.jobs;

import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.exp.Export;
import de.hybris.platform.impex.jalo.exp.ExportConfiguration;
import de.hybris.platform.impex.jalo.exp.HeaderLibrary;
import de.hybris.platform.impex.jalo.exp.ImpExExportMedia;
import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;
import com.sabmiller.core.model.SABMEmailExportCronJobModel;


/**
 * The Class ExportCustomerJob.
 */
public class ExportCustomerJob extends AbstractJobPerformable<SABMEmailExportCronJobModel>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ExportCustomerJob.class);

	/** The sabm imp ex media dao. */
	@Resource(name = "sabmImpExMediaDao")
	private GenericDao<ImpExMediaModel> sabmImpExMediaDao;

	/** The email service. */
	@Resource(name = "emailService")
	private SystemEmailService emailService;

	/** The catalog version service. */
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	/** The model service. */
	@Resource
	private ModelService modelService;

	/** The single file. */
	@Value(value = "${customer.export.single.file:false}")
	private Boolean singleFile;

	/** The single file. */
	@Value(value = "${customer.export.field.separator:,}")
	private String fieldseparator;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public PerformResult perform(final SABMEmailExportCronJobModel cronJob)
	{
		CronJobResult result = CronJobResult.ERROR;

		final Map<String, Object> params = new HashMap<>();
		params.put("code", Config.getString("customer.export.media.code", "Customer_export"));

		//Getting the already existing HeaderLibrary with the script containing the attributes to export.
		final List<ImpExMediaModel> impexMediaList = sabmImpExMediaDao.find(params);

		LOG.debug("Retrieved ImpExMedia: {}", impexMediaList);

		if (CollectionUtils.isNotEmpty(impexMediaList))
		{
			final HeaderLibrary impexMedia = modelService.getSource(impexMediaList.get(0));
			try
			{
				final ExportConfiguration config = new ExportConfiguration(impexMedia, ImpExManager.getExportOnlyMode());
				config.setFieldSeparator(fieldseparator);
				config.setSingleFile(BooleanUtils.isTrue(singleFile));
				final Export export = ImpExManager.getInstance().exportDataLight(config);

				final ImpExExportMedia exportedData = export.getExportedData();

				LOG.debug("Exported data [{}] by job [{}]", exportedData, cronJob);

				//Setting catalog in session for emailAttachment
				catalogVersionService.setSessionCatalogVersion(
						Config.getString("email.attachment.default.catalog", "sabmContentCatalog"),
						Config.getString("email.attachment.default.catalog.version", "Staged"));

				//Creating attachment for the email with generated report file.
				final EmailAttachmentModel emailAttachment = emailService.createEmailAttachment(
						new DataInputStream(new FileInputStream(exportedData.getFile())), getAttachmentFileName(), getMimeType());

				final SystemEmailMessageModel systemEmail = emailService.constructSystemEmail(cronJob.getEmailFrom(),
						cronJob.getEmailTo(), cronJob.getEmailTo(), cronJob.getSubject(), Collections.singletonList(cronJob.getBody()),
						Collections.singletonList(emailAttachment));

				//If the email is correctly generated than it will be sent by the systemEmailCronJob.
				if (systemEmail != null)
				{
					result = CronJobResult.SUCCESS;
				}
			}
			catch (final ImpExException | FileNotFoundException | JaloSystemException e)
			{
				LOG.error("Exception performing customer export " + e, e);
			}
		}
		else
		{
			LOG.error("Missing ImpExMediaModel [{}]! It's required for customer export report.",
					Config.getString("customer.export.media.code", "Customer_export"));
		}

		LOG.info("ExportCustomerJob performed with status");

		return new PerformResult(result, CronJobStatus.FINISHED);
	}

	/**
	 * Gets the attachment file name. It contains a timestamp to avoid duplicate names.
	 *
	 * @return the attachment file name. If the singleFile flag is true, it will be a CSV file, else a ZIP one.
	 */
	protected String getAttachmentFileName()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(Config.getString("customer.export.media.code", "customers_report"));
		sb.append("_");
		sb.append(new Date().getTime());
		sb.append(BooleanUtils.isTrue(singleFile) ? ".csv" : ".zip");

		return sb.toString();
	}

	/**
	 * Gets the mime type checking the singleFile flag.
	 *
	 * @return the mime type. If the singleFile flag is true, it will be text/csv, else a application/zip.
	 */
	protected String getMimeType()
	{
		return BooleanUtils.isTrue(singleFile) ? "text/csv" : "application/zip";
	}

}