package com.sabmiller.facades.sfmc.polulators.credit;

import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import com.sabmiller.facades.sfmc.context.SABMCreditApprovedEmailContextData;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

/**
 * Created by zhuo.a.jiang on 18/9/18.
 */
public class SABMCreditApprovedEmailContextDataPopulator
        implements Populator<InvoiceDiscrepancyRequestModel, SABMCreditApprovedEmailContextData> {


    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(InvoiceDiscrepancyRequestModel source, SABMCreditApprovedEmailContextData target) throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        Assert.notNull(source.getB2bUnit(), "B2BUnit  cannot be null.");

        target.setAccountNumber(source.getB2bUnit().getUid());
       /* target.setCreditAmount(source.getTotalAmountCreditedFromSAP());*/
        target.setAmount(source.getTotalAmountCreditedFromSAP());
        target.setCcAddress(StringUtils.EMPTY);
        target.setInvoiceNumber(source.getInvoiceNumber());
    }


}
