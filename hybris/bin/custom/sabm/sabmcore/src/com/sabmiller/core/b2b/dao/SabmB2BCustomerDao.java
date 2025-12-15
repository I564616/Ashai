/**
 *
 */
package com.sabmiller.core.b2b.dao;

import com.sabmiller.core.jalo.BDECustomerImported;
import com.sabmiller.core.model.BDECustomerImportedModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;
import java.util.Set;

import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.SabmMessageModel;
import com.sabmiller.core.model.SabmUserMessagesStatusModel;


/**
 * SabmB2BCustomerDao
 */
public interface SabmB2BCustomerDao extends Dao
{

	/**
	 * Get the Customer by uids
	 *
	 * @param orUids
	 *           the b2b unit ids
	 * @param andUids
	 *           the b2b unit ids
	 * @return List<B2BCustomerModel> the Customers
	 *
	 */
	public List<B2BCustomerModel> getCustomerByUnits(List<String> orUids, List<String> andUids);

	/**
	 * get the user with similar uid from group by groupid
	 *
	 * @param uid
	 *           the uid
	 *
	 * @return List<B2BCustomerModel>
	 */
	public List<B2BCustomerModel> getSimilarB2BCustomer(final String uid);

	public BDECustomerModel getBDECustomer(final String uid);

	BDECustomerImportedModel getBDECustomerImported(final String uid);

	/**
	 * search for users with the inputted uid
	 *
	 * @param email
	 * @return List<B2BCustomerModel>
	 */
	List<B2BCustomerModel> searchB2BCustomerByEmail(String email);

	/**
	 * @param email
	 * @return
	 */
	List<B2BCustomerModel> searchB2BDeletedCustomerByEmail(String email);

	/**
	 * @param b2bUnit
	 * @param userId
	 * @return
	 */
	public List<SabmMessageModel> getUserUnreadNotifications(B2BUnitModel b2bUnit, String userId);

	/**
	 * @param messageCode
	 * @return
	 */
	public List<SabmUserMessagesStatusModel> getAllUserMessageEntries(String messageCode);

	/**
	 * search for users by lastLogin
	 *
	 */
	public Set<B2BCustomerModel> getCustomerByLastLogIn();

	/**
	 * @param email
	 * @return
	 */
	List<B2BCustomerModel> searchB2BCustomerByExactEmail(String email);

	List<BDECustomerImportedModel> getBDECustomerImportedAll();

	List<B2BCustomerModel> getModifiedALBCustomersForSF();

	List<B2BCustomerModel> getAllALBCustomersForSF();
}
