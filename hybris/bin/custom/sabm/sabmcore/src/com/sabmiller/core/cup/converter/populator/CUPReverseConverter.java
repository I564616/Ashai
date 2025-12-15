/**
 *
 */
package com.sabmiller.core.cup.converter.populator;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.*;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmPriceRowService;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;
import com.sabmiller.integration.sap.cup.response.CustomerUnitPricingResponse;
import com.sabmiller.integration.sap.cup.response.CustomerUnitPricingResponse.CustomerUnitPricingDiscountResponse.CustomerUnitPricingResponseItem;


/**
 * Convert Customer Unit Price (CUP) response to Hybris Model and persist the same. If Price row does not exist for the
 * product, a new one is created. The Price row is linked to the {@link SABMAlcoholVariantProductEANModel}. Also, the
 * {@link UserPriceGroup} is the customer Id
 *
 * @author joshua.a.antony
 */

public class CUPReverseConverter implements Converter<CustomerUnitPricingResponse, List<PriceRowModel>>
{
	private static final Logger LOG = LoggerFactory.getLogger(CUPReverseConverter.class);

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "priceRowService")
	private SabmPriceRowService priceRowService;

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "unitService")
	private SabmUnitService unitService;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	@Resource(name = "sessionService")
	private SessionService sessionService;



	@Override
	public List<PriceRowModel> convert(final CustomerUnitPricingResponse bogofResponse) throws ConversionException
	{
		return convert(bogofResponse, new ArrayList<PriceRowModel>());
	}


	@Override
	public List<PriceRowModel> convert(final CustomerUnitPricingResponse cupResponse, final List<PriceRowModel> target)
			throws ConversionException
	{
		LOG.debug("In convert(). cupResponse : " + cupResponse);

		final String customerId = cupResponse.getCustomerUnitPricingResponseHeader().getCustomerID();

		if (StringUtils.isNotBlank(customerId))
		{
			//If the UserPriceGroup does not exist, create one.
			final UserPriceGroup userPriceGroup = UserPriceGroup.valueOf(customerId);
			modelService.save(userPriceGroup);
			final Date currentDeliveryDate = (Date) sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);
			LOG.debug("Delivery Date" + currentDeliveryDate);


			for (final CustomerUnitPricingResponse.CustomerUnitPricingDiscountResponse eachCupItem : ListUtils
					.emptyIfNull(cupResponse.getCustomerUnitPricingDiscountResponse()))
			{
				try
				{
					//LOG.debug("Each price item : " + ReflectionToStringBuilder.toString(eachCupItem));

					final String material = eachCupItem.getMaterialID();

					final ProductModel productModel = productService.getProductFromCodeWithGivenCatalogVersion(
							catalogVersionDeterminationStrategy.onlineCatalogVersion(), material);

					if (productModel != null)
					{
						final SABMAlcoholVariantProductMaterialModel product = (SABMAlcoholVariantProductMaterialModel) productModel;
						PriceRowModel priceRowModel = findPriceRow(customerId,
								(SABMAlcoholVariantProductEANModel) product.getBaseProduct(), currentDeliveryDate);
						if (priceRowModel == null)
						{
							priceRowModel = modelService.<PriceRowModel> create(PriceRowModel.class);
							//priceRowModel.setProduct(product.getBaseProduct());
							priceRowModel.setProductId(product.getBaseProduct().getCode());
							//priceRowModel.setPg(userPriceGroup);
						}
						CustomerUnitPricingResponse.CustomerUnitPricingDiscountResponse.CustomerUnitPricingResponseItem customerUnitPricingResponseItemObj = eachCupItem.getCustomerUnitPricingResponseItem();
						if(Objects.isNull(customerUnitPricingResponseItemObj)) {
							continue;
						}
						priceRowModel.setBasePrice(Double.valueOf(StringUtils.trim( customerUnitPricingResponseItemObj.getBasePrice())));
						priceRowModel.setPrice(Double.valueOf(StringUtils.trim( customerUnitPricingResponseItemObj.getCustUnitPrice())));
						priceRowModel.setCurrency(commonI18NService.getCurrency(StringUtils.trim(eachCupItem.getUnit())));
						priceRowModel.setStartTime(DateUtils.truncate(currentDeliveryDate, Calendar.DATE));
						priceRowModel.setEndTime(DateUtils.truncate(currentDeliveryDate, Calendar.DATE));
						priceRowModel.setUnit(unitService.getUnitForCode(eachCupItem.getUnitOfMeasure()));
						priceRowModel.setUnitFactor(Integer.valueOf(StringUtils.trim(eachCupItem.getSaleUnit())));
						priceRowModel.setUg(userPriceGroup);

						//modelService.save(priceRowModel);

						LOG.debug("Successfully saved Product :{}, Base Price : {}, CUP Price : {},  ug :  ", material,
								customerUnitPricingResponseItemObj.getBasePrice(),
								customerUnitPricingResponseItemObj.getCustUnitPrice(), userPriceGroup);

						target.add(priceRowModel);
					}

				}
				catch (final Exception e)
				{
					//We handle exception to make sure that a single CUP failure does not break the entire import
					LOG.error("Exception occured while creating PriceRow for product code : " + eachCupItem.getMaterialID(), e);
				}
			}
		}

		return target;
	}


	protected PriceRowModel findPriceRow(final String customerId, final SABMAlcoholVariantProductEANModel eanProductModel,
			final Date date)
	{
		return priceRowService.getPriceRowByDate(customerId, eanProductModel, date);
	}
}
