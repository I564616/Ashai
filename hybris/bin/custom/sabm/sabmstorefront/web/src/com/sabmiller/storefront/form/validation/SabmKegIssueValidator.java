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
import com.sabmiller.facades.businessenquiry.data.SabmKegIssueData;
import com.sabmiller.storefront.form.validation.service.SabmBusinessEnquiryValidation;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Component("sabmKegIssueValidator")
public class SabmKegIssueValidator implements SabmBusinessEnquiryValidation
{
	@Resource
	private Converter<AbstractBusinessEnquiryData, SabmKegIssueData> sabmKegIssueConverter;

	@Resource
	private ConfigurationService configurationService;

	private static int MAX_LENGTH = 7;

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
		final SabmKegIssueData sabmKegIssueData = new SabmKegIssueData();

		sabmKegIssueConverter.convert(businessEnquiryData, sabmKegIssueData);

		validateObjectFieldNotNull(sabmKegIssueData.getKegBrand(), SabmKegIssueField.KEG_BRAND, null, errors);
		validateObjectFieldNotNull(sabmKegIssueData.getKegNumber(), SabmKegIssueField.KEG_NUMBER,
				Integer.valueOf(SabmKegIssueValidator.MAX_LENGTH), errors);
		if ("Yes".equalsIgnoreCase(sabmKegIssueData.getBestBeforeDateAvailable()))
		{
			validateObjectFieldNotNull(sabmKegIssueData.getBestBeforeDate(), SabmKegIssueField.BEST_BEFORE_DATE, null, errors);
		}
		validateObjectFieldNotNull(sabmKegIssueData.getKegProblem(), SabmKegIssueField.KEG_PROBLEM, null, errors);
		validateObjectFieldNotNull(sabmKegIssueData.getReasonCode(), SabmKegIssueField.REASON_CODE, null, errors);

	}

	private void validateObjectFieldNotNull(final Object field, final SabmKegIssueField fieldType, final Integer fieldLength,
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
			else if (fieldLength != null)
			{
				if (field.toString().length() > fieldLength.intValue())
				{
					errors.add(configurationService.getConfiguration().getString(SabmKegIssueField.KEG_LENGTH_EXCEEDED.getProperty(),
							SabmKegIssueField.KEG_LENGTH_EXCEEDED.getDefaultMsg()));
				}
			}
		}
	}

	private enum SabmKegIssueField
	{
		KEG_BRAND("enquiry.keg.issue.brand.invalid", "Keg brand is required."), KEG_NUMBER("enquiry.keg.issue.number.invalid",
				"Keg number is required."), BEST_BEFORE_DATE("enquiry.keg.issue.bestbefore.invalid",
						"Best before date is required."), KEG_LENGTH_EXCEEDED("enquiry.keg.issue.length.exceeded",
								"Keg number length exceeded."), KEG_PROBLEM("enquiry.keg.issue.problem.invalid",
										"Perceived problem with keg is required."), REASON_CODE("enquiry.keg.issue.reason.code.invalid",
											"Reason code is required.");

		private final String property;
		private final String defaultMsg;

		private SabmKegIssueField(final String property, final String defaultMsg)
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
