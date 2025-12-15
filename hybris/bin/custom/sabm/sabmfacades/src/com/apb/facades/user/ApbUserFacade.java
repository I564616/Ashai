package com.apb.facades.user;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserModel;

import java.util.List;

import com.apb.facades.kegreturn.data.KegSizeData;
import com.apb.facades.product.data.AsahiRoleData;
import com.asahi.facades.planograms.PlanogramData;
import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 *
 */
public interface ApbUserFacade extends UserFacade
{
	/**
	 * @return
	 */
	List<AsahiRoleData> getAsahiRole();

	/**
	 * @param cmsSiteModel
	 * @return
	 */
	List<RegionData> getStates(CMSSiteModel cmsSiteModel);

	/**
	 * @param currentUser
	 * @return
	 */
	List<AddressData> getB2BUnitAddressesForUser(UserModel currentUser);

	/**
	 * @param currentSite
	 * @return
	 */
	List<KegSizeData> getKegSizes(CMSSiteModel currentSite);

	UserModel getCurrentUser();

	/**
	 * The customers will sorted based on the two level -- Status and Name Ascending
	 *
	 * @param searchPageData
	 * @return SearchPageData
	 */
	SearchPageData<CustomerData> sortCustomers(SearchPageData<CustomerData> searchPageData);


	boolean removeCustomerFromB2bUnit(final String userId,  final AsahiB2BUnitModel currentUnit);

	/**
	 * @param userId
	 * @return
	 */
	boolean sendWelcomeEmail(String userId);

	/**
	 * @param userId
	 * @param siteUid
	 * @return
	 */
	boolean isUserEligibleToReceiveWelcomeEmail(String userId, String siteUid);

	/**
	 * @return
	 */
	List<PlanogramData> getPlanogramsForB2BUnit();

	/**
	 * @return
	 */
	List<PlanogramData> getDefaultPlanogram();

	/**
	 * @param form
	 * @return
	 */
	boolean savePlanogram(PlanogramData data);

	/**
	 * @param code
	 */
	void removePlanogram(String code);

	/**
	 *
	 */
	void removeAllPlanograms();

}
