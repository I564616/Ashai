package com.apb.core.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commons.renderer.exceptions.RendererException;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.ticket.model.CsTicketModel;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

import com.apb.core.model.ApbCompanyDetailsEmailModel;
import com.apb.core.model.ApbKegReturnEmailModel;
import com.apb.core.model.ApbRequestRegisterEmailModel;
import com.apb.core.model.ContactUsQueryEmailModel;
import com.apb.core.model.KegReturnSizeModel;
import com.apb.core.model.ProdPricingTierModel;
import com.apb.facades.contactust.data.ApbContactUsData;
import com.apb.facades.user.data.ApbCompanyData;
import com.asahi.facades.planograms.PlanogramData;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiSAMAccessModel;
import com.sabmiller.core.model.PlanogramModel;


/**
 * ApbCustomerAccountService implementation of {@link CustomerAccountService}
 */
public interface ApbCustomerAccountService extends CustomerAccountService
{

	/**
	 * @param newCustomer
	 * @param password
	 * @param asahiUnits
	 * @throws DuplicateUidException
	 */
	void register(B2BCustomerModel newCustomer, String password, Set<AsahiB2BUnitModel> asahiUnits) throws DuplicateUidException;

	/**
	 * @param requestRegistrationEmailModel
	 * @return
	 * @throws DuplicateUidException
	 * @throws RendererException
	 */
	void sendRequestRegisterEmail(final ApbRequestRegisterEmailModel requestRegistrationEmailModel) throws DuplicateUidException,
			RendererException;

	/**
	 * @return customer credit limit flag
	 */
	boolean getCustomerAccountCreditLimit();

	/**
	 * @return
	 */
	ApbCompanyData getB2BCustomerData();

	/**
	 * Gets the order list.
	 *
	 * @param customerModel
	 *           the customer model
	 * @param store
	 *           the store
	 * @param pageableData
	 *           the pageable data
	 * @return the order list
	 */
	SearchPageData<OrderModel> getOrderList(CustomerModel customerModel, BaseStoreModel store, PageableData pageableData,
			String cofoDate) throws ParseException;

	/**
	 * @param apbCompanyDetailsEmail
	 * @param apbCompanyData
	 * @throws RendererException
	 * @throws DuplicateUidException
	 */

	void updateCompanyDetails(ApbCompanyDetailsEmailModel apbCompanyDetailsEmail);

	/**
	 * Returns the specified order for the supplied user.
	 *
	 * @param AsahiB2BUnitModel
	 *           the b2bunit to retrieve order for
	 * @param code
	 *           the code of the order to retrieve
	 * @param store
	 *           the current store
	 * @return the order
	 */
	OrderModel getOrderForCode(AsahiB2BUnitModel b2bUnitModel, String code, BaseStoreModel store);

	List<B2BCustomerModel> findB2BCustomerByGroup(AsahiB2BUnitModel unit, String userGroupId);

	/**
	 * @return
	 */
	ApbContactUsData getLogedInB2BCustomer();

	/**
	 * @param contactUsQueryEmailModel
	 */
	String sendContactUsQueryEmail(final ContactUsQueryEmailModel contactUsQueryEmailModel);

	/**
	 * @param currentSite
	 * @return
	 */
	List<KegReturnSizeModel> getKegSizes(CMSSiteModel currentSite);

	/**
	 * @param apbKegReturnEmailModel
	 */
	void sendKegReturnEmail(ApbKegReturnEmailModel apbKegReturnEmailModel);

	/**
	 * @param currentUser
	 * @param b
	 * @return
	 */
	List<AddressModel> getB2BUnitAddressesForUser(UserModel currentUser, boolean b);

	/**
	 * This method is used to check if given token has expired or is valid.
	 *
	 * @param token
	 * @return boolean
	 */
	boolean checkTokenValid(String token);

	/**
	 * This method is used to publish the reset password event for back office user.
	 *
	 * @param customerModel
	 */
	void assistedForgotPassword(final CustomerModel customerModel);

	/**
	 * The method will send the order confirmation email to the customer.
	 *
	 * @param orderModel
	 */
	void sendOrderConfirmationEmail(OrderModel orderModel);

	/**
	 * This method will return the product ids associated with customer/B2bUnit
	 *
	 * @param userId
	 * @return list of product id
	 */
	Set<String> getCustomerCatalogProductIds(String userId);

	/**
	 * This method will return the product id's associated with Pricing Tier
	 *
	 * @param tierCode
	 * @return ProdPricingTierModel
	 */
	ProdPricingTierModel getPricingTierProductIds(String tierCode);

	/**
	 * The Method will update the payer access and trigger the email notification
	 *
	 * @param customer
	 * @param accessModel
	 * @param accessType
	 * @return String
	 */
	String updateAndNotifyPayAccess(B2BCustomerModel customer, AsahiSAMAccessModel accessModel, String accessType);

	/**
	 * The Method will get the access model
	 *
	 * @param customer
	 * @param b2bUnit
	 * @return AsahiSAMAccessModel
	 */
	AsahiSAMAccessModel getAccessModel(B2BCustomerModel customer, AsahiB2BUnitModel b2bUnit);

	/**
	 * Gets the enquiries list.
	 *
	 * @param customerModel
	 *           the customer model
	 * @param store
	 *           the store
	 * @param pageableData
	 *           the pageable data
	 * @return the order list
	 */
	public SearchPageData<CsTicketModel> getAllEnquiries(final AsahiB2BUnitModel b2bunit, final PageableData pageableData, final String cofoDate) throws ParseException;

	/**
	 * @return
	 */
	public Set<String> getCustomerCatalogRestrictedCategories();

	/**
	 *
	 */
	void setRestrictedCategoriesInSession();

	/**
	 * @param customerModel
	 */
	void sendPasswordResetEmail(B2BCustomerModel customerModel);

	/**
	 * @param customerModel
	 */
	void setCustomerToken(B2BCustomerModel customerModel);

	boolean removeCustomerFromUnit(final B2BCustomerModel customerModel, final AsahiB2BUnitModel currentUnit);

	/**
	 * @return
	 */
	boolean sendWelcomeEmail(final UserModel user);

	/**
	 * @param customerId
	 * @param currentUnit
	 */
	void sendCustomerProfileUpdatedNoticeEmail(final B2BCustomerModel customerModel, AsahiB2BUnitModel currentUnit);

	UserModel getUserByUid(String userId);


	/**
	 * @param catalogHierarchy
	 * @return
	 */
	List<PlanogramModel> getDefaultPlanograms(List<String> catalogHierarchy);

	/**
	 * @param data
	 */
	boolean savePlanogram(PlanogramData data);

	/**
	 * @param code
	 */
	void removePlanogram(String code);

	/**
	 *
	 */
	void removeAllPlanogramsForCurrentB2BUnit();

	/**
	 * @param code
	 * @return
	 */
	PlanogramModel fetchPlanogramByCode(final String code);

}
