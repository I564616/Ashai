package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionEntryData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.apb.core.model.AsahiPaymentTransactionEntryModel;


/**
 *
 */
public class AsahiPaymentTransactionEntryReversePopulator
		implements Populator<PaymentTransactionEntryData, AsahiPaymentTransactionEntryModel>
{
	private CommonI18NService commonI18NService;
	private static final Logger LOG = LoggerFactory.getLogger(AsahiPaymentTransactionEntryReversePopulator.class);

	@Override
	public void populate(final PaymentTransactionEntryData source, final AsahiPaymentTransactionEntryModel target)
			throws ConversionException
	{
		if (source != null && target != null)
		{
			LOG.info("Populate Transaction Entry..");
			if (StringUtils.isNotEmpty(source.getCode()))
			{
				target.setCode(source.getCode());
			}
			if (null != source.getAmount())
			{
				target.setAmount(source.getAmount());
			}

			if (StringUtils.isNotEmpty(source.getDecimalAmount()))
			{
				target.setDecimalAmount(source.getDecimalAmount());
			}
			if (StringUtils.isNotEmpty(source.getCapturedAmount()))
			{
				target.setCapturedAmount(source.getCapturedAmount());
			}
			if (StringUtils.isNotEmpty(source.getCaptureStatus()))
			{
				target.setCaptureStatus(source.getCaptureStatus());
			}
			if (StringUtils.isNotEmpty(source.getAuthorizationId()))
			{
				target.setAuthorizationId(source.getAuthorizationId());
			}
			if (StringUtils.isNotEmpty(source.getCardNumber()))
			{
				target.setCardNumber(source.getCardNumber());
			}
			if (StringUtils.isNotEmpty(source.getCardHolder()))
			{
				target.setCardHolder(source.getCardHolder());
			}
			if (StringUtils.isNotEmpty(source.getCardExpiry()))
			{
				target.setCardExpiry(source.getCardExpiry());
			}
			if (StringUtils.isNotEmpty(source.getRequestToken()))
			{
				target.setRequestToken(source.getRequestToken());
			}
			if (StringUtils.isNotEmpty(source.getSuccessfulStatus()))
			{
				target.setSuccessfulStatus(source.getSuccessfulStatus());
			}
			if (StringUtils.isNotEmpty(source.getResponseMessage()))
			{
				target.setResponseMessage(source.getResponseMessage());
			}
			if (StringUtils.isNotEmpty(source.getRequestedReference()))
			{
				target.setRequestedReference(source.getRequestedReference());
			}
			if (StringUtils.isNotEmpty(source.getCurrencyCode()))
			{
				target.setCurrencyCode(source.getCurrencyCode());
			}
			if (StringUtils.isNotEmpty(source.getSettlementDate()))
			{
				target.setSettlementDate(source.getSettlementDate());
			}
			if (StringUtils.isNotEmpty(source.getTransactionDate()))
			{
				target.setTransactionDate(source.getTransactionDate());
			}
			if (StringUtils.isNotEmpty(source.getTransactionStatusDetails()))
			{
				target.setTransactionStatusDetails(source.getTransactionStatusDetails());
			}
			if (StringUtils.isNotEmpty(source.getResponseCode()))
			{
				target.setResponseCode(source.getResponseCode());
			}
			if (StringUtils.isNotEmpty(source.getRrn()))
			{
				target.setRrn(source.getRrn());
			}
			if (StringUtils.isNotEmpty(source.getCvvMatch()))
			{
				target.setCvvMatch(source.getCvvMatch());
			}
			if (StringUtils.isNotEmpty(source.getCardType()))
			{
				target.setCardType(source.getCardType());
			}
			if (StringUtils.isNotEmpty(source.getCardCategory()))
			{
				target.setCardCategory(source.getCardCategory());
			}
			if (StringUtils.isNotEmpty(source.getCardSubCategory()))
			{
				target.setCardSubCategory(source.getCardSubCategory());
			}
			if (StringUtils.isNotEmpty(source.getTransactionId()))
			{
				target.setTransactionId(source.getTransactionId());
			}

		}
	}

	/**
	 * @return
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}


}
