/**
 *
 */
package com.sabmiller.core.jalo;

import de.hybris.platform.jalo.security.PasswordEncoderNotFoundException;
import de.hybris.platform.jalo.user.Employee;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.b2b.services.SabmChangePasswordHistoryService;
import com.sabmiller.core.model.UserPasswordHistoryModel;


/**
 * The Class SabmEmployee.
 */
public class SabmEmployee extends Employee
{

	/** The model service. */
	@Resource
	ModelService modelService;

	/** The sabm change password history service. */
	@Resource
	SabmChangePasswordHistoryService sabmChangePasswordHistoryService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.jalo.user.User#setPassword(java.lang.String, java.lang.String)
	 */
	@Override
	public void setPassword(final String password, final String encoding) throws PasswordEncoderNotFoundException
	{

		if (!validatePassword(password))
		{

			throw new PasswordEncoderNotFoundException(Config.getString("employee.reset.password.format.message", ""), 0);
		}

		if (isMatchingWithOldPasswords(password))
		{
			throw new PasswordEncoderNotFoundException(Config.getString("employee.reset.password.match.old.password.message", ""),
					0);
		}

		super.setPassword(password, encoding);

		addNewPasswordToHistory();
	}



	/**
	 * This method compare the encoded old passwords with new password (since we don't store the plain passwords in
	 * history). It used old password encoding method to encode.
	 *
	 * @param password
	 *           the password
	 * @return true, if is matching with old passwords
	 */
	private boolean isMatchingWithOldPasswords(final String password)
	{
		final List<UserPasswordHistoryModel> hostoryList = sabmChangePasswordHistoryService
				.getPreviousEncodedPasswords(super.getUid(), Config.getInt("employee.password.check.no.previous.passwords", 5));
		if (hostoryList != null)
		{
			for (final UserPasswordHistoryModel entry : hostoryList)
			{
				final String encodedNewPassword = getEncoder(entry.getPasswordEncoding()).encode(getUid(), password);
				if (StringUtils.equals(encodedNewPassword, entry.getEncodedPassword()))
				{
					return true;
				}

			}

		}

		return false;
	}

	/**
	 * This method validates the format of the password.
	 *
	 * @param passWord
	 *           the pass word
	 * @return true, if successful
	 */
	private boolean validatePassword(final String passWord)
	{
		final String reg = Config.getString("employeePassword.format.regx", "");
		final Pattern pattern = Pattern.compile(reg);
		final Matcher matcher = pattern.matcher(passWord);
		return matcher.matches();
	}


	/**
	 * This method saves the new password into the history.
	 */
	private void addNewPasswordToHistory()
	{

		final UserPasswordHistoryModel changePasswordModel = modelService.create(UserPasswordHistoryModel.class);

		changePasswordModel.setUid(getUid());
		changePasswordModel.setPasswordEncoding(getPasswordEncoding());
		changePasswordModel.setEncodedPassword(getEncodedPassword());
		modelService.save(changePasswordModel);
	}
}
