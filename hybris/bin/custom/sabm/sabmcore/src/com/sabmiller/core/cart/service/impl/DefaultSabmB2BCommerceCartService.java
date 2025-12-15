/**
 *
 */
package com.sabmiller.core.cart.service.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.order.impl.DefaultB2BCommerceCartService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.model.ApbProductModel;
import com.apb.core.model.OrderTemplateEntryModel;
import com.apb.core.model.OrderTemplateModel;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.product.strategy.AsahiInclusionExclusionProductStrategy;
import com.sabmiller.core.cart.dao.SabmCommerceCartDao;
import com.sabmiller.core.cart.service.SABMB2BCommerceCartService;
import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * @author joshua.a.antony
 *
 */
public class DefaultSabmB2BCommerceCartService extends DefaultB2BCommerceCartService implements SABMB2BCommerceCartService
{

	/** The asahi commerce cart dao. */
	@Resource(name = "commerceCartDao")
	private SabmCommerceCartDao sabmCommerceCartDao;

	/** The cart service. */
	@Resource(name = "cartService")
	private CartService cartService;

	/** The product service. */
	@Resource(name = "productService")
	private ProductService productService;

	/** The inclusion exclusion product strategy. */
	@Resource(name = "inclusionExclusionProductStrategy")
	private AsahiInclusionExclusionProductStrategy inclusionExclusionProductStrategy;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.cart.service.SABMCommerceCartService#getCartsForSiteAndUserAndB2BUnit(de.hybris.platform.
	 * basecommerce.model.site.BaseSiteModel, de.hybris.platform.core.model.user.UserModel,
	 * de.hybris.platform.b2b.model.B2BUnitModel)
	 */


	@Override
	public List<CartModel> getCartsForSiteAndUserAndB2BUnit(final BaseSiteModel site, final UserModel user,
			final B2BUnitModel b2bUnit)
	{
		return ((SabmCommerceCartDao) getCommerceCartDao()).getCartsForSiteAndUserAndB2BUnit(site, user, b2bUnit);
	}

	@Override
	public CartModel getCartForSiteAndUserAndB2BUnit(final BaseSiteModel site, final UserModel user, final B2BUnitModel b2bUnit)
	{
		return ((SabmCommerceCartDao) getCommerceCartDao()).getCartForSiteAndUserAndB2BUnit(site, user, b2bUnit);
	}

	/**
	 * Gets the cart for code and B 2 B unit.
	 *
	 * @param code
	 *           the code
	 * @param defaultB2BUnit
	 *           the default B 2 B unit
	 * @return the saved carts for name
	 */
	@Override
	public List<OrderTemplateModel> getCartForCodeAndB2BUnit(final String code, final B2BUnitModel defaultB2BUnit)
	{
		return this.sabmCommerceCartDao.getCartForCodeAndB2BUnit(code, defaultB2BUnit);
	}

	/**
	 * Gets the saved cart for code and B 2 B unit.
	 *
	 * @param pageableData
	 *           the pageable data
	 * @param b2bUnit
	 *           the b 2 b unit
	 * @return the saved carts for name
	 */
	@Override
	public SearchPageData<OrderTemplateModel> getSavedCartForCodeAndB2BUnit(final PageableData pageableData,
			final AsahiB2BUnitModel b2bUnit)
	{
		return this.sabmCommerceCartDao.getSavedCartForCodeAndB2BUnit(pageableData, b2bUnit);
	}
	
	
	/**
	 * Gets the saved cart for code and B 2 B unit.
	 *
	 * @param b2bUnit
	 *           the b 2 b unit
	 * @return the saved carts for name
	 */
	@Override
	public List<OrderTemplateModel> getAllSavedCartForB2BUnit(final AsahiB2BUnitModel b2bUnit)
	{
		return this.sabmCommerceCartDao.getAllSavedCartForB2BUnit(b2bUnit);
	}

	/**
	 * Gets the order template for code and B 2 B unit.
	 *
	 * @param templateCode
	 *           the template code
	 * @param defaultB2BUnit
	 *           the default B 2 B unit
	 * @return the order template for code and B 2 B unit
	 */
	@Override
	public OrderTemplateModel getOrderTemplateForCodeAndB2BUnit(final String templateCode, final AsahiB2BUnitModel defaultB2BUnit)
	{
		return this.sabmCommerceCartDao.getOrderTemplateForCodeAndB2BUnit(templateCode, defaultB2BUnit);
	}

	/**
	 * Gets the order template entry for PK.
	 *
	 * @param orderTemplateEntryPK
	 *           the order template entry PK
	 * @return the order template for code and B 2 B unit
	 */
	@Override
	public OrderTemplateEntryModel getOrderTemplateEntryForPK(final String orderTemplateEntryPK)
	{
		return this.sabmCommerceCartDao.getOrderTemplateEntryForPK(orderTemplateEntryPK);
	}

	/**
	 * Reorder entries for order template.
	 *
	 * @param orderTemplateId
	 *           the order template id
	 * @param defaultB2BUnit
	 *           the default B 2 B unit
	 *
	 */
	@Override
	public void reorderEntriesForOrderTemplate(final String orderTemplateId, final AsahiB2BUnitModel defaultB2BUnit,
			final boolean keepCart)
	{
		final OrderTemplateModel orderTemplate = this.sabmCommerceCartDao.getOrderTemplateForCodeAndB2BUnit(orderTemplateId,
				defaultB2BUnit);

		if (this.cartService.hasSessionCart())
		{
			int entryNumber = 0;
			List<AbstractOrderEntryModel> completeEntries = null;
			if (keepCart)
			{
				final List<AbstractOrderEntryModel> entries = this.cartService.getSessionCart().getEntries();
				completeEntries = new ArrayList<AbstractOrderEntryModel>(entries);
				entryNumber = entries.size();
			}
			else
			{
				this.cartService.removeSessionCart();
				completeEntries = new ArrayList<AbstractOrderEntryModel>();
			}

			completeEntries = this.addEntriesToCurrentCart(completeEntries, orderTemplate, entryNumber);
			this.cartService.getSessionCart().setEntries(completeEntries);
			final CartModel cartModel = this.cartService.getSessionCart();

			getModelService().save(cartModel);
		}

	}

	/**
	 * Adds the entries to current cart.
	 *
	 * @param completeEntries
	 *
	 * @param orderTemplate
	 *           the order template
	 * @param entryNumber
	 *           the entry number
	 * @return
	 */
	private List<AbstractOrderEntryModel> addEntriesToCurrentCart(final List<AbstractOrderEntryModel> completeEntries,
			final OrderTemplateModel orderTemplate, int entryNumber)
	{
		if (null != orderTemplate && CollectionUtils.isNotEmpty(orderTemplate.getTemplateEntry()))
		{

			for (final OrderTemplateEntryModel templateEntry : orderTemplate.getTemplateEntry())
			{
				final CartModel cartModel = this.cartService.getSessionCart();
				final List<CartEntryModel> entriesList = this.cartService.getEntriesForProduct(cartModel, templateEntry.getProduct());

				//Check if the Non Bonus Product exists in the cart...
				final CartEntryModel entry = getNonBonusEntry(entriesList);

				if (null != entry)
				{
					final CartEntryModel cartEntry = entry;
					final long oldQty = cartEntry.getQuantity();
					long newQty = oldQty;

					//Update the allowed entry to be added upto the maximum limit of the product for SGA...
					final Long maxProductQty = asahiSiteUtil.getSgaGlobalMaxOrderQty();
					if (asahiSiteUtil.isSga() && (templateEntry.getQuantity() > (maxProductQty - oldQty)))
					{
						newQty = oldQty + (maxProductQty - oldQty);

					}
					else
					{
						newQty = oldQty + templateEntry.getQuantity();
					}

					cartEntry.setQuantity(newQty);
					getModelService().save(cartEntry);

				}
				else
				{
					final ApbProductModel product = (ApbProductModel) templateEntry.getProduct();
					if (null != product && (this.inclusionExclusionProductStrategy.isProductIncluded(product.getCode())
							&& product.isActive() && product.getApprovalStatus().equals(ArticleApprovalStatus.APPROVED)))
					{
						final CartEntryModel newCartEntry = getModelService().create(CartEntryModel.class);

						newCartEntry.setEntryNumber(++entryNumber);
						newCartEntry.setBasePrice(templateEntry.getBasePrice());
						newCartEntry.setTotalPrice(templateEntry.getTotalPrice());
						newCartEntry.setQuantity(templateEntry.getQuantity());

						final ProductModel productModel = templateEntry.getProduct();
						newCartEntry.setProduct(productModel);

						if (null != productModel.getUnit())
						{
							newCartEntry.setUnit(productModel.getUnit());
						}
						else
						{
							newCartEntry.setUnit(this.productService.getOrderableUnit(productModel));
						}

						final AbstractOrderModel orderModel = this.cartService.getSessionCart();
						newCartEntry.setOrder(orderModel);

						completeEntries.add(newCartEntry);
					}
				}
			}
		}
		return completeEntries;
	}

	/**
	 * The method will return the Non Bonus Entry from the session cart if the two entry has the same product code for
	 * non bonus and bonus product.
	 *
	 * @return CartEntryModel
	 */
	@Override
	public CartEntryModel getNonBonusEntry(final List<CartEntryModel> entriesList)
	{
		if (CollectionUtils.isNotEmpty(entriesList))
		{
			for (final CartEntryModel entry : entriesList)
			{
				if (!entry.getIsBonusStock())
				{
					return entry;
				}
			}
		}
		return null;
	}

}
