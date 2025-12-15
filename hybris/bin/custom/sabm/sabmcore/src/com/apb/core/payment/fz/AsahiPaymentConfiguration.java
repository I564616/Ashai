package com.apb.core.payment.fz;

import jakarta.annotation.Resource;

import com.apb.core.service.config.AsahiConfigurationService;


/**
 * Represents the static/singleton Payment Integration configuration
 */
public class AsahiPaymentConfiguration
{

	private static final String PAYMENT_USERNAME = "asahi.payment.target.account.id";
	private static final String DEFAULT_PAYMENT_USERNAME = "TESTAsahi";
	private static final String PAYMENT_TOKEN = "asahi.payment.target.transaction.token.id";
	private static final String DEFAULT_PAYMENT_TOKEN = "0501434920a18146740843221689a77fa7028f76";
	private static final String PAYMENT_SANDBOX = "asahi.payment.target.sandbox.url.enabled";
	private static final boolean DEFAULT_PAYMENT_SANDBOX = false;




	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;



	/**
	 * Provides a gateway context object.
	 *
	 * @return build context
	 */
	public AsahiPaymentGatewayContext getContext()
	{
		final AsahiPaymentGatewayContext ctx = new AsahiPaymentGatewayContext();
		ctx.setUsername(this.asahiConfigurationService.getString(PAYMENT_USERNAME, DEFAULT_PAYMENT_USERNAME));
		ctx.setToken(this.asahiConfigurationService.getString(PAYMENT_TOKEN, DEFAULT_PAYMENT_TOKEN));

		//add top level configuration here
		ctx.setSandbox(this.asahiConfigurationService.getBoolean(PAYMENT_SANDBOX, DEFAULT_PAYMENT_SANDBOX));

		return ctx;
	}
}
