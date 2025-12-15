/**
 *
 */
package com.sabmiller.core.b2b.services;

import java.util.List;

import com.sabmiller.core.model.UserPasswordHistoryModel;


/**
 * The Interface SabmChangePasswordHistoryService.
 */
public interface SabmChangePasswordHistoryService
{

	/**
	 * Gets the previous encoded passwords.
	 *
	 * @param uid
	 *           the uid
	 * @param count
	 *           the count
	 * @return the previous encoded passwords
	 */
	List<UserPasswordHistoryModel> getPreviousEncodedPasswords(String uid, int count);

}
