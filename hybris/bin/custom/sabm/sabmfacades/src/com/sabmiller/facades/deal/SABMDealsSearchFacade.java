package com.sabmiller.facades.deal;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.apb.facades.deal.data.AsahiDealData;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.deal.data.CartDealsJson;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.repdriven.data.RepDrivenDealConditionData;


/**
 * The Interface SABMDealsSearchFacade.
 */
public interface SABMDealsSearchFacade extends SABMDealsFacade
{

	/**
	 * Constructs a cart deals data which contains information about the following: 1. Partially Qualified Deals 2.
	 * Conflicting deals 3. Free goods selection.
	 *
	 * @return the list of Partially Qualified Deals.
	 */
	CartDealsJson getCartDealsData();

	/**
	 * Search deals.
	 *
	 * @return the list
	 */
	List<DealJson> searchDeals();


	/**
	 * Search deals.
	 *
	 * @param b2BUnitModel the b2b unit
	 * @return the list
	 */
	List<DealJson> searchDeals(B2BUnitModel b2BUnitModel, Date deliveryDate);

	/**
	 * Search deals.
	 *
	 * @param deliveryDate
	 *           the delivery date
	 * @param judgeValidPeriod
	 *           the judge valid period
	 * @return the list
	 */
	List<DealJson> searchDeals(Date deliveryDate, Boolean judgeValidPeriod);

	/**
	 * Search deals.
	 *
	 * @param deliveryDate
	 *           the delivery date
	 * @param judgeValidPeriod
	 *           the judge valid period
	 * @param b2BUnitModel the b2b unit
	 * @return the list
	 */
	List<DealJson> searchDeals(Date deliveryDate, Boolean judgeValidPeriod, B2BUnitModel b2BUnitModel);

	/**
	 * Convert date.
	 *
	 * @param date
	 *           the date
	 * @param format
	 *           the format
	 * @return the date
	 */
	Date convertDate(String date, String format);

	/**
	 * Convert date format.
	 *
	 * @param dateString
	 *           the date string
	 * @param formatFrom
	 *           the format from
	 * @param formatTo
	 *           the format to
	 * @return the string
	 */
	String convertDateFormat(String dateString, String formatFrom, String formatTo);

	/**
	 * Valid date.
	 *
	 * @param date
	 *           the date
	 * @return true, if successful
	 */
	boolean validDate(String date);

	/**
	 * Check whether has deals in the upcoming period of the enable dates.
	 *
	 * @return true, if successful
	 */
	boolean hasUpcomingDeals();

	/**
	 * Get Other can select customer by B2bUnit and group.
	 *
	 * @param b2bnit
	 *           the b2bnit
	 * @return List
	 */
	List<String> getOtherSelectCustomers(B2BUnitModel b2bnit);

	/**
	 * Send confirm enable deal email.
	 *
	 * @param b2bUnitId
	 *           the b2b unit id
	 * @param changedDeals
	 *           the changed deals
	 * @param behaviourRequirements
	 *           the behaviour requirements
	 * @param sendToMe
	 *           the send to me
	 * @param toEmails
	 *           the to emails
	 * @return true, if successful
	 */
	boolean sendConfirmEnableDealEmail(String b2bUnitId, List<RepDrivenDealConditionData> changedDeals,
			String behaviourRequirements, Boolean sendToMe, List<String> toEmails);



	/**
	 * Gets the changed deals title for current user.
	 *
	 * @param changedDeals
	 *           the changed deals
	 * @return the changed deals title for current user
	 */
	Map<String, List<String>> getChangedDealsTitleForCurrentUser(List<RepDrivenDealConditionData> changedDeals);

	/**
	 * Get Rep-Driven exclusive deals.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @param inStore
	 *           This is Rep-Driven deal identifier
	 * @return List<DealJson>
	 */
	List<DealJson> getSpecificDeals(final B2BUnitModel b2bUnit, final boolean inStore);

	/**
	 * Search deals.
	 *
	 * @param onlyValidDelivery
	 *           the only valid delivery
	 * @return the list
	 */
	List<DealJson> searchDeals(boolean onlyValidDelivery);

	/**
	 * @param productCode
	 * @return
	 */
	List<String> getDealsForProduct(String productCode);

	/**
	 * Get Rep-Driven exclusive deals.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @param inStore
	 *           This is Rep-Driven deal identifier
	 * @return List<DealJson>
	 */
	List<AsahiDealData> getSGASpecificDeals(final AsahiB2BUnitModel b2bUnit);

	/**
	 * @param customerAccount
	 * @param dealsToActivate
	 * @param dealsToRemove
	 * @param customerEmails
	 * @param dealsDetails
	 */
	void saveAsahiRepDealChange(String customerAccount, List<String> dealsToActivate, List<String> dealsToRemove,
			List<String> customerEmails, String dealsDetails);

	List<String> getCustomerEmails(final AsahiB2BUnitModel b2bUnit);

	/**
	 * @param code
	 * @param b2bUnitModel
	 * @return
	 */
	List<String> getSGADealsTitleForProductAndUnit(String code, B2BUnitModel b2bUnitModel);

	List<AsahiDealData> getCustomerSpecificDeals(final AsahiB2BUnitModel b2bUnit);

	/**
	 * @param code
	 * @param b2bUnitModel
	 * @return
	 */
	List<AsahiDealData> getSGADealsDataForProductAndUnit(String code, B2BUnitModel b2bUnitModel);


}
