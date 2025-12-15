package com.sabmiller.merchantsuiteservices.strategy.impl;

import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;

import jakarta.annotation.Resource;

import java.util.UUID;

import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTransactionProcessData;
import com.sabmiller.merchantsuiteservices.strategy.SABMMerchantSuitePaymentTypePersistenceStategy;

public class SABMMerchantSuiteEFTPersistenceStategyImpl implements SABMMerchantSuitePaymentTypePersistenceStategy {

    @Resource
    private ModelService modelService;

    @Override
    public PaymentInfoModel createPaymentInfo(final SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData , final UserModel user)
    {
        //Add payment info
        final DebitPaymentInfoModel paymentInfoModel = modelService.create(DebitPaymentInfoModel._TYPECODE);
        paymentInfoModel.setCode(UUID.randomUUID().toString());
        paymentInfoModel.setBankIDNumber(sabmMerchantSuiteTransactionProcessData.getAccountNumber() );
        paymentInfoModel.setBaOwner(sabmMerchantSuiteTransactionProcessData.getAccountName());
        paymentInfoModel.setAccountNumber(sabmMerchantSuiteTransactionProcessData.getAccountNumber());
        paymentInfoModel.setBank(sabmMerchantSuiteTransactionProcessData.getBsbNumber());
        paymentInfoModel.setSaved(false);
        paymentInfoModel.setUser(user);
        paymentInfoModel.setOwner(user);

        return paymentInfoModel;
    }
}
