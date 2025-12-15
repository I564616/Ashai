package com.sabmiller.webservice.creditadjustment.handler;

import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.facades.customer.InvoiceUpdateException;
import com.sabmiller.facades.customer.SABMInvoiceFacade;
import com.sabmiller.facades.invoice.SABMInvoiceDiscrepancyData;
import com.sabmiller.facades.util.SabmFeatureUtil;
import com.sabmiller.integration.enums.ErrorEventType;
import com.sabmiller.integration.facade.ErrorEventFacade;
import com.sabmiller.webservice.creditadjustment.CreditAdjustment;
import com.sabmiller.webservice.enums.DataImportStatusEnum;
import com.sabmiller.webservice.enums.EntityTypeEnum;
import com.sabmiller.webservice.enums.OperationEnum;
import com.sabmiller.webservice.importer.AbstractImportHandler;
import com.sabmiller.webservice.model.ImportRecordModel;
import com.sabmiller.webservice.response.CreditAdjustmentResponse;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;

/**
 * Created by zhuo.a.jiang on 19/12/2017.
 */
public class CreditAdjustmentImportHandler extends AbstractImportHandler<CreditAdjustment, CreditAdjustmentResponse, ImportRecordModel> {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "invoiceFacade")
    private SABMInvoiceFacade invoiceFacade;

    @Resource(name = "errorEventFacade")
    private ErrorEventFacade errorEventFacade;

    @Resource(name = "sabmConfigurationService")
    private SabmConfigurationService sabmConfigurationService;

    @Resource(name = "sabmFeatureUtil")
    private SabmFeatureUtil sabmFeatureUtil;


    @Resource(name = "b2bUnitService")
    private SabmB2BUnitService b2bUnitService;

    @Resource(name = "modelService")
    private ModelService modelService;

    @Resource(name = "creditAdjustmentWsConverter")
    private Converter<CreditAdjustment.Invoice, SABMInvoiceDiscrepancyData> creditAdjustmentWsConverter;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public Converter<CreditAdjustmentResponse, ImportRecordModel> getImportRecordReverseConverter() {
        return null;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return null;
    }

    @Override
    public CreditAdjustmentResponse importEntity(final CreditAdjustment creditAdjustment) {

        LOG.debug("CreditAdjustmentImportHandler importEntity");
        Exception creditAdjustmentProcessException = null;

        if (CollectionUtils.isNotEmpty(creditAdjustment.getInvoice())) {
            for (final CreditAdjustment.Invoice invoice : creditAdjustment.getInvoice()) {
                final SABMInvoiceDiscrepancyData data = creditAdjustmentWsConverter.convert(invoice);
                final String b2bUnitCode = data.getSoldTo();

                B2BUnitModel b2BUnitModel = null ;
                if(StringUtils.isNotEmpty(b2bUnitCode)) {
                    b2BUnitModel= b2bUnitService.getUnitForUid(b2bUnitCode);
                }

                boolean creditAdjustmentFeatureEnabled = sabmFeatureUtil.isFeatureEnabledForUnit(SabmcommonsConstants.INVOICEDISCREPANY, b2BUnitModel);

                if (!creditAdjustmentFeatureEnabled) {
                    LOG.error("credit adjustment feature is not enabled for b2bUnit: " + b2bUnitCode);
                    creditAdjustmentProcessException = new Exception ("credit adjustment feature is not enabled for b2bUnit: " + b2bUnitCode);
                }
                try {
                    if (creditAdjustmentFeatureEnabled) {

                        invoiceFacade.updateInvoiceDiscrepancyRequestWithProcessResult(data);
                    }

                } catch (final ModelSavingException e) {
                    LOG.error("invoice update error: " + e.getMessage());
                    errorEventFacade.createErrorEntry(e, "SAP_Invoice_Update", null, ErrorEventType.SAP, e.getMessage());

                    creditAdjustmentProcessException = e;
                } catch (final NumberFormatException e) {
                    LOG.error("invoice update error with number parse exception with amount: " + data.getExpectedTotalAmount());
                    errorEventFacade.createErrorEntry(e, "SAP_Invoice_Update", null, ErrorEventType.SAP, e.getMessage());
                    creditAdjustmentProcessException = e;
                } catch (final InvoiceUpdateException e) {
                    LOG.error("invoice update error: " + e.getMessage());
                    errorEventFacade.createErrorEntry(e, "SAP_Invoice_Update", null, ErrorEventType.SAP, e.getMessage());
                    creditAdjustmentProcessException = e;
                }
            }
        }

        return generateResponse(creditAdjustment, creditAdjustmentProcessException, false);

    }

    public String handleXSDValidationError(final String message) {
        LOG.error("XSD Valdiation Error occurred for Credit Adjustment request from SAP invocie status request ");

        return "XSD Valdiation Error occurred for Credit Adjustment request from SAP invocie status request";
    }

    public CreditAdjustmentResponse logCreditAdjustmentRequestStatus(final CreditAdjustmentResponse creditAdjustmentResponse) {
        return creditAdjustmentResponse;
    }

    @Override
    public CreditAdjustmentResponse generateResponse(final CreditAdjustment creditAdjustment, final Exception e,
            final Boolean entityExist) {

        final CreditAdjustmentResponse response = new CreditAdjustmentResponse();

        if (e == null) {
            response.setCode("01");
            response.setStatus(DataImportStatusEnum.SUCCESS);
            response.setOperation(OperationEnum.CREATE);

        } else {
            response.setCode("02");
            response.setError(e.getMessage());
            response.setStatus(DataImportStatusEnum.ERROR);

        }

        return response;
    }

}
