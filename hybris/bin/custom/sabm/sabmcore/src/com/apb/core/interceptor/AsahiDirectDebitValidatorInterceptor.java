package com.apb.core.interceptor;

import com.apb.core.integration.AsahiIntegrationPointsServiceImpl;
import com.sabmiller.core.model.AsahiSAMDirectDebitModel;
import com.apb.facades.sam.data.AsahiDirectDebitData;
import com.apb.facades.sam.data.AsahiDirectDebitPaymentData;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;

public class AsahiDirectDebitValidatorInterceptor implements ValidateInterceptor<AsahiSAMDirectDebitModel>{

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = Logger.getLogger(AsahiDirectDebitValidatorInterceptor.class);

    /**
     * The Constant COMPANY_CODE.
     */
    private static final String COMPANY_CODE = "sga";

    private static final String UPDATE_INDICATOR = "update";

    /**
     * The asahi integration points service.
     */
    @Resource
    private AsahiIntegrationPointsServiceImpl asahiIntegrationPointsService;

    private AsahiDirectDebitData directDebitData;
    private AsahiDirectDebitPaymentData directDebitPaymentData;

    @Override
    public void onValidate(AsahiSAMDirectDebitModel directDebit, InterceptorContext interceptorContext) throws InterceptorException {
        if (null != directDebit.getPaymentTransaction() && null != directDebit.getPaymentTransaction().getRequestToken()
                && !interceptorContext.isNew(directDebit) && !interceptorContext.getDirtyAttributes(directDebit).containsKey(AsahiSAMDirectDebitModel.UPDATEREMOVEINDICATOR)) {
            LOG.info("Direct debit on validate interceptor called");
            prepareData(directDebit);
            //Sending Fat Zebra token Id to ECC
            this.asahiIntegrationPointsService.sendDirectDebitDetails(directDebitData, directDebit, false, UPDATE_INDICATOR);
        }
    }

    public boolean isSendDirectDebitSuccess(final AsahiSAMDirectDebitModel directDebit, final boolean isCronjobCall, final String indicator) {
        if (null != directDebit.getPaymentTransaction() && null != directDebit.getPaymentTransaction().getRequestToken()) {
            prepareData(directDebit);
            return asahiIntegrationPointsService.sendDirectDebitDetails(directDebitData, directDebit, isCronjobCall, indicator);
        }
        return false;
    }

    private void prepareData(final AsahiSAMDirectDebitModel directDebit){
            directDebitData = new AsahiDirectDebitData();
            directDebitPaymentData = new AsahiDirectDebitPaymentData();
            if (null != directDebit.getCustomer() && null != directDebit.getCustomer().getPayerAccount()) {
                directDebitData.setCustAccount(directDebit.getCustomer().getPayerAccount().getAccountNum());
                directDebitData.setPaymentTerm(directDebit.getCustomer().getPayerAccount().getPaymentTerm());
            }
            if (null != directDebit.getPaymentTransaction() && null != directDebit.getPaymentTransaction().getRequestToken()) {
                directDebitPaymentData.setToken(directDebit.getPaymentTransaction().getRequestToken());
            }
            if (null != directDebit.getPaymentTransaction() && null != directDebit.getPaymentTransaction().getPaymentMode()) {
                if ("BANK_ACCOUNT".equalsIgnoreCase(directDebit.getPaymentTransaction().getPaymentMode().toString())) {
                    directDebitPaymentData.setTokenType("BANK_ACCOUNT");
                    directDebitPaymentData.setBsb(directDebit.getPaymentTransaction().getBsb());
                    directDebitPaymentData.setAccountName(directDebit.getPaymentTransaction().getAccountName());
                    directDebitPaymentData.setAccountNum(directDebit.getPaymentTransaction().getAccountNumber());
                } else {
                    directDebitPaymentData.setTokenType("DEBIT_CREDIT_CARD");
                }
            }

            directDebitPaymentData.setCompanyCode(COMPANY_CODE);
            directDebitData.setDirectDebitPaymentData(directDebitPaymentData);
        }

}
