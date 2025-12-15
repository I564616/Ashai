/**
 *
 */
package com.sabmiller.facades.b2bunit;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.company.B2BCommerceUnitFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;

import java.util.Collection;
import java.util.List;

import com.sabmiller.facades.order.data.OrderTemplateData;


/**
 * The Interface SabmB2BCommerceUnitFacade.
 */
public interface SabmB2BCommerceUnitFacade extends B2BCommerceUnitFacade
{

	/**
	 * B2b unit exist.
	 *
	 * @param unitId
	 *           the unit id
	 * @return true, if successful
	 */
	boolean b2bUnitExist(String unitId);

	/**
	 * Creates the b2 b unit.
	 *
	 * @param b2bUnitData
	 *           the b2b unit data
	 */
	void createB2BUnit(final B2BUnitData b2bUnitData);

	/**
	 * Update b2 b unit.
	 *
	 * @param b2bUnitData
	 *           the b2b unit data
	 */
	void updateB2BUnit(final B2BUnitData b2bUnitData, final B2BUnitModel b2BUnit);

	/**
	 * SAB-535
	 *
	 * Method for get B2bUnit of User.
	 *
	 * @param unitId
	 *           the unit id
	 * @return the b2b unit data
	 */
	de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData getB2bUnitData(String unitId);

	/**
	 * Gets the session B2BUnit order templates and convert to @OrderTemplateData.
	 *
	 * @return the list of @OrderTemplateData related to the session b2bUnit
	 */
	List<OrderTemplateData> getB2BUnitOrderTemplates();

	/**
	 * Gets order template detail by code and the session B2BUnit.
	 *
	 * @param orderCode
	 *           the order code
	 * @return the @OrderTemplateData related to the session b2bUnit and the param orderCode.
	 */
	OrderTemplateData getB2BUnitOrderTemplateDetail(String orderCode);

	/**
	 * Creates the order template by cart.
	 *
	 * @param orderName
	 *           the order name
	 * @return true, if successful
	 */
	boolean createOrderTemplateByCart(String orderName);

	/**
	 * Adds the product to template.
	 *
	 * @param orderCode
	 *           the order code
	 * @param productCode
	 *           the product code
	 * @param fromUnit
	 *           the from unit
	 * @param quantity
	 *           the quantity
	 * @return true, if successful
	 */
	boolean addProductToTemplate(String orderCode, String productCode, String fromUnit, Long quantity);

	/**
	 * Removes the product order template.
	 *
	 * @param orderCode
	 *           the order code
	 * @param entryNumber
	 *           the entry number
	 * @return the order template data
	 */
	OrderTemplateData removeProductOrderTemplate(String orderCode, long entryNumber);

	/**
	 * Update product to template.
	 *
	 * @param orderCode
	 *           the order code
	 * @param entryNumber
	 *           the entry number
	 * @param quantity
	 *           the quantity
	 * @param fromUnit
	 *           the from unit
	 * @return true, if successful
	 */
	boolean updateProductToTemplate(String orderCode, long entryNumber, long quantity, String fromUnit);

	/**
	 * Update product to template name.
	 *
	 * @param orderCode
	 *           the order code
	 * @param orderName
	 *           the order name
	 * @return true, if successful
	 */
	boolean updateProductToTemplateName(String orderCode, String orderName);

	/**
	 * Update the minimum stock on hand of order template entry.
	 *
	 * @param orderCode
	 *           the order template code
	 * @param entryNumber
	 *           the number of the entry need to be updated
	 * @param minimumStock
	 *           the new quantity of the entry
	 * @return
	 */
	public boolean updateMinimumStock(String orderCode, final long entryNumber, Integer minimumStock);

	/**
	 * Move order templates.
	 *
	 * @param orderCode
	 *           the order code
	 * @param directionUp
	 *           the direction up
	 * @return true, if successful
	 */
	boolean moveOrderTemplates(String orderCode, boolean directionUp);


	/**
	 * Delete template.
	 *
	 * @param orderCode
	 *           the order code
	 * @return true, if successful
	 */
	boolean deleteTemplate(final String orderCode);


	/**
	 * Get all the b2bUnits from the CurrentUse.
	 *
	 * @return the b2 b units
	 */
	List<B2BUnitData> getB2BUnits();

	/**
	 * Gets the session B2BUnit order templates and sorts them by name.
	 *
	 * @param sortAsc
	 *           the sort asc
	 * @return the order templates name sorted
	 */
	List<OrderTemplateData> getOrderTemplatesNameSorted(boolean sortAsc);

	/**
	 * Gets the current b2 b unit id.
	 *
	 * @return the current b2 b unit id
	 */
	String getCurrentB2BUnitId();

	/**
	 * Checks if is cut off time exceeded.
	 *
	 * @return true, if is cut off time exceeded
	 */
	boolean isCutOffTimeExceeded();

	/**
	 * Checks if is cut off time exceeded.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return true, if is cut off time exceeded
	 */
	boolean isCutOffTimeExceeded(B2BUnitModel b2bUnitModel);

	/**
	 * Creates the empty order template by Name.
	 *
	 * @param templateName
	 *           the template name
	 * @return template code
	 */
	String createEmptyOrderTemplateByName(String templateName);

	/**
	 * Creates the order template by order entry.
	 *
	 * @param orderName
	 *           the order name
	 * @param orderCode
	 *           the order code
	 * @return true, if create success
	 */
	boolean createOrderTemplateByOrder(String orderName, String orderCode);

	/**
	 * Get all the sub B2BUnit under the ZADP, according to the Region group.
	 *
	 * @return All of the region under the ZADP country
	 */
	List<RegionData> getSubB2BUnitForZADP();

	/**
	 * Get user B2BUnits.
	 *
	 * @param uid
	 *           This is B2BCustomerModel{@link de.hybris.platform.b2b.model.B2BCustomerModel}}
	 * @return List of units{@link B2BUnitData} in the customer
	 */
	List<B2BUnitData> getB2BUnitsByCustomer(String uid);

	/**
	 * Check if the B2BUnit belongs to current customer in session.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @return true, if successful
	 */
	boolean b2bUnitBelongsToCurrentCustomer(String b2bUnit);

	/**
	 * Get user B2BUnits.
	 *
	 * @param uid
	 *           This is B2BCustomerModel{@link de.hybris.platform.b2b.model.B2BCustomerModel}}
	 * @return List of units{@link B2BUnitData} in the customer
	 */
	List<B2BUnitData> getZalbB2BUnitsByCustomer(String uid);

	/**
	 * The current user B2BUnit whether the customer.
	 *
	 * @param uid
	 *           This is B2BCustomerModel{@link de.hybris.platform.b2b.model.B2BCustomerModel}}
	 * @return true, if Exist
	 */
	boolean isCurrentB2BUnitExistOfCustomer(String uid);

	/**
	 * Gets the current user ZADP B2BUnit.
	 *
	 * @return ZADP B2BUnit
	 */
	de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData getZADPB2BUnitByCurrentCustomer();

	/**
	 * Gets the b2b unit data except zadp user.
	 *
	 * @param unitId
	 *           the unit id
	 * @return the b2b unit data except zadp user
	 */
	B2BUnitData getB2bUnitDataExceptZADPUser(String unitId);


	/**
	 * Retrieve the entire B2bUnits of current user.
	 *
	 * @author Ross
	 * @return the entire b2b units
	 */
	List<B2BUnitData> getEntireB2bUnits();


	/**
	 * Retrieve the root B2bUnit of current user.
	 *
	 * @author Ross
	 * @return the root b2b unit
	 */
	de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData getRootB2bUnit();

	/**
	 * Get none zadp users under the current user's zadp business unit.
	 *
	 * @author Ross
	 * @return the none zadp users
	 */
	List<CustomerData> getNoneZADPUsers();

	/**
	 * The current user B2BUnit whether the customer.
	 *
	 * @param uid
	 *           This is B2BCustomerModel{@link de.hybris.platform.b2b.model.B2BCustomerModel}}
	 * @return true, if Exist
	 */
	boolean isCurrentB2BUnitExistOfUid(String uid);

	/**
	 * Get current user ZADP B2BUnit.
	 *
	 * @return B2BUnitData
	 */
	de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData getTopLevelB2BUnit();

	List<de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData> getBranchesForCustomer(final String uid);

	/**
	 * Move order entries in the order template.
	 *
	 * @param orderCode
	 *           the order code
	 * @param entryNumber
	 *           the entry number of the orderEntryData
	 * @param newEntryNum
	 *           the new entryNumber of the orderEntry
	 * @return true, if successful
	 * @throws CommerceCartModificationException
	 */
	boolean moveOrderEntry(String orderCode, Integer entryNumber, Integer newEntryNum);

	/**
	 * Get all users along with ZADP users of the current user business .
	 *
	 * @author Ross
	 * @return the none zadp users
	 */
	List<CustomerData> getUsersWithZADP();


	/**
	 * @param orderCode
	 * @return
	 */
	boolean addOrderTemplateToCart(String orderCode);


	/**
	 * @param orderCode
	 * @param orderTemplateEntryList
	 * @return
	 */
	List<CartModificationData> addOrderTemplateToCart(String orderCode, List<OrderEntryData> orderTemplateEntryList);


	de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData getB2bUnitDataOnlyForInvoiceUser(final String unitId, final boolean bdeUser);

	void removeSapUnavailabilityEntriesFromTemplate(SABMOrderTemplateModel orderTemplateModel);

	/**
	 * @param customerData
	 * @param b2bUnitModel
	 */
	void setActiveStatus(Collection<CustomerData> customerData, String b2bUnitId);
}
