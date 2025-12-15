package com.apb.facades.process.email.context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.AsahiPaymentConfirmationProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Velocity context for a invoice payment confirmation email.
 */

public class AsahiPaymentConfirmationEmailContext extends AbstractEmailContext<AsahiPaymentConfirmationProcessModel>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AsahiPaymentConfirmationEmailContext.class);
	
	@Resource(name = "sessionService")
	private SessionService sessionService;

	
	@Override
	public void init(final AsahiPaymentConfirmationProcessModel asahiPaymentConfirmationProcessModel , final EmailPageModel emailPageModel)
	{
		super.init(asahiPaymentConfirmationProcessModel, emailPageModel);
		final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) asahiPaymentConfirmationProcessModel.getCustomer();
		put("asahiUserName", b2bCustomerModel.getName());
		put("amountPaid", asahiPaymentConfirmationProcessModel.getAmountPaid());
		put("paymentDate", getDayAndDate(asahiPaymentConfirmationProcessModel.getPaymentDate()));
		put("referenceNo", asahiPaymentConfirmationProcessModel.getReferenceNo());
		put("paymentReference", asahiPaymentConfirmationProcessModel.getPaymentReference());
		put("paymentMethod", asahiPaymentConfirmationProcessModel.getPaymentMethod());
		put("invoices", asahiPaymentConfirmationProcessModel.getAsahiSAMInvoices());
		B2BUnitModel b2bUnitModel = b2bCustomerModel.getDefaultB2BUnit();
		if(null != b2bUnitModel && b2bUnitModel instanceof AsahiB2BUnitModel){
			AsahiB2BUnitModel payerAccount  = ((AsahiB2BUnitModel)b2bUnitModel).getPayerAccount();
			put("customerName", payerAccount.getLocName());
			put("customerId", payerAccount.getAccountNum());
		}
		put(EMAIL, b2bCustomerModel.getUid());
		put("invoicePageUrl", getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(),getUrlEncodingAttributes(), false, "/invoice"));
		put("samPaymentHistoryUrl", getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(),getUrlEncodingAttributes(), false, "/paymentHistory"));
	}

	private String getDayAndDate(final String dateStr) {
		try {
			if(StringUtils.isNotEmpty(dateStr)){
				final Date date = new SimpleDateFormat(ApbCoreConstants.SAM_DOCUMENT_HYBRIS_DATEPATTERN).parse(dateStr);
				final String day = new SimpleDateFormat(ApbCoreConstants.DEFER_DELIVERY_DATEPATTERN).format(date);
				return day;
			}
			
		} catch (ParseException e) {
			LOGGER.error("Parse Exception caught in converting to date" + e.getMessage());
		}
		return dateStr;
	}

	@Override
	protected BaseSiteModel getSite(final AsahiPaymentConfirmationProcessModel businessProcessModel) {
		return businessProcessModel.getSite();
	}

	@Override
	protected LanguageModel getEmailLanguage(final AsahiPaymentConfirmationProcessModel businessProcessModel) {
		return businessProcessModel.getLanguage();
	}

	@Override
	protected CustomerModel getCustomer(final AsahiPaymentConfirmationProcessModel businessProcessModel) {
		return businessProcessModel.getCustomer();
	}	
	
}
