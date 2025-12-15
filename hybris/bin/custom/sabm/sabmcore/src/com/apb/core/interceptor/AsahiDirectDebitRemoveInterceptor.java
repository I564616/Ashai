package com.apb.core.interceptor;

import com.apb.core.integration.AsahiIntegrationPointsServiceImpl;
import com.sabmiller.core.model.AsahiSAMDirectDebitModel;
import com.apb.facades.sam.data.AsahiDirectDebitData;
import com.apb.facades.sam.data.AsahiDirectDebitPaymentData;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;

public class AsahiDirectDebitRemoveInterceptor implements RemoveInterceptor<AsahiSAMDirectDebitModel>{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(AsahiDirectDebitRemoveInterceptor.class);
	
	/** The Constant COMPANY_CODE. */
	private static final String COMPANY_CODE = "sga";

	private static final String REMOVE_INDICATOR = "remove";
	
	/** The asahi integration points service. */
	@Resource
	private AsahiIntegrationPointsServiceImpl asahiIntegrationPointsService;
	
	public void onRemove(AsahiSAMDirectDebitModel directDebit, InterceptorContext arg1) throws InterceptorException{
            LOG.info("AsahiDirectDebitRemoveInterceptor calling onRemove start---");
            AsahiDirectDebitData directDebitData = new AsahiDirectDebitData();
            AsahiDirectDebitPaymentData directDebitPaymentData = new AsahiDirectDebitPaymentData();
            if (null != directDebit.getCustomer() && null != directDebit.getCustomer().getPayerAccount()) {
                directDebitData.setCustAccount(directDebit.getCustomer().getPayerAccount().getAccountNum());
            }
            directDebitPaymentData.setToken(" ");
            directDebitPaymentData.setTokenType(" ");
            if (null != directDebit.getPaymentTransaction()) {
                if ("BANK_ACCOUNT".equalsIgnoreCase(directDebit.getPaymentTransaction().getPaymentMode().toString())) {
                    directDebitPaymentData.setBsb(directDebit.getPaymentTransaction().getBsb());
                    directDebitPaymentData.setAccountName(directDebit.getPaymentTransaction().getAccountName());
                    directDebitPaymentData.setAccountNum(directDebit.getPaymentTransaction().getAccountNumber());
                } else {
                }
            }

            directDebitPaymentData.setCompanyCode(COMPANY_CODE);
            directDebitData.setPaymentTerm(directDebit.getCustomer().getPayerAccount().getPaymentTerm());
            directDebitData.setDirectDebitPaymentData(directDebitPaymentData);

            //Sending Fat Zebra token Id to ECC
            if (!this.asahiIntegrationPointsService.sendDirectDebitDetails(directDebitData, directDebit, false, REMOVE_INDICATOR)) {
                throw new InterceptorException(String.format("Backend didn't send successful response, direct debit %s won't be removed", directDebit.getPk().toString()));
            }
	}
}
