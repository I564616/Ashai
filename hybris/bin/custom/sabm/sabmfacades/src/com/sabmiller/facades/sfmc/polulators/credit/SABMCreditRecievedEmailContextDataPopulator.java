package com.sabmiller.facades.sfmc.polulators.credit;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.sabmiller.core.enums.InvoiceDiscrepancyType;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestModel;
import com.sabmiller.core.model.InvoiceDiscrepancyRequestNotificationEmailModel;
import com.sabmiller.facades.sfmc.context.SABMCreditRecievedEmailContextData;

/**
 * Created by zhuo.a.jiang on 18/9/18.
 */
public class SABMCreditRecievedEmailContextDataPopulator
        implements Populator<InvoiceDiscrepancyRequestModel, SABMCreditRecievedEmailContextData> {

    private static final Logger LOG = LoggerFactory.getLogger(SABMCreditRecievedEmailContextDataPopulator.class);

    private static final String CCEMAIL_SEPARATOR = ";" ;

    private static final int CCEMAIL_MAX_LENGTH =  3999;

    /**
     * Populate the target instance with values from the source instance.
     *
     * @param source the source object
     * @param target the target to fill
     * @throws ConversionException if an error occurs
     */
    @Override
    public void populate(InvoiceDiscrepancyRequestModel source, SABMCreditRecievedEmailContextData target) throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        Assert.notNull(source.getB2bUnit(), "B2BUnit  cannot be null.");

        target.setAccountNumber(source.getB2bUnit().getUid());
        target.setBusinessName(source.getB2bUnit().getName());
        target.setInvoiceNumber(source.getInvoiceNumber());
        target.setIssueType(source.getType().getCode());

        target.setRaisedBy(source.getRaisedBy().getName());

        if(InvoiceDiscrepancyType.PRICE.equals(source.getType())){
            /*target.setExpectedDiscount(source.getTotalDiscountExpected());*/
      	  target.setAmount(source.getTotalDiscountExpected());
        }

        if(InvoiceDiscrepancyType.FREIGHT.equals(source.getType())){
            /*target.setExpectedDiscount(source.getTotalFreightDiscountCharged() - source.getTotalFreightDiscountExpected());*/
      	  target.setAmount(source.getTotalFreightDiscountCharged() - source.getTotalFreightDiscountExpected());

            //TODO discuss with business which value to display in email
            //target.setExpectedDiscount(source.getTotalFreightAmountToCredit);
        }
        target.setComments(source.getDescription());

        String ccEmailList = "";

        StringBuilder sb = new StringBuilder(ccEmailList);
        
        //COmment out below for loop as per incident - INC1076578 to send email to the cc users separately. 

        /*for (InvoiceDiscrepancyRequestNotificationEmailModel model : source.getConfirmationEmailList()) {

            sb.append(model.getEmailAddress() + CCEMAIL_SEPARATOR);

        }*/

        if (validateCCEmailListMaxLength(sb.toString())) {
            target.setCcAddress(sb.toString());
        } else {
            target.setCcAddress(StringUtils.EMPTY);
        }



    }

    private boolean validateCCEmailListMaxLength(final String ccEmailList) {

        if (StringUtils.length(ccEmailList) > CCEMAIL_MAX_LENGTH) {
            return false;
        }
        return true;

    }


}
