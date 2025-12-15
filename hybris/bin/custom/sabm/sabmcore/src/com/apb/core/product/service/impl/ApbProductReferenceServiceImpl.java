package com.apb.core.product.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.apb.core.model.AlcoholTypeModel;
import com.apb.core.model.BrandModel;
import com.apb.core.model.FlavourModel;
import com.apb.core.model.ItemGroupsModel;
import com.apb.core.model.PackageSizeModel;
import com.apb.core.model.PackageTypeModel;
import com.apb.core.model.ProductGroupModel;
import com.apb.core.model.SubProductGroupModel;
import com.apb.core.model.UnitVolumeModel;
import com.apb.core.product.dao.ApbProductReferenceDao;
import com.apb.core.product.service.ApbProductReferenceService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;


/**
 * The Class ApbProductReferenceServiceImpl.
 * 
 * @author Kuldeep.Singh1
 */
public class ApbProductReferenceServiceImpl implements ApbProductReferenceService
{

	/** The apb product reference dao. */
	@Resource(name = "apbProductReferenceDao")
	private ApbProductReferenceDao apbProductReferenceDao;

	/**
	 * Gets the alcohol type for code.
	 *
	 * @param code
	 *           the code
	 * @return the alcohol type for code
	 */
	@Override
	public AlcoholTypeModel getAlcoholTypeForCode(final String code)
	{
		return this.apbProductReferenceDao.getAlcoholTypeForCode(code);
	}

	/**
	 * Gets the item group for code.
	 *
	 * @param code
	 *           the code
	 * @return the item group for code
	 */
	@Override
	public ItemGroupsModel getItemGroupForCode(final String code)
	{
		return this.apbProductReferenceDao.getItemGroupForCode(code);
	}

	/**
	 * Gets the package type for code.
	 *
	 * @param code
	 *           the code
	 * @return the package type for code
	 */
	@Override
	public PackageTypeModel getPackageTypeForCode(final String code)
	{
		return this.apbProductReferenceDao.getPackageTypeForCode(code);
	}

	/**
	 * Gets the flavour for code.
	 *
	 * @param code
	 *           the code
	 * @return the flavour for code
	 */
	@Override
	public FlavourModel getFlavourForCode(final String code)
	{
		return this.apbProductReferenceDao.getFlavourForCode(code);
	}

	/**
	 * Gets the brand for code.
	 *
	 * @param code
	 *           the code
	 * @return the brand for code
	 */
	@Override
	public BrandModel getBrandForCode(final String code)
	{
		return this.apbProductReferenceDao.getBrandForCode(code);
	}


	/**
	 * Gets the product for code.
	 *
	 * @param code
	 *           the code
	 * @return the product for code
	 */
	@Override
	public ProductModel getProductForCode(final CatalogVersionModel catalogVersionModel, final String code)
	{
		if (null != catalogVersionModel && null != code)
		{
			final List<ProductModel> products = this.apbProductReferenceDao.findProductsByCode(catalogVersionModel, code);
			if (CollectionUtils.isNotEmpty(products))
			{
				return products.get(0);
			}
		}

		return null;
	}

	/**
	 * Gets the unit for code.
	 *
	 * @param code
	 *           the code
	 * @return the unit for code
	 */
	@Override
	public UnitModel getUnitForCode(final String code)
	{
		return this.apbProductReferenceDao.getUnitForCode(code);
	}

	/**
	 * Gets the currency by iso code.
	 *
	 * @param currencyIso
	 *           the currency iso
	 * @return the currency by iso code
	 */
	@Override
	public CurrencyModel getCurrencyForIsoCode(final String currencyIso)
	{
		return this.apbProductReferenceDao.getCurrencyForIsoCode(currencyIso);
	}

	/**
	 * Gets the package size for code.
	 *
	 * @param code
	 *           the code
	 * @return the package size for code
	 */
	@Override
	public PackageSizeModel getPackageSizeForCode(final String code)
	{
		return this.apbProductReferenceDao.getPackageSizeForCode(code);
	}

	/**
	 * Gets the unit volume for code.
	 *
	 * @param code
	 *           the code
	 * @return the unit volume for code
	 */
	@Override
	public UnitVolumeModel getUnitVolumeForCode(final String code)
	{
		return this.apbProductReferenceDao.getUnitVolumeForCode(code);
	}

	/**
	 * Gets the sub product group for code.
	 *
	 * @param code
	 *           the code
	 * @return the sub product group for code
	 */
	@Override
	public SubProductGroupModel getSubProductGroupForCode(final String code)
	{
		return this.apbProductReferenceDao.getSubProductGroupForCode(code);
	}

	/**
	 * Gets the order entry by product and order id.
	 *
	 * @param orderEntryWET
	 *           the order entry WET
	 * @param orderId
	 *           the order id
	 * @return the order entry by product and order id
	 */
	@Override
	public List<OrderEntryModel> getOrderEntryByProductAndOrderId(final String productId, final String orderId)
	{
		return this.apbProductReferenceDao.getOrderEntryByProductAndOrderId(productId, orderId);
	}

	/**
	 * Gets the sub product group for code.
	 *
	 * @param code
	 *           the code
	 * @return the sub product group for code
	 */
	@Override
	public ProductGroupModel getProductGroupForCode(final String code)
	{
		return this.apbProductReferenceDao.getProductGroupForCode(code);
	}
	
	/** Get the last 3 monts orders
	 * 
	 * @param user the current user
	 */
	@Override
	public List<OrderModel> getRecommendedProducts(final CustomerModel user){
		return this.apbProductReferenceDao.findPreviousOrderEntries(user);
	}
}
