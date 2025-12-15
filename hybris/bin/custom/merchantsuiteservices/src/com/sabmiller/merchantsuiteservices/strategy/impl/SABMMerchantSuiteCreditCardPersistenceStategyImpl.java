package com.sabmiller.merchantsuiteservices.strategy.impl;

import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTransactionProcessData;
import com.sabmiller.merchantsuiteservices.strategy.SABMMerchantSuitePaymentTypePersistenceStategy;

public class SABMMerchantSuiteCreditCardPersistenceStategyImpl  implements SABMMerchantSuitePaymentTypePersistenceStategy {

    @Resource
    private ModelService modelService;

    public PaymentInfoModel createPaymentInfo(final SABMMerchantSuiteTransactionProcessData responseData, final UserModel user)
    {
        //Add payment info
        final CreditCardPaymentInfoModel paymentInfoModel = modelService.create(CreditCardPaymentInfoModel._TYPECODE);
        paymentInfoModel.setCode(UUID.randomUUID().toString());
        paymentInfoModel.setPaymentReference(responseData.getReference1());
        if (StringUtils.isNotBlank(responseData.getCardHolderName()))
        {
            paymentInfoModel.setCcOwner(responseData.getCardHolderName());
        }
        else
        {
            paymentInfoModel.setCcOwner("Customer");
        }
        if (StringUtils.isNotBlank(responseData.getMaskedCardNumber()))
        {
            paymentInfoModel.setNumber(StringUtils.leftPad(responseData.getMaskedCardNumber(), 16, "*"));
        }
        else
        {
            paymentInfoModel.setNumber("****************");
        }
        if (StringUtils.isNotBlank(responseData.getExpiryDate()))
        {

            paymentInfoModel.setValidToMonth(responseData.getExpiryDate().substring(0,2));
        }
        else
        {
            paymentInfoModel.setValidToMonth("00");
        }
        if (StringUtils.isNotBlank(responseData.getExpiryDate()))
        {
            paymentInfoModel.setValidToYear(responseData.getExpiryDate().substring(2,responseData.getExpiryDate().length()));
        }
        else
        {
            paymentInfoModel.setValidToYear("00");

        }
        if (StringUtils.isNotBlank(responseData.getCardType()))
        {
            if (responseData.getCardType().equals("VC"))
            {
                paymentInfoModel.setType(CreditCardType.VISA);
            }
            else if (responseData.getCardType().equals("AX"))
            {
                paymentInfoModel.setType(CreditCardType.AMEX);
            }
            else if (responseData.getCardType().equals("MC"))
            {
                paymentInfoModel.setType(CreditCardType.MASTER);
            }
        }
        paymentInfoModel.setSaved(false);
        paymentInfoModel.setUser(user);
        paymentInfoModel.setOwner(user);
        return paymentInfoModel;
    }

}
