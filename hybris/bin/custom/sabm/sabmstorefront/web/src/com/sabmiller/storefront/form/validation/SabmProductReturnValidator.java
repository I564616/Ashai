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
import com.sabmiller.facades.businessenquiry.data.SabmProductReturnData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Component("sabmProductReturnValidator")
public class SabmProductReturnValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmProductReturnData> sabmProductReturnConverter;

	@Resource
	private ConfigurationService configurationService;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation#validate(com.sabmiller.facades.
	 * businessenquiry.data.AbstractBusinessEnquiryData, java.util.List)
	 */
	@Override
	public void validate(final AbstractBusinessEnquiryData businessEnquiryData, final List<String> errors)
	{
		final SabmProductReturnData sabmProductReturnData = new SabmProductReturnData();

		sabmProductReturnConverter.convert(businessEnquiryData, sabmProductReturnData);

		validateObjectFieldNotNull(sabmProductReturnData.getInvoiceNumber(), SabmProductReturnEnquiry.INVOICE_NUMBER, errors);
		validateObjectFieldNotNull(sabmProductReturnData.getInvoiceDate(), SabmProductReturnEnquiry.INVOICE_DATE, errors);
		validateObjectFieldNotNull(sabmProductReturnData.getProductDescription(), SabmProductReturnEnquiry.PRODUCT_DESC, errors);
		validateObjectFieldNotNull(sabmProductReturnData.getProductQuantity(), SabmProductReturnEnquiry.QUANTITY, errors);
		validateObjectFieldNotNull(sabmProductReturnData.getProductQuantityUOM(), SabmProductReturnEnquiry.QUANTITY_UOM, errors);
		validateObjectFieldNotNull(sabmProductReturnData.getReturnReason(), SabmProductReturnEnquiry.RETURN_REASON, errors);
	}

	private void validateObjectFieldNotNull(final Object field, final SabmProductReturnEnquiry fieldType,
			final List<String> errors)
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

	private enum SabmProductReturnEnquiry
	{
		INVOICE_NUMBER("enquiry.product.return.invoicenumber.invalid", "Invoice number is required."), INVOICE_DATE(
				"enquiry.product.return.invoicedate.invalid", "Invoice date is required."), PRODUCT_DESC(
						"enquiry.product.return.description.invalid", "Product description is required."), QUANTITY(
								"enquiry.product.return.quantity.invalid", "Product quantity is required."), QUANTITY_UOM(
										"enquiry.product.return.quantityuom.invalid", "Product quantity UOM is required."), RETURN_REASON(
												"enquiry.product.return.returnreason.invalid", "Return reason is required.");

		private final String property;
		private final String defaultMsg;

		private SabmProductReturnEnquiry(final String property, final String defaultMsg)
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
