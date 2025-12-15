package com.sabmiller.core.jobs;

import com.sabmiller.core.order.SabmB2BOrderService;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.webservice.model.TrackOrderImportRecordModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
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
import de.hybris.platform.util.Config;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.sabmiller.commons.email.service.SystemEmailService;
import com.sabmiller.commons.model.SystemEmailMessageModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMEmailExportCronJobModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.util.SabmStringUtils;
import com.sabmiller.integration.salesforce.SabmCSVFileGenerator;
import com.sabmiller.integration.salesforce.SabmCSVUtils;


/**
 * The Class ExportCustomerJob.
 */
public class SabmTrackOrderExportJob extends AbstractJobPerformable<SABMEmailExportCronJobModel>
{

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SabmTrackOrderExportJob.class);

    @Resource(name = "b2bOrderService")
    private SabmB2BOrderService b2bOrderService;


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


    private static final String DATE_PATTERN2 = "yyyyMMddHHmmss";

    private static final String TRACK_ORDERS = "SELECT {" + TrackOrderImportRecordModel.PK + "} " + "FROM {" + TrackOrderImportRecordModel._TYPECODE
            +"} WHERE {" + TrackOrderImportRecordModel.CREATIONTIME + "} >= ?date  ORDER BY {" +TrackOrderImportRecordModel.CREATIONTIME + "} DESC";
    private static final String DATE_PATTERN = "dd/MM/yyyy";

    private static final String MESSSAGETYPE_ETA = "ETA";
    private static final String MESSSAGETYPE_ARRIVED = "Arrived";
    private static final String MESSSAGETYPE_DELIVERED = "Delivered";
    private static final String MESSSAGETYPE_NOT_DELIVERED = "NotDelivered";
    private static final String MESSAGETYPE_NOTDELIVERED_TEMP = "NotDeliveredTemp";

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


        LOG.debug(" Exported data by job [{}]", cronJob);

        //Setting catalog in session for emailAttachment
        catalogVersionService.setSessionCatalogVersion(
                Config.getString("email.attachment.default.catalog", "sabmContentCatalog"),
                Config.getString("email.attachment.default.catalog.version", "Staged"));

        FileInputStream fis = null;
        try
        {
            SabmCSVUtils.purgeOldFiles(SabmCSVUtils.getFullPath("trackorder").getPath());

            final File reportData = getReportData();
            if (reportData != null)
            {
                fis = new FileInputStream(reportData);
                //Creating attachment for the email with generated report file.
                final EmailAttachmentModel emailAttachment = emailService.createEmailAttachment(
                        new DataInputStream(fis), getAttachmentFileName(), getMimeType());

                final SystemEmailMessageModel systemEmail = emailService.constructSystemEmail(cronJob.getEmailFrom(),
                        cronJob.getEmailTo(), cronJob.getEmailTo(), cronJob.getSubject(), Collections.singletonList(cronJob.getBody()),
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
        sb.append(Config.getString("track.order.export.report", "TrackOrders"));
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

        headers.add("SAP Sales Delivery Number");
        headers.add("Venue");
        headers.add("Order number");
        headers.add("Order Placed Date");
        headers.add("Requested Delivery Date");
        headers.add("Warehouse / State");
        headers.add("1st ETA");
        headers.add("Next Next In Queue");
        headers.add("Arrival Time");
        headers.add("Actual Delivered Time");
        headers.add("Non-delivered Reason");
        headers.add("final status");

        return headers;
    }

    public List<List<String>> getOrderReportData()
    {

        final Date date = DateUtils.addDays(new Date(), -7);
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("date", date);

        final FlexibleSearchQuery fsq = new FlexibleSearchQuery(TRACK_ORDERS, params);

        final SearchResult<TrackOrderImportRecordModel> result = flexibleSearchService.search(fsq);
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

        if (CollectionUtils.isEmpty(result.getResult()))
        {
            LOG.info("Nothing to process");
            return null;
        }


        Map<String, List<TrackOrderImportRecordModel>> trackOrderDeliveryMap= result.getResult().stream().collect(
                Collectors.groupingBy(TrackOrderImportRecordModel::getSAPDeliveryNumber));

        final List<List<String>> reportList = new ArrayList<List<String>>();

        Iterator<Map.Entry<String,List<TrackOrderImportRecordModel>>> entries = trackOrderDeliveryMap.entrySet().iterator();

        while(entries.hasNext()){
            Map.Entry<String,List<TrackOrderImportRecordModel>> entry = entries.next();

            List<String> recordList = new ArrayList<>();

            String deliveryNumber = entry.getKey();
            recordList.add(deliveryNumber);

            OrderModel orderModel = getOrderFromDeliveryNumber(deliveryNumber);
            recordList.add(orderModel!=null && orderModel.getUnit()!=null?orderModel.getUnit().getUid()+"-"+orderModel.getUnit().getName():"");
            recordList.add(orderModel!=null ? orderModel.getSapSalesOrderNumber():"");
            recordList.add(orderModel!=null? orderModel.getCreationtime().toString():"");
            recordList.add(orderModel!=null? SabmDateUtils.toFormattedString(orderModel.getRequestedDeliveryDate()):"");
            recordList.add(orderModel!=null&& orderModel.getDeliveryAddress()!=null && orderModel.getDeliveryAddress().getRegion()!=null ?
                    orderModel.getDeliveryAddress().getRegion().getName():"");
            if(CollectionUtils.isNotEmpty(entry.getValue())){

            List<TrackOrderImportRecordModel> records = entry.getValue();
                Collections.sort(records, Comparator.comparing(TrackOrderImportRecordModel::getCreationtime));

                String firstETA = "";
                String nextInQueue="";
                String notDeliveredReason ="";
                String notDeliveredTime="";
                String actualDeliveredTime="";
                String finalStatus="";
                String arrivalTime="";

                for(TrackOrderImportRecordModel record: records){

                 String messageType = record.getMessageType();

                    String dateTime = StringUtils.isNotBlank(record.getDateTime()) ? record.getDateTime().replace("T"," ").replace("Z"," "):"";
                    switch (messageType)
                {
                    case MESSSAGETYPE_ETA:

                        if(firstETA == "" && !BooleanUtils.toBoolean(record.getNextInQueueIndicator())) {
                            firstETA = dateTime;
                        }
                        if(BooleanUtils.toBoolean(record.getNextInQueueIndicator())) {
                            nextInQueue = dateTime;
                        }
                        break;

                    case MESSAGETYPE_NOTDELIVERED_TEMP:
                        notDeliveredReason = record.getNotDeliveredReason()!=null ? record.getNotDeliveredReason():"";
                        finalStatus = MESSAGETYPE_NOTDELIVERED_TEMP;
                        break;


                    case MESSSAGETYPE_ARRIVED:
                        arrivalTime = dateTime;
                        finalStatus =  MESSSAGETYPE_ARRIVED;
                        break;

                    case MESSSAGETYPE_DELIVERED:
                        actualDeliveredTime = dateTime;
                        finalStatus = MESSSAGETYPE_DELIVERED;
                        break;

                    case MESSSAGETYPE_NOT_DELIVERED:
                        finalStatus = MESSSAGETYPE_NOT_DELIVERED;
                        notDeliveredReason = record.getNotDeliveredReason()!=null ? record.getNotDeliveredReason():"";
                        break;
                }

            }

            recordList.add(firstETA);
            recordList.add(nextInQueue);
            recordList.add(arrivalTime);
            recordList.add(actualDeliveredTime);
            recordList.add(notDeliveredReason);
            recordList.add(finalStatus);
        }
        reportList.add(recordList);
        }

        return reportList;
    }

    private OrderModel getOrderFromDeliveryNumber(String deliveryNumber){


        return b2bOrderService.getOrderByConsignment(deliveryNumber);
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
            final File file = sabmCSVFileGenerator.writeToFile(SabmCSVUtils.getFullPath("trackorders"),
                    sdf.format(new Date()) + "_", fileExt, reportData, headerData);
            return file;
        }
        return null;
    }

}