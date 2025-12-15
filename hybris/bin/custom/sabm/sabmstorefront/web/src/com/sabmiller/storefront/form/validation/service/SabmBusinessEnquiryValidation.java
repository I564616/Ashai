/**
 *
 */
package com.sabmiller.storefront.form.validation.service;

import java.util.List;

import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;


/**
 *
 */
public interface SabmBusinessEnquiryValidation
{
	public void validate(final AbstractBusinessEnquiryData businessEnquiryData, final List<String> errors);
}
