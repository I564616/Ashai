package com.sabmiller.core.jobs;

import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMBDEEmailExportCronJobModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.integration.salesforce.SabmCSVFileGenerator;
import com.sabmiller.integration.salesforce.SabmCSVUtils;


/**
 * The Class ExportCustomerJob.
 */
public class ExportBDEOrdersJob extends AbstractJobPerformable<SABMBDEEmailExportCronJobModel>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ExportBDEOrdersJob.class);


	@Resource(name = "sabmCSVFileGenerator")
	private SabmCSVFileGenerator sabmCSVFileGenerator;

	/** The email service. */
	@Resource(name = "emailService")
	private SystemEmailService emailService;

	/** The catalog version service. */
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	/** The model service. */
	@Resource
	private ModelService modelService;

	@Resource(name = "productService")
	private SabmProductService productService;


	/** The single file. */
	@Value(value = "${customer.export.single.file:false}")
	private Boolean singleFile;

	@Resource
	private BaseStoreService baseStoreService;


	private SABMBDEEmailExportCronJobModel sabmBDEEmailExportCronJobModel = null;

	private static final String DATE_PATTERN2 = "yyyyMMddHHmmss";

	private static final String FIND_ORDER_AFTER_GIVEN_DATE = "SELECT {" + OrderModel.PK + "} " + "FROM {" + OrderModel._TYPECODE
			+ "} WHERE  {" + OrderModel.CREATIONTIME + "} >= ?date AND {" + OrderModel.STORE + "}=?store AND {" + OrderModel.BDEORDER
			+ "} =?bdeOrder ORDER BY {"
			+ OrderModel.CREATIONTIME + "} DESC";
	private static final String DATE_PATTERN = "dd/MM/yyyy";

	private static final String CUB_STORE = "sabmStore";

	/*cd
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public PerformResult perform(final SABMBDEEmailExportCronJobModel cronJob)
	{
		CronJobResult result = CronJobResult.ERROR;

		sabmBDEEmailExportCronJobModel = cronJob;

		LOG.debug("BDE Ordering Exported data by job [{}]", cronJob);

				//Setting catalog in session for emailAttachment
				catalogVersionService.setSessionCatalogVersion(
						Config.getString("email.attachment.default.catalog", "sabmContentCatalog"),
						Config.getString("email.attachment.default.catalog.version", "Staged"));

		FileInputStream fis = null;
		try
		{
			SabmCSVUtils.purgeOldFiles(SabmCSVUtils.getFullPath("bdeorders").getPath());

			final File reportData = getReportData();
			if (reportData != null)
			{
				fis = new FileInputStream(reportData);
				//Creating attachment for the email with generated report file.
				final EmailAttachmentModel emailAttachment = emailService.createEmailAttachment(
					new DataInputStream(fis), getAttachmentFileName(), getMimeType());

				final List<String> toemailIds = sabmBDEEmailExportCronJobModel.getEmailTo();

				final SystemEmailMessageModel systemEmail = emailService.constructSystemEmailForDailyMonitoringSheet(
                  sabmBDEEmailExportCronJobModel.getEmailFrom(),toemailIds, sabmBDEEmailExportCronJobModel.getEmailFrom(), sabmBDEEmailExportCronJobModel.getSubject(),
                  Collections.singletonList(sabmBDEEmailExportCronJobModel.getBody()),
                  Collections.singletonList(emailAttachment));

				//If the email is correctly generated than it will be sent by the systemEmailCronJob.
				if (systemEmail != null)
				{
					result = CronJobResult.SUCCESS;
				}

			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}

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
		sb.append(Config.getString("bde.order.export.report", "BDE Orders"));
		sb.append("_");
		sb.append(new Date().getTime());
		sb.append(".csv");

		return sb.toString();
	}

	/**
	 * Gets the mime type checking the singleFile flag.
	 *
	 * @return the mime type. If the singleFile flag is true, it will be text/csv, else a application/zip.
	 */
	protected String getMimeType()
	{
		return "text/csv";
	}


	private List<String> getHeaderLine(final List<String> headers)
	{

		headers.add("SAP Sales Order Number");
		headers.add("Venue ID");
		headers.add("Venue Name");
		headers.add("Order Placed Date");
		headers.add("Total Price");
		headers.add("Requested Delivery Date");
		headers.add("Placed By");
		headers.add("BDE Email Ids");
		headers.add("BDE Customer Email Ids");
		headers.add("BDE Additional Text");
		headers.add("Order Items");

		return headers;
	}

	public List<List<String>> getOrderReportData()
	{

		final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid(CUB_STORE);
		final Date date = DateUtils.addDays(new Date(), -7);
			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("date", date);
		params.put("bdeOrder", true);
		params.put("store", baseStore);

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_ORDER_AFTER_GIVEN_DATE, params);

		final SearchResult<OrderModel> result = flexibleSearchService.search(fsq);
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

		if (CollectionUtils.isEmpty(result.getResult()))
		{
			LOG.info("Nothing to process");
			return null;
		}

		final List<List<String>> ordersList = new ArrayList<List<String>>();
		for (final OrderModel row : result.getResult())
		{

			final List<String> orderData = new ArrayList<>();

				orderData.add(row.getSapSalesOrderNumber());
				orderData.add(row.getUnit() != null && row.getUnit().getUid() != null ? row.getUnit().getUid() : "");
			orderData.add(row.getUnit() != null && row.getUnit().getName() != null ? row.getUnit().getName() : "");
				orderData.add(row.getCreationtime() != null ? sdf.format(row.getCreationtime()) : "");
			orderData.add(String.valueOf(row.getTotalPrice()));

			orderData.add(row.getRequestedDeliveryDate() != null ? sdf.format(row.getRequestedDeliveryDate()) : "");

			orderData.add(row.getPlacedBy() != null ? row.getPlacedBy().getUid() : "");
			orderData.add(row.getBdeOrderUserEmails() != null ? String.join(",", row.getBdeOrderUserEmails()) : "");
			orderData.add(row.getBdeOrderCustomerEmails() != null ? String.join(",", row.getBdeOrderCustomerEmails()) : "");
			orderData.add(row.getBdeOrderEmailText() != null ? row.getBdeOrderEmailText().replaceAll("\\r\\n|\\r|\\n", " ") : "");

			final StringBuffer entries = new StringBuffer("");

			for (final AbstractOrderEntryModel entry : row.getEntries())
			{

				if (entry.getProduct() != null)
				{
				final SABMAlcoholVariantProductEANModel ean = productService.getEanFromMaterial(entry.getProduct().getCode());
					entries.append(ean != null ? SabmStringUtils.trimToEmpty(ean.getCode()) : "").append("-");

					entries.append(entry.getProduct() != null ? SabmStringUtils.trimToEmpty(getProductTitle(ean)) : "").append("-");
					entries.append(String.valueOf(entry.getQuantity())).append("-");
					entries.append(entry.getUnit() != null ? String.valueOf(entry.getUnit().getCode()) : "");
					entries.append(" | ");
				}

			}
			orderData.add(entries.toString());

				ordersList.add(orderData);
			}

		return ordersList;
	}


	private String getProductTitle(final SABMAlcoholVariantProductEANModel product)
	{
		if (product == null)
		{
			return "";
		}
		if (StringUtils.isNotEmpty(product.getSellingName()) && StringUtils.isNotEmpty(product.getPackConfiguration()))
		{
			return product.getSellingName() + " " + product.getPackConfiguration();
		}
		return product.getName();
	}

	protected File getReportData()
	{
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN2);


		final List<String> headers = new ArrayList<String>();
		final List<String> headerData = getHeaderLine(headers);

		final List<List<String>> reportData = getOrderReportData();

		if (reportData != null)
		{

			final String fileExt = ".csv";
			final File file = sabmCSVFileGenerator.writeToFile(SabmCSVUtils.getFullPath("bdeorders"),
					sdf.format(new Date()) + "_", fileExt, reportData, headerData);
			return file;
		}
		return null;
	}

}