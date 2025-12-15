package com.sabmiller.core.report.strategy;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.b2b.model.B2BUnitModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;

import java.util.Set;

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
import com.sabmiller.core.model.AsahiB2BUnitModel;



/**
 * Author: Himanshu.Kumar
 * Created as part of HybrisSVOC Project
 */
public class SalesForceUserToVenueDataExportStrategyImpl implements DefaultUserToVenueDataExportStrategy
{

	private static final Logger LOG = LoggerFactory.getLogger(SalesForceUserToVenueDataExportStrategyImpl.class);

	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private static final String DATE_PATTERN2 = "yyyyMMddHHmmss";
	private static final String FIND_CUSTOMERS = "SELECT {pk} FROM {B2bCustomer AS b} WHERE {b:modifiedtime} >= ?cutoff and {b:uid} NOT LIKE 'bde-%' ";
	private static final String SABM_STORE_ID = "sabmStore";
	private static final String ALB_STORE_ID = "sga";
	private static final String CUB_BUSINESSUNIT = "CUB";
	private static final String ALB_BUSINESSUNIT = "ALB";

	private FlexibleSearchService flexibleSearchService;
	private SabmSftpFileUpload sabmSftpFileUpload;
	private SabmCSVFileGenerator sabmCSVFileGenerator;

	@Resource(name = "sabmSFTPService")
	private SabmSFTPService sabmSFTPService;

	@Override
	public List<String> getHeaderLine(final List<String> headers)
	{
		headers.add("Email");
		headers.add("HybrisPk");
		headers.add("VenueID");
		headers.add("Venue");
		headers.add("BusinessUnit");
		headers.add("DisabledUser");
		headers.add("LastLoggedInVenue(Default)");
		return headers;
	}

	@Override
	public List<List<String>> getUserToVenueReportData()
	{

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_CUSTOMERS);
		Date cutoff = Date.from(ZonedDateTime.now(ZoneId.systemDefault()).minusDays(1).toInstant());
		query.addQueryParameter("cutoff", cutoff);

		final SearchResult<B2BCustomerModel> result = flexibleSearchService.search(query);
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		if (CollectionUtils.isEmpty(result.getResult()))
		{
			LOG.info("No Customers to process");
			return Collections.emptyList();
		}

		final List<List<String>> units = new ArrayList<List<String>>();
		for (final B2BCustomerModel row : result.getResult())
		{
			Set<PrincipalGroupModel> groups = row.getGroups();

			if (CollectionUtils.isEmpty(groups)) {
				LOG.info("No B2BUnits attached to customer - ", row.getUid());
				continue;
			}

			for (PrincipalGroupModel group : groups)
			{
				if (group instanceof B2BUnitModel b2bUnit)
				{
					List<String> unitData = new ArrayList<>();
					unitData.add(row.getUid());
					unitData.add(row.getPk().toString());
					unitData.add(b2bUnit.getUid());
					unitData.add(b2bUnit.getName());

					if (SABM_STORE_ID.equalsIgnoreCase(b2bUnit.getCompanyUid())) {
						unitData.add(CUB_BUSINESSUNIT);
						unitData.add(String.valueOf(isUserDisabledForVenue(row, b2bUnit)));
						unitData.add(String.valueOf(isDefaultVenue(row, b2bUnit)));
					} else if (b2bUnit instanceof AsahiB2BUnitModel asahib2bUnit && ALB_STORE_ID.equalsIgnoreCase(asahib2bUnit.getCompanyCode())) {
						unitData.add(ALB_BUSINESSUNIT);
						unitData.add(String.valueOf(isUserDisabledForALBVenue(row, asahib2bUnit)));
						unitData.add(String.valueOf(isDefaultVenue(row, asahib2bUnit)));
					}
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

		List<List<String>> data = getUserToVenueReportData();
		if (CollectionUtils.isEmpty(data)) {
			LOG.info("Skipping UserToVenue export — no data to write");
			return;
		}

		final String fileExt = ".csv";
		final File file = sabmCSVFileGenerator.writeToFile(SabmCSVUtils.getFullPath("userToVenueData"),
				sdf.format(new Date()) + "_UserToVenueData", fileExt, data, getHeaderLine(headers));
		PGPPublicKey key = null;
		final String ecryptedFileName = SabmCSVUtils.getFullPath("userToVenueData") + File.separator + sdf.format(new Date())
				+ "_UserToVenueData" + ".csv.pgp";
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
		sabmSFTPService.uploadCSVFile(file, Config.getString("sabm.sftp.salesforce_pardot.remote.directory", ""));
	}


	private boolean isUserDisabledForVenue(B2BCustomerModel customer, B2BUnitModel b2bUnit) {
		Collection<String> cubDisabled = b2bUnit.getCubDisabledUsers();
		return CollectionUtils.isNotEmpty(cubDisabled) && cubDisabled.contains(customer.getUid());
	}

	private boolean isUserDisabledForALBVenue(B2BCustomerModel customer, AsahiB2BUnitModel b2bUnit) {
		return isUserDisabledForVenue(customer, b2bUnit)
				|| (CollectionUtils.isNotEmpty(b2bUnit.getDisabledUser())
				&& b2bUnit.getDisabledUser().contains(customer.getUid()));
	}

	private boolean isDefaultVenue(B2BCustomerModel customer, B2BUnitModel b2bUnit) {
		return b2bUnit.equals(customer.getDefaultB2BUnit());
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
