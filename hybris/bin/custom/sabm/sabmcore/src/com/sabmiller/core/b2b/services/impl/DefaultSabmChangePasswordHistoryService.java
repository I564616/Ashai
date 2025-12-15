/**
 *
 */
package com.sabmiller.core.b2b.services.impl;

import java.util.List;

import jakarta.annotation.Resource;

import com.sabmiller.core.b2b.dao.SabmChangePasswordHistoryDao;
import com.sabmiller.core.b2b.services.SabmChangePasswordHistoryService;
import com.sabmiller.core.model.UserPasswordHistoryModel;


/**
 * The Class DefaultSabmChangePasswordHistoryService.
 */
public class DefaultSabmChangePasswordHistoryService implements SabmChangePasswordHistoryService
{


	/** The sabm change password history dao. */
	@Resource
	SabmChangePasswordHistoryDao sabmChangePasswordHistoryDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sabmiller.core.b2b.services.SabmChangePasswordHistoryService#getPreviousEncodedPasswords(java.lang.String,
	 * int)
	 */
	@Override
	public List<UserPasswordHistoryModel> getPreviousEncodedPasswords(final String uid, final int count)
	{

		return sabmChangePasswordHistoryDao.findPreviousEncodedPasswords(uid, count);
	}
}
