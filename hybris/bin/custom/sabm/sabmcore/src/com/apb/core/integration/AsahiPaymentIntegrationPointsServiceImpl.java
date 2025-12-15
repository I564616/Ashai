package com.apb.core.integration;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.apb.core.payment.fz.AsahiPaymentGatewayContext;
import com.apb.core.payment.fz.models.AsahiDirectDebitPaymentRequest;
import com.apb.core.payment.fz.models.AsahiPaymentResponse;
import com.apb.core.payment.fz.net.AsahiPaymentResource;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.sam.data.AsahiDirectDebitData;
import com.apb.integration.data.AsahiDirectDebitBankResponse;
import com.google.gson.Gson;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

/**
 * The Class AsahiPaymentIntegrationPointsServiceImpl.
 * 
 * @author Kuldeep.Singh1
 */
@Service("asahiPaymentIntegrationPointsService")
public class AsahiPaymentIntegrationPointsServiceImpl extends AsahiPaymentResource{

	/** The Constant LOG. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AsahiPaymentIntegrationPointsServiceImpl.class);
	
	/** The Constant PAYMENT_TARGET_SANDBOX_ENABLED. */
	private static final String PAYMENT_TARGET_SANDBOX_ENABLED = "asahi.payment.target.sandbox.url.enabled.dd.";
	
	/** The Constant SAM_PAYMENT_TARGET_ACCOUNT_ID. */
	private static final String SAM_PAYMENT_TARGET_ACCOUNT_ID = "asahi.payment.target.account.id.dd.";
	
	/** The Constant SAM_PAYMENT_TARGET_TRANSACTION_ID. */
	private static final String SAM_PAYMENT_TARGET_TRANSACTION_ID = "asahi.payment.target.transaction.token.id.dd.";
	
	/** The Constant DEFAULT_ACCOUNT_ID. */
	private static final String DEFAULT_ACCOUNT_ID = "TESTAsahi";
	
	/** The Constant DEFAULT_TOKEN_ID. */
	private static final String DEFAULT_TOKEN_ID = "0501434920a18146740843221689a77fa7028f76";
	
	/** The Constant DEFAULT_BANK_ACCOUNT_URL. */
	private static final String DEFAULT_BANK_ACCOUNT_URL = "https://gateway.pmnts-sandbox.io/v1.0/bank_accounts";
	
	/** The Constant DIRECT_DEBIT_BANK_ACCOUNT_URL. */
	private static final String DIRECT_DEBIT_BANK_ACCOUNT_URL = "direct.debit.bank.account.service.url.";
	
	/** The rest template. */
	@Resource
	private RestTemplate asahiRestTemplate;
	
	/** The asahi payment gateway context. */
	@Resource(name = "asahiPaymentGatewayContext")
	AsahiPaymentGatewayContext asahiPaymentGatewayContext;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	/** The cms site service. */
	@Resource
	private CMSSiteService cmsSiteService;

	/**
	 * Creates the bank account.
	 *
	 * @param directDebitdata the direct debitdata
	 * @return the asahi direct debit bank response
	 */
	public AsahiDirectDebitBankResponse createBankAccount(AsahiDirectDebitData directDebitdata) {
		try
		{
			final Gson gson = new Gson();
			AsahiDirectDebitPaymentRequest asahiDirectDebitPaymentRequest = new AsahiDirectDebitPaymentRequest();
			
			asahiDirectDebitPaymentRequest.setAccount_name(directDebitdata.getDirectDebitPaymentData().getAccountName());
			asahiDirectDebitPaymentRequest.setAccount_number(directDebitdata.getDirectDebitPaymentData().getAccountNum());
			
			if(null!=directDebitdata.getDirectDebitPaymentData().getBsb()){
				String arr [] = directDebitdata.getDirectDebitPaymentData().getBsb().split("(?<=\\G...)");
				StringBuffer bsb = new StringBuffer();
				bsb.append(arr[0]).append("-").append(arr[1]);
				asahiDirectDebitPaymentRequest.setBsb(bsb.toString());
			}
			asahiDirectDebitPaymentRequest.setAccount_type("AU");
			
			asahiPaymentGatewayContext.setUsername(this.asahiConfigurationService
					.getString(SAM_PAYMENT_TARGET_ACCOUNT_ID + cmsSiteService.getCurrentSite().getUid(), DEFAULT_ACCOUNT_ID));
			asahiPaymentGatewayContext.setToken(this.asahiConfigurationService
					.getString(SAM_PAYMENT_TARGET_TRANSACTION_ID + cmsSiteService.getCurrentSite().getUid(), DEFAULT_TOKEN_ID));
			asahiPaymentGatewayContext.setSandbox(this.asahiConfigurationService
					.getBoolean(PAYMENT_TARGET_SANDBOX_ENABLED + cmsSiteService.getCurrentSite().getUid(), false));

			asahiPaymentGatewayContext.setSiteId(cmsSiteService.getCurrentSite().getUid());
			
			asahiPaymentGatewayContext.setDirectDebit(true);
			
			final AsahiPaymentResponse<AsahiDirectDebitBankResponse> response = doRequest(this.asahiConfigurationService.getString(DIRECT_DEBIT_BANK_ACCOUNT_URL + cmsSiteService.getCurrentSite().getUid(), DEFAULT_BANK_ACCOUNT_URL),
					asahiDirectDebitPaymentRequest, RequestType.POST, AsahiDirectDebitBankResponse.class, asahiPaymentGatewayContext);
			if(null!=response.getResponseBody()){
				LOGGER.info("Direct Debit Bank Account Response---", response.getResponseBody());
				return gson.fromJson(response.getResponseBody(), AsahiDirectDebitBankResponse.class);
			}
		}
		catch (final Exception e)
		{
			LOGGER.info("exception in createBankAccount", e);
		}
		return null;
	}
}
