package com.apb.storefront.validators;

import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.RegistrationValidator;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.ApbXSSEncoderUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.storefront.data.AlbCompanyInfoData;
import com.apb.storefront.forms.ApbRegisterForm;


/**
 * Validate Apb Registration form
 */
@Component("apbRegistrationValidator")
public class ApbRegistrationValidator extends RegistrationValidator
{
	private static final Logger LOG = LoggerFactory.getLogger("ApbRegistrationValidator");

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/**
	 * Characters and Numeric pattern
	 */
	public static final String CHARACTER_NUMERIC = "customer.character.numeric.pattern.";
	/**
	 * Default Characters and Numeric pattern from ConfigurationItem
	 */
	public static final String DEFAULT_CHARACTER_NUMERIC = "^[a-zA-Z0-9_]+$";
	/**
	 * Password pattern calling from ConfigurationItem
	 */
	public static final String PWD = "storefront.passwordPattern.";

	/**
	 * ABN number validation pattern
	 */
	public static final String ABN_VALIDATION = "customer.abn.validation.pattern.";
	/**
	 * Default ABN number validation pattern from ConfigurationItem
	 */
	public static final String DEFAULT_ABN_VALIDATION = "^\\d{11,11}$";


	public static final String ACCOUNT_ID_PATTERN = "customer.account.id.pattern.";

	public static final String DEFAULT_ACCOUNT_ID_PATTERN = "^0[a-zA-Z0-9_]+$";

	public static final String ACCOUNT_ID_STARTS_WITH = "customer.account.id.starts.with.";

	public static final String DEFAULT_ACCOUNT_ID_STARTS_WITH = "0";


	@Autowired
	private CMSSiteService cmsSiteService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/**
	 * Default Password Pattern
	 */
	public static final String DEFAULT_PWD = "^(?=.*\\d)(?=.*[a-zA-Z])(?!.*\\s).{5,10}$";

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final ApbRegisterForm registerForm = (ApbRegisterForm) object;
		registerForm.setTermsCheck(true);
		super.validate(object, errors);
		validateSiteSpecificChecks(errors, registerForm);
		validateEmailAddress(errors, registerForm);
		
		if (!registerForm.isTermsCondition())
		{
			errors.rejectValue("termsCondition", "register.term.condition.check");
		}
		if (StringUtils.isEmpty(registerForm.getCheckPwd()))
		{
			errors.rejectValue("checkPwd", "register.pwd.invalid");
		}
		if (StringUtils.isNotEmpty(registerForm.getCheckPwd()) && !validatePasswordPattern(registerForm.getPwd()))
		{
			errors.rejectValue("checkPwd", "register.pwd.invalid");
		}
	}
	
	protected void validateSiteSpecificChecks(final Errors errors, final ApbRegisterForm registerForm) {
		if(asahiSiteUtil.isSga()) {
			if(CollectionUtils.isNotEmpty(registerForm.getAlbCompanyInfoData())) {
				int counter=0;
				Iterator<AlbCompanyInfoData> comapnyInfoIterator = registerForm.getAlbCompanyInfoData().iterator();
				while(comapnyInfoIterator.hasNext()) {
					final AlbCompanyInfoData companyInfo = comapnyInfoIterator.next();
					if (StringUtils.isEmpty(companyInfo.getAbnNumber()) || !validateValue(companyInfo.getAbnNumber())
							|| !validateAbnNumber(companyInfo.getAbnNumber()))
					{
						LOG.debug("Abn Number " +ApbXSSEncoderUtil.encodeValue(companyInfo.getAbnNumber()));
						errors.rejectValue("albCompanyInfoData[" + counter + "].abnNumber", "register.abn.invalid");
					}
					if ((StringUtils.isEmpty(companyInfo.getAbnAccountId()) || !validateValue(companyInfo.getAbnAccountId())))
					{
						LOG.debug("Abn Account Id " + ApbXSSEncoderUtil.encodeValue(companyInfo.getAbnAccountId()));
						errors.rejectValue("albCompanyInfoData[" + counter + "].abnAccountId", "register.alb.account.id.invalid");
					}

					if(StringUtils.isEmpty(companyInfo.getSamAccess()) )
					{
						errors.rejectValue("albCompanyInfoData[" + counter + "].samAccess", "register.samAccess.invalid");
					}
					//update account number with leading zero for sga specific customer
					if (StringUtils.isNotEmpty(companyInfo.getAbnAccountId()))
					{
						updateAccountNumber(companyInfo);
					}
					counter++;
				}	
			} else {
				errors.rejectValue("albCompanyInfoData[0].abnNumber", "register.abn.invalid");
				errors.rejectValue("albCompanyInfoData[0].abnAccountId", "register.alb.account.id.invalid");
				errors.rejectValue("albCompanyInfoData[0].samAccess", "register.samAccess.invalid");
			}
		}else {
			if (StringUtils.isEmpty(registerForm.getAbnNumber()) || !validateValue(registerForm.getAbnNumber())
					|| !validateAbnNumber(registerForm.getAbnNumber()))
			{
				LOG.debug("Abn Number " +ApbXSSEncoderUtil.encodeValue(registerForm.getAbnNumber()));
				errors.rejectValue("abnNumber", "register.abn.invalid");
			}
			if ((StringUtils.isEmpty(registerForm.getAbnAccountId()) || !validateValue(registerForm.getAbnAccountId())))
			{
				LOG.debug("Abn Account Id " + ApbXSSEncoderUtil.encodeValue(registerForm.getAbnAccountId()));
				errors.rejectValue("abnAccountId", "register.apb.account.id.invalid");
			}
			if (StringUtils.isEmpty(registerForm.getRole()))
			{
				errors.rejectValue("role", "register.role.select");
			}
			if (StringUtils.isEmpty(registerForm.getRoleOther()) && StringUtils.isNotEmpty(registerForm.getRoleOtherTemp()))
			{
				errors.rejectValue("roleOther", "registerRoleOther.invalid");
			}
		}
	}


	protected void validateEmailAddress(final Errors errors, final ApbRegisterForm registerForm)
	{

		if (StringUtils.isNotEmpty(registerForm.getEmail()) && StringUtils.isNotEmpty(registerForm.getConfirmEmail())
				&& !StringUtils.equals(registerForm.getEmail(), registerForm.getConfirmEmail()))
		{
			errors.rejectValue("confirmEmail", "register.email.confirm.invalid");
		}
		else
		{
			if (StringUtils.isEmpty(registerForm.getConfirmEmail()))
			{
				errors.rejectValue("confirmEmail", "register.email.confirm.match.invalid");
			}
		}
	}

	/**
	 * @param errors
	 * @param titleCode
	 * @see validate the title code.
	 */
	@Override
	protected void validateTitleCode(final Errors errors, final String titleCode)
	{
		if (!asahiSiteUtil.isSga())
		{
			if (StringUtils.isEmpty(titleCode))
			{
				errors.rejectValue("titleCode", "register.title.invalid");
			}
			else if (StringUtils.length(titleCode) > 255)
			{
				errors.rejectValue("titleCode", "register.title.invalid");
			}
		}
	}

	protected boolean validateAbnNumber(final String value)
	{
		final Pattern apnValidator = Pattern.compile(this.asahiConfigurationService
				.getString(ABN_VALIDATION + cmsSiteService.getCurrentSite().getUid(), DEFAULT_ABN_VALIDATION));
		final Matcher matcher = apnValidator.matcher(value);
		return matcher.matches();
	}

	/**
	 * @param value
	 * @return
	 */
	public boolean validateValue(final String value)
	{
		final Pattern charNumeric = Pattern.compile(this.asahiConfigurationService
				.getString(CHARACTER_NUMERIC + cmsSiteService.getCurrentSite().getUid(), DEFAULT_CHARACTER_NUMERIC));
		final Matcher matcher = charNumeric.matcher(value);
		return matcher.matches();
	}

	@Override
	protected void validatePassword(final Errors errors, final String pwd)
	{
		if (StringUtils.isEmpty(pwd))
		{
			errors.rejectValue("pwd", "register.pwd.invalid");
		}
		if (StringUtils.isNotEmpty(pwd) && !validatePasswordPattern(pwd))
		{
			errors.rejectValue("pwd", "register.pwd.case.sensentive.invalid");
		}
	}

	protected boolean validatePasswordPattern(final String pwd)
	{
		final String pwdPattern = this.asahiConfigurationService.getString(PWD + cmsSiteService.getCurrentSite().getUid(),
				DEFAULT_PWD);
		final Pattern pattern = Pattern.compile(pwdPattern);
		final Matcher matcher = pattern.matcher(pwd);
		return matcher.matches();
	}

	@Override
	protected void comparePasswords(final Errors errors, final String pwd, final String checkPwd)
	{
		if (validatePasswordPattern(pwd)
				&& (StringUtils.isNotEmpty(pwd) && StringUtils.isNotEmpty(checkPwd) && !StringUtils.equals(pwd, checkPwd)))
		{
			errors.rejectValue("checkPwd", "validation.checkPwd.equals");
		}
	}

	/**
	 * <p>
	 * This method will update the account number for sga specific accounts.
	 * <p>
	 *
	 * @param companyInfo
	 */
	private void updateAccountNumber(final AlbCompanyInfoData companyInfo)
	{
		final String accountIdPattern = this.asahiConfigurationService
				.getString(ACCOUNT_ID_PATTERN + cmsSiteService.getCurrentSite().getUid(), DEFAULT_ACCOUNT_ID_PATTERN);

		final Pattern pattern = Pattern.compile(accountIdPattern);

		final Matcher matcher = pattern.matcher(companyInfo.getAbnAccountId());
		if (!matcher.matches())
		{
			companyInfo.setAbnAccountId(
					this.asahiConfigurationService.getString(ACCOUNT_ID_STARTS_WITH + cmsSiteService.getCurrentSite().getUid(),
							DEFAULT_ACCOUNT_ID_STARTS_WITH) + companyInfo.getAbnAccountId());
		}
	}
}
