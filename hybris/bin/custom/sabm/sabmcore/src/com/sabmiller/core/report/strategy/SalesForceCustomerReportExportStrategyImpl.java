package com.sabmiller.core.report.strategy;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMNotificationModel;
import com.sabmiller.core.model.SABMNotificationPrefModel;
import com.sabmiller.core.order.SabmB2BOrderService;
import com.sabmiller.core.util.PGPUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.core.util.SabmUtils;
import com.sabmiller.integration.salesforce.SabmCSVFileGenerator;
import com.sabmiller.integration.salesforce.SabmCSVUtils;
import com.sabmiller.integration.salesforce.SabmSftpFileUpload;

/**
 * Created by zhuo.a.jiang on 14/12/2017.
 */
public class SalesForceCustomerReportExportStrategyImpl implements DefaultCustomerReportExportStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SalesForceCustomerReportExportStrategyImpl.class);

	/*
	 * private static final String FIND_ACTIVE_B2B_CUSTOMER = "SELECT {" + B2BCustomerModel.PK + "}" + "FROM {" +
	 * B2BCustomerModel._TYPECODE + "} WHERE {" + B2BCustomerModel.ACTIVE + "}=1";
	 */

    private static final String FIND_B2B_CUSTOMER = "SELECT {" + B2BCustomerModel.PK + "}" + "FROM {"
			 + B2BCustomerModel._TYPECODE + "} WHERE {" + B2BCustomerModel.UID + "} NOT LIKE 'bde-%' ";

    private static final String GET_NOTIFICATIONS = "SELECT {" + SABMNotificationModel.PK + "}" + "FROM {"
          + SABMNotificationModel._TYPECODE + "}";


    private final DateFormat dataDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final String DATE_PATTERN = "yyyyMMddHHmmss";
	private static final String countryCode = "61";
	private static final String DELETED_CUSTOMER_GROUP = "deletedcustomergroup";

	@Resource(name = "sabmSFTPService")
	private SabmSFTPService sabmSFTPService;

    private FlexibleSearchService flexibleSearchService;
    private SabmB2BOrderService b2bOrderService;
    private SabmCSVFileGenerator sabmCSVFileGenerator;
    private SabmSftpFileUpload sabmSftpFileUpload;
	private SabmB2BUnitService b2bUnitService;


    @Override
    public List<String> getHeaderLine(final List<String> headers) {
        headers.add("Pk");
        headers.add("Email");
		headers.add("MobileNumber");
        headers.add("FirstName");
        headers.add("LastName");
        headers.add("DefaultB2BUnit");
		headers.add("ZADP");

		headers.add("welcomeEmailSentDate");
		headers.add("IsPasswordSet");

        headers.add("LastLoginDate");
        headers.add("FirstOnlineOrderDate");
        headers.add("LastOrderDate");
		headers.add("RegistrationDate");

        headers.add("UserRole");
        headers.add("Venues");
        headers.add("ReceiveUpdates");
        headers.add("ReceiveUpdatesForSms");
        headers.add("OrderLimit");
		headers.add("PrimaryAdmin");

		headers.add("TMDInTransitEmailOptIn");
		headers.add("TMDInTransitSmsOptIn");
		headers.add("TMDNextDeliveryEmailOptIn");
		headers.add("TMDNextDeliverySmsOptIn");
		headers.add("TMDETAExceedEmailOptIn");
		headers.add("TMDETAExceedSmsOptIn");
		headers.add("TMDOrderDeliveredEmailOptIn");
		headers.add("TMDOrderDeliveredSmsOptIn");

		headers.add("CUBOnlineStatus");


        return headers;

    }

    @Override
    public List<List<String>> getCustomerReportData() {


        final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_B2B_CUSTOMER);
        final SearchResult<B2BCustomerModel> result = flexibleSearchService.search(fsq);

        final FlexibleSearchQuery fsqForNotification = new FlexibleSearchQuery(GET_NOTIFICATIONS);
        final SearchResult<SABMNotificationModel> resultForNotification = flexibleSearchService.search(fsqForNotification);

        final List<SABMNotificationModel> sabmNotifications = CollectionUtils.isEmpty(resultForNotification.getResult()) ? Collections.emptyList() : resultForNotification.getResult() ;

        if (CollectionUtils.isEmpty(result.getResult()))
        {
            LOG.info("Nothing to process");
            return null;
        }

        final List<List<String>> units = new ArrayList<List<String>>();
        for (final B2BCustomerModel row : result.getResult())
        {
      	  if (null != row.getDefaultB2BUnit() && StringUtils.isNotBlank(row.getDefaultB2BUnit().getCompanyUid())
      			  && row.getDefaultB2BUnit().getCompanyUid().equalsIgnoreCase(SabmCoreConstants.CUB_STORE)
      			  || null != fetchCUBB2BUnit(row)) {
               final List<String> unitData = new ArrayList<>();
               unitData.add(row.getPk() + "");
               unitData.add(row.getUid());
   			unitData.add(
   					StringUtils.isNotEmpty(row.getMobileContactNumber()) && !row.getMobileContactNumber().equals("04")
   							? countryCode + StringUtils.substring(StringUtils.deleteWhitespace(row.getMobileContactNumber()), 1)
   							: "");
   			unitData.add(SabmStringUtils.trimToEmpty(row.getFirstName()));
   			unitData.add(SabmStringUtils.trimToEmpty(row.getLastName()));
   			B2BUnitModel defaultb2bUnit = null;
   			if (null != row.getDefaultB2BUnit() && StringUtils.isNotBlank(row.getDefaultB2BUnit().getCompanyUid())
   					&& row.getDefaultB2BUnit().getCompanyUid().equalsIgnoreCase(SabmCoreConstants.CUB_STORE)) {
   				unitData.add(row.getDefaultB2BUnit().getUid());
   				defaultb2bUnit = row.getDefaultB2BUnit();
   			} else {
   				final PrincipalGroupModel b2bUnit  = fetchCUBB2BUnit(row);
   				if (null != b2bUnit) {
   					unitData.add(b2bUnit.getUid());
   					if (b2bUnit instanceof B2BUnitModel) {
   						defaultb2bUnit = (B2BUnitModel) b2bUnit;
   					}
   				} else {
   					unitData.add("");
   				}
   			}

   			unitData.add(getTopLevelB2BUnit(row, defaultb2bUnit));
   			unitData.add(getWelcomeEmailSentDate(row));
               unitData.add(BooleanUtils.toStringTrueFalse(StringUtils.isNotBlank(row.getEncodedPassword())));
               unitData.add(getLastLoginDate(row));
               unitData.add(getFirstOnlineOrder(row));
               unitData.add(getLastOnlineOrder(row));
   			unitData.add(dataDateFormat.format(row.getCreationtime()));

               unitData.add(getUserGroups(row));
               unitData.add(getCustomerB2BUnits(row));
               unitData.add(BooleanUtils.isTrue(row.getReceiveUpdates()) ? "TRUE" : "FALSE");
               unitData.add(BooleanUtils.isTrue(row.getReceiveUpdatesForSms()) ? "TRUE" : "FALSE");
               unitData.add(row.getOrderLimit() != null ? String.valueOf(row.getOrderLimit()) : "");
   			unitData.add(BooleanUtils.isTrue(row.getPrimaryAdmin()) ? "TRUE" : "FALSE");

   			boolean TMDInTransitEmailOptIn = false;
   			boolean TMDInTransitSmsOptIn = false;
   			boolean TMDNextDeliveryEmailOptIn = false;
   			boolean TMDNextDeliverySmsOptIn = false;
   			boolean TMDETAExceedEmailOptIn = false;
   			boolean TMDETAExceedSmsOptIn = false;
   			boolean TMDOrderDeliveredEmailOptIn = false;
   			boolean TMDOrderDeliveredSmsOptIn = false;

   			for(final SABMNotificationModel sabmNotification : sabmNotifications) {
   				if(row.getUid().equals(sabmNotification.getUser().getUid())) {
   					for(final SABMNotificationPrefModel sabmNotificationPrefModel : sabmNotification.getNotificationPreferences()) {

   						switch(sabmNotificationPrefModel.getNotificationType()) {
   							case INTRANSIT:
   								TMDInTransitEmailOptIn = sabmNotificationPrefModel.getEmailEnabled();
   								TMDInTransitSmsOptIn = sabmNotificationPrefModel.getSmsEnabled();
   							break;
   							case NEXT_IN_QUEUE:
   								TMDNextDeliveryEmailOptIn = sabmNotificationPrefModel.getEmailEnabled();
   								TMDNextDeliverySmsOptIn = sabmNotificationPrefModel.getSmsEnabled();
   							break;
   							case UPDATE_FOR_ETA:
   								TMDETAExceedEmailOptIn = sabmNotificationPrefModel.getEmailEnabled();
   								TMDETAExceedSmsOptIn = sabmNotificationPrefModel.getSmsEnabled();
   							break;
   							case DELIVERED:
   								TMDOrderDeliveredEmailOptIn = sabmNotificationPrefModel.getEmailEnabled();
   								TMDOrderDeliveredSmsOptIn = sabmNotificationPrefModel.getSmsEnabled();
   							break;
   							default:
   								break;
   						}

   					}
   				}
   			}

   			unitData.add(BooleanUtils.isTrue(TMDInTransitEmailOptIn) ? "TRUE" : "FALSE");
   			unitData.add(BooleanUtils.isTrue(TMDInTransitSmsOptIn) ? "TRUE" : "FALSE");
   			unitData.add(BooleanUtils.isTrue(TMDNextDeliveryEmailOptIn) ? "TRUE" : "FALSE");
   			unitData.add(BooleanUtils.isTrue(TMDNextDeliverySmsOptIn) ? "TRUE" : "FALSE");
   			unitData.add(BooleanUtils.isTrue(TMDETAExceedEmailOptIn) ? "TRUE" : "FALSE");
   			unitData.add(BooleanUtils.isTrue(TMDETAExceedSmsOptIn) ? "TRUE" : "FALSE");
   			unitData.add(BooleanUtils.isTrue(TMDOrderDeliveredEmailOptIn) ? "TRUE" : "FALSE");
   			unitData.add(BooleanUtils.isTrue(TMDOrderDeliveredSmsOptIn) ? "TRUE" : "FALSE");

   			unitData.add(BooleanUtils.isTrue(SabmUtils.isCustomerActiveForCUB(row)) ? "TRUE" : "FALSE");

   			units.add(unitData);
      	  }
        }
      	  return units;

    }

	/**
	 * @param row
	 * @return
	 */
	private PrincipalGroupModel fetchCUBB2BUnit(final B2BCustomerModel row)
	{
		final Optional<PrincipalGroupModel> cubUnit = row.getGroups().stream().filter(unit -> unit instanceof B2BUnitModel
				&& StringUtils.isNotBlank(((B2BUnitModel) unit).getCompanyUid())
				&& ((B2BUnitModel) unit).getCompanyUid().equalsIgnoreCase(SabmCoreConstants.CUB_STORE)
				&& ((unit.getUid().equalsIgnoreCase(DELETED_CUSTOMER_GROUP))
						|| (StringUtils.isNotBlank(((B2BUnitModel) unit).getAccountGroup())
								&& ((B2BUnitModel) unit).getAccountGroup().equalsIgnoreCase(SabmCoreConstants.ZALB))))
				.findFirst();
		if (cubUnit.isPresent()) {
			return cubUnit.get();
		}
		return null;
	}

	/**
	 * @param row
	 * @return
	 */
	private String getTopLevelB2BUnit(final B2BCustomerModel row, final B2BUnitModel unit)
	{
		String topLevelb2bUnitId = "";
		try
		{
			if (unit != null && null != unit.getPayerId())
			{
					final B2BUnitModel zadp = b2bUnitService.findTopLevelB2BUnit(unit.getPayerId());
					if (zadp != null)
					{
						topLevelb2bUnitId = zadp.getUid();
					}
				}
		}
		catch (final Exception e)
		{
			LOG.info("no ZADP found while generating salesforce export data", e);
		}
		return topLevelb2bUnitId;
	}

    private String getWelcomeEmailSentDate(final B2BCustomerModel customer)
    {
        String welcomeEmailSentDate = "";
        if (null != customer.getWelcomeEmailSentDate())
        {
            welcomeEmailSentDate = dataDateFormat.format(customer.getWelcomeEmailSentDate());
        }
        return welcomeEmailSentDate;
    }

    private String getLastLoginDate(final B2BCustomerModel customer)
    {

        String returnDate = "";
        if (null != customer.getLastLogin())
        {
            returnDate = dataDateFormat.format(customer.getLastLogin());
        }
        return returnDate;
    }

    private String getFirstOnlineOrder(final B2BCustomerModel customer)
    {
        Date last = new Date(0L);
        final OrderModel lastOrder = b2bOrderService.getFirstOnlineOrderByCustomer(customer);
        if (lastOrder != null)
        {
            last = lastOrder.getDate();
        }

        if (!last.equals(new Date(0L)))
        {
            return dataDateFormat.format(last);
        }
        else
        {
            return StringUtils.EMPTY;
        }
    }

    private String getLastOnlineOrder(final B2BCustomerModel customer)
    {
        Date last = new Date(0L);
        final OrderModel lastOrder = b2bOrderService.getLastOrderByCustomer(customer);
        if (lastOrder != null)
        {
            last = lastOrder.getDate();
        }

        if (!last.equals(new Date(0L)))
        {
            return dataDateFormat.format(last);
        }
        else
        {
            return StringUtils.EMPTY;
        }
    }
    private String getUserGroups(final B2BCustomerModel customer)
    {
        String result = StringUtils.EMPTY;

        //Checking if everything is ok to perform the attribute translation
        if (CollectionUtils.isNotEmpty(customer.getGroups()))
        {
            final List<String> userGroupList = new ArrayList<>();

            for (final PrincipalGroupModel group : customer.getGroups())
            {
                //Filtering only the real UserGroup (removing the B2BUnits)
                if (group.getClass().equals(UserGroupModel.class))
                {
                    userGroupList.add(group.getUid());
                }
            }

            result = StringUtils.join(userGroupList, ",");
        }

        LOG.debug("Result translation for item [{}] is [{}]", customer, result);

        return result;
    }

    private String getCustomerB2BUnits(final B2BCustomerModel customer)
    {
        String result = StringUtils.EMPTY;

        //Checking if everything is ok to perform the attribute translation
        if (CollectionUtils.isNotEmpty(customer.getGroups()))
        {
            final List<String> b2bUnitsList = new ArrayList<>();
            for (final PrincipalGroupModel group : customer.getGroups())
            {
                //Filtering only the real B2BUnits (removing the UserGroup)
                if (group.getClass().equals(B2BUnitModel.class))
                {
                    final B2BUnitModel businessUnit = (B2BUnitModel) group;
                    //Filtering only ZALB B2BUnits (removing the ZADP)
                    if ("ZALB".equals(businessUnit.getAccountGroup()))
                    {
                        b2bUnitsList.add(group.getUid());
                    }
                }
            }
            result = StringUtils.join(b2bUnitsList, ";");
        }

        return result;
    }


    @Override
    public void uploadFileToSFTP() throws IOException, PGPException, NoSuchProviderException, JSchException, SftpException {

        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        final List<String> headers = new ArrayList<String>();
        final String fileExt = ".csv";
        final File file = sabmCSVFileGenerator.writeToFile(SabmCSVUtils.getFullPath("customers"), sdf.format(new Date()) + "_HybrisUsers",
                fileExt, getCustomerReportData(), getHeaderLine(headers));
        PGPPublicKey key = null;
        final String encryptedFileName =
                SabmCSVUtils.getFullPath("customers") + File.separator + sdf.format(new Date()) + "_HybrisUsers" + ".csv.pgp";
        key = PGPUtils.readPublicKey(Config.getString("salesforce.encryptionkey", ""));
        try (final OutputStream out = new FileOutputStream(encryptedFileName)) {
            PGPUtils.encryptFile(out, file, key, false, false);
        } catch (final Exception e) {
            LOG.error("Exception while encrypting file:", e);
            throw e;
        }

        //sabmSftpFileUpload.upload(new File(encryptedFileName));
        sabmSFTPService.uploadCSVFile(file, Config.getString("sabm.sftp.salesforce_pardot.remote.directory", ""));
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public SabmB2BOrderService getB2bOrderService() {
        return b2bOrderService;
    }

    public void setB2bOrderService(final SabmB2BOrderService b2bOrderService) {
        this.b2bOrderService = b2bOrderService;
    }

    public SabmCSVFileGenerator getSabmCSVFileGenerator() {
        return sabmCSVFileGenerator;
    }

    public void setSabmCSVFileGenerator(final SabmCSVFileGenerator sabmCSVFileGenerator) {
        this.sabmCSVFileGenerator = sabmCSVFileGenerator;
    }

    public SabmSftpFileUpload getSabmSftpFileUpload() {
        return sabmSftpFileUpload;
    }

    public void setSabmSftpFileUpload(final SabmSftpFileUpload sabmSftpFileUpload) {
        this.sabmSftpFileUpload = sabmSftpFileUpload;
    }

	/**
	 * @return the b2bUnitService
	 */
	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}
}
