/**
 *
 */
package com.sabmiller.facades.user.impl;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import com.sabmiller.facades.user.EmployeeData;
import com.sabmiller.facades.user.EmployeeFacade;


/**
 * @author dale.bryan.a.mercado
 *
 */
public class EmployeeFacadeImpl implements EmployeeFacade
{
	private UserService userService;
	private Converter<UserModel, EmployeeData> employeeConverter;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.user.EmployeeFacade#getCurrentEmployee()
	 */
	@Override
	public EmployeeData getCurrentEmployee()
	{
		return employeeConverter.convert(getUserService().getCurrentUser());
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the employeeConverter
	 */
	public Converter<UserModel, EmployeeData> getEmployeeConverter()
	{
		return employeeConverter;
	}

	/**
	 * @param employeeConverter
	 *           the employeeConverter to set
	 */
	public void setEmployeeConverter(final Converter<UserModel, EmployeeData> employeeConverter)
	{
		this.employeeConverter = employeeConverter;
	}


}
