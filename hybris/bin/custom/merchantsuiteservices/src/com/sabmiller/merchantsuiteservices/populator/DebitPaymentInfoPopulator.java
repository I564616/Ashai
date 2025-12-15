package com.sabmiller.merchantsuiteservices.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;

import com.sabmiller.facades.payment.DebitPaymentData;

public class DebitPaymentInfoPopulator implements Populator<DebitPaymentInfoModel, DebitPaymentData>
{

    public void populate(final DebitPaymentInfoModel debitPaymentInfoModel, final DebitPaymentData debitPaymentData)
    {
        debitPaymentData.setAccountNumber( debitPaymentInfoModel.getAccountNumber() );
        debitPaymentData.setAccountName( debitPaymentInfoModel.getBaOwner() );
        debitPaymentData.setBsb( debitPaymentInfoModel.getBank() );
    }

}