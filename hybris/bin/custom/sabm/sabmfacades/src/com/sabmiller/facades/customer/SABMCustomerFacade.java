/**
 *
 */
package com.sabmiller.facades.customer;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commons.renderer.exceptions.RendererException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.media.MediaIOException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.validation.BindingResult;

import com.apb.core.exception.AsahiBusinessException;
import com.apb.facades.contactust.data.ApbContactUsData;
import com.apb.facades.kegreturn.data.ApbKegReturnData;
import com.apb.facades.register.data.ApbRequestRegisterData;
import com.apb.facades.user.data.ApbCompanyData;
import com.apb.storefront.data.ApbRegisterData;
import com.apb.storefront.data.LoginValidateInclusionData;
import com.sabmiller.commons.enumerations.LoginStatus;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.facades.delivery.data.B2bDeliveryDatesConfig;
import com.sabmiller.facades.invoice.SABMInvoicePDFData;
import com.sabmiller.facades.invoice.SABMInvoicePageData;
import com.sabmiller.facades.registrationrequest.data.RegistrationRequestForm;
import com.sabmiller.facades.user.NotificationData;

/**
 * The Interface SABMCustomerFacade.
 */
public interface SABMCustomerFacade extends CustomerFacade
{

	/**
	 * Creates and persists a @ProductExclusionModel.
	 *
	 * @param productExclusionData
	 *           the product exclusion data used as source to create a @ProductExclusionModel
	 * @throws Exception
	 * @throws ModelSavingException
	 *            the model saving exception in case of any error (e.g.: validation exception, database exception...)
	 */
	void createProductExclusion(Set<ProductExclusionData> productExclusions, B2BUnitModel b2bUnit) throws Exception;

	/**
	 * Gets the customer invoices.
	 *
	 * @param lineItem
	 *           the line item
	 * @param forUnit
	 *           the for unit
	 * @param startDate
	 *           the start date
	 * @param endDate
	 *           the end date
	 * @return the customer invoices
	 */
	SABMInvoicePageData getCustomerInvoices(String lineItem, String forUnit, String startDate, String endDate,final String invoiceType);


	/**
	 * Gets the invoice pdf.
	 *
	 * @param docNum
	 *           the doc num
	 * @return the invoice pdf
	 */
	SABMInvoicePDFData getInvoicePDF(String docNum);

	/**
	 * Send invoices email.
	 *
	 * @param docNumList
	 *           the doc num list
	 * @return true, if successful
	 */
	boolean sendInvoicesEmail(List<String> docNumList);


	/**
	 * Send change pwd email message.
	 *
	 * @author yuxiao.wang
	 *
	 *         Sent a mail to User when Update Password Successfully
	 */
	void sendChangePwdEmailMessage();

	/**
	 * Save user.
	 *
	 * @param customerJson
	 *           the create user form data
	 * @return CustomerData
	 */
	CustomerData saveUser(CustomerJson customerJson);

	/**
	 * Gets the customer for uid.
	 *
	 * @param uId
	 *           the u id
	 * @return the customer data
	 */
	CustomerData getCustomerForUid(String uId);

	/**
	 * Gets the b2 b unit for id.
	 *
	 * @param businessId
	 *           the business id
	 * @return the customer data
	 */
	B2BUnitModel getB2BUnitForId(String businessId);

	/**
	 * Edits the user.
	 *
	 * @param customerJson
	 *           the customer json
	 * @return the customer data
	 * @throws DuplicateUidException
	 */
	CustomerData editUser(CustomerJson customerJson) throws DuplicateUidException;


	/**
	 * Update receive updates.
	 *
	 * @param receiveUpdates
	 *           the receive updates
	 * @throws DuplicateUidException
	 *            the duplicate uid exception
	 */
	void updateReceiveUpdates_MobileNumber(Boolean receiveUpdates,Boolean receiveUpdatesForSms,final String mobileNumber , final String businessPhoneNumber) throws DuplicateUidException;

	/**
	 * Save active for customer.
	 *
	 * @param customerUid
	 *           the customer uid
	 * @param activeFlag
	 *           the active flag
	 * @param string
	 * @return true, if successful
	 * @throws DuplicateUidException
	 *            the duplicate uid exception
	 */
	boolean saveActiveForCustomer(String customerUid, String activeFlag, String b2bUnitId) throws DuplicateUidException;

	/**
	 * Send welcome email message.
	 *
	 * @param uId
	 *           the u id
	 */
	void sendWelcomeEmailMessage(String uId);

	/**
	 * Check if the user set as argument can login, based on the SAP attributes.
	 *
	 * @param user
	 *           the user
	 * @return true, if successful
	 */
	boolean canUserLogin(B2BCustomerModel user);

	/**
	 * Gets the login status.
	 *
	 * @param user
	 *           the user
	 * @return the login status
	 */
	LoginStatus getLoginStatus(UserModel user);

	/**
	 * Gets the login status of the session customer.
	 *
	 * @return the login status
	 */
	LoginStatus getLoginStatus();

	/**
	 * Refresh core entities.
	 *
	 * @param date
	 *           the date
	 */
	void refreshCoreEntities();


	void verifyAndUpdateCUPForRDD(Date deliveryDate, String packType);


	/**
	 * Checks if is password set for the argument user.
	 *
	 * @param userUid
	 *           the user uid
	 * @return true, if is password set
	 */
	boolean isPasswordSet(String userUid);

	/**
	 * Gets the delivery dates.
	 *
	 * @param validDates
	 *           the valid dates
	 * @return the delivery dates
	 */
	List<Long> getDeliveryDates(final boolean validDates);

	Set<Date> enabledCalendarDates();

	/**
	 * Gets the next delivery date and set it in session.
	 *
	 * @return the next delivery date
	 */
	Date getNextDeliveryDateAndUpdateSession();

	/**
	 * Change b2 b unit.
	 *
	 * @param b2bUnitId
	 *           the b2b unit id
	 */
	void changeB2BUnit(final String b2bUnitId);

	/**
	 * Creates and persists a @SABMUserAccessHistoryModel.
	 *
	 * @param userAccessHistoryData
	 *           userAccessHistoryData used as source to create a @SABMUserAccessHistoryModel
	 * @throws ModelSavingException
	 *            the model saving exception in case of any error (e.g.: validation exception, database exception...)
	 */
	void createUserAccessHistory(SABMUserAccessHistoryData userAccessHistoryData);

	/**
	 * Checks if is deal refresh in progress.
	 *
	 * @return true, if is deal refresh in progress
	 */
	boolean isDealRefreshInProgress();

	/**
	 * Checks if is cup refresh in progress.
	 *
	 * @return true, if is cup refresh in progress
	 */
	boolean isCupRefreshInProgress();

	/**
	 * Checks if is product exclusion refresh in progress.
	 *
	 * @return true, if is cup refresh in progress
	 */
	boolean isProductExclRefreshInProgress();



	/**
	 * Turn off impersonation.
	 */
	void turnOffImpersonation();

	/**
	 * Turn back impersonation.
	 */
	void turnBackImpersonation();


	/**
	 * Get the uids for update profile.
	 *
	 * @param units
	 *           the b2b unit ids
	 * @return List<String> the uids
	 */
	List<CustomerData> getUserForUpdateProfile(List<String> units);


	/**
	 * update default customer unit.
	 *
	 * @param unitId
	 *           the unit id
	 */
	void updateDefaultCustomerUnit(String unitId);


	/**
	 * Removes the customer from b2b unit.
	 *
	 * @param unitId
	 *           the unit id
	 * @param customerId
	 *           the customer id
	 * @return true, if successful
	 */
	boolean removeCustomerFromB2bUnit(String unitId, String customerId);

	/**
	 * get users by userID.
	 *
	 * @param registerEmail
	 *           the register email
	 * @return List
	 */
	List<CustomerData> getUsersByUserId(final String registerEmail);

	/**
	 * Get B2BUnitData by B2BUnitModel.
	 *
	 * @param businessId
	 *           the business id
	 * @return B2BUnitData
	 */
	B2BUnitData getB2BUnitForUnitModel(B2BUnitModel businessId);

	/**
	 * Delete user.
	 *
	 * @param uid
	 *           the user who need to be delete
	 * @return CustomerData, the user need to be delete
	 */
	CustomerData deleteUser(String uid);


	/**
	 * Get current customer states.
	 *
	 * @return CustomerJson
	 */
	CustomerJson getCurrentCustomerJsonStates();

	/**
	 * Get CustomerJson according to the uid.
	 *
	 * @param uid
	 *           the uid
	 * @return CustomerJson
	 */
	CustomerJson getCustomerJsonByUid(final String uid);

	/**
	 * Get the Status from the b2bUnit's PrimaryAdmin customer
	 *
	 * Active _ the PrimaryAdmin customer's active is true
	 *
	 * Invited _ the PrimaryAdmin customer's active is true but password not set
	 *
	 * Inactive _ the PrimaryAdmin customer's active is false or not fund the PrimaryAdmin customer
	 *
	 * @param uid
	 * @return String
	 */
	String getPrimaryAdminStatus(final String uid);

	/**
	 * update the status by customer
	 *
	 * @param token
	 * @param sendMail
	 * @param setPassword
	 */
	void updateB2BUnitStatus(final String token, final boolean sendMail, final boolean setPassword);

	/**
	 *
	 * @return
	 */
	boolean isEmployeeUser(final UserModel userModel);

	/**
	 *
	 * @return
	 */
	BDECustomerModel getOrCreateBDECustomer(final String uid, final String emailAddress);

	/**
	 * @param token
	 * @return
	 * @throws TokenInvalidatedException
	 */
	UserModel validateSecureToken(String token) throws TokenInvalidatedException;

	/**
	 * @param uid
	 * @return
	 */
	String getNewSecureToken(String uid);

	/**
	 * send the customer profile updated notice email
	 *
	 * @param customer
	 *           the customer
	 * @param fromUser
	 *           the admin
	 */
	void sendCustomerProfileUpdatedNoticeEmail(CustomerModel customer, UserModel fromUser);

	boolean sendRegistrationRequestsMessage(RegistrationRequestForm registrationRequestForm);

	List<NotificationData> getUnreadSiteNotification();

	void markSiteNotificationAsRead(String messagecode);

	/**
	 * Returns the B2b Unit's Delivery Method, Pack types, dates and UI configuration for Delivery Date Picker
	 *
	 * @return
	 */
	B2bDeliveryDatesConfig getDeliveryDatesConfig(B2BUnitModel b2bUnit);

	/**
	 * Get Public holidays for calendar
	 *
	 * @param b2bUnit
	 * @return
	 */

	List<Long> addPublicHolidayData(final B2BUnitModel b2bUnit);

	/**
	 * @param registerData
	 * @param bindingResult
	 * @throws DuplicateUidException
	 */
	void register(ApbRegisterData registerData, BindingResult bindingResult) throws DuplicateUidException, AsahiBusinessException;

	/**
	 * @param apbRequestRegisterData
	 * @param apbRequestRegisterEmailModel
	 * @param apbRegisterData
	 * @throws DuplicateUidException
	 * @throws RendererException
	 * @throws MediaIOException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @return
	 */
	void sendRequestRegisterEmail(ApbRequestRegisterData apbRequestRegisterData) throws DuplicateUidException, RendererException,
			MediaIOException, IllegalArgumentException, IOException;


	/**
	 * @return customer credit limit flag
	 */
	boolean getCustomerAccountCreditLimit();

	/**
	 * @return
	 */
	ApbCompanyData getB2BCustomerData();

	/**
	 * @param apbCompanyData
	 */
	void updateCompanyDetails(ApbCompanyData apbCompanyData);

	/**
	 * @return ApbContactUsData
	 */
	ApbContactUsData getLogedInB2BCustomer();

	/**
	 * @param setKegReturnData
	 */
	void sendKegReturnEmail(ApbKegReturnData setKegReturnData);

	/**
	 * this method will fetch the customer's product inclusion/exclusion details
	 */

	LoginValidateInclusionData setCustomerCreditAndInclusionInSession();


	/**
	 * This Method will fetch the paged multi accounts related with current user.
	 *
	 * @param pageableData
	 * @return
	 */
	SearchPageData<B2BUnitData> getPagedMultiAccounts(PageableData pageableData);

	/**
	 * @param searchPageData
	 *           This Method will restrict results for pagination.
	 */
	void retrictResultsPerPage(SearchPageData<B2BUnitData> searchPageData);



	/**
	 * This method will sort the multi account results.
	 *
	 * @param searchPageData
	 */
	void sortMultiAccountResults(SearchPageData<B2BUnitData> searchPageData);

	/**
	 * The Method will check if the current user has the payer account permission.
	 *
	 * @return boolean
	 */
	boolean isSAMPayAccessEnable();

	/**
	 * The Method will update the boolean attribute if customer logged in earlier.
	 *
	 * @param user
	 */
	void updateCustomerLoggedIn(UserModel user);

	/**
	 * The Method will approve or reject the pay access
	 *
	 * @param samAccess
	 * @param pk
	 * @return String
	 */
	String approveORRejectPayAccess(String samAccess, String pk);

	/**
	 * block customer from registration in case required
	 *
	 * @param samAccess
	 * @param asahiB2BUnitModel
	 * @throws AsahiBusinessException
	 */
	void blockRegBasedOnCustomerType(String samAccess, AsahiB2BUnitModel asahiB2BUnitModel)
			throws AsahiBusinessException;

	/**
	 * The Method will update the payer access and trigger the email notification
	 *
	 * @param access
	 * @return Boolean
	 */
	Boolean requestOrderORPayAccess(String access);

	/**
	 * The Method will validate the Pay Access for the user, Send the expiry Email to user in case request for access
	 * expire. Update the Pay Access Model in case the Request Expire.
	 *
	 * @param user
	 */
	void validatePayAccess(UserModel user);

	boolean isOnAccountPaymentRestricted();

	public SearchPageData<ApbContactUsData> getAllEnquiries(final PageableData pageableData) throws ParseException;

	public void setRestrictedCategoriesInSession();

	/**
	 * @param uid
	 * @return
	 */
	CustomerJson isExistUserMail(String uid);

	boolean isCustomerActiveForCUB(final B2BCustomerModel customer);

}
