package com.apb.core.service.address.impl;

import jakarta.annotation.Resource;

import com.apb.core.dao.address.AsahiAddressDao;
import com.apb.core.service.address.AsahiAddressService;

public class AsahiAddressServiceImpl implements AsahiAddressService
{
	@Resource(name="apbAddressDao")
	AsahiAddressDao apbAddressDao;
	
	@Override
	public String getAddressStatusMapping(String backendStatusCode)
	{
		return apbAddressDao.getAddressStatusMapping(backendStatusCode);
	}
}
