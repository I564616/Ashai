package com.apb.core.attribute;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.apb.core.model.AsahiPaymentTransactionEntryModel;


/**
 * AsahiRequestedReferenceAttributeHandler implementation of {@link DynamicAttributeHandler}
 */
public class AsahiRequestedReferenceAttributeHandler implements DynamicAttributeHandler<String, AbstractOrderModel>
{
	private final static Logger LOG = LoggerFactory.getLogger("AsahiRequestedReferenceAttributeHandler");

	@Override
	public String get(final AbstractOrderModel abstractOrderModel)
	{
		final List<PaymentTransactionModel> paymentTransactionModels = abstractOrderModel.getPaymentTransactions();
		if (CollectionUtils.isNotEmpty(paymentTransactionModels))
		{
			final PaymentTransactionModel paymentTransactionModel = paymentTransactionModels.get(0);
			final List<PaymentTransactionEntryModel> paymentTransactionEntryModels = paymentTransactionModel.getEntries();
			if (CollectionUtils.isNotEmpty(paymentTransactionEntryModels))
			{
				final PaymentTransactionEntryModel paymentTransactionEntryModel = paymentTransactionEntryModels.get(0);
				if (paymentTransactionEntryModel instanceof AsahiPaymentTransactionEntryModel)
				{
					LOG.debug("Payment transaction entry models size : " + paymentTransactionEntryModels.size());
					return ((AsahiPaymentTransactionEntryModel) paymentTransactionEntryModel).getRequestedReference();
				}
			}
		}
		LOG.debug("Payment transaction models not found : " + paymentTransactionModels.size());
		return StringUtils.EMPTY;
	}

	@Override
	public void set(final AbstractOrderModel arg0, final String arg1)
	{
		// TODO Auto-generated method stub

	}

}
