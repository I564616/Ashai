/**
 *
 */
package com.sabmiller.core.product;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;


/**
 * SAB-560, This is PriceRow service impl class.
 *
 * @author xue.zeng
 */
public class DefaultSabmPriceRowService extends AbstractBusinessService implements SabmPriceRowService
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmPriceRowService.class);

	/** The product service. */
	private ProductService productService;

	/** The common i18 n service. */
	private CommonI18NService commonI18NService;

	/** The user service. */
	private UserService userService;

	/** The sabm price row dao. */
	private SabmPriceRowDao sabmPriceRowDao;

	/** The b2b unit service. */
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	/** The b2b commerce unit service. */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "sessionService")
	private SessionService sessionService;



	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmPriceRowService#getPriceRowByProdAndCustAndCry(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public PriceRowModel getPriceRowByProduct(final String code)
	{
		validateParameterNotNullStandardMessage("product code", code);

		final ProductModel product = getProductService().getProductForCode(code);
		return getPriceRowByProduct(product);
	}

	@Override
	public PriceRowModel getPriceRowByProductCode(final String code){
		validateParameterNotNullStandardMessage("product code", code);
		if (!(userService.getCurrentUser() instanceof B2BCustomerModel))
		{
			return null;
		}

		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();
		return getPriceRowByProductCodeAndUnit(code, b2bUnit);
	}

	@Override
	public PriceRowModel getPriceRowByProductCodeAndUnit(String producCode, B2BUnitModel b2BUnit) {

		validateParameterNotNullStandardMessage("product code", producCode);
		validateParameterNotNullStandardMessage("b2bUnit ", b2BUnit);


		final PriceRowModel priceRow = getPriceRowByCodeAndUnitWithSessionDeliveryDate(producCode,b2BUnit);
		if (null != priceRow)
		{
			return priceRow;
		}

		LOG.warn("Product[{}] doesn't have a pricerow.", producCode);

		return null;
	}


	/**
	 * Helper Method Only
	 * @param code
	 * @param unit
	 * @return
	 */
	protected PriceRowModel getPriceRowByCodeAndUnitWithSessionDeliveryDate(final String code,final B2BUnitModel unit){

		final UserPriceGroup userPriceGroup = UserPriceGroup.valueOf(unit.getUid());
		if (getModelService().isNew(userPriceGroup))
		{
			getModelService().save(userPriceGroup);
		}

		final Date currentDeliveryDate =  sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);

		return getSabmPriceRowDao().getPriceRow(userPriceGroup, code, currentDeliveryDate);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmPriceRowService#getPriceRowByProduct(java.lang.String, java.lang.String)
	 */
	@Override
	public PriceRowModel getPriceRowByProduct(final String code, final String b2bUnitId)
	{
		validateParameterNotNullStandardMessage("product code", code);
		validateParameterNotNullStandardMessage("b2bUnitId", b2bUnitId);
		return getPriceRowByProduct(getProductService().getProductForCode(code), b2bUnitService.getUnitForUid(b2bUnitId));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmPriceRowService#getPriceRowByProduct(de.hybris.platform.core.model.product.
	 * ProductModel)
	 */
	@Override
	public PriceRowModel getPriceRowByProduct(final ProductModel product)
	{
		validateParameterNotNullStandardMessage("product code", product);
		if (!(userService.getCurrentUser() instanceof B2BCustomerModel))
		{
			return null;
		}

		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();
		return getPriceRowByProduct(product, b2bUnit);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmPriceRowService#getPriceRowByProduct(de.hybris.platform.core.model.product.
	 * ProductModel, de.hybris.platform.b2b.model.B2BUnitModel)
	 */
	@Override
	public PriceRowModel getPriceRowByProduct(final ProductModel product, final B2BUnitModel b2bUnit)
	{
		validateParameterNotNullStandardMessage("product code", product);
		validateParameterNotNullStandardMessage("b2bUnit ", b2bUnit);


		//final CurrencyModel currentCurrency = getCommonI18NService().getCurrentCurrency();
		//get priceRow by user,currency and product.
		final PriceRowModel priceRow = getPriceRow(b2bUnit.getUid(), product);
		if (null != priceRow)
		{
			return priceRow;
		}

		LOG.warn("Product[{}] doesn't have a pricerow.", product.getCode());

		return null;
	}

	/**
	 * Gets the product service.
	 *
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * Sets the product service.
	 *
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * Gets the common i18 n service.
	 *
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}


	/**
	 * Sets the common i18 n service.
	 *
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}


	/**
	 * Gets the user service.
	 *
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}


	/**
	 * Sets the user service.
	 *
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmPriceRowService#getPriceRows(java.lang.String, java.lang.String)
	 */
	@Override
	public PriceRowModel getPriceRow(final String b2bUnitId, final String material)
	{
		final SABMAlcoholVariantProductMaterialModel materialModel = (SABMAlcoholVariantProductMaterialModel) productService
				.getProductForCode(material);
		return getPriceRow(UserPriceGroup.valueOf(b2bUnitId), materialModel.getBaseProduct());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmPriceRowService#getPriceRow(java.lang.String,
	 * com.sabmiller.core.model.SABMAlcoholVariantProductEANModel)
	 */
	@Override
	public PriceRowModel getPriceRow(final String b2bUnitId, final ProductModel productModel)
	{
		final UserPriceGroup ug = UserPriceGroup.valueOf(b2bUnitId);
		if (getModelService().isNew(ug))
		{
			getModelService().save(ug);
		}
		return getPriceRow(ug, productModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.product.SabmPriceRowService#getPriceRow(de.hybris.platform.europe1.enums.UserPriceGroup,
	 * com.sabmiller.core.model.SABMAlcoholVariantProductEANModel)
	 */
	@Override
	public PriceRowModel getPriceRow(final UserPriceGroup userPriceGroup, final ProductModel productModel)
	{

		final Date currentDeliveryDate = (Date) sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);


		return getSabmPriceRowDao().getPriceRow(userPriceGroup, productModel, currentDeliveryDate);
	}



	@Override
	public PriceRowModel getPriceRowByDate(final String b2bUnitId, final ProductModel productModel, final Date date)
	{

		final UserPriceGroup userPriceGroup = UserPriceGroup.valueOf(b2bUnitId);
		if (getModelService().isNew(userPriceGroup))
		{
			getModelService().save(userPriceGroup);
		}


		return getSabmPriceRowDao().getPriceRow(userPriceGroup, productModel, date);
	}


	/**
	 * Find old price rows.
	 *
	 * @param startBefore
	 *           the started before
	 * @param batchSize
	 *           the batch size
	 * @return list of @PriceRowModel
	 */
	@Override
	public List<PriceRowModel> findOldPriceRow(final Date startBefore, final int batchSize)
	{
		return getSabmPriceRowDao().findOldPriceRow(startBefore, batchSize);
	}

	/**
	 * Gets the sabm price row dao.
	 *
	 * @return the sabmPriceRowDao
	 */
	public SabmPriceRowDao getSabmPriceRowDao()
	{
		return sabmPriceRowDao;
	}

	/**
	 * Sets the sabm price row dao.
	 *
	 * @param sabmPriceRowDao
	 *           the sabmPriceRowDao to set
	 */
	public void setSabmPriceRowDao(final SabmPriceRowDao sabmPriceRowDao)
	{
		this.sabmPriceRowDao = sabmPriceRowDao;
	}

}
