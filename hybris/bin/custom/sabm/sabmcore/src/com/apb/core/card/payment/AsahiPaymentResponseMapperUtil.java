package com.apb.core.card.payment;

import java.text.SimpleDateFormat;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;

import com.apb.core.payment.fz.models.AsahiPaymentPurchase;
import com.apb.core.payment.fz.models.AsahiPaymentResponse;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.facades.card.payment.AsahiPaymentPurchaseResponseData;


/**
 *
 */
public class AsahiPaymentResponseMapperUtil
{

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	private static final String ORDER_DETAIL_CARD_EXPIRY_DATE_PATTERN = "order.detail.card.expiry.date.pattern";
	
	private static final Logger LOG = LoggerFactory.getLogger(AsahiPaymentResponseMapperUtil.class);


	
	/**
	 * @param asahiPaymentResponse
	 * @return
	 */
	public AsahiPaymentPurchaseResponseData mapPaymentPurchaseResponse(
			final AsahiPaymentResponse<AsahiPaymentPurchase> asahiPaymentResponse)
	{
		final AsahiPaymentPurchaseResponseData asahiPaymentPurchaseResponseData = new AsahiPaymentPurchaseResponseData();

		asahiPaymentPurchaseResponseData.setSuccessfulStatus(String.valueOf(asahiPaymentResponse.isSuccessful()));
		asahiPaymentPurchaseResponseData.setResponseErrorMessages(asahiPaymentResponse.getErrors());

		if (null != asahiPaymentResponse.getResult())
		{
			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getAuthorization()))
			{
				asahiPaymentPurchaseResponseData.setAuthorizationId(asahiPaymentResponse.getResult().getAuthorization());
			}
			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getCaptured_amount()))
			{
				asahiPaymentPurchaseResponseData.setCapturedAmount(asahiPaymentResponse.getResult().getCaptured_amount());
			}

			asahiPaymentPurchaseResponseData.setCaptureStatus(String.valueOf(asahiPaymentResponse.getResult().isCaptured()));

			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getCard_category()))
			{
				asahiPaymentPurchaseResponseData.setCardCategory(asahiPaymentResponse.getResult().getCard_category());
			}

			if (null != asahiPaymentResponse.getResult().getCard_expiry())
			{
				SimpleDateFormat sdfr = new SimpleDateFormat(asahiConfigurationService.getString(ORDER_DETAIL_CARD_EXPIRY_DATE_PATTERN, "MMM/yyyy"));
				try
				{
					String dateString = sdfr.format(asahiPaymentResponse.getResult().getCard_expiry());
					asahiPaymentPurchaseResponseData.setCardExpiry(dateString);
				}
				catch(Exception e)
				{
					if(LOG.isDebugEnabled()){
						LOG.debug("Configuration for order detail card expiry date pattern: order.detail.card.expiry.date.pattern is not date parseable pattern", e );
					}
					sdfr = new SimpleDateFormat("MMM/yyyy");
					asahiPaymentPurchaseResponseData.setCardExpiry(sdfr.format(asahiPaymentResponse.getResult().getCard_expiry()));
				}
				
			}

			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getCard_holder()))
			{
				asahiPaymentPurchaseResponseData.setCardHolder(asahiPaymentResponse.getResult().getCard_holder());
			}

			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getCard_number()))
			{
				asahiPaymentPurchaseResponseData.setCardNumber(getMaskedCardNumber(asahiPaymentResponse.getResult().getCard_number()));
			}

			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getCard_subcategory()))
			{
				asahiPaymentPurchaseResponseData.setCardSubCategory(asahiPaymentResponse.getResult().getCard_subcategory());
			}

			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getCard_type()))
			{
				asahiPaymentPurchaseResponseData.setCardType(asahiPaymentResponse.getResult().getCard_type());
			}

			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getCurrency()))
			{
				asahiPaymentPurchaseResponseData.setCurrencyCode(asahiPaymentResponse.getResult().getCurrency());
			}

			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getCvv_match()))
			{
				asahiPaymentPurchaseResponseData.setCvvMatch(asahiPaymentResponse.getResult().getCvv_match());
			}
			asahiPaymentPurchaseResponseData.setDecimalAmount(String.valueOf(asahiPaymentResponse.getResult().getDecimal_amount()));
			asahiPaymentPurchaseResponseData.setRequestAmount(String.valueOf(asahiPaymentResponse.getResult().getAmount()));

			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getReference()))
			{
				asahiPaymentPurchaseResponseData.setRequestedReference(asahiPaymentResponse.getResult().getReference());
			}
			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getResponse_code()))
			{
				asahiPaymentPurchaseResponseData.setResponseCode(asahiPaymentResponse.getResult().getResponse_code());
			}
			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getId()))
			{
				asahiPaymentPurchaseResponseData.setResponseId(asahiPaymentResponse.getResult().getId());
			}

			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getMessage()))
			{
				asahiPaymentPurchaseResponseData.setResponseMessage(asahiPaymentResponse.getResult().getMessage());
			}

			asahiPaymentPurchaseResponseData.setResponsePayload(asahiPaymentResponse.getResult().toString());

			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getRrn()))
			{
				asahiPaymentPurchaseResponseData.setRrn(asahiPaymentResponse.getResult().getRrn());
			}
			if (null != asahiPaymentResponse.getResult().getSettlement_date())
			{
				asahiPaymentPurchaseResponseData.setSettlementDate(asahiPaymentResponse.getResult().getSettlement_date().toString());
			}
			if (null != asahiPaymentResponse.getResult().getTransaction_date())
			{
				asahiPaymentPurchaseResponseData
						.setTransactionDate(asahiPaymentResponse.getResult().getTransaction_date().toString());
			}
			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getTransaction_id()))
			{
				asahiPaymentPurchaseResponseData.setTransactionId(asahiPaymentResponse.getResult().getTransaction_id());
			}
			if (StringUtils.isNotEmpty(asahiPaymentResponse.getResult().getCard_token()))
			{
				asahiPaymentPurchaseResponseData.setCardToken(asahiPaymentResponse.getResult().getCard_token());
			}
		}

		return asahiPaymentPurchaseResponseData;
	}
	
	protected String getMaskedCardNumber(final String cardNumber)
	{
		if (cardNumber != null && cardNumber.trim().length() > 4)
		{
			final String endPortion = cardNumber.trim().substring(cardNumber.length() - 4);
			return "************" + endPortion;
		}
		return null;
	}

}
