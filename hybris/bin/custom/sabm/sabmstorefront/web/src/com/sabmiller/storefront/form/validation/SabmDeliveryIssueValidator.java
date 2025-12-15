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
import com.sabmiller.facades.businessenquiry.data.SabmDeliveryIssueData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Component("sabmDeliveryIssueValidator")
public class SabmDeliveryIssueValidator implements SabmBusinessEnquiryValidation
{

	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmDeliveryIssueData> sabmDeliveryIssueConverter;

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
		final SabmDeliveryIssueData sabmDeliveryIssueData = new SabmDeliveryIssueData();

		sabmDeliveryIssueConverter.convert(businessEnquiryData, sabmDeliveryIssueData);

		validateObjectFieldNotNull(sabmDeliveryIssueData.getInvoiceNumber(), SabmDeliveryIssueField.INVOICE_NUMBER, errors);
		validateObjectFieldNotNull(sabmDeliveryIssueData.getInvoiceDate(), SabmDeliveryIssueField.INVOICE_DATE, errors);
		//validateObjectFieldNotNull(sabmDeliveryIssueData.getProduct(), SabmDeliveryIssueField.PRODUCT, errors);
		//validateObjectFieldNotNull(sabmDeliveryIssueData.getQuantity(), SabmDeliveryIssueField.QUANTITY, errors);
		//validateObjectFieldNotNull(sabmDeliveryIssueData.getQuantityUOM(), SabmDeliveryIssueField.QUANTITY_UOM, errors);
		validateObjectFieldNotNull(sabmDeliveryIssueData.getDamagePremise(), SabmDeliveryIssueField.DAMAGE_PREMISE, errors);
		validateObjectFieldNotNull(sabmDeliveryIssueData.getDamageStock(), SabmDeliveryIssueField.DAMAGE_STOCK, errors);
		validateObjectFieldNotNull(sabmDeliveryIssueData.getDriverComplaint(), SabmDeliveryIssueField.DRIVER_COMPLAINT, errors);
		validateObjectFieldNotNull(sabmDeliveryIssueData.getKegsNotCollected(), SabmDeliveryIssueField.KEGS_NOT_COLLECTED, errors);
		validateObjectFieldNotNull(sabmDeliveryIssueData.getNotAllItemsDelivered(), SabmDeliveryIssueField.ITEMS_NOT_DELIVERED,
				errors);
		validateObjectFieldNotNull(sabmDeliveryIssueData.getPickingError(), SabmDeliveryIssueField.PICKING_ERROR, errors);
		validateObjectFieldNotNull(sabmDeliveryIssueData.getOther(), SabmDeliveryIssueField.OTHER, errors);
	}

	private void validateObjectFieldNotNull(final Object field, final SabmDeliveryIssueField fieldType, final List<String> errors)
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

	private enum SabmDeliveryIssueField
	{
		INVOICE_NUMBER("enquiry.delivery.issue.invoicenumber.invalid", "Invoice number is required."), INVOICE_DATE(
				"enquiry.delivery.issue.invoicedate.invalid",
				"Invoice date is required."), PRODUCT("enquiry.delivery.issue.product.invalid", "Product is required."), QUANTITY(
						"enquiry.delivery.issue.quantity.invalid",
						"Quantity is required."), QUANTITY_UOM("enquiry.delivery.issue.quantityuom.invalid",
								"Quantity UOM is required."), DAMAGE_PREMISE("enquiry.delivery.issue.premise.invalid",
										"Damage premise is required."), DAMAGE_STOCK("enquiry.delivery.issue.stock.invalid",
												"Damage stock is required."), DRIVER_COMPLAINT("enquiry.delivery.issue.driver.invalid",
														"Driver complaint is required."), KEGS_NOT_COLLECTED(
																"enquiry.delivery.issue.kegs.invalid",
																"Kegs not collected is required."), ITEMS_NOT_DELIVERED(
																		"enquiry.delivery.issue.notdelivered.invalid",
																		"Items not delivered is required."), PICKING_ERROR(
																				"enquiry.delivery.issue.picking.invalid",
																				"Picking error is required."), OTHER(
																						"enquiry.delivery.issue.other.invalid", "Other is required.");

		private final String property;
		private final String defaultMsg;

		private SabmDeliveryIssueField(final String property, final String defaultMsg)
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
