/**
 *
 */
package com.sabmiller.core.b2b.dao;

import java.util.List;

import com.sabmiller.core.model.UserPasswordHistoryModel;


/**
 * The Interface SabmChangePasswordHistoryDao.
 */
public interface SabmChangePasswordHistoryDao
{

	/**
	 * Find previous encoded passwords.
	 *
	 * @param uid
	 *           the uid
	 * @param count
	 *           the count
	 * @return the list
	 */
	List<UserPasswordHistoryModel> findPreviousEncodedPasswords(String uid, int count);
}
