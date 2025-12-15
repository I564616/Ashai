package com.apb.storefront.util;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import jakarta.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.apb.storefront.controllers.ControllerConstants;
import com.apb.core.service.config.AsahiConfigurationService;


/**
 * Util file to load iframe
 */
public class AsahiPaymentIframeUrlUtil
{

	/**
	 * @return
	 */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	private static final String REFERENCE_ID = "Test101";
	private static final String REFERENCE_AMOUNT = "00.00";
	private static final String SECRET_KEY = "asahi.payment.target.shared.secret.key.";
	private static final String CURRENCY_ID = "AUD";
	private static final String DEFAULT_ALGO = "HmacMD5";

	private static final String ACCOUNT_ID = "asahi.payment.target.account.id.";
	private static final String TARGET_URL = "asahi.payment.target.url.";
	private static final String TARGET_QUERY_PARAM = "asahi.payment.target.query.param.";
	private static final String DEFAULT_SECRET_KEY = "30d589ba44";
	private static final String DEFAULT_TARGET_QUERY_PARAM = "?iframe=true&show_extras=false&show_email=false&l=v2&return_target=_self&tokenize_only=true&postmessage=true&hide_button=true";
	private static final String CREDIT_CARD_EXTERNAL_CSS_URL = "credit.card.iframe.external.css.url.";
	private static final String HOST_CUSTOMER_IP = "apb.payment.iframe.host.ip";

	private static final Logger LOG = LoggerFactory.getLogger(AsahiConfigurationService.class);

	@Autowired
	private CMSSiteService cmsSiteService;

	/**
	 * @return iframe target url.
	 */
	public String getIframeUrl()
	{
		LOG.info("Site id " + cmsSiteService.getCurrentSite().getUid());

		final String externalCSSUrl = asahiConfigurationService
				.getString(CREDIT_CARD_EXTERNAL_CSS_URL + cmsSiteService.getCurrentSite().getUid(), StringUtils.EMPTY);

		final String verificationstring = REFERENCE_ID + ":" + REFERENCE_AMOUNT + ":" + CURRENCY_ID;


		//The following code calculate the verification value
		final SecretKeySpec keySpec = new SecretKeySpec(asahiConfigurationService
				.getString(SECRET_KEY + cmsSiteService.getCurrentSite().getUid(), DEFAULT_SECRET_KEY).getBytes(), DEFAULT_ALGO);
		Mac mac = null;
		try
		{
			mac = Mac.getInstance(DEFAULT_ALGO);
			mac.init(keySpec);
		}
		catch (NoSuchAlgorithmException | InvalidKeyException e)
		{
			LOG.error("Cannot get Mac instance", e);
		}

		byte[] hashBytes = null;
		if (null != mac)
		{
			hashBytes = mac.doFinal(verificationstring.getBytes());
		}
		final String verification = Hex.encodeHexString(hashBytes).toLowerCase();

		//This is the URL that will be the source of the iframe. The payment page options are included in the URL

		final String postUrlFinal = asahiConfigurationService.getString(TARGET_URL + cmsSiteService.getCurrentSite().getUid(),
				"https://paynow-sandbox.pmnts.io/v2/")
				+ asahiConfigurationService.getString(ACCOUNT_ID + cmsSiteService.getCurrentSite().getUid(), "TESTAsahi") + "/"
				+ REFERENCE_ID + "/" + CURRENCY_ID + "/" + REFERENCE_AMOUNT + "/" + verification + asahiConfigurationService
						.getString(TARGET_QUERY_PARAM + cmsSiteService.getCurrentSite().getUid(), DEFAULT_TARGET_QUERY_PARAM);
		if (StringUtils.isNotEmpty(externalCSSUrl) && null != mac)
		{
			final byte[] cssHashBytes = mac.doFinal(externalCSSUrl.getBytes());
			final String css_signature = Hex.encodeHexString(cssHashBytes).toLowerCase();
			return postUrlFinal + "&css=" + externalCSSUrl + "&css_signature=" + css_signature;
		}
		return postUrlFinal;

	}


	/**
	 * @param request
	 * @return
	 */
	public String getClientIPAddress(final HttpServletRequest request)
	{
		String customerIP = request.getRemoteAddr();

		if (StringUtils.isNotEmpty(request.getHeader("X-FORWARDED-FOR")))
		{
			final String[] forwardedIPS = request.getHeader("X-FORWARDED-FOR").split(",");
			customerIP = forwardedIPS[0];
		}

		if (StringUtils.isEmpty(customerIP))
		{
			try
			{
				final InetAddress IP = InetAddress.getLocalHost();
				LOG.info("Inet IP Address.." + IP);
				customerIP = IP.getHostAddress();
			}
			catch (final UnknownHostException e)
			{
				LOG.error("Cannot get IP Address", e);
			}
			/*final InetAddress IP = InetAddress.getLocalHost();
			LOG.info("Inet IP Address.." + IP);
			customerIP = IP.getHostAddress();*/
			customerIP = asahiConfigurationService.getString(HOST_CUSTOMER_IP, ControllerConstants.Utils.Payment.DefaultCustomerIP);
		}
		return customerIP;

	}

}
