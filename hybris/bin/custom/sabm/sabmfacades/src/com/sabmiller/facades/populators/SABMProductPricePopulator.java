/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPricePopulator;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;

import java.math.BigDecimal;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;

import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.cdlvalue.service.SabmCDLValueService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.SabmPriceRowService;
import com.sabmiller.facades.util.SavePriceUtil;


/**
 * SABMProductPricePopulator displayed for the following price types: price,basePrice,savingsPrice
 *
 * @author yaopeng
 *
 */
public class SABMProductPricePopulator extends ProductPricePopulator<ProductModel, ProductData>
{
	/** The Constant LOG. */
	private static final Logger  LOG = Logger.getLogger(SABMProductPricePopulator.class);

	private static final String USERID_ANONYMOUS = "anonymous";
	private SabmPriceRowService priceRowService;
	private UserService userService;
	private PriceDataFactory priceDataFactory;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource
	private SabmCDLValueService sabmCDLValueService;

	@Resource
	private ConfigurationService configurationService;

	/*
	 * Override method populate, to populate the attribute
	 * PriceRowModel.price,PriceRowModel.basePrice,PriceRowModel.savingsPrice to ProductData
	 *
	 * @see
	 * de.hybris.platform.commercefacades.product.converters.populator.ProductPricePopulator#populate(de.hybris.platform.
	 * core.model.product.ProductModel, de.hybris.platform.commercefacades.product.data.ProductData)
	 */
	@Override
	public void populate(final ProductModel productModel, final ProductData productData) throws ConversionException
	{

		if(!asahiCoreUtil.isNAPUserForSite()) {
			if (asahiSiteUtil.isCub()) {
				//Judge productModel is not null and User login required
				if (productModel != null) {
					if (!getUserService().getCurrentUser().getUid().equals(USERID_ANONYMOUS)) {
						//According to the productModel achieve PriceRowModel
						final PriceRowModel priceRow = priceRowService.getPriceRowByProduct(productModel);
						PriceDataType priceType;
						if (CollectionUtils.isEmpty(productModel.getVariants())) {
							priceType = PriceDataType.BUY;
						} else {
							priceType = PriceDataType.FROM;
						}
						if (null != priceRow) {
							BigDecimal netPrice = BigDecimal.valueOf(SavePriceUtil.checkDoubleEmpty(priceRow.getPrice()));
							if (productModel instanceof SABMAlcoholVariantProductEANModel)
							{
								BigDecimal cdlPrice = null;
								final SABMAlcoholVariantProductEANModel eanProduct = (SABMAlcoholVariantProductEANModel) productModel;
								if (null != eanProduct.getLevel4()
										&& ("C".equalsIgnoreCase(eanProduct.getLevel4()) || "N".equalsIgnoreCase(eanProduct.getLevel4())
												|| "P".equalsIgnoreCase(eanProduct.getLevel4())))
								{
									final String presentation = eanProduct.getPresentation();
									cdlPrice = sabmCDLValueService.getCDLPrice(eanProduct.getLevel4(), presentation);
									netPrice = null != cdlPrice ? netPrice.add(cdlPrice) : netPrice;
								}
								if (eanProduct.getWetEligible() != null && eanProduct.getWetEligible())
								{
									final BigDecimal wetPercentage = this.configurationService.getConfiguration()
											.getBigDecimal(SabmCoreConstants.CUB_WET_PRICE_PERCENTAGE);
									netPrice = null != wetPercentage
											? (netPrice.multiply(wetPercentage)).setScale(2, BigDecimal.ROUND_HALF_UP)
											: netPrice;

								}


							}
							//  populate the attribute priceRow.price to productData.price
							productData.setPrice(getPriceData(priceType, netPrice,
									priceRow.getCurrency()));

							//  populate the attribute priceRow.basePrice to productData.basePrice
							productData.setBasePrice(getPriceData(priceType,
									BigDecimal.valueOf(SavePriceUtil.checkDoubleEmpty(priceRow.getBasePrice()).doubleValue()),
									priceRow.getCurrency()));

							// calculated  the SavingsPrice by BasePrice subtract Price
							productData.setSavingsPrice(
									SavePriceUtil.getSavingsPrice(priceType, priceRow, commonI18NService, getPriceDataFactory()));
						} else {
							LOG.warn("Unable to find priceRowModel for product:[" + productModel.getCode() + "]");

						}
					} else {
						if (LOG.isDebugEnabled()) {
							LOG.warn("The user not logged in ");
						}

					}
				} else {
					if (LOG.isDebugEnabled()) {
						LOG.warn("Unable to find productModel");
					}
				}

				productData.setPurchasable(BooleanUtils.isTrue(productModel.getPurchasable()));
			} else {

				final PriceDataType priceType;
				final PriceInformation info;
				LOG.info(" Sabm Price Populator " + productModel.getCode());
				if (CollectionUtils.isEmpty(productModel.getVariants())) {

					priceType = PriceDataType.BUY;
					info = getCommercePriceService().getWebPriceForProduct(productModel);

				} else {
					priceType = PriceDataType.FROM;
					info = getCommercePriceService().getFromPriceForProduct(productModel);
				}

				if (info != null) {
					LOG.info(info.getPriceValue().getValue() + " " + productModel.getCode());
					final PriceData priceData = getPriceDataFactory().create(priceType, BigDecimal.valueOf(info.getPriceValue().getValue()),
							info.getPriceValue().getCurrencyIso());
					productData.setPrice(priceData);
				} else {
					productData.setPurchasable(Boolean.FALSE);
				}
				//super.populate(productModel, productData);
			}
		}
	}



	/**
	 *
	 * Generating new PriceData from the incoming parameters
	 *
	 * @param priceType
	 * @param value
	 * @param currencyModel
	 * @return PriceData
	 */
	@SuppressWarnings("unused")
	private PriceData getPriceData(final PriceDataType priceType, final BigDecimal value, final CurrencyModel currencyModel)
	{
		return getPriceDataFactory().create(priceType, value, currencyModel);
	}

	/**
	 * @return the priceRowService
	 */
	public SabmPriceRowService getPriceRowService()
	{
		return priceRowService;
	}

	/**
	 * @param priceRowService
	 *           the priceRowService to set
	 */
	public void setPriceRowService(final SabmPriceRowService priceRowService)
	{
		this.priceRowService = priceRowService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the priceDataFactory
	 */
	@Override
	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Override
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}
}
