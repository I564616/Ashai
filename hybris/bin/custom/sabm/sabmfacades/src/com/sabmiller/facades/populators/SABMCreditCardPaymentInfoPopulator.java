package com.sabmiller.facades.populators;

import de.hybris.platform.commercefacades.order.converters.populator.CreditCardPaymentInfoPopulator;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import com.apb.core.util.AsahiSiteUtil;


/**
 * The Class SABMCreditCardPaymentInfoPopulator.
 */
public class SABMCreditCardPaymentInfoPopulator extends CreditCardPaymentInfoPopulator
{

	/** The last digits. */
	@Value(value = "${cc.last.digits.to.show:3}")
	private int lastDigits;

	/** The full digits. */
	@Value(value = "${cc.full.digits:16}")
	private int fullDigits;

	/** The masking char. */
	@Value(value = "${cc.masking.char:*}")
	private String maskingChar;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator#addCommon(de.hybris.platform.
	 * core.model.order.AbstractOrderModel, de.hybris.platform.commercefacades.order.data.AbstractOrderData)
	 */

	@Override
	public void populate(final CreditCardPaymentInfoModel source, final CCPaymentInfoData target)
	{
		super.populate(source, target);
		
		if(!asahiSiteUtil.isCub())
		{
		target.setDisplayName(source.getDisplayName());
		target.setToken(source.getToken());
		}
		
		else
		{
   		if (StringUtils.length(source.getNumber()) >= lastDigits)
   		{
   			target.setHideCardNumber(
   					StringUtils.leftPad(source.getNumber().substring(source.getNumber().length() - 3, source.getNumber().length()),
   							fullDigits, maskingChar));
   		}
		}
	}

}
