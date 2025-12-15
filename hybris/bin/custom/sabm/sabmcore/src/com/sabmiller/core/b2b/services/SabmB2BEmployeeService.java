package com.sabmiller.core.b2b.services;

import de.hybris.platform.core.model.user.EmployeeModel;


/**
 * SabmB2BCustomerService
 */
public interface SabmB2BEmployeeService
{

	public EmployeeModel searchBDEByName(String name);

	public EmployeeModel searchBDEByUid(String uid);

}
