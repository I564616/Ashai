package com.sabmiller.core.invoices.converters.reversePopulators;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.enums.InvoiceDiscrepancyProcessResultEnum;
import com.sabmiller.core.enums.InvoiceDiscrepancyRaisedFromEnum;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import com.sabmiller.facades.invoice.SABMInvoiceDiscrepancyData;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhuo.a.jiang on 21/9/18.
 */
public class CreditAdjustmentSAPReversePopulator implements Populator<SABMInvoiceDiscrepancyData, InvoiceDiscrepancyRequestModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CreditAdjustmentSAPReversePopulator.class);

    /**
     * The b2b unit service.
     */
    @Resource(name = "b2bUnitService")
    private SabmB2BUnitService b2bUnitService;

    private final String DATE_PATTERN = "ddMMyyyy";

    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(SABMInvoiceDiscrepancyData source, InvoiceDiscrepancyRequestModel target) throws ConversionException {

        target.setInvoiceNumber(source.getInvoiceNumber());
        target.setProcessResultDescription(source.getRequestDescription());
        target.setType(null);
        target.setInvoiceDate(convertDateFromSAP(source.getInvoiceDate()));
        target.setRaisedFrom(InvoiceDiscrepancyRaisedFromEnum.SAP);
        target.setProcessResult(InvoiceDiscrepancyProcessResultEnum.APPROVED);
        target.setSapInvoiceNumber(source.getSapInvoiceNumber());
        target.setSapInvoiceType(source.getInvoiceType());
        if (StringUtils.isNotEmpty(source.getActualTotalAmount())) {
            try {
                target.setTotalAmountCreditedFromSAP(Double.valueOf(source.getActualTotalAmount()));
            } catch (Exception e) {
                LOG.error("double value parse exception for : " + source.getActualTotalAmount());
            }
        }

        B2BUnitModel unit = b2bUnitService.getUnitForUid(source.getSoldTo());
        target.setB2bUnit(unit);

    }

    private Date convertDateFromSAP(String date) {

        if (StringUtils.isNotEmpty(date)) {

            final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

            try {
                return sdf.parse(date);
            } catch (ParseException e) {
                LOG.error("parse date error:  " + date);
            }

        }

        return null;
    }

}
