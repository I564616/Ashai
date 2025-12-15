package com.sabmiller.merchantsuiteservices.strategy.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.merchantsuiteservices.data.SABMMerchantSuiteTransactionProcessData;

@UnitTest
public class SABMMerchantSuiteCreditCardPersistenceStategyImplTest {

    @InjectMocks
    SABMMerchantSuiteCreditCardPersistenceStategyImpl sabmMerchantSuiteCreditCardPersistenceStategy;

    @Mock
    private ModelService modelService;

    @Mock
    CreditCardPaymentInfoModel creditCardPaymentInfoModel;

    SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData;

    @Mock
    UserModel userModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sabmMerchantSuiteTransactionProcessData = new SABMMerchantSuiteTransactionProcessData();
        sabmMerchantSuiteTransactionProcessData.setReference2("Reference2");
        sabmMerchantSuiteTransactionProcessData.setMaskedCardNumber("123456");
        sabmMerchantSuiteTransactionProcessData.setExpiryDate("123");
        sabmMerchantSuiteTransactionProcessData.setCardType("VC");
    }

    @Test
    public void testCreatePaymentInfo()
    {
        when(modelService.create(CreditCardPaymentInfoModel._TYPECODE)).thenReturn(creditCardPaymentInfoModel);
        Assert.assertNotNull(sabmMerchantSuiteCreditCardPersistenceStategy.createPaymentInfo(sabmMerchantSuiteTransactionProcessData,userModel));
    }

    @Test(expected = ModelSavingException.class)
    public void testCreatePaymentInfo_throw_ModelSavingExceptionm()
    {
        when(modelService.create(CreditCardPaymentInfoModel._TYPECODE)).thenThrow(ModelSavingException.class);
        Assert.assertNotNull(sabmMerchantSuiteCreditCardPersistenceStategy.createPaymentInfo(sabmMerchantSuiteTransactionProcessData,userModel));
    }
}
