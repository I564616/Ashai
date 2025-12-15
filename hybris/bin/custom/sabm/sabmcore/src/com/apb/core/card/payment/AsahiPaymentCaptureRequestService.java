package com.apb.core.card.payment;

import de.hybris.platform.core.model.order.OrderModel;

import java.util.HashMap;

import com.sabmiller.core.model.AsahiSAMInvoiceModel;
import com.apb.facades.sam.data.AsahiCaptureResponseData;


/**
 * Capture Request Interface.
 */
public interface AsahiPaymentCaptureRequestService
{

	/**
	 * @param orderModel
	 * @return
	 */
	HashMap<String, String> placeCaptureRequest(OrderModel orderModel);
	
	/**
	 * @param orderModel
	 *           This method will setup the capture request for SGA Site.
	 */
	void createCaptureRequest(OrderModel orderModel);

	/**
	 * This Method will make capture request for SAM Payment
	 *
	 * @param asahiSAMInvoiceModel
	 * @return The Capture Response Data
	 */
	AsahiCaptureResponseData createSAMPaymentCaptureRequest(AsahiSAMInvoiceModel asahiSAMInvoiceModel);

}
