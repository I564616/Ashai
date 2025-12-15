package com.sabmiller.core.b2b.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.List;

import com.sabmiller.core.model.BDECustomerImportedModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.SabmUserMessagesStatusModel;
import com.sabmiller.facades.user.NotificationData;


/**
 * SabmB2BCustomerService
 */
public interface SabmB2BCustomerService
{
	/**
	 * find other of the customer
	 *
	 * @return List<? extend String>
	 */
	public List<String> getOtherCustomerByUnitAndGroups(B2BUnitModel b2bUnit, String customerUid);


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
	public List<B2BCustomerModel> getCustomerForUpdateProfile(List<String> orUids, List<String> andUids);




	/**
	 * delete the user
	 *
	 * @param customer
	 *           the user need to be change uid
	 *
	 * @return CustomerModel the changed uid user
	 */
	public B2BCustomerModel deleteCustomer(final B2BCustomerModel customer);

	/**
	 * change the user's uid
	 *
	 * @param customer
	 *           the user need to be change uid
	 *
	 * @param newUid
	 *           the new uid
	 *
	 * @return CustomerModel the changed uid user
	 */
	public B2BCustomerModel changeCustomerUid(final B2BCustomerModel customer, final String newUid);

	/**
	 * get the user with similar uid
	 *
	 * @param uid
	 *           the uid
	 *
	 * @return List<B2BCustomerModel>
	 */
	public List<B2BCustomerModel> getSimilarB2BCustomer(final String uid);

	/**
	 * get users by B2BCustomerModel's b2bunitGroup
	 *
	 * @param b2bCustomerModel
	 * @return List
	 */
	public List<B2BCustomerModel> getUsersByGroups(B2BCustomerModel b2bCustomerModel);

	/**
	 * get users by B2BCustomerModel's b2bunitGroup
	 *
	 * @param b2bCustomerModel
	 * @return List
	 */
	public BDECustomerModel getBDECustomer(final String uid);



	BDECustomerImportedModel getBDECustomerImported(final String uid);

	List<BDECustomerImportedModel> getBDECustomerImportedAll();

	/**
	 * get users by B2bCustomerModel's UID
	 *
	 * @param email
	 * @return
	 */
	public List<B2BCustomerModel> searchB2BCustomerByEmail(String email);


	/**
	 * @param email
	 * @return
	 */
	List<B2BCustomerModel> searchB2BDeletedCustomerByEmail(String email);


	/**
	 * @param email
	 * @return
	 */
	List<B2BCustomerModel> searchCustomerByEmail(String email);


	/**
	 * @param user
	 *
	 */
	List<NotificationData> getUnreadSiteNotification(UserModel user);

	/**
	 * @param uid
	 * @param messageCode
	 * @return
	 */
	public void markSiteNotificationAsRead(String uid, String messageCode);



	/**
	 * @param messageCode
	 * @return
	 */
	List<SabmUserMessagesStatusModel> getAllUserMessageEntries(String messageCode);


	/**
	 * @param email
	 * @return
	 */
	List<B2BCustomerModel> searchCustomerByExactEmail(String email);

	/**
	 * @param user
	 * @param b
	 * @return
	 */
	public boolean isRegistrationAllowed(UserModel user, final String b2bUnitId);

	/**
	 * Check if user registered for other sites.
	 *
	 * @param user
	 *           the user
	 * @param createUser
	 * @return true, if successful
	 */
	public boolean checkIfUserRegisteredForOtherSites(final UserModel user, boolean createUser);

	/**
	 * @return list of alb customers
	 */
	List<B2BCustomerModel> getALBCustomersToSFList();

	List<B2BCustomerModel> getAllALBCustomersToSFList();


}
