/**
 *
 */
package com.sabmiller.storefront.form.validation;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmDeliveryEnquiryData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Component("sabmDeliveryEnquiryValidator")
public class SabmDeliveryEnquiryValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmDeliveryEnquiryData> sabmDeliveryEnquiryDataConverter;

	@Resource
	private ConfigurationService configurationService;

	@Override
	public void validate(final AbstractBusinessEnquiryData businessEnquiryData, final List<String> errors)
	{
		final SabmDeliveryEnquiryData sabmDeliveryEnquiryData = new SabmDeliveryEnquiryData();

		sabmDeliveryEnquiryDataConverter.convert(businessEnquiryData, sabmDeliveryEnquiryData);

		validateObjectFieldNotNull(sabmDeliveryEnquiryData.getOrderNumber(), SabmDeliveryField.ORDER_NUMBER, errors);
		validateObjectFieldNotNull(sabmDeliveryEnquiryData.getOrderDate(), SabmDeliveryField.ORDER_DATE, errors);
		validateObjectFieldNotNull(sabmDeliveryEnquiryData.getExpectedDeliveryDate(), SabmDeliveryField.EXPECTED_DELIVERY_DATE,
				errors);
	}

	private void validateObjectFieldNotNull(final Object field, final SabmDeliveryField fieldType, final List<String> errors)
	{
		if (field instanceof String)
		{
			if (StringUtils.isEmpty((String) field))
			{
				errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
			}
		}
		else
		{
			if (field == null)
			{
				errors.add(configurationService.getConfiguration().getString(fieldType.getProperty(), fieldType.getDefaultMsg()));
			}
		}
	}

	private enum SabmDeliveryField
	{
		ORDER_NUMBER("enquiry.delivery.ordernumber.invalid", "Order number is required."), ORDER_DATE(
				"enquiry.delivery.orderdate.invalid", "Order date is required."), EXPECTED_DELIVERY_DATE(
						"enquiry.delivery.expecteddate.invalid", "Expected date is required.");

		private final String property;
		private final String defaultMsg;

		private SabmDeliveryField(final String property, final String defaultMsg)
		{
			this.property = property;
			this.defaultMsg = defaultMsg;
		}

		public String getProperty()
		{
			return property;
		}

		public String getDefaultMsg()
		{
			return defaultMsg;
		}
	}
}
