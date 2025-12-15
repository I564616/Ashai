/**
 *
 */
package com.sabmiller.core.cart.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.order.dao.CommerceCartDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.apb.core.model.OrderTemplateEntryModel;
import com.apb.core.model.OrderTemplateModel;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SabmCartRuleModel;


/**
 * @author joshua.a.antony
 *
 */
public interface SabmCommerceCartDao extends CommerceCartDao
{
	public List<CartModel> getCartsForSiteAndUserAndB2BUnit(BaseSiteModel site, UserModel user, B2BUnitModel b2bUnit);

	public CartModel getCartForSiteAndUserAndB2BUnit(final BaseSiteModel site, final UserModel user, final B2BUnitModel b2bUnit);

	public List<SabmCartRuleModel> getCustomCartRules();

	/**
	 * Gets the cart for code and B 2 B unit.
	 *
	 * @param code the code
	 * @param defaultB2BUnit the default B 2 B unit
	 * @return the cart for code and B 2 B unit
	 */
	List<OrderTemplateModel> getCartForCodeAndB2BUnit(String code, B2BUnitModel defaultB2BUnit);

	/**
	 * Gets the saved cart for code and B 2 B unit.
	 *
	 * @param pageableData the pageable data
	 * @param b2bUnit the b 2 b unit
	 * @return the saved cart for code and B 2 B unit
	 */
	SearchPageData<OrderTemplateModel> getSavedCartForCodeAndB2BUnit(
			PageableData pageableData, AsahiB2BUnitModel b2bUnit);

	/**
	 * Gets the order template for code and B 2 B unit.
	 *
	 * @param templateCode the template code
	 * @param defaultB2BUnit the default B 2 B unit
	 * @return the order template for code and B 2 B unit
	 */
	OrderTemplateModel getOrderTemplateForCodeAndB2BUnit(String templateCode,
			AsahiB2BUnitModel defaultB2BUnit);

	/**
	 * Gets the order template entry for PK.
	 *
	 * @param orderTemplateEntryPK the order template entry PK
	 * @return the order template entry for PK
	 */
	OrderTemplateEntryModel getOrderTemplateEntryForPK(
			String orderTemplateEntryPK);

	List<OrderEntryModel> getOrderEntriesForCustomerRule(final ProductModel productModel, final B2BUnitModel b2bUnitModel,
			final CMSSiteModel cmsSiteModel, final Map<String, Date> maxOrderQtyDatesMap);
	List<OrderEntryModel> getOrderEntriesForPlantRule(final ProductModel productModel, final PlantModel plantModel,
			final CMSSiteModel cmsSiteModel, final Map<String, Date> maxOrderQtyDatesMap);

	List<OrderEntryModel> getOrderEntriesForGlobalRule(final ProductModel productModel, final CMSSiteModel cmsSiteModel,
			final Map<String, Date> maxOrderQtyDatesMap);

	List<OrderTemplateModel> getAllSavedCartForB2BUnit(final AsahiB2BUnitModel b2bUnit);

}
