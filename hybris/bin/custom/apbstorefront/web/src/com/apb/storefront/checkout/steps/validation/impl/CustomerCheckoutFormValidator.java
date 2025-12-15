package com.apb.storefront.checkout.steps.validation.impl;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sabmiller.core.enums.DeliveryMethodType;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.storefront.checkout.form.CustomerCheckoutForm;
import com.apb.storefront.checkout.form.DeliveryMethodForm;

import de.hybris.platform.commercefacades.user.data.AddressData;


@Component("customerCheckoutFormValidator")
public class CustomerCheckoutFormValidator implements Validator
{
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	private static final int MAX_FIELD_LENGTH = 255;
	private static final int MAX_POSTCODE_LENGTH = 10;

	@Override
	public boolean supports(Class<?> aClass) 
	{
		return CustomerCheckoutForm.class.equals(aClass);
	}

	@Override
	public void validate(Object object, Errors errors) 
	{
		final CustomerCheckoutForm customerCheckoutForm = (CustomerCheckoutForm) object;
		validateAddressFields(customerCheckoutForm.getDeliveryAddressId(), errors);
		validateDeliveryMethod(customerCheckoutForm.getDeliveryMethod(), errors);
		validatePaymentMethod(customerCheckoutForm.getPaymentMethod(), errors);
				
	}	

	private void validatePaymentMethod(String paymentType, Errors errors) 
	{
		if (StringUtils.isBlank(paymentType))
		{
			errors.rejectValue("paymentMethod", "general.required");
		}
		
	}

	private void validateDeliveryMethod(DeliveryMethodForm deliveryMethodForm, Errors errors) 
	{
		
			if(asahiSiteUtil.isSga()){
				if (StringUtils.isBlank(deliveryMethodForm.getDeferredDeliveryDate()))
				{
					errors.rejectValue("deliveryMethod.deferredDeliveryDate", "general.required");
				}
				
			}
			else if (DeliveryMethodType.DEFERRED.getCode().equals(deliveryMethodForm.getDeliveryType())
					&& StringUtils.isBlank(deliveryMethodForm.getDeferredDeliveryDate()))
				{
			errors.rejectValue("deliveryMethod.deferredDeliveryDate", "general.required");
				}
	}

	private void validateAddressFields(String deliveryAddressId, Errors errors) {
		if(StringUtils.isBlank(deliveryAddressId))
		{
			errors.rejectValue("deliveryAddressId", "general.required");
		}
	}
	
	protected void validateStandardFields(final AddressData address, final Errors errors)
	{
		if(null != address.getCountry()){
			validateStringField(address.getCountry().getIsocode(), CustomerCheckoutField.COUNTRY, MAX_FIELD_LENGTH, errors);
		}
		validateStringField(address.getFirstName(), CustomerCheckoutField.FIRSTNAME, MAX_FIELD_LENGTH, errors);
		validateStringField(address.getLastName(), CustomerCheckoutField.LASTNAME, MAX_FIELD_LENGTH, errors);
		validateStringField(address.getLine1(), CustomerCheckoutField.LINE1, MAX_FIELD_LENGTH, errors);
		validateStringField(address.getTown(), CustomerCheckoutField.TOWN, MAX_FIELD_LENGTH, errors);
		validateStringField(address.getPostalCode(), CustomerCheckoutField.POSTCODE, MAX_POSTCODE_LENGTH, errors);
	}

	protected static void validateStringField(final String addressField, final CustomerCheckoutField fieldType,
			  final int maxFieldLength, final Errors errors)
	{
		if (addressField == null || StringUtils.isEmpty(addressField) || (StringUtils.length(addressField) > maxFieldLength))
		{
			errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
		}
	}
	
	protected static void validateFieldNotNull(final String addressField, final CustomerCheckoutField fieldType,
				   final Errors errors)
	{
		if (addressField == null)
		{
			errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
		}
	}
		
	protected enum CustomerCheckoutField
	{
		TITLE("deliveryAddress.titleCode", "address.title.invalid"), FIRSTNAME("deliveryAddress.firstName", "address.firstName.invalid"),
		LASTNAME("deliveryAddress.lastName", "address.lastName.invalid"), LINE1("deliveryAddress.line1", "address.line1.invalid"),
		LINE2("deliveryAddress.line2", "address.line2.invalid"), TOWN("deliveryAddress.town", "address.townCity.invalid"),
		POSTCODE("deliveryAddress.postalCode", "address.postcode.invalid"), REGION("deliveryAddress.region.isocode", "address.regionIso.invalid"),
		COUNTRY("deliveryAddress.country.isocode", "address.country.invalid");
		
		private String fieldKey;
		private String errorKey;
		
		private CustomerCheckoutField(final String fieldKey, final String errorKey)
		{
			this.fieldKey = fieldKey;
			this.errorKey = errorKey;
		}
		
		public String getFieldKey()
		{
			return fieldKey;
		}
		
		public String getErrorKey()
		{
			return errorKey;
		}
	}

}