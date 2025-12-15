package com.sabmiller.core.report.strategy;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.sabmiller.commons.email.service.SabmSFTPService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.B2BUnitGroupModel;
import com.sabmiller.core.model.PlantDeliveryDayModel;
import com.sabmiller.core.model.SalesOrgDataModel;
import com.sabmiller.core.model.UnloadingPointModel;
import com.sabmiller.core.util.PGPUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.integration.salesforce.SabmCSVFileGenerator;
import com.sabmiller.integration.salesforce.SabmCSVUtils;
import com.sabmiller.integration.salesforce.SabmSftpFileUpload;

/**
 * Created by zhuo.a.jiang on 15/12/2017.
 */
public class SalesForceVenueReportExportStrategyImpl implements DefaultVenueReportExportStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(SalesForceVenueReportExportStrategyImpl.class);

    private static final String FIND_ACTIVE_B2B_UNIT = "SELECT {" + B2BUnitModel.PK + "}" + "FROM {" + B2BUnitModel._TYPECODE
            + "!} WHERE {" + B2BUnitModel.ACTIVE + "}=1";


    private FlexibleSearchService flexibleSearchService;

    private SabmB2BUnitService b2bUnitService;
    private SabmCSVFileGenerator sabmCSVFileGenerator;
    private SabmSftpFileUpload sabmSftpFileUpload;

    @Resource(name = "sabmSFTPService")
 	private SabmSFTPService sabmSFTPService;

	/** The caldendar delivery day map. */
	@Resource(name = "caldendarDeliveryDayMap")
	private Map<Integer, Integer> caldendarDeliveryDayMap;

	private final DateFormat dataDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private static final String DATE_PATTERN = "yyyyMMddHHmmss";

    @Override
    public List<String> getHeaderLine(final List<String> headers) {
        headers.add("Uid");
        headers.add("Name");
        headers.add("AccountGroup");


		headers.add("Users");
		headers.add("PlantId");
		headers.add("DefaultCarrierId");
		headers.add("DefaultCarrierName");

		headers.add("EffectiveDeliveryDaysPack");
		headers.add("EffectiveDeliveryDaysBulk");
		headers.add("IsPaymentRequired");

		headers.add("SAPGroupKey");
		headers.add("SAPGroupDescription");
		headers.add("SAPPrimaryGroupKey");
		headers.add("SAPPrimaryGroupDescription");
		headers.add("SAPSubGroupKey");
		headers.add("SAPSubGroupDescription");
		headers.add("SAPSubChannelKey");
		headers.add("SAPSubChannelDescription");


		headers.add("CustomerGroup");
		headers.add("CustomerStatisticGroup");
		headers.add("CustomerGroupDescription");
		headers.add("PriceGroup");
		headers.add("SalesDistrictCode");
		headers.add("SalesGroup");
		headers.add("SalesOfficeCode");
		headers.add("ShippingCondition");

        headers.add("Status");
        headers.add("OrderingStatus");
        headers.add("ShippingAddressStreetName");
        headers.add("ShippingAddressStreetNo");
        headers.add("ShippingAddressTown");
        headers.add("ShippingAddressState");
        headers.add("ShippingAddressPostCode");
        headers.add("ContactAddressStreetName");
        headers.add("ContactAddressStreetNo");
        headers.add("ContactAddressTown");
        headers.add("ContactAddressState");
        headers.add("ContactAddressPostCode");
        headers.add("RegistrationDate");
        headers.add("AutoPayIndicator");

        return headers;
    }

    @Override
    public List<List<String>> getVenueReportData() {
        final FlexibleSearchQuery fsq = new FlexibleSearchQuery(FIND_ACTIVE_B2B_UNIT);
        final SearchResult<B2BUnitModel> result = flexibleSearchService.search(fsq);


		  if (CollectionUtils.isEmpty(result.getResult()))
        {
            LOG.info("Nothing to process");
            return null;
        }

        final List<List<String>> units = new ArrayList<List<String>>();
		  for (final B2BUnitModel row : result.getResult())
        {
            final List<String> unitData = new ArrayList<>();
            unitData.add(row.getUid());
            unitData.add(row.getName());
            unitData.add(row.getAccountGroup());

			String allUsersString = "";
			final List<B2BCustomerModel> allUsers = b2bUnitService.getZADPUsersByB2BUnit(row);


			if (CollectionUtils.isNotEmpty(allUsers))
			{
				allUsersString = allUsers.stream().map(user -> String.valueOf(user.getPk())).collect(Collectors.joining(", "));
            }

			unitData.add(allUsersString);

			unitData.add(row.getPlant() != null ? row.getPlant().getPlantId() : "");

			unitData.add(row.getDefaultCarrier() != null ? row.getDefaultCarrier().getCarrierCode() : "");

			unitData.add(row.getDefaultCarrier() != null
					? SabmStringUtils.trimToEmpty(row.getDefaultCarrier().getCarrierDescription()) : "");


			unitData.add(StringUtils.join(getEffectiveDeliveryDays("PACK", row), ","));
			unitData.add(StringUtils.join(getEffectiveDeliveryDays("BULK", row), ","));

			unitData.add(String.valueOf(isCashOnlyCustomer(row)));
			final B2BUnitGroupModel sapGroup = row.getSapGroup();
			if (sapGroup != null)
			{
				unitData.add(SabmStringUtils.trimToEmpty(sapGroup.getGroupKey()));
				unitData.add(SabmStringUtils.trimToEmpty(sapGroup.getGroupDescription()));

				unitData.add(SabmStringUtils.trimToEmpty(sapGroup.getPrimaryGroupKey()));
				unitData.add(SabmStringUtils.trimToEmpty(sapGroup.getPrimaryGroupDescription()));

				unitData.add(SabmStringUtils.trimToEmpty(sapGroup.getSubGroupKey()));
				unitData.add(SabmStringUtils.trimToEmpty(sapGroup.getSubGroupDescription()));

				unitData.add(SabmStringUtils.trimToEmpty(sapGroup.getSubChannel()));
				unitData.add(SabmStringUtils.trimToEmpty(sapGroup.getSubChannelDescription()));
			}
			else
			{
				unitData.add("");
				unitData.add("");
				unitData.add("");
				unitData.add("");
				unitData.add("");
				unitData.add("");
				unitData.add("");
				unitData.add("");
			}
			final SalesOrgDataModel salesOrgData = row.getSalesOrgData();
			if (salesOrgData != null)
			{
				unitData.add(SabmStringUtils.trimToEmpty(salesOrgData.getCustomerGroup()));
				unitData.add(SabmStringUtils.trimToEmpty(salesOrgData.getCustomerStatisticGroup()));
				unitData.add(SabmStringUtils.trimToEmpty(salesOrgData.getGroupDescription()));
				unitData.add(SabmStringUtils.trimToEmpty(salesOrgData.getPriceGroup()));
				unitData.add(SabmStringUtils.trimToEmpty(salesOrgData.getSalesDistrictCode()));
				unitData.add(SabmStringUtils.trimToEmpty(salesOrgData.getSalesGroup()));
				unitData.add(SabmStringUtils.trimToEmpty(salesOrgData.getSalesOfficeCode()));
				unitData.add(SabmStringUtils.trimToEmpty(salesOrgData.getShippingCondition()));
			}
			else
			{
				unitData.add("");
				unitData.add("");
				unitData.add("");
				unitData.add("");
				unitData.add("");
				unitData.add("");
				unitData.add("");
				unitData.add("");
			}

            String b2bUnitStatus = "";
            if (null != row.getB2BUnitStatus())
            {
                b2bUnitStatus = row.getB2BUnitStatus().getCode().toLowerCase();
            }
            unitData.add(b2bUnitStatus);
            unitData.add(orderingStatus(row));

            final AddressModel shippingAddress = getAddress(row, "shipping");
            SabmCSVUtils.addAddress(unitData, shippingAddress);
            final AddressModel contactAddress = getAddress(row, "contact");
            SabmCSVUtils.addAddress(unitData, contactAddress);

            // addAddress(unitData, contactAddress);
            unitData.add(dataDateFormat.format(row.getCreationtime()));

            final String sapSalesVolume = SabmCoreConstants.AUTOPAY_STATUS_SAP_SALES_VOLUME_MAP.get(row.getAutoPayStatus());
            unitData.add(StringUtils.isNotEmpty(sapSalesVolume) ? sapSalesVolume : SabmCoreConstants.SAP_SALES_VOLUME_NONMEMBER);

            units.add(unitData);
        }

        return units;
    }


    @Override
    public void uploadFileToSFTP()  throws IOException, PGPException, NoSuchProviderException, JSchException, SftpException {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        final List<String> headers = new ArrayList<String>();
        final String fileExt = ".csv";
		final File file = sabmCSVFileGenerator.writeToFile(SabmCSVUtils.getFullPath("venue"),
				sdf.format(new Date()) + "_HybrisCustomers",
                fileExt, getVenueReportData(), getHeaderLine(headers));
		final String ecryptedFileName = SabmCSVUtils.getFullPath("venue") + File.separator + sdf.format(new Date())
				+ "_HybrisCustomers" + ".csv.pgp";
        PGPPublicKey key = null;
        key = PGPUtils.readPublicKey(Config.getString("salesforce.encryptionkey", ""));
	    try (final OutputStream out = new FileOutputStream(ecryptedFileName)) {
		    PGPUtils.encryptFile(out, file, key, false, false);
	    } catch (final Exception e) {
		    LOG.error("Exception while encrypting file:", e);
		    throw e;
	    }
		//sabmSftpFileUpload.upload(new File(ecryptedFileName));
		sabmSFTPService.uploadCSVFile(file, Config.getString("sabm.sftp.salesforce_pardot.remote.directory", ""));
    }


	private Boolean isCashOnlyCustomer(final B2BUnitModel b2bUnit)
	{

		if (b2bUnit != null && b2bUnit.getPayerId() != null)
		{
			final B2BUnitModel parentB2BUnitModel = b2bUnitService.findTopLevelB2BUnit(b2bUnit.getPayerId());

			if (parentB2BUnitModel != null && parentB2BUnitModel.getPaymentRequired())
			{
				return Boolean.TRUE;
			}

		}
		return Boolean.FALSE;
	}

	private Set<String> getEffectiveDeliveryDays(final String packType, final B2BUnitModel b2bUnit)
	{

		final Set<String> deliveryDaysOfTheWeek = new HashSet<>();
		if (b2bUnit.getPlant() != null)
		{
		final List<PlantDeliveryDayModel> plantDeliveryDates = b2bUnit.getPlant().getDeliveryDays();

   	for (final UnloadingPointModel unloadingPoint : ListUtils.emptyIfNull(b2bUnit.getUnloadingPoints()))
		{
				if (unloadingPoint != null &&  StringUtils.contains(unloadingPoint.getCode(),packType))
				{
					final Set<String> dayOfTheWeekSet = unloadingPoint.getMap().keySet();

					for(final String dayOfTheWeekNo: dayOfTheWeekSet)
					{

						final PlantDeliveryDayModel dayOfTheWeekModel = plantDeliveryDates.stream()
								.filter((p) -> caldendarDeliveryDayMap.get(Integer.parseInt(dayOfTheWeekNo)).equals(p.getDayOfWeek()))
								.findFirst().orElse(null);

						if (dayOfTheWeekModel != null)
						{
					deliveryDaysOfTheWeek.add(dayOfTheWeekModel.getName());
						}
					}
				}
		}
		}
		return deliveryDaysOfTheWeek;
   }

    private String orderingStatus(final B2BUnitModel b2bunit)
    {

        final DateTime toDay = new DateTime();
        final DateTime ordersAfterDate = toDay.minusWeeks(13);

        return b2bUnitService.getOrderingStatus(b2bunit, ordersAfterDate.toDate());
    }


    private AddressModel getAddress(final B2BUnitModel row, final String type)
    {
        for (final AddressModel address : CollectionUtils.emptyIfNull(row.getAddresses()))
        {
            if (StringUtils.equals("shipping", type) && address.getShippingAddress())
            {
                return address;
            }
            if (StringUtils.equals("contact", type) && address.getContactAddress())
            {
                return address;
            }
        }
        return null;
    }



    public SabmSftpFileUpload getSabmSftpFileUpload() {
        return sabmSftpFileUpload;
    }
    public void setSabmSftpFileUpload(final SabmSftpFileUpload sabmSftpFileUpload) {
        this.sabmSftpFileUpload = sabmSftpFileUpload;
    }

    public SabmCSVFileGenerator getSabmCSVFileGenerator() {
        return sabmCSVFileGenerator;
    }

    public void setSabmCSVFileGenerator(final SabmCSVFileGenerator sabmCSVFileGenerator) {
        this.sabmCSVFileGenerator = sabmCSVFileGenerator;
    }

    public SabmB2BUnitService getB2bUnitService() {
        return b2bUnitService;
    }

    public void setB2bUnitService(final SabmB2BUnitService b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
