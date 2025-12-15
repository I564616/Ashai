/**
 *
 */
package com.sabmiller.core.b2b.services;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;


/**
 * The Interface SabmOrderTemplateService.
 */
public interface SabmOrderTemplateService
{

	/**
	 * Find order template by code and b2bUnit.
	 *
	 * @param code
	 *           the code
	 * @param b2bUnit
	 *           the b2b unit
	 * @return the SABM order template model
	 * @throws AmbiguousIdentifierException
	 *            the ambiguous identifier exception
	 * @throws UnknownIdentifierException
	 *            the unknown identifier exception
	 */
	SABMOrderTemplateModel findOrderTemplateByCode(String code, B2BUnitModel b2bUnit);

	/**
	 * Find order template by name and b2bUnit.
	 *
	 * @param name
	 *           the name
	 * @param b2bUnit
	 *           the b2b unit
	 * @return the SABM order template model
	 * @throws AmbiguousIdentifierException
	 *            the ambiguous identifier exception
	 * @throws UnknownIdentifierException
	 *            the unknown identifier exception
	 */
	SABMOrderTemplateModel findOrderTemplateByName(String name, B2BUnitModel b2bUnit);

	/**
	 * Find template by b2 b unit and sequence.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @param sequence
	 *           the sequence
	 * @return the list of order templates or empty list if no template is found.
	 */
	List<SABMOrderTemplateModel> findTemplateByB2BUnitAndSequence(B2BUnitModel b2bUnit, Integer sequence);

	/**
	 * Adds the product to order template.
	 *
	 * @param orderCode
	 *           the order code
	 * @param productCode
	 *           the product code
	 * @param fromUnit
	 *           the from unit
	 * @param quantity
	 *           the quantity
	 * @return the commerce cart modification
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	CommerceCartModification addProductToOrderTemplate(String orderCode, String productCode, String fromUnit, Long quantity)
			throws CommerceCartModificationException;

	CommerceCartModification addProductToOrderTemplate(String orderCode, String productCode, String fromUnit, Long quantity,
			B2BUnitModel b2bUnitModel) throws CommerceCartModificationException;

	CommerceCartModification addProductToOrderTemplate(SABMOrderTemplateModel templateModel, String productCode, String fromUnit,
			Long quantity) throws CommerceCartModificationException;

	CommerceCartModification addProductToOrderTemplate(SABMOrderTemplateModel templateModel, String fromUnit, Long quantity,
			ProductModel productModel) throws CommerceCartModificationException;

	/**
	 * Creates the order template by the session cart.
	 *
	 * @param orderName
	 *           the order name
	 * @return the SABM order template model
	 */
	SABMOrderTemplateModel createOrderTemplateFromSessionCart(String orderName);

	/**
	 * Delete order template.
	 *
	 * @param orderCode
	 *           the order code
	 * @return true, if successful
	 */
	boolean deleteOrderTemplate(final String orderCode);

	public void createOrderTempletWithCoreProductRange(B2BUnitModel b2bUnitModel) throws CommerceCartModificationException;

	public void createEmptyOrderTemplate(B2BUnitModel b2bUnitModel);


	/**
	 * Create Empty Order Template by name
	 *
	 * @param templateName
	 *           the template code
	 * @return the SABM order template model
	 */
	public SABMOrderTemplateModel createEmptyOrderTemplateForCurrentUnit(String templateName);


	/**
	 * Create Order Template by Order Detail info
	 *
	 * @param orderName
	 * @param orderCode
	 * @return the SABM order template model
	 */
	public SABMOrderTemplateModel createOrderTemplateByOrderCode(String orderName, String orderCode);

	/**
	 * Update quantity for the Order Template entry with given <code>entryNumber</code> with the given SABMC-904
	 * <code>newQuantity</code>. Then cart is calculated.
	 *
	 * @return the cart modification data that includes a statusCode and the actual quantity that the entry was updated
	 *         to
	 * @throws CommerceCartModificationException
	 *            if the <code>product</code> is a base product OR the quantity is less than 1 or no usable unit was
	 *            found (only when given <code>unit</code> is also <code>null</code>) or any other reason the cart could
	 *            not be modified.
	 */
	CommerceCartModification updateQuantityForOrderTemplateEntry(final CommerceCartParameter parameter)
			throws CommerceCartModificationException;

	/**
	 * Service for remove the order template entry SABMC-904
	 *
	 * @param cartModel
	 *           the cart Model
	 * @param entryToUpdate
	 *           the entry To Update
	 * @return CommerceCartModification
	 */
	CommerceCartModification removeEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate);

}
