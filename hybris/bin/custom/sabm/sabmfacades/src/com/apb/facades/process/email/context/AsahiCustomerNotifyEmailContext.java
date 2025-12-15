package com.apb.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.AsahiCustomerNotifyProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.service.config.AsahiConfigurationService;


public class AsahiCustomerNotifyEmailContext extends AbstractEmailContext<AsahiCustomerNotifyProcessModel>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AsahiCustomerNotifyEmailContext.class);
	
	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	@Override
	public void init(final AsahiCustomerNotifyProcessModel asahiCustomerNotifyProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(asahiCustomerNotifyProcessModel, emailPageModel);
		final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) asahiCustomerNotifyProcessModel.getCustomer();
		put("asahiUserName", b2bCustomerModel.getName());
		put("holidayText", asahiCustomerNotifyProcessModel.getHoliday());
		put("orderDate", getDayAndDate(asahiCustomerNotifyProcessModel.getCutOffDate()));
		put("deliveryDate", getDayAndDate(asahiCustomerNotifyProcessModel.getDeliveryDate()));
		put(EMAIL, b2bCustomerModel.getUid());
	}

	private String getDayAndDate(final String dateStr) {
		try {
			final Date originalDate = new SimpleDateFormat(ApbCoreConstants.DATE_PATTERN_DDMMYYYY).parse(dateStr);
			final String requiredDateFormat = asahiConfigurationService.getString("sga.customer.notification.date.format", ApbCoreConstants.DEFER_DELIVERY_DATEPATTERN);
			final DateFormat targetFormat = new SimpleDateFormat(requiredDateFormat);
			return targetFormat.format(originalDate);
		} catch (ParseException e) {
			LOGGER.error("Parse Exception caught in converting to date" + e.getMessage());
		}
		return dateStr;
	}

	@Override
	protected BaseSiteModel getSite(final AsahiCustomerNotifyProcessModel businessProcessModel) {
		return businessProcessModel.getSite();
	}

	@Override
	protected LanguageModel getEmailLanguage(final AsahiCustomerNotifyProcessModel businessProcessModel) {
		return businessProcessModel.getLanguage();
	}

	@Override
	protected CustomerModel getCustomer(final AsahiCustomerNotifyProcessModel businessProcessModel) {
		return businessProcessModel.getCustomer();
	}	
	
}
