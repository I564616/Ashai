/**
 *
 */
package com.sabmiller.core.cart.service;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.order.B2BCommerceCartService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.List;

import com.apb.core.model.OrderTemplateEntryModel;
import com.apb.core.model.OrderTemplateModel;
import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * @author joshua.a.antony
 *
 */
public interface SABMB2BCommerceCartService extends B2BCommerceCartService
{

	public List<CartModel> getCartsForSiteAndUserAndB2BUnit(BaseSiteModel site, UserModel user, B2BUnitModel b2bUnit);

	public CartModel getCartForSiteAndUserAndB2BUnit(final BaseSiteModel site, final UserModel user, final B2BUnitModel b2bUnit);
	
	/**
	 * Gets the cart for code and B 2 B unit.
	 *
	 * @param cartId
	 *           the cart id
	 * @param defaultB2BUnit
	 *           the default B 2 B unit
	 * @return the cart for code and B 2 B unit
	 */
	List<OrderTemplateModel> getCartForCodeAndB2BUnit(String cartId, B2BUnitModel defaultB2BUnit);

	SearchPageData<OrderTemplateModel> getSavedCartForCodeAndB2BUnit(PageableData pageableData, AsahiB2BUnitModel b2bUnit);

	/**
	 * Gets the order template for code and B 2 B unit.
	 *
	 * @param templateCode
	 *           the template code
	 * @param defaultB2BUnit
	 *           the default B 2 B unit
	 * @return the order template for code and B 2 B unit
	 */
	OrderTemplateModel getOrderTemplateForCodeAndB2BUnit(String templateCode, AsahiB2BUnitModel defaultB2BUnit);

	/**
	 * Gets the order template entry for PK.
	 *
	 * @param orderTemplateEntryPK
	 *           the order template entry PK
	 * @return the order template entry for PK
	 */
	OrderTemplateEntryModel getOrderTemplateEntryForPK(String orderTemplateEntryPK);

	/**
	 * Reorder entries for order template.
	 *
	 * @param orderTemplateId
	 *           the order template id
	 * @param defaultB2BUnit
	 *           the default B 2 B unit
	 * @param keepCart
	 *           the keep cart
	 */
	void reorderEntriesForOrderTemplate(String orderTemplateId, AsahiB2BUnitModel defaultB2BUnit, boolean keepCart);

	/**
	 * The method will return the Non Bonus Entry from the session cart if the two entry has the same product code for
	 * non bonus and bonus product.
	 * 
	 * @param entriesList
	 *
	 * @return CartEntryModel
	 */
	public CartEntryModel getNonBonusEntry(final List<CartEntryModel> entriesList);
	
	
	List<OrderTemplateModel> getAllSavedCartForB2BUnit(final AsahiB2BUnitModel b2bUnit);
}


