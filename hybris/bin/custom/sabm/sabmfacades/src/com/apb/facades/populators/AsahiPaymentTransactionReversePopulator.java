package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionData;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionEntryData;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.apb.core.model.AsahiPaymentTransactionEntryModel;
import com.apb.core.model.AsahiPaymentTransactionModel;


/**
 *
 * AsahiPaymentTransactionReversePopulator implementation of {@link Populator}
 */
public class AsahiPaymentTransactionReversePopulator implements Populator<PaymentTransactionData, AsahiPaymentTransactionModel>
{
	private Converter<PaymentTransactionEntryData, AsahiPaymentTransactionEntryModel> paymentTransactionEntryReverseConverter;

	private CommonI18NService commonI18NService;
	private ModelService modelService;

	@Override
	public void populate(final PaymentTransactionData source, final AsahiPaymentTransactionModel target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCaptureStatus(source.getCaptureStatus());
		target.setCurrency(getCommonI18NService().getCurrency(source.getCurrencyIsocode()));
		target.setPlannedAmount(source.getPlannedAmount());
		target.setCode(source.getCode());
		target.setRequestId(source.getRequestId());
		target.setRequestToken(source.getRequestToken());
		final List<PaymentTransactionEntryModel> paymentTransactionEntries = new ArrayList<>();
		source.getEntries().forEach(paymentTransactionEntryData -> {
			final AsahiPaymentTransactionEntryModel paymentTransactionEntryModel = getModelService()
					.create(AsahiPaymentTransactionEntryModel.class);
			final AsahiPaymentTransactionEntryModel paymentTransactionEntry = getPaymentTransactionEntryReverseConverter()
					.convert(paymentTransactionEntryData, paymentTransactionEntryModel);
			paymentTransactionEntry.setPaymentTransaction(target);
			paymentTransactionEntries.add(paymentTransactionEntry);
		});
		target.setEntries(paymentTransactionEntries);

		if (StringUtils.isNotEmpty(source.getSamPaymentRef()))
		{
			target.setInvoicePaymentReference(source.getSamPaymentRef());
		}

		if (StringUtils.isNotEmpty(source.getSamPartialPayReason()))
		{
			target.setInvoicePartialPaymentReason(source.getSamPartialPayReason());
		}
	}

	public Converter<PaymentTransactionEntryData, AsahiPaymentTransactionEntryModel> getPaymentTransactionEntryReverseConverter()
	{
		return paymentTransactionEntryReverseConverter;
	}


	/**
	 * @param paymentTransactionEntryReverseConverter
	 */
	public void setPaymentTransactionEntryReverseConverter(
			final Converter<PaymentTransactionEntryData, AsahiPaymentTransactionEntryModel> paymentTransactionEntryReverseConverter)
	{
		this.paymentTransactionEntryReverseConverter = paymentTransactionEntryReverseConverter;
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


	public ModelService getModelService()
	{
		return modelService;
	}


	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}


