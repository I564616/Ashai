package com.sabmiller.core.invoices.strategy;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.InvoiceDiscrepancyType;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestItemDetailModel;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import com.sabmiller.core.report.service.WelcomeEmailSaleForceDataExportServiceImpl;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.integration.salesforce.SabmCSVFileGenerator;
import com.sabmiller.integration.salesforce.SabmCSVUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by zhuo.a.jiang on 10/9/18.
 */
public class DefaultSabmCreditAdjustmentSalesTeamReportStrategy implements SabmCreditAdjustmentSalesTeamReportStrategy {

    @Resource(name = "sabmCSVFileGenerator")
    private SabmCSVFileGenerator sabmCSVFileGenerator;

    private static final String DATE_PATTERN1 = "dd/MM/YYYY";
    private static final String DATE_PATTERN2 = "yyyyMMddHHmmss";

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmCreditAdjustmentSalesTeamReportStrategy.class);

    protected List<String> getHeaderLine(final List<String> headers) {
        headers.add("Request ID");
        headers.add("Type");
        headers.add("Customer Name");
        headers.add("RaisedBy");  // the value will be Customer or CUB
        headers.add("State");
        headers.add("Submit Date And Time");
        headers.add("ZALB");
        headers.add("ZALB Name");
        headers.add("Invoice Number");
        headers.add("Invoice Date");
        headers.add("SKU Code");
        headers.add("Product Name");
        headers.add("Quantity");
        headers.add("Description");
        headers.add("Discount Received Per Item");
        headers.add("Discount Required");
        headers.add("Freight Charged");
        headers.add("Freight Expected");

        return headers;
    }

    protected List<List<String>> getCreditAdjustmentSalesTeamEmailData(
            final InvoiceDiscrepancyRequestModel invoiceDiscrepancyRequestModel) {

        final List<List<String>> reportList = new ArrayList<List<String>>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN1);

        if (InvoiceDiscrepancyType.PRICE.equals(invoiceDiscrepancyRequestModel.getType())) {

            for (InvoiceDiscrepancyRequestItemDetailModel model : invoiceDiscrepancyRequestModel.getItems()) {

                List<String> itemList = new ArrayList<>();
                itemList.add(invoiceDiscrepancyRequestModel.getPk().toString());
                itemList.add(invoiceDiscrepancyRequestModel.getType().getCode());
                itemList.add(
                        invoiceDiscrepancyRequestModel.getRaisedBy() != null ? invoiceDiscrepancyRequestModel.getRaisedBy().getName() : StringUtils.EMPTY);

                // the value will be Customer or CUB
                if(invoiceDiscrepancyRequestModel.getRaisedByBDE() != null ){
                    if(invoiceDiscrepancyRequestModel.getRaisedByBDE()){

                        itemList.add("CUB");
                    }
                    else {
                        itemList.add("Customer");
                    }
                }
                else {
                    itemList.add(StringUtils.EMPTY);
                }

                if (!Objects.isNull(invoiceDiscrepancyRequestModel.getB2bUnit()) && !Objects
                        .isNull(invoiceDiscrepancyRequestModel.getB2bUnit().getDefaultShipTo()) && !Objects
                        .isNull(invoiceDiscrepancyRequestModel.getB2bUnit().getDefaultShipTo().getRegion())) {
                    itemList.add(invoiceDiscrepancyRequestModel.getB2bUnit().getDefaultShipTo().getRegion().getIsocode());

                } else {
                    itemList.add(StringUtils.EMPTY);
                }

                itemList.add(simpleDateFormat.format(new Date()));
                itemList.add(invoiceDiscrepancyRequestModel.getB2bUnit() != null ?
                        SabmStringUtils.stripLeadingZeroes(invoiceDiscrepancyRequestModel.getB2bUnit().getUid()) :
                        "");
                itemList.add(
                        invoiceDiscrepancyRequestModel.getB2bUnit() != null ? invoiceDiscrepancyRequestModel.getB2bUnit().getName() : "");
                itemList.add(invoiceDiscrepancyRequestModel.getInvoiceNumber());
                itemList.add(invoiceDiscrepancyRequestModel.getInvoiceDate() != null ?
                        simpleDateFormat.format(invoiceDiscrepancyRequestModel.getInvoiceDate()) :
                        "");
                itemList.add(StringUtils.isNotEmpty(model.getSkuCode()) ? SabmStringUtils.stripLeadingZeroes(model.getSkuCode()) : "");

                itemList.add(model.getItemDescriptionLine1() + " " + model.getItemDescriptionLine2());

                itemList.add(model.getQuantity() != null ? model.getQuantity() : "");
                itemList.add(invoiceDiscrepancyRequestModel.getDescription());
                itemList.add(model.getDiscountReceived() != null ? "$" + String.valueOf(model.getDiscountReceived()) : "");
                itemList.add(model.getDiscountExpected() != null ? "$" + String.valueOf(model.getDiscountExpected()) : "");
                itemList.add("");  //Freight charged
                itemList.add("");  //Freight Expected

                reportList.add(itemList);
            }
        }

        if (InvoiceDiscrepancyType.FREIGHT.equals(invoiceDiscrepancyRequestModel.getType())) {
            List<String> itemList = new ArrayList<>();
            itemList.add(invoiceDiscrepancyRequestModel.getPk().toString());
            itemList.add(invoiceDiscrepancyRequestModel.getType().getCode());
            itemList.add(
                    invoiceDiscrepancyRequestModel.getRaisedBy() != null ? invoiceDiscrepancyRequestModel.getRaisedBy().getName() : "");

            // the value will be Customer or CUB
            if(invoiceDiscrepancyRequestModel.getRaisedByBDE() != null ){
                if(invoiceDiscrepancyRequestModel.getRaisedByBDE()){

                    itemList.add("CUB");
                }
                else {
                    itemList.add("Customer");
                }
            }
            else {
                itemList.add(StringUtils.EMPTY);
            }

            if (!Objects.isNull(invoiceDiscrepancyRequestModel.getB2bUnit()) && !Objects
                    .isNull(invoiceDiscrepancyRequestModel.getB2bUnit().getDefaultShipTo()) && !Objects
                    .isNull(invoiceDiscrepancyRequestModel.getB2bUnit().getDefaultShipTo().getRegion())) {
                itemList.add(invoiceDiscrepancyRequestModel.getB2bUnit().getDefaultShipTo().getRegion().getIsocode());

            } else {
                itemList.add(StringUtils.EMPTY);
            }


            itemList.add(simpleDateFormat.format(new Date()));
            itemList.add(invoiceDiscrepancyRequestModel.getB2bUnit() != null ?
                    SabmStringUtils.stripLeadingZeroes(invoiceDiscrepancyRequestModel.getB2bUnit().getUid()) :
                    "");
            itemList.add(invoiceDiscrepancyRequestModel.getB2bUnit() != null ? invoiceDiscrepancyRequestModel.getB2bUnit().getName() : StringUtils.EMPTY);
            itemList.add(invoiceDiscrepancyRequestModel.getInvoiceNumber());
            itemList.add(invoiceDiscrepancyRequestModel.getInvoiceDate() != null ?
                    simpleDateFormat.format(invoiceDiscrepancyRequestModel.getInvoiceDate()) :
                    "");
            itemList.add(StringUtils.EMPTY);
            itemList.add(StringUtils.EMPTY);
            itemList.add(StringUtils.EMPTY);
            itemList.add(invoiceDiscrepancyRequestModel.getDescription());
            itemList.add(StringUtils.EMPTY);//Discount_Received_Per_Item
            itemList.add(StringUtils.EMPTY);//Discount_Required
            itemList.add(invoiceDiscrepancyRequestModel.getTotalFreightDiscountCharged() != null ?
                    "$" + String.valueOf(invoiceDiscrepancyRequestModel.getTotalFreightDiscountCharged()) :
                    StringUtils.EMPTY);
            itemList.add(invoiceDiscrepancyRequestModel.getTotalFreightDiscountExpected() != null ?
                    "$" + String.valueOf(invoiceDiscrepancyRequestModel.getTotalFreightDiscountExpected()) :
                    StringUtils.EMPTY);
            reportList.add(itemList);
        }

        return reportList;
    }

    protected String getMimeType() {
        return "text/csv";
    }

    public File getEmailData(final InvoiceDiscrepancyRequestModel invoiceDiscrepancyRequestModel) {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN2);

        final List<String> headers = new ArrayList<String>();
        final List<String> headerData = getHeaderLine(headers);

        final List<List<String>> data = getCreditAdjustmentSalesTeamEmailData(invoiceDiscrepancyRequestModel);

        if (data != null) {

            final String fileExt = ".csv";
            final File file = sabmCSVFileGenerator
                    .writeToFile(SabmCSVUtils.getFullPath(SabmCoreConstants.CREDITADJUSTMENT_GENERATED_FILES_HYBRIS_FOLDER_MAIN),
                            sdf.format(new Date()) + "_", fileExt, data, headerData);

            final String ecryptedFileName = SabmCSVUtils.getFullPath(SabmCoreConstants.CREDITADJUSTMENT_GENERATED_FILES_HYBRIS_FOLDER_MAIN) + File.separator
                    + sdf.format(new Date())+"_"
                    + invoiceDiscrepancyRequestModel.getRaisedBy().getName()+"_"
                    + invoiceDiscrepancyRequestModel.getPk().toString()
                    + ".csv";

            final File file2 = new File(ecryptedFileName);

            try {
                FileUtils.moveFile(file, file2);
                return file2;
            } catch (IOException e) {
                LOG.error("Exception while creating file:", e);
            }

        }
        return null;
    }
}
