/**
 *
 */
package com.sabmiller.facades.order.populators;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;



/**
 * @author joshua.a.antony
 *
 */

public class OrderEntryReversePopulator implements Populator<OrderData, OrderModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(OrderEntryReversePopulator.class.getName());

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "unitService")
	private SabmUnitService unitService;

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;



	@Override
	public void populate(final OrderData source, final OrderModel target) throws ConversionException
	{

		final List<AbstractOrderEntryModel> newOrderEntries = new ArrayList<AbstractOrderEntryModel>();
		for (final OrderEntryData orderEntryData : source.getEntries())
		{
			final String unitCode = orderEntryData.getUnit().getCode();
			final String productCode = orderEntryData.getProduct().getCode();

			final OrderEntryModel model = findOrCreateOrderEntry(target, productCode, orderEntryData.isIsFreeGood(),
					orderEntryData.isRejected());
			model.setQuantity(orderEntryData.getQuantity());
			model.setRejected(orderEntryData.isRejected());
			if (modelService.isNew(model))
			{
				model.setProduct(productService.getProductForCode(catalogVersionDeterminationStrategy.onlineCatalogVersion(),
						productCode));
				model.setUnit(unitService.getUnitForCode(unitCode));
				model.setSapLineNumber(orderEntryData.getSapLineNumber());
				model.setIsFreeGood(orderEntryData.isIsFreeGood());
				model.setOrder(target);

				for (final ConsignmentModel consignmentModel : CollectionUtils.emptyIfNull(target.getConsignments()))
				{
					for (final ConsignmentEntryModel consignmentEntryModel : CollectionUtils.emptyIfNull(consignmentModel.getConsignmentEntries()))
					{
						if (null != consignmentEntryModel && null != consignmentEntryModel.getOrderEntry()
								&& null != consignmentEntryModel.getOrderEntry().getProduct()
								&& consignmentEntryModel.getOrderEntry().getProduct().getCode().equals(productCode))
						{
							consignmentEntryModel.setOrderEntry(model);
						}
					}
				}
			}

			newOrderEntries.add(model);
			LOG.debug("product : {} , unit : {} , quantity : {} , sapLineNumber : {} , totalPrice : {}, freeGood : {}", productCode,
					model.getUnit(), model.getQuantity(), model.getSapLineNumber(), model.getTotalPrice(), model.getIsFreeGood());
		}
		deleteObsoleteEntries(target.getEntries(), newOrderEntries);

		target.setEntries(newOrderEntries);
	}

	protected void deleteObsoleteEntries(final List<AbstractOrderEntryModel> existingOrderEntries,
			final List<AbstractOrderEntryModel> newOrderEntries)
	{
		if (newOrderEntries != null)
		{
			for (final AbstractOrderEntryModel eachExistingOrderEntryModel : ListUtils.emptyIfNull(existingOrderEntries))
			{
				for (int i = 0; i < newOrderEntries.size(); i++)
				{
					if (!newOrderEntries.contains(eachExistingOrderEntryModel))
					{
						newOrderEntries.get(i).setBasePrice(eachExistingOrderEntryModel.getBasePrice());
						newOrderEntries.get(i).setTotalPrice(eachExistingOrderEntryModel.getTotalPrice());
						modelService.remove(eachExistingOrderEntryModel);
						break;
					}
				}
			}
		}
	}

	private OrderEntryModel findOrCreateOrderEntry(final OrderModel orderModel, final String material, final boolean isFreeGood,
			final boolean isRejected)
	{
		if (modelService.isNew(orderModel))
		{
			LOG.info("Order Model is new. Creating a new order entry model ");
			return modelService.create(OrderEntryModel.class);
		}
		//If order model already exist, lookup for the product
		for (final AbstractOrderEntryModel entryModel : orderModel.getEntries())
		{
			final SABMAlcoholVariantProductMaterialModel materialModel = (SABMAlcoholVariantProductMaterialModel) entryModel
					.getProduct();
			final boolean isOrderEntryModelFreeGood = entryModel.getIsFreeGood() != null && entryModel.getIsFreeGood();
			final boolean isOrderEntryModelRejected = entryModel.getRejected() != null && entryModel.getRejected();

			//Also compare against free good since we can have the same product on multiple lines (1 original item and the other as free good)
			if (material.equals(materialModel.getCode()) && isFreeGood == isOrderEntryModelFreeGood
					&& isRejected == isOrderEntryModelRejected)
			{
				LOG.info("Located the order entry! Returning " + entryModel);
				return (OrderEntryModel) entryModel;
			}
		}

		LOG.warn("Order Entry not found for material " + material + ". Creating a new one! ");

		return modelService.create(OrderEntryModel.class);
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
