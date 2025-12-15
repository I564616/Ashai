/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sabmiller.core.deals.vo.DealCodeGeneratorParam;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.DealAssigneeModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.model.EntryOfferInfoModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.integration.sap.cup.response.CustomerUnitPricingResponse;



/**
 * The Interface DealsService.
 *
 * @author joshua.a.antony
 */
public interface DealsService
{

	/**
	 * Gets the deal.
	 *
	 * @param dealCode
	 *           the deal code
	 * @return the deal
	 */
	DealModel getDeal(String dealCode);

	<T> T createImportContext();

	/**
	 * Determines is a deal is multirange
	 * @param dealConditions
	 * @return
	 */
	boolean isMultiRange(final List<AbstractDealConditionModel> dealConditions);


	/**
	 * Check if the deal condition is across
	 * @param dealCondition
	 * @return
	 */
	boolean isAcross(final DealConditionGroupModel dealCondition);


	/**
	 * Gets the deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param fromDate
	 *           the from date
	 * @param toDate
	 *           the to date
	 * @return the deals
	 */
	List<DealModel> getDeals(B2BUnitModel b2bUnitModel, Date fromDate, Date toDate);

	/**
	 * Gets the validated complex deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return the validated complex deals
	 */
	List<DealModel> getValidatedComplexDeals(B2BUnitModel b2bUnitModel);

	/**
	 * Gets the deals for product.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param productCode
	 *           the product code
	 * @param fromDate
	 *           the from date
	 * @param toDate
	 *           the to date
	 * @return the deals for product
	 */
	List<DealModel> getDealsForProduct(B2BUnitModel b2bUnitModel, List<String> productCode, Date fromDate, Date toDate);


	/**
	 * Find deal products removing any excluded product.
	 *
	 * @param dealConditions
	 *           the deal conditions
	 * @return the list
	 */
	List<ProductModel> findDealProducts(List<AbstractDealConditionModel> dealConditions);

	/**
	 * Generate deals code.
	 *
	 * @param param
	 *           the param
	 * @return the int
	 */
	int generateDealsCode(DealCodeGeneratorParam param);

	/**
	 * Generate trigger hash.
	 *
	 * @param dealModel
	 *           the deal model
	 * @return the int
	 */
	int generateTriggerHash(DealModel dealModel);

	/**
	 * Refresh deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 */
	void refreshDeals(B2BUnitModel b2bUnitModel);

	/**
	 * import deals from customerUnitPricingResponse
	 * @param b2BUnit
	 * @param deliveryDate
	 * @param customerUnitPricingResponse
	 */
	void importDeals(final B2BUnitModel b2BUnit, final Date deliveryDate, final CustomerUnitPricingResponse customerUnitPricingResponse);

	/**
	 * Refresh one off deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 */
	void refreshOneOffDeals(B2BUnitModel b2bUnitModel);

	/**
	 * To determine whether to exist deals by b2bunit,productCode,date.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param productCode
	 *           the product code
	 * @param fromDate
	 *           the from date
	 * @return true, if successful
	 */
	List<List<DealModel>> dealExistForProduct(B2BUnitModel b2bUnitModel, String productCode, Date fromDate);

	/**
	 * Deal belongs to product.
	 *
	 * @param deal
	 *           the deal
	 * @param material
	 *           the material
	 * @return true, if successful
	 */
	boolean productBelongsToDeal(DealModel deal, SABMAlcoholVariantProductMaterialModel material);

	/**
	 * Import complex deal.
	 *
	 * @param complexDeal
	 *           the complex deal
	 * @return the data import response
	 */
	void importComplexDeal(DealModel complexDeal, final ImportContext importContext);

	/**
	 * Normalize assignees.
	 */
	void normalizeAssigneesDeals();

	/**
	 * To compose the deals which have the same triggerHash.
	 *
	 * @param dealModels
	 *           the deal models
	 * @return List<DealModel>
	 */
	List<List<DealModel>> composeComplexFreeProducts(List<DealModel> dealModels);

	/**
	 * send the confirm email.
	 *
	 * @param behaviourRequirements
	 *           the behaviour requirements
	 * @param activatedDealTitles
	 *           the activated deal titles
	 * @param deactivatedDealTitles
	 *           the deactivated deal titles
	 * @param fromUser
	 *           the from User
	 * @param toEmails
	 *           the to emails
	 * @param ccEmails
	 *           the cc emails
	 * @param b2bUnit
	 *           the b2b unit
	 * @param primaryAdminStatus
	 *           the primary admin status
	 */
	void sendConfirmEnabledDealsEmail(String behaviourRequirements, List<String> activatedDealTitles,
			List<String> deactivatedDealTitles, UserModel fromUser, List<String> toEmails, List<String> ccEmails,
			B2BUnitModel b2bUnit, String primaryAdminStatus);

	/**
	 * Get Rep-Driven exclusive deals.
	 *
	 * @param b2bUnit
	 *           This is Customer B2BUnit
	 * @param inStore
	 *           This is Rep-Driven deal identifier
	 * @return List<DealModel>
	 */
	List<DealModel> getSpecificDeals(B2BUnitModel b2bUnit, boolean inStore);

	/**
	 * Gets the lost deal.
	 *
	 * @param cart
	 *           the cart
	 * @param entryNumber
	 *           the entry number
	 * @param quantity
	 *           the quantity
	 * @param uom
	 *           the uom
	 * @return the lost deal
	 */
	Map<String, List<ItemModel>> getLostDeal(CartModel cart, String entryNumber, int quantity, String uom);

	/**
	 * Get the on line deals.
	 *
	 * @param deals
	 *           This is complex deals
	 * @return List<DealModel> the result deals
	 */
	List<DealModel> filterOnlineDeals(Collection<DealModel> deals);

	/**
	 * Get the deals which have been active in the BDE.
	 *
	 * @param deals
	 *           This is complex deals
	 * @return List<DealModel> the result deals
	 */
	List<DealModel> getDealsByRepDrivenStatus(List<DealModel> deals);

	/**
	 * Get Validation qualified deals.
	 *
	 * @param deals
	 *           This is complex deals
	 * @param judgValidPeriod
	 *           boolean
	 * @return List<DealModel> the result deals
	 */
	List<DealModel> getValidationDeals(List<DealModel> deals, boolean judgValidPeriod);

	/**
	 * Get Validation qualified deals.
	 *
	 * @param deliveryDate
	 *           Customer specified delivery date
	 * @param deals
	 *           This is complex deals
	 * @param judgValidPeriod
	 *           boolean
	 * @return List<DealModel> the result deals
	 */
	public List<DealModel> getValidationDeals(Date deliveryDate, List<DealModel> deals, boolean judgValidPeriod);


	/**
	 * Checks if is only single bogof deal.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if is only single bogof deal
	 */
	boolean isOnlySingleBogofDeal(DealModel deal);

	/**
	 * Checks that the following aspects of the deal is valid: 1. Checks that complex deal conditions are valid 2. Checks
	 * that product deal condition is valid. 3. Checks that the free good benefits of the deal are valid.
	 *
	 * @param deal
	 *           the deal
	 * @return true if useable, false otherwise.
	 */
	boolean isValidDeal(final DealModel deal);

	/**
	 * Find limited deal with offer info.
	 *
	 * @param offerInfo
	 *           the offer info
	 * @param dealList
	 *           the deal list
	 * @param orderEntry
	 *           the order entry
	 * @return the deal model
	 */
	DealModel findLimitedDealWithOfferInfo(EntryOfferInfoModel offerInfo, List<DealModel> dealList,
			AbstractOrderEntryModel orderEntry);

	/**
	 * Find limited deal with offer info.
	 *
	 * @param offerInfoType
	 *           the offer info type
	 * @param dealList
	 *           the deal list
	 * @param entryProductCode
	 *           the entry product code
	 * @return the deal model
	 */
	DealModel findLimitedDealWithOfferInfo(String offerInfoType, List<DealModel> dealList, String entryProductCode);

	/**
	 * Find is deal is of discount type.
	 *
	 * @param deals
	 *           the deals
	 * @return true, if is discount deal exists
	 */
	boolean isDiscountDealExists(List<DealModel> deals);

	/**
	 * Checks if is manual scale proportion.
	 *
	 * @param deals
	 *           the deals
	 * @return true, if is manual scale proportion
	 */
	boolean isManualScaleProportion(List<DealModel> deals);

	/**
	 * Checks if is manual scale proportion.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if is manual scale proportion
	 */
	boolean isManualScaleProportion(DealModel deal);

	/**
	 * Checks if is manual scale proportion by each deal.
	 *
	 * @param benefitList
	 *           the benefit list
	 * @return true, if is manual scale proportion by each deal
	 */
	boolean isManualScaleProportionByEachDeal(List<AbstractDealBenefitModel> benefitList);

	/**
	 * Gets the scale.
	 *
	 * @param dealScales
	 *           the deal scales
	 * @param scale
	 *           the scale
	 * @return the scale
	 */
	Integer getScale(List<DealScaleModel> dealScales, String scale);

	/**
	 * Gets the validated complex deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return the validated complex deals
	 */
	List<DealModel> getValidatedNonComplexDeals(B2BUnitModel b2bUnitModel);




	/**
	 * Gets the titles of each deals given
	 *
	 * @param composedDeals the list of deals
	 * @raturn dealsTitles the deals title
	 *
	 * */
	List<String> getDealsTitles(List<List<DealModel>> composedDeals);

	/**
	 * @param deliveryDate
	 * @param judgeValidPeriod
	 * @return
	 */
	List<DealJson> searchDeals(Date deliveryDate, Boolean judgeValidPeriod);

	/**
	 * @param productCode
	 * @param dealJson
	 * @return
	 */
	boolean isProductBelongsToDeal(String productCode, DealJson dealJson);

	/**
	 * Get AbstractDealCondition assosciated to month back expired deals
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of AbstractDealCondition
	 */
	List<AbstractDealConditionModel> getAbstractDealCondition(final Date date, final int batchSize);

	/**
	 * Get AbstractDealBenefit assosciated to month back expired deals
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of AbstractDealBenefit
	 */
	List<AbstractDealBenefitModel> getAbstractDealBenefit(final Date date, final int batchSize);

	/**
	 * Get DealScales assosciated to month back expired deals
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of DealScales
	 */
	List<DealScaleModel> getDealsScales(final Date date, final int batchSize);

	/**
	 * Get DealConditionGroup assosciated to month back expired deals
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of DealConditionGroup
	 */
	List<DealConditionGroupModel> getDealConditionGroup(final Date date, final int batchSize);

	/**
	 * Returns DealConditionGroup with deals validity less or =  to date
	 * @param date
	 * @param batchSize
	 * @return
	 */
	List<DealConditionGroupModel> getDealConditionGroupForExpiredDeals(final Date date, final int batchSize);
	/**
	 * Get DealAssignee assosciated to month back expired deals
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of DealAssignee
	 */
	List<DealAssigneeModel> getDealAssignee(final Date date, final int batchSize);

	/**
	 * Returns DealAssignee for expired Deals based on date
	 * @param date
	 * @param batch
	 * @return
	 */
	List<DealAssigneeModel> getDealAssigneeForExpiredDeals(final Date date, final int batch);

	/**
	 * Get CartDealCondition assosciated to month back expired deals
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of CartDealCondition
	 */
	List<CartDealConditionModel> getCartDealCondition(final Date date, final int batchSize);

	/**
	 * Get CartDealCondition for expired deals based on date
	 * @param date
	 * @param batch
	 * @return
	 */
	List<CartDealConditionModel> getCartDealConditionForExpiredDeals(final Date date, final int batch);
	/**
	 * Get Expired deals a month back
	 *
	 * @param date
	 *           30 days before date
	 *
	 * @return List of Deals
	 */
	List<DealModel> getDealsbeforethirtydays(final Date date, final int batchSize);

	/**
	 * Check the valid period with specified deliveryDate.
	 *
	 * @param deliveryDate
	 *           the delivery date
	 * @param deal
	 *           the deal
	 * @param judgValidPeriod
	 *           the judg valid period
	 * @return true, if is validity period
	 */
	boolean isValidityPeriod(final Date deliveryDate, final DealModel deal, final boolean judgValidPeriod);

	void refreshDealCache();

	@FunctionalInterface
	interface ImportContext{
		<T> T getData();
	}

	/**
	 * @param b2bUnit
	 * @return
	 */
	List<AsahiDealModel> getSGASpecificDeals(AsahiB2BUnitModel b2bUnit);

	/**
	 * @param b2bUnitModel
	 * @param dealsToActivate
	 * @param dealsToRemove
	 * @param customerEmails
	 * @param dealsDetails
	 */
	void saveAsahiRepDealChange(AsahiB2BUnitModel b2bUnitModel, List<String> dealsToActivate, List<String> dealsToRemove,
			List<String> customerEmails, String dealsDetails);

	/**
	 * @param activatedDeals
	 */
	List<AsahiDealModel> getSGADealsForCode(List<String> activatedDeals);

	/**
	 * @param b2bUnit
	 * @return
	 */
	List<String> getCustomerEmails(AsahiB2BUnitModel b2bUnit);

	/**
	 * @param code
	 * @param b2bUnitModel
	 * @return
	 */
	List<AsahiDealModel> getSGADealsForProductAndUnit(String code, AsahiB2BUnitModel b2bUnitModel);

	/**
	 * @param b2bUnit
	 * @return
	 */
	List<AsahiDealModel> getCustomerSpecificDeals(AsahiB2BUnitModel b2bUnit);
}
