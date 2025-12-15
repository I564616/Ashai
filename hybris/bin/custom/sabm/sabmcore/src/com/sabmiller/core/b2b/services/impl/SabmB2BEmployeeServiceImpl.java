/**
 *
 */
package com.sabmiller.core.b2b.services.impl;

import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import com.sabmiller.core.b2b.services.SabmB2BEmployeeService;

/**
 * @author ramsatish.jagajyothi
 *
 */
public class SabmB2BEmployeeServiceImpl implements SabmB2BEmployeeService
{

	@Resource(name = "employeeDao")
	private GenericDao<EmployeeModel> employeeDao;

	@Override
	public EmployeeModel searchBDEByName(final String name)
	{

		final List<EmployeeModel> userList = employeeDao.find(Collections.singletonMap("name", name));

		return userList.isEmpty() ? null : (EmployeeModel) userList.get(0);
	}
	@Override
	public EmployeeModel searchBDEByUid(String uid)
	{
		final List<EmployeeModel> userList = employeeDao.find(Collections.singletonMap("uid", uid));

		return userList.isEmpty() ? null : (EmployeeModel) userList.get(0);
	}

}
