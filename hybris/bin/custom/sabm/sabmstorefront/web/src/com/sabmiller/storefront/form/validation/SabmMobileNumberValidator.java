package com.sabmiller.storefront.form.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sabmiller.storefront.form.SABMNotificationForms;
import com.sabmiller.storefront.form.UpdateProfileForm;

/**
 * Created by zhuo.a.jiang on 8/02/2018.
 */

@Component("sabmMobileNumberValidator")
public class SabmMobileNumberValidator implements Validator
{


    public static final Pattern MOBILE_REGEX = Pattern.compile("^04[0-9]{8}$");
    public static final Pattern BUSINESS_MOBILE_REGEX = Pattern.compile("^[0-9]{10}$");

    @Override
    public boolean supports(final Class<?> aClass) {
        return SabmMobileNumberValidator.class.equals(aClass);
    }

    @Override
    public void validate(final Object object, final Errors errors) {


        if(object instanceof SABMNotificationForms )
        {
            validateSABMNotificationFormMobile((SABMNotificationForms)object,errors);
        }

        if(object instanceof UpdateProfileForm)
        {
            validateUpdateProfileFormMobile((UpdateProfileForm)object,errors);
        }

    }



    private void validateSABMNotificationFormMobile(final SABMNotificationForms form,final Errors errors){

        final String mobilePhone = form.getMobileNumber();


        if (StringUtils.isEmpty(mobilePhone)) {
            errors.rejectValue("mobileNumber", "address.phone.invalid ");
            return;
        }
        if (StringUtils.isNotEmpty(mobilePhone)) {
            final Matcher matcher = MOBILE_REGEX.matcher(StringUtils.deleteWhitespace(mobilePhone));
            if (!matcher.matches()) {
                errors.rejectValue("mobileNumber", "address.phone.invalid");
            }
        }
    }

    private void validateUpdateProfileFormMobile(final UpdateProfileForm form,final Errors errors){

        final String mobilePhone = form.getMobileNumber();
        final String businessPhone = form.getBusinessPhoneNumber();


        if (StringUtils.isNotEmpty(mobilePhone)) {
            final Matcher matcher1 = MOBILE_REGEX.matcher(StringUtils.deleteWhitespace(mobilePhone));
            if (!matcher1.matches()) {
                errors.rejectValue("mobileNumber", "address.phone.invalid");
            }
        }
        if (StringUtils.isNotEmpty(businessPhone)) {
            final Matcher matcher2 = BUSINESS_MOBILE_REGEX.matcher(StringUtils.deleteWhitespace(businessPhone));
            if (!matcher2.matches()) {
                errors.rejectValue("businessPhoneNumber", "address.businessphone.invalid");
            }
        }
    }

}
