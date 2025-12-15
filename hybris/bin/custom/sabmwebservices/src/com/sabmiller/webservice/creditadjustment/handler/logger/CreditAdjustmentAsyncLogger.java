/**
 *
 */
package com.sabmiller.webservice.creditadjustment.handler.logger;

import com.sabmiller.webservice.creditadjustment.CreditAdjustment;
import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.model.CreditAdjustmentImportRecordModel;
import com.sabmiller.webservice.model.MasterImportModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.xml.transformer.UnmarshallingTransformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhuo.a.jiang on 19/12/2017.
 */

@Component
public class CreditAdjustmentAsyncLogger {

    private static final Logger LOG = LoggerFactory.getLogger(CreditAdjustmentAsyncLogger.class.getName());

    @Resource
    private ModelService modelService;

    @Resource
    private SessionService sessionService;

    @Resource
    private UnmarshallingTransformer creditAdjustmentUnmarshallingTransformer;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public Message saveCreditAdjustmentPayload(final Message message) {

        if (!Registry.hasCurrentTenant()) {
            Registry.activateMasterTenant();
        }
        if (!sessionService.hasCurrentSession()) {
            sessionService.createNewSession();
        }

        // there is no unique payload id from SAP invoice request, each Request will be recorded in System
        final String paylaodId = dateFormat.format(new Date());

        final MasterImportModel masterImportModel = modelService.create(MasterImportModel.class);

        masterImportModel.setPayloadId(paylaodId);
        masterImportModel.setPayload(message.getPayload().toString());

        masterImportModel.setEntity(EntityTypeEnum.CREDIT_ADJUSTMENT_STATUS_UPDATE);

        masterImportModel.setStatus(DataImportStatusEnum.NEW);
        modelService.save(masterImportModel);

        try {
            final CreditAdjustment creditAdjustment = (CreditAdjustment) creditAdjustmentUnmarshallingTransformer.doTransform(message);
            saveCreditAdjustmentImportEntity(creditAdjustment, masterImportModel);

        } catch (final Exception e) {
            LOG.error("CreditAdjustmentAsyncLogger payload logger error", e);

        }

        LOG.debug("CreditAdjustmentAsyncLogger payload logger");

        return message;
    }

    private void saveCreditAdjustmentImportEntity(final CreditAdjustment creditAdjustment, final MasterImportModel masterImportModel) {
        if (CollectionUtils.isNotEmpty(creditAdjustment.getInvoice())) {
            for (final CreditAdjustment.Invoice invoice : creditAdjustment.getInvoice()) {
                LOG.debug("saveCreditAdjustmentImportEntity SAP Invoice Number ", invoice.getPurchaseOrderNumber());

                final CreditAdjustmentImportRecordModel recordModel = modelService.create(CreditAdjustmentImportRecordModel.class);
                recordModel.setInvoiceNumber(invoice.getInvoiceNumber());
                recordModel.setPurchaseOrderNumber(invoice.getPurchaseOrderNumber());

                recordModel.setAmount(invoice.getAmount());

                recordModel.setCreditAdjustmentStatus(invoice.getStatus());
                recordModel.setType(invoice.getType());
                recordModel.setSoldTo(invoice.getSoldTo());
                recordModel.setPaymentDate(invoice.getPaymentDate());
                recordModel.setDueDate(invoice.getDueDate());

                modelService.save(recordModel);
            }

        }
    }
}
