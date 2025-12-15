package com.sabmiller.core.report.strategy;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.sabmiller.commons.email.service.SabmSFTPService;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.order.SabmB2BOrderService;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.util.PGPUtils;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.integration.salesforce.SabmCSVFileGenerator;
import com.sabmiller.integration.salesforce.SabmCSVUtils;
import com.sabmiller.integration.salesforce.SabmSftpFileUpload;

/**
 * Created by zhuo.a.jiang on 10/01/2018.
 */
public class SalesForceOrderReportExportStrategyImpl implements  DefaultOrderReportExportStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SalesForceOrderReportExportStrategyImpl.class);


	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private static final String DATE_PATTERN2 = "yyyyMMddHHmmss";
	private static final String FIND_ORDER = "select {pk} from {Order as o} where {o.site} NOT IN ({{select {pk} from {CMSSite} where {uid} IN('sga','apb')}}) ORDER BY {"
			+ OrderModel.CREATIONTIME + "} ASC ";

	private static final String FIND_ORDER_AFTER_GIVEN_DATE = "SELECT {" + OrderModel.PK + "} " + "FROM {" + OrderModel._TYPECODE
			+ "} WHERE {" + OrderModel.SITE + "} NOT IN ({{select {pk} from {CMSSite} where {uid} IN('sga','apb')}}) AND  {"
			+ OrderModel.MODIFIEDTIME + "} >= ?date ORDER BY {" + OrderModel.MODIFIEDTIME + "} ASC";


    private FlexibleSearchService flexibleSearchService;
    private SabmB2BOrderService b2bOrderService;
    private SabmCSVFileGenerator sabmCSVFileGenerator;
    private SabmSftpFileUpload sabmSftpFileUpload;
	private TimeService timeService;

	@Resource(name = "sabmSFTPService")
	 private SabmSFTPService sabmSFTPService;

	/** The product service. */
	@Resource(name = "productService")
	private SabmProductService productService;


    @Override
    public List<String> getHeaderLine(final List<String> headers) {

        headers.add("OrderCode");
        headers.add("SAPSalesOrderNumber");
        headers.add("UserID");
        headers.add("Venue");
        headers.add("DispatchNotifEmailSent");
        headers.add("SalesChannel");
        headers.add("RequestedDeliveryDate");
        headers.add("Currency");
        headers.add("OrderPlacedDate");
        headers.add("SKU");
        headers.add("EAN");
        headers.add("ProductName");
		headers.add("ProductHierarchy");
		headers.add("ProductBrand");
		headers.add("ProductSubBrand");


        headers.add("Quantity");
        headers.add("UnitType");
        headers.add("Price");
        headers.add("TotalDeliveryCost");
        headers.add("Discount");
        headers.add("UnitPrice");
        headers.add("OrderStatus");
        headers.add("StatusInfo");

        headers.add("DeliveryAddressStreetName");
        headers.add("DeliveryAddressStreetNumber");
        headers.add("DeliveryAddressTown");
        headers.add("DeliveryAddressState");
        headers.add("DeliveryAddressPostCode");

        headers.add("DeliveryMode");
        headers.add("ShippingCarrier");
        headers.add("OneOffDealApplied");

        return headers;
    }

    @Override
	public List<List<String>> getOrderReportData(final Date date)
	{

         FlexibleSearchQuery fsq =null;
         if(date!=null){
			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("date", date);
			fsq = new FlexibleSearchQuery(FIND_ORDER_AFTER_GIVEN_DATE, params);
		}
         else {
         	fsq = new FlexibleSearchQuery(FIND_ORDER);
         }
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
           for(final AbstractOrderEntryModel entry: row.getEntries()){

         	  final List<String> orderData = new ArrayList<>();
            orderData.add(row.getCode());
            orderData.add(row.getSapSalesOrderNumber());
				orderData.add(row.getUser() != null && row.getUser().getUid() != null ? row.getUser().getUid() : "");
			orderData.add(row.getUnit() != null && row.getUnit().getUid() != null ? row.getUnit().getUid() : "");
			orderData.add(
					row.getDispatchNotifEmailSent() != null ? BooleanUtils.toStringTrueFalse(row.getDispatchNotifEmailSent()) : "");
			orderData.add(row.getSalesApplication() != null && row.getSalesApplication().getCode() != null
					? row.getSalesApplication().getCode() : "");
            orderData.add(row.getRequestedDeliveryDate() != null ? sdf.format(row.getRequestedDeliveryDate()) : "");
			orderData.add(row.getCurrency() != null && row.getCurrency().getIsocode() != null ? row.getCurrency().getIsocode() : "");
            orderData.add(row.getCreationtime() != null ? sdf.format(row.getCreationtime()) : "");


            orderData.add(entry.getProduct() !=null ? SabmStringUtils.trimToEmpty(entry.getProduct().getCode()) : "");

             final SABMAlcoholVariantProductEANModel ean = productService.getEanFromMaterial(entry.getProduct().getCode());
				orderData.add(ean != null ? SabmStringUtils.trimToEmpty(ean.getCode()) : "");

				orderData.add(entry.getProduct() != null ? SabmStringUtils.trimToEmpty(getProductTitle(ean)) : "");
				orderData.add(
						entry.getProduct() != null && entry.getProduct() instanceof SABMAlcoholVariantProductMaterialModel
								? ((SABMAlcoholVariantProductMaterialModel) entry.getProduct()).getHierarchy() : "");
				if (ean != null && ean.getBaseProduct() != null && ean.getBaseProduct() instanceof SABMAlcoholProductModel)
				{
					final SABMAlcoholProductModel alcoholProduct = ((SABMAlcoholProductModel) ean.getBaseProduct());
					orderData.add(alcoholProduct.getBrand());
					orderData.add(alcoholProduct.getSubBrand());
				}
				else
				{
					orderData.add("");
					orderData.add("");
				}
				orderData.add(String.valueOf(entry.getQuantity()));
				orderData.add(entry.getUnit() != null ? String.valueOf(entry.getUnit().getCode()) : "");
				orderData.add(String.valueOf(entry.getTotalPrice()));



				orderData.add(String.valueOf(String.valueOf(entry.getDeliveryCost())));
				orderData.add(String.valueOf(entry.getTotalEntryDiscount()));

				orderData.add(String.valueOf(entry.getBasePrice()));

            orderData.add(row.getStatus() != null ? row.getStatus().getCode() : "");
			orderData.add(row.getStatusInfo() != null ? row.getStatusInfo() : "");

            SabmCSVUtils.addAddress(orderData, row.getDeliveryAddress());
            orderData.add(row.getDeliveryMode() != null ? row.getDeliveryMode().getCode() : "");
            orderData.add(row.getDeliveryShippingCarrier()!= null ? row.getDeliveryShippingCarrier().getCarrierCode() : "");
			orderData.add(row.getOneOffDealApplied() != null ? BooleanUtils.toStringTrueFalse(row.getOneOffDealApplied()) : "");

				ordersList.add(orderData);
           }
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


    @Override
	public void uploadFileToSFTP(Integer batchSize, final Integer deltaHours)
			throws IOException, PGPException, NoSuchProviderException, JSchException, SftpException
	{
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN2);
		final List<String> headers = new ArrayList<String>();
		final List<String> headerData = getHeaderLine(headers);
		Date fromDate=null;
		if (deltaHours != null && deltaHours > 0)
		{

			fromDate = new DateTime(timeService.getCurrentTime()).minusHours(deltaHours).toDate();
		}
		final List<List<String>> reportData = getOrderReportData(fromDate);
		if (reportData != null)
		{
		int fileCounter = 1;
		if (batchSize == null)
		{
			batchSize = reportData.size();
		}
		for (int counter = 0; counter < reportData.size(); counter = counter + batchSize)
		{

        final String fileExt = ".csv";
			final File file = sabmCSVFileGenerator.writeToFile(SabmCSVUtils.getFullPath("transaction"),
					sdf.format(new Date()) + "_Transaction_" + fileCounter, fileExt,
					reportData.subList(counter, reportData.size() < counter + batchSize ? reportData.size() : counter + batchSize),
					headerData);
			PGPPublicKey key = null;
			final String ecryptedFileName = SabmCSVUtils.getFullPath("transaction") + File.separator + sdf.format(new Date())
					+ "_Transaction_" + fileCounter + ".csv.pgp";
			fileCounter++;
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
		}
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

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

	/**
	 * @return the timeService
	 */
	public TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}
}
