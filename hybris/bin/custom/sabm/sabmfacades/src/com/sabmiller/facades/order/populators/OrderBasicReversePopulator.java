/**
 *
 */
package com.sabmiller.facades.order.populators;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author joshua.a.antony
 *
 */

public class OrderBasicReversePopulator implements Populator<OrderData, OrderModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(OrderBasicReversePopulator.class.getName());

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource
	private BaseSiteService baseSiteService;

	@Override
	public void populate(final OrderData source, final OrderModel target) throws ConversionException
	{
		final BaseStoreModel baseStoreModel = baseStoreService
				.getBaseStoreForUid(Config.getString("base.store.default", "sabmStore"));
		final BaseSiteModel site = baseSiteService.getBaseSiteForUID(Config.getString("base.site.default", "sabmStore"));

		target.setSapSalesOrderNumber(source.getSapSalesOrderNumber());
		//	target.setNetAmount(convert(source.getNetAmount()));
		//target.setTotalPrice(convert(source.getTotalPrice()));
		//target.setSubtotal(convert(source.getSubTotal()));
		target.setRequestedDeliveryDate(source.getRequestedDeliveryDate());
		target.setPurchaseOrderNumber(source.getPurchaseOrderNumber());
		target.setStore(baseStoreModel);
		target.setSite(site);
		target.setCurrency(baseStoreModel.getDefaultCurrency());
		target.setProcessingTypeCode(source.getProcessingTypeCode());

		LOG.debug("sapSalesOrderNumber : {} , netAmount : {} , totalPrice : {} , subtotal : {} code : {} , guId : {}",
				target.getSapSalesOrderNumber(), target.getNetAmount(), target.getTotalPrice(), target.getSubtotal(),
				target.getCode(), target.getGuid());
	}

	private Double convert(final PriceData priceData)
	{
		if (priceData != null && priceData.getValue() != null)
		{
			return priceData.getValue().doubleValue();
		}
		return 0.0;
	}

}
