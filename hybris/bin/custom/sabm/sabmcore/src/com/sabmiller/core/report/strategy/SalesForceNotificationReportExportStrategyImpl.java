package com.sabmiller.core.report.strategy;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.sabmiller.commons.email.service.SabmSFTPService;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;
import com.sabmiller.core.util.PGPUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.integration.salesforce.SabmCSVFileGenerator;
import com.sabmiller.integration.salesforce.SabmCSVUtils;
import com.sabmiller.integration.salesforce.SabmSftpFileUpload;


/**
 * Created by zhuo.a.jiang on 10/01/2018.
 */
public class SalesForceNotificationReportExportStrategyImpl implements DefaultNotificationReportExportStrategy
{

	private static final Logger LOG = LoggerFactory.getLogger(SalesForceNotificationReportExportStrategyImpl.class);

	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private static final String DATE_PATTERN2 = "yyyyMMddHHmmss";
	private static final String FIND_NOTIFICATION = "select {pk} from {SABMNotification} ";

	private FlexibleSearchService flexibleSearchService;
	private SabmCSVFileGenerator sabmCSVFileGenerator;
	private SabmSftpFileUpload sabmSftpFileUpload;

	private static final String countryCode = "61";

	@Resource(name = "sabmSFTPService")
	private SabmSFTPService sabmSFTPService;

	@Override
	public List<String> getHeaderLine(final List<String> headers)
	{

		headers.add("Email");
		headers.add("Venue");
		headers.add("VenueID");
		headers.add("NotificationType");
		headers.add("NotificationTypeEnabled");
		headers.add("MobileNumber");
		headers.add("OrderCutoff");
		headers.add("OrderCutOffTime");
		headers.add("OrderDispatch");
		headers.add("Deals");
		headers.add("DealPreferredDay");
		headers.add("EmailOptIn");
		headers.add("LastEmailSendDate");
		headers.add("SMSOptin");
		headers.add("SMSLastSendDate");

		return headers;
	}

	@Override
	public List<List<String>> getNotificationReportData()
	{

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_NOTIFICATION);
		final SearchResult<SABMNotificationModel> result = flexibleSearchService.search(fsq);
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		if (CollectionUtils.isEmpty(result.getResult()))
		{
			LOG.info("Nothing to process");
			return null;
		}
		final List<List<String>> units = new ArrayList<List<String>>();
		for (final SABMNotificationModel row : result.getResult())
		{

			if (CollectionUtils.isNotEmpty(row.getNotificationPreferences()))
			{

				for (final SABMNotificationPrefModel pref : row.getNotificationPreferences())
				{
					final List<String> unitData = new ArrayList<>();

					unitData.add(row.getUser().getUid());
					unitData.add(row.getB2bUnit().getName());
					unitData.add(row.getB2bUnit().getUid());
					unitData.add(pref.getNotificationType() != null ? pref.getNotificationType().getCode() : "");
					unitData.add(BooleanUtils.toStringTrueFalse(pref.getNotificationTypeEnabled()));
					unitData.add(String.valueOf(SabmStringUtils.trimToEmpty(String.valueOf(pref.getMobileNumber()))));
					unitData.add(row.getOrderCutOffPrefString());
					unitData.add(row.getOrderCutOffPrefDetailsString());
					unitData.add(row.getOrderDispatchPrefString());
					unitData.add(row.getDealPrefString());
					unitData.add(row.getDealPrefDetailsString());
					unitData.add(BooleanUtils.toStringTrueFalse(pref.getEmailEnabled()));
					unitData.add(pref.getEmailLastSendDate() != null ? sdf.format(pref.getEmailLastSendDate()) : "");
					unitData.add(BooleanUtils.toStringTrueFalse(pref.getSmsEnabled()));
					unitData.add(pref.getSmsLastSendDate() != null ? sdf.format(pref.getSmsLastSendDate()) : "");
					units.add(unitData);
				}

			}

		}
		return units;
	}

	@Override
	public void uploadFileToSFTP() throws IOException, PGPException, NoSuchProviderException, JSchException, SftpException
	{
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN2);
		final List<String> headers = new ArrayList<String>();
		final String fileExt = ".csv";
		final File file = sabmCSVFileGenerator.writeToFile(SabmCSVUtils.getFullPath("notification"),
				sdf.format(new Date()) + "_Notification", fileExt, getNotificationReportData(), getHeaderLine(headers));
		PGPPublicKey key = null;
		final String ecryptedFileName = SabmCSVUtils.getFullPath("notification") + File.separator + sdf.format(new Date())
				+ "_Notification" + ".csv.pgp";
		key = PGPUtils.readPublicKey(Config.getString("salesforce.encryptionkey", ""));
		try (final OutputStream out = new FileOutputStream(ecryptedFileName))
		{
			PGPUtils.encryptFile(out, file, key, false, false);
		}
		catch (final Exception e)
		{
			LOG.error("Exception while encrypting file:", e);
			throw e;
		}
		//sabmSftpFileUpload.upload(new File(ecryptedFileName));
		sabmSFTPService.uploadCSVFile(file, Config.getString("sabm.sftp.salesforce_pardot.remote.directory", ""));
	}

	@Override
	public File generatefile() throws IOException, PGPException, NoSuchProviderException
	{
		final List<String> headers = new ArrayList<String>();
		final String fileExt = ".csv";
		final File file = sabmCSVFileGenerator.writeToFile(SabmCSVUtils.getNotificationFullPath("notification"),
				"NotificationReport", fileExt, getCustomerNotificationReportData(), getNotificationHeaderLine(headers));
		return file;

	}

	@Override
	public List<String> getNotificationHeaderLine(final List<String> headers)
	{



		headers.add("Email");
		headers.add("B2BUnit");
		headers.add("Order Cutoff");
		headers.add("Order CutOffT SMS");
		headers.add("Order CutOffT Time");
		headers.add("Order Dispatch");
		headers.add("Deals");
		headers.add("DealPreferredDay");
		headers.add("In Transit");
		headers.add("Next In Queue");
		headers.add("Update For ETA");
		headers.add("Delivered");
		headers.add("Invoice Discrepancy");
		headers.add("Customer Status");
		headers.add("Contact Number");




		return headers;
	}


	@Override
	public List<List<String>> getCustomerNotificationReportData()
	{


		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_NOTIFICATION);
		final SearchResult<SABMNotificationModel> result = flexibleSearchService.search(fsq);
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		if (CollectionUtils.isEmpty(result.getResult()))
		{
			LOG.info("Nothing to process");
			return null;
		}
		final List<List<String>> infos = new ArrayList<List<String>>();
		for (final SABMNotificationModel row : result.getResult())
		{


			if (CollectionUtils.isNotEmpty(row.getNotificationPreferences()) && row.getUser().getUid() != null)
			{
				final List<String> unitData = new ArrayList<>();
				unitData.add(row.getUser().getUid());
				unitData.add(row.getB2bUnit().getUid());
				unitData.add(row.getOrderCutOffPrefString());
				unitData.add(row.getOrderCutOffPrefSMS());
				unitData.add(row.getOrderCutOffPrefDetailsString());
				unitData.add(row.getOrderDispatchPrefString());
				unitData.add(row.getDealPrefString());
				unitData.add(row.getDealPrefDetailsString());
				unitData.add(BooleanUtils.toStringTrueFalse(row.getIsTypeTransitPref()));
				unitData.add(BooleanUtils.toStringTrueFalse(row.getIsTypeNextInQueuePref()));
				unitData.add(BooleanUtils.toStringTrueFalse(row.getIsTypeUpdateForETAPref()));
				unitData.add(BooleanUtils.toStringTrueFalse(row.getIsTypeDeliveredPref()));
				unitData.add(BooleanUtils.toStringTrueFalse(row.getIsTypeInvoiceDiscrepancyRequestPref()));
				unitData.add(row.getUser().getActive().toString());
				unitData.add(StringUtils.isNotEmpty(row.getUser().getMobileContactNumber()) && !row.getUser().getMobileContactNumber().equals("04")
								? countryCode + StringUtils.substring(StringUtils.deleteWhitespace(row.getUser().getMobileContactNumber()), 1) : "");
				infos.add(unitData);
			}


		}
		return infos;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	public SabmCSVFileGenerator getSabmCSVFileGenerator()
	{
		return sabmCSVFileGenerator;
	}

	public void setSabmCSVFileGenerator(final SabmCSVFileGenerator sabmCSVFileGenerator)
	{
		this.sabmCSVFileGenerator = sabmCSVFileGenerator;
	}

	public SabmSftpFileUpload getSabmSftpFileUpload()
	{
		return sabmSftpFileUpload;
	}

	public void setSabmSftpFileUpload(final SabmSftpFileUpload sabmSftpFileUpload)
	{
		this.sabmSftpFileUpload = sabmSftpFileUpload;
	}
}
