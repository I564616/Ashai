package com.sabmiller.core.ordercreate.converters.populator;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.integration.sap.constants.SabmintegrationConstants;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest.DealCondition;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest.FreeText;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest.SalesOrderReqHeader;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest.SalesOrderReqPartner;




public class SalesOrderCreateRequestPopulator implements Populator<AbstractOrderModel, SalesOrderCreateRequest>
{
	private Converter<AbstractOrderModel, SalesOrderReqHeader> salesOrderCreateReqHeaderConverter;

	private Converter<AbstractOrderModel, SalesOrderReqPartner> salesOrderCreateReqPartnerConverter;

	private Converter<AbstractOrderEntryModel, SalesOrderCreateRequest.SalesOrderReqItem> salesOrderCreateReqItemConverter;


	@Override
	public void populate(final AbstractOrderModel cart, final SalesOrderCreateRequest target)
	{

		target.setSalesOrderReqHeader(getSalesOrderCreateReqHeaderConverter().convert(cart));
		target.setSalesOrderReqPartner(getSalesOrderCreateReqPartnerConverter().convert(cart));

		final List<AbstractOrderEntryModel> entriesWithoutFreeGoods = new ArrayList<AbstractOrderEntryModel>();

		for (final AbstractOrderEntryModel entry : cart.getEntries())
		{
			if (BooleanUtils.isNotTrue(entry.getIsFreeGood()))
			{
				entriesWithoutFreeGoods.add(entry);
			}
		}

		target.getSalesOrderReqItem().addAll(Converters.convertAll(entriesWithoutFreeGoods, getSalesOrderCreateReqItemConverter()));

		if (StringUtils.isNotBlank(cart.getDeliveryInstructions()))
		{
			target.getFreeText().add(getDeliveryInstructionsToken(cart));
		}
		setPaymentDetails(cart, target);

		if (!CollectionUtils.isEmpty(cart.getComplexDealConditions()))
		{
			target.getDealCondition().addAll(transform(cart.getComplexDealConditions()));
		}
	}

	private List<DealCondition> transform(final List<CartDealConditionModel> dealConditions)
	{
		final List<DealCondition> dcns = new ArrayList<DealCondition>();
		for (final CartDealConditionModel eachModel : org.apache.commons.collections4.CollectionUtils.emptyIfNull(dealConditions))
		{
			final DealCondition dc = new DealCondition();
			//       Checking for cartdeal is empty or not
			//       PRB0041296:Null Pointer Exception (INC0479637, INC0481854, INC0476731, INC0476729)
			if (null != eachModel.getDeal() && StringUtils.isNotEmpty(eachModel.getDeal().getCode()))
			{
				dc.setDealConditionNumber(eachModel.getDeal().getCode());
							dcns.add(dc);
			}

		}
		return dcns;
	}



	private void setPaymentDetails(final AbstractOrderModel cart, final SalesOrderCreateRequest target)
	{

		final List<PaymentTransactionModel> paymentTrans = cart.getPaymentTransactions();

		if (!CollectionUtils.isEmpty(paymentTrans) && paymentTrans.get(0).getInfo() != null)
		{

			final PaymentTransactionModel paymentTransaction = paymentTrans.get(0);

			if (paymentTransaction.getInfo() instanceof CreditCardPaymentInfoModel)
			{
				setCardType(target, paymentTransaction.getInfo());
				setPaymentToken(target, paymentTransaction);
			}

		}

	}

	private void setPaymentToken(final SalesOrderCreateRequest target, final PaymentTransactionModel paymentTransaction)
	{
		if (CollectionUtils.isNotEmpty(paymentTransaction.getEntries())
				&& StringUtils.isNotBlank(paymentTransaction.getEntries().get(0).getRequestId()))
		{
			target.getFreeText().add(getPaymentToken(paymentTransaction.getEntries().get(0).getRequestId()));
		}

	}

	private void setCardType(final SalesOrderCreateRequest target, final PaymentInfoModel paymentInfo)
	{

		final CreditCardPaymentInfoModel cardInfo = (CreditCardPaymentInfoModel) paymentInfo;
		if (cardInfo.getType() != null && StringUtils.isNotBlank(cardInfo.getType().getCode()))
		{
			final String sapCardType = getPaymentCardTypeToken(cardInfo.getType().getCode());
			if (sapCardType != null)
			{
				target.getSalesOrderReqHeader().setCCPaymentFlag(sapCardType);
			}
		}
	}

	private FreeText getPaymentToken(final String requestToken)
	{
		final FreeText paymentTokenText = new FreeText();
		paymentTokenText.setTextID(SabmintegrationConstants.FREE_TEXT_ID_PAYMENT_TOKEN);
		paymentTokenText.setTextLine(requestToken);
		paymentTokenText.setLanguage("EN");
		return paymentTokenText;
	}


	private FreeText getDeliveryInstructionsToken(final AbstractOrderModel cart)
	{
		String deliveryInstructions = cart.getDeliveryInstructions();
		final FreeText deliveryInstructionText = new FreeText();
		deliveryInstructionText.setTextID(SabmintegrationConstants.FREE_TEXT_ID_DELIVERY_INSTRUCTION);
		if (deliveryInstructions.contains("\r\n"))
		{
			deliveryInstructions = deliveryInstructions.replaceAll("\r\n", " ");
		}
		deliveryInstructionText.setTextLine(deliveryInstructions);
		deliveryInstructionText.setLanguage(StringUtils.upperCase(cart.getSite().getDefaultLanguage().getIsocode()));
		return deliveryInstructionText;
	}

	private String getPaymentCardTypeToken(final String hybrisCardType)
	{
		String sapCardType = null;

		if (StringUtils.equalsIgnoreCase(CreditCardType.VISA.getCode(), hybrisCardType))
		{
			sapCardType = SabmintegrationConstants.SAP_VISA_CARD_CODE;
		}
		else if (StringUtils.equalsIgnoreCase(CreditCardType.MASTER.getCode(), hybrisCardType))
		{
			sapCardType = SabmintegrationConstants.SAP_MASTER_CARD_CODE;
		}
		else if (StringUtils.equalsIgnoreCase(CreditCardType.AMEX.getCode(), hybrisCardType))
		{
			sapCardType = SabmintegrationConstants.SAP_AMEX_CARD_CODE;
		}

		return sapCardType;

	}



	public Converter<AbstractOrderModel, SalesOrderReqHeader> getSalesOrderCreateReqHeaderConverter()
	{
		return salesOrderCreateReqHeaderConverter;
	}



	public Converter<AbstractOrderModel, SalesOrderReqPartner> getSalesOrderCreateReqPartnerConverter()
	{
		return salesOrderCreateReqPartnerConverter;
	}



	public Converter<AbstractOrderEntryModel, SalesOrderCreateRequest.SalesOrderReqItem> getSalesOrderCreateReqItemConverter()
	{
		return salesOrderCreateReqItemConverter;
	}



	public void setSalesOrderCreateReqHeaderConverter(
			final Converter<AbstractOrderModel, SalesOrderReqHeader> salesOrderCreateReqHeaderConverter)
	{
		this.salesOrderCreateReqHeaderConverter = salesOrderCreateReqHeaderConverter;
	}



	public void setSalesOrderCreateReqPartnerConverter(
			final Converter<AbstractOrderModel, SalesOrderReqPartner> salesOrderCreateReqPartnerConverter)
	{
		this.salesOrderCreateReqPartnerConverter = salesOrderCreateReqPartnerConverter;
	}



	public void setSalesOrderCreateReqItemConverter(
			final Converter<AbstractOrderEntryModel, SalesOrderCreateRequest.SalesOrderReqItem> salesOrderCreateReqItemConverter)
	{
		this.salesOrderCreateReqItemConverter = salesOrderCreateReqItemConverter;
	}

}