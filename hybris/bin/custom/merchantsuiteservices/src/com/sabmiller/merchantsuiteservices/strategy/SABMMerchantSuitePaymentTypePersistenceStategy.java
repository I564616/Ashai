package com.sabmiller.merchantsuiteservices.strategy;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;

import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTransactionProcessData;

public interface SABMMerchantSuitePaymentTypePersistenceStategy {

    PaymentInfoModel createPaymentInfo(final SABMMerchantSuiteTransactionProcessData responseData, final UserModel user);
}
