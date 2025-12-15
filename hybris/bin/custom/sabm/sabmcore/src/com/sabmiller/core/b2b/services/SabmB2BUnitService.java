/**
 *
 */
package com.sabmiller.core.b2b.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.sabmiller.core.b2b.dao.SearchB2BUnitQueryParam;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.LastUpdateTimeEntityModel;
import com.sabmiller.core.model.ShippingCarrierModel;


/**
 * Extension to OOTB B2BUnitService.
 */
public interface SabmB2BUnitService extends B2BUnitService<B2BUnitModel, B2BCustomerModel>
{

	/**
	 * Find ZADP, i.e B2BUnit with account group ZADP
	 *
	 * @param payerId
	 *           the payer id
	 * @return the b2 b unit model
	 */
	B2BUnitModel findTopLevelB2BUnit(String payerId);

	/**
	 * Find branch.
	 *
	 * @param payerId
	 *           the payer id
	 * @return the b2 b unit model
	 */
	B2BUnitModel findBranch(String payerId);

	/**
	 * Find branches.
	 *
	 * @param payerId
	 *           the payer id
	 * @return the list
	 */
	List<B2BUnitModel> findBranches(String payerId);

	/**
	 * Persist.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param b2bUnitData
	 *           the b2b unit data
	 * @return the b2 b unit model
	 */
	B2BUnitModel persist(B2BUnitModel b2bUnitModel, B2BUnitData b2bUnitData);

	/**
	 * Check if discount deals need to be refreshed from SAP. If the deals have become obsolete (old), then Hybris needs
	 * to fetch the latest deals from SAP and update it into Hybris.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 * @return true, if is discount deals obsolete
	 */
	boolean isDiscountDealsObsolete(B2BUnitModel b2bUnitModel, Date deliveryDate);

	/**
	 * Check if the BOGOF deals need refresh (as it might have become old).
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 * @return true, if is BOGOF deals obsolete
	 */
	boolean isBOGOFDealsObsolete(B2BUnitModel b2bUnitModel, Date deliveryDate);

	/**
	 * Checks if is once off deals obsolete.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 * @return true, if is once off deals obsolete
	 */
	boolean isOnceOffDealsObsolete(B2BUnitModel b2bUnitModel, Date deliveryDate);

	/**
	 * Check if the customer unit price need to be refreshed from SAP. If the CUP have become obsolete (old), then Hybris
	 * needs to fetch the latest CUP from SAP and update it into Hybris.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 * @return true, if is CUP obsolete
	 */
	boolean isCUPObsolete(B2BUnitModel b2bUnitModel, Date deliveryDate);

	/**
	 * Mark discount deals as refreshed.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 */
	void markDiscountDealsAsRefreshed(B2BUnitModel b2bUnitModel, Date deliveryDate);

	/**
	 * Mark once off deals as refreshed.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 */
	void markOnceOffDealsAsRefreshed(B2BUnitModel b2bUnitModel, Date deliveryDate);

	/**
	 * Mark bogof deals as refreshed.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 */
	void markBOGOFDealsAsRefreshed(B2BUnitModel b2bUnitModel, Date deliveryDate);

	/**
	 * Mark cup as refreshed.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 */
	void markCUPAsRefreshed(B2BUnitModel b2bUnitModel, Date deliveryDate);

	/**
	 * Calls Customer Unit Pricing API integration to retrieve updated price
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 */
	void refreshCUP(final B2BUnitModel b2bUnitModel);


	/**
	 * Find primary admin.
	 *
	 * @param payerId
	 *           the payer id
	 * @return the user model
	 */
	UserModel findPrimaryAdmin(String payerId);

	/**
	 * Checks if is deal refresh in progress.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return true, if is deal refresh in progress
	 */
	boolean isDealRefreshInProgress(B2BUnitModel b2bUnitModel);

	/**
	 * Checks if is cup refresh in progress.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return true, if is cup refresh in progress
	 */
	boolean isCupRefreshInProgress(B2BUnitModel b2bUnitModel);

	/**
	 * Find top level b2 b unit.
	 *
	 * @param customerModel
	 *           the customer model
	 * @return the b2 b unit model
	 */
	B2BUnitModel findTopLevelB2BUnit(B2BCustomerModel customerModel);

	/**
	 * Turn off impersonation.
	 *
	 * @param customerModel
	 *           the customer model
	 */
	void turnOffImpersonation(B2BCustomerModel customerModel);

	/**
	 * Turn back impersonation.
	 */
	void turnBackImpersonation();

	/**
	 * Search b2 b unit.
	 *
	 * @param aueryParam
	 *           the auery param
	 * @return the list
	 */
	List<B2BUnitModel> searchB2BUnit(final SearchB2BUnitQueryParam aueryParam);



	/**
	 * Update default customer unit.
	 *
	 * @param unitId
	 *           the unit id
	 * @param customer
	 *           the customer
	 * @return true, if successful
	 */
	boolean updateDefaultCustomerUnit(String unitId, B2BCustomerModel customer);

	/**
	 * Update default customer unit.
	 *
	 * @param unitId
	 *           the unit id
	 */
	void updateDefaultCustomerUnit(final String unitId);

	/**
	 * Update default customer unit.
	 *
	 * @param unitId
	 *           the unit id
	 * @param customerId
	 *           the customer id
	 * @return true, if successful
	 */
	boolean updateDefaultCustomerUnit(final String unitId, final String customerId);

	/**
	 * Gets the custmoers except zadp.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return the custmoers except zadp
	 */
	List<B2BCustomerModel> getCustmoersExceptZADP(B2BUnitModel b2bUnitModel);

	/**
	 * Removes the customer from unit.
	 *
	 * @param unitId
	 *           the unit id
	 * @param customerId
	 *           the customer id
	 * @return true, if successful
	 */
	boolean removeCustomerFromUnit(String unitId, String customerId);

	/**
	 * SABMC-438
	 *
	 * Get none ZADP user with specified ZADP.
	 *
	 * @param businessUnit
	 *           the business unit
	 * @param excludeUser
	 *           the exclude user
	 * @return the none zadp users with specified business unit
	 */
	List<B2BCustomerModel> getNoneZADPUsersWithSpecifiedBusinessUnit(final B2BUnitModel businessUnit, final UserModel excludeUser);

	/**
	 * Find the ZADP Unit for the customer base on the customer group.
	 *
	 * @param customerModel
	 *           the customer model
	 * @return the list
	 */
	List<B2BUnitModel> findCustomerTopLevelUnit(B2BCustomerModel customerModel);

	/**
	 * if don't set ContactAddress, the default to the first contact address record from the address attribute.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @return AddressModel
	 */
	AddressModel getContactAddressFormB2BUnit(final B2BUnitModel b2bUnit);

	/**
	 * get the status from PrimaryAdmin by b2bUnitId
	 *
	 * @param b2bUnitId
	 * @return String
	 */
	public String findPrimaryAdminStatus(String b2bUnitId);

	/**
	 * save the status to customer
	 *
	 * @param customerModel
	 */
	void updateB2BUnitStatus(final CustomerModel customerModel, final boolean sendMail, final boolean setPassword);

	/**
	 * find the status from B2BUnitModel
	 *
	 * @param b2bUnitId
	 */
	String findB2BUnitStatus(final String b2bUnitId);

	List<B2BUnitModel> searchB2BUnitByAccount(final String accountNumber);

	List<B2BUnitModel> searchB2BUnitByCustomer(final String customerNumber, final String customerName);

	/**
	 * @param b2bUnit
	 * @return
	 */
	List<B2BCustomerModel> getZADPUsersByB2BUnit(B2BUnitModel b2bUnit);

	/**
	 * Fetches the sub channel from the given B2B unit. Ensures that the supplied B2BUnit is a ZALB type unit before
	 * retrieving the sub-channel.
	 *
	 * @param b2BUnitModel
	 *           the B2B unit to retrieve the sub-channel from.
	 * @return the B2B unit's sub-channel as a string.
	 */
	String getSubChannelByB2BUnit(final B2BUnitModel b2BUnitModel);

	/**
	 * Gets the B2BUnit in the current session.
	 *
	 * @return the B2BUnit model if it is in session, null otherwise.
	 */
	B2BUnitModel getB2BUnitInCurrentSession();

	/**
	 * @param b2bUnitModel
	 *
	 * @param date
	 */
	void updateCUP(B2BUnitModel b2bUnitModel, Date date);

	/*
	 * Find old LastUpdateTimeEntityModel while LastUpdateTimeEntityModel.deliveryDate < deliveryBefore.
	 *
	 * @param deliveryBefore the delivery before
	 *
	 * @param batchSize the batch size
	 *
	 * @return list of @LastUpdateTimeEntityModel
	 */
	List<LastUpdateTimeEntityModel> findOldLastUpdateTimeEntities(final Date deliveryBefore, final int batchSize);

	/*
	 * Async call to SAP to send the exclusions for the customer via import job
	 *
	 *
	 */
	void requestProductExclusions(B2BUnitModel b2bUnitModel);

	/**
	 * Check if the customer unit price need to be refreshed from SAP. If the product exclusions have become obsolete
	 * (old), then Hybris needs to fetch the latest exclusions from SAP and update it into Hybris.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 * @return true, if is Exclusion obsolete
	 */
	boolean isProductExclObsolete(B2BUnitModel b2bUnitModel, Date deliveryDate);

	/**
	 * @param b2bUnitModel
	 * @param deliveryDate
	 */
	void markProductExclAsRefreshed(B2BUnitModel b2bUnitModel, Date deliveryDate);

	/**
	 * @param b2bUnitModel
	 * @return
	 */
	boolean isProductExclRefreshInProgress(B2BUnitModel b2bUnitModel);


	/**
	 * @param b2bUnitModel
	 * @param ordersAfterDate
	 * @return
	 */
	String getOrderingStatus(B2BUnitModel b2bUnitModel, Date ordersAfterDate);

	/**
	 * Update prices for active customers since last week.
	 *
	 * @param b2bUnitModel
	 * @return
	 */
	 void importLastWeekCustomersCUP();


	 List<B2BCustomerModel> getCustomersWithInvoicePermission(final B2BUnitModel b2bUnitModel);

	/**
	 * Returns the current store date
	 * @return
	 */
	Date getStoreDate();

	/**
	 * @param Asahib2bUnitModel
	 * @param ordersAfterDate
	 * @return
	 */
	String getAsahiOrderingStatus(AsahiB2BUnitModel b2bUnitModel, Date ordersAfterDate);

	/**
	 * @param uid
	 * @return
	 */
	List<B2BUnitModel> getActiveB2BUnitModelsByCustomer(String uid);


	/**
	 * @param b2bUnitId
	 * @return
	 */
	Collection<String> getCUBDisabledList(String b2bUnitId);

	/**
	 *
	 * @param carriers
	 * @return
	 */
	public List<ShippingCarrierModel> getAllowedCarries(final List<ShippingCarrierModel> carriers);

}
