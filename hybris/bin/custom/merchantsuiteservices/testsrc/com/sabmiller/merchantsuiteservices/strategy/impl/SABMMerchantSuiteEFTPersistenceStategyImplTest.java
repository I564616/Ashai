package com.sabmiller.merchantsuiteservices.strategy.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
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
public class SABMMerchantSuiteEFTPersistenceStategyImplTest {

    @Mock
    private ModelService modelService;

    @Mock
    private DebitPaymentInfoModel debitPaymentInfoModel;

    SABMMerchantSuiteTransactionProcessData sabmMerchantSuiteTransactionProcessData;

    @InjectMocks
    SABMMerchantSuiteEFTPersistenceStategyImpl sabmMerchantSuiteEFTPersistenceStategy;

    @Mock
    UserModel userModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sabmMerchantSuiteTransactionProcessData = new SABMMerchantSuiteTransactionProcessData();
    }

    @Test
    public void testCreatePaymentInfo(){

        when(modelService.create(DebitPaymentInfoModel._TYPECODE)).thenReturn(debitPaymentInfoModel);
        Assert.assertNotNull(sabmMerchantSuiteEFTPersistenceStategy.createPaymentInfo(sabmMerchantSuiteTransactionProcessData,userModel));
    }

    @Test(expected = ModelSavingException.class)
    public void testCreatePaymentInfo_throw_ModelSavingException(){

        when(this.modelService.create(DebitPaymentInfoModel._TYPECODE)).thenThrow(ModelSavingException.class);
        sabmMerchantSuiteEFTPersistenceStategy.createPaymentInfo(sabmMerchantSuiteTransactionProcessData,userModel);
    }


}
