/**
 *
 */
package com.sabmiller.core.b2bunit.converters.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.B2BUnitGroupModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.SalesDataModel;
import com.sabmiller.core.model.SalesOrgDataModel;
import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.core.model.UnloadingPointModel;
import com.sabmiller.core.strategy.RelationshipUpdateStrategy;
import com.sabmiller.facades.b2bunit.data.B2BUnitGroup;
import com.sabmiller.facades.b2bunit.data.SalesData;
import com.sabmiller.facades.b2bunit.data.SalesOrgData;
import com.sabmiller.facades.b2bunit.data.ShippingCarrier;
import com.sabmiller.facades.b2bunit.data.UnloadingPoint;


/**
 * @author joshua.a.antony
 *
 */
public class SabmB2BUnitReversePopulator implements Populator<B2BUnitData, B2BUnitModel>
{

	private static final Logger LOG = Logger.getLogger(SabmB2BUnitReversePopulator.class);

	@Resource(name = "unloadingPointUpdateStrategy")
	private RelationshipUpdateStrategy<B2BUnitModel, UnloadingPoint, UnloadingPointModel> unloadingPointUpdateStrategy;
	@Resource(name = "shippingCarrierUpdateStrategy")
	private RelationshipUpdateStrategy<B2BUnitModel, ShippingCarrier, ShippingCarrierModel> shippingCarrierUpdateStrategy;
	@Resource(name = "salesDataUpdateStrategy")
	private RelationshipUpdateStrategy<B2BUnitModel, SalesData, SalesDataModel> salesDataUpdateStrategy;
	@Resource(name = "salesOrgDataUpdateStrategy")
	private RelationshipUpdateStrategy<B2BUnitModel, SalesOrgData, SalesOrgDataModel> salesOrgDataUpdateStrategy;
	@Resource(name = "addressUpdateStrategy")
	private RelationshipUpdateStrategy<B2BUnitModel, AddressData, AddressModel> addressUpdateStrategy;
	@Resource(name = "sapGroupUpdateStrategy")
	private RelationshipUpdateStrategy<B2BUnitModel, B2BUnitGroup, B2BUnitGroupModel> sapGroupUpdateStrategy;
	@Resource(name = "deliveryPlantUpdateStrategy")
	private RelationshipUpdateStrategy<B2BUnitModel, String, PlantModel> deliveryPlantUpdateStrategy;


	@Override
	public void populate(final B2BUnitData b2bUnitData, final B2BUnitModel b2bUnitModel) throws ConversionException
	{
		LOG.debug("In populate() : Going to populate B2BUnitModel from B2BUnitData");
		populateCoreData(b2bUnitModel, b2bUnitData);
		populateRelationships(b2bUnitModel, b2bUnitData);
		setDefaultCarrier(b2bUnitModel, b2bUnitData);
		setDefaultUnloadingPoint(b2bUnitModel, b2bUnitData);
		setDefaultShipTo(b2bUnitModel, b2bUnitData);
	}

	public void populateCoreData(final B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		b2bUnitModel.setUid(b2bUnitData.getUid());
		b2bUnitModel.setName(b2bUnitData.getName());
		b2bUnitModel.setSoldto(b2bUnitData.getSoldTo());
		b2bUnitModel.setLocName(b2bUnitData.getName());
		b2bUnitModel.setAccountGroup(b2bUnitData.getAccountGroup());
		b2bUnitModel.setPayerId(b2bUnitData.getPayerId());
		b2bUnitModel.setPaymentRequired(b2bUnitData.isPaymentRequired());
		b2bUnitModel.setExternalCustomerId(b2bUnitData.getExternalCustomerId());
		if (b2bUnitData.getSalesData() != null)
		{
			b2bUnitModel.setSalesOrgId(b2bUnitData.getSalesData().getSalesOrgId());
		}
		b2bUnitModel.setCompanyUid(SabmCoreConstants.CUB_COMPANY_UID);
		b2bUnitModel.setBlockType(b2bUnitData.getBlockType());
		b2bUnitModel.setCustomerFlag(b2bUnitData.getCustomerFlag());
		b2bUnitModel.setAutoPayStatus(b2bUnitData.getAutoPayStatus());
		b2bUnitModel.setActive(Boolean.TRUE);
	}

	public void populateRelationships(final B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		b2bUnitModel
				.setUnloadingPoints(unloadingPointUpdateStrategy.deriveModelList(b2bUnitModel, b2bUnitData.getUnloadingPoints()));
		b2bUnitModel
				.setShippingCarriers(shippingCarrierUpdateStrategy.deriveModelList(b2bUnitModel, b2bUnitData.getShippingCarriers()));
		b2bUnitModel.setSalesData(salesDataUpdateStrategy.deriveModel(b2bUnitModel, b2bUnitData.getSalesData()));
		b2bUnitModel.setSalesOrgData(salesOrgDataUpdateStrategy.deriveModel(b2bUnitModel, b2bUnitData.getSalesOrgData()));
		b2bUnitModel.setAddresses(addressUpdateStrategy.deriveModelList(b2bUnitModel, b2bUnitData.getAddresses()));
		b2bUnitModel.setSapGroup(sapGroupUpdateStrategy.deriveModel(b2bUnitModel, b2bUnitData.getB2bUnitGroup()));
		b2bUnitModel.setPlant(
				deliveryPlantUpdateStrategy.deriveModel(b2bUnitModel, b2bUnitData.getSalesData().getDefaultDeliveryPlant()));
	}


	protected void setDefaultUnloadingPoint(final B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		if (b2bUnitModel.getUnloadingPoints() != null && b2bUnitModel.getUnloadingPoints().size() == 1)
		{
			b2bUnitModel.setDefaultUnloadingPoint(b2bUnitModel.getUnloadingPoints().get(0));
			return;
		}

		LOG.debug("Setting the default unloading point");
		if (b2bUnitData.getDefaultUnloadingPoint() != null)
		{
			for (final UnloadingPointModel model : ListUtils.emptyIfNull(b2bUnitModel.getUnloadingPoints()))
			{
				LOG.debug("Comparing " + model.getCode() + " with " + b2bUnitData.getDefaultUnloadingPoint().getCode());
				if (model.getCode().equals(b2bUnitData.getDefaultUnloadingPoint().getCode()))
				{
					LOG.debug("Found default unloading point");
					b2bUnitModel.setDefaultUnloadingPoint(model);
					break;
				}
			}
		}
	}

	protected void setDefaultCarrier(final B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		if (b2bUnitModel.getShippingCarriers() != null && b2bUnitModel.getShippingCarriers().size() == 1)
		{
			b2bUnitModel.setDefaultCarrier(b2bUnitModel.getShippingCarriers().get(0));
			return;
		}

		LOG.debug("Setting the default shipping carrier");
		if (b2bUnitData.getDefaultCarrier() != null)
		{
			for (final ShippingCarrierModel model : ListUtils.emptyIfNull(b2bUnitModel.getShippingCarriers()))
			{
				LOG.debug("Comparing " + model.getCarrierCode() + " with " + b2bUnitData.getDefaultCarrier().getCode());
				if (model.getCarrierCode().equals(b2bUnitData.getDefaultCarrier().getCode()))
				{
					LOG.debug("Found default carrier ");
					b2bUnitModel.setDefaultCarrier(model);
					break;
				}
			}
		}
	}

	protected void setDefaultShipTo(final B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		if (persistDefaultShipTo(b2bUnitModel))
		{
			return;
		}

		final AddressModel addressModel = getAddress(b2bUnitModel, b2bUnitData.getDefaultShipTo());
		b2bUnitModel.setDefaultShipTo(addressModel != null ? addressModel : getAddress(b2bUnitModel, b2bUnitData.getSoldTo()));
	}

	private AddressModel getAddress(final B2BUnitModel b2bUnitModel, final String shipToId)
	{
		LOG.debug("Setting the default ship to");
		if (shipToId != null)
		{
			for (final AddressModel model : CollectionUtils.emptyIfNull(b2bUnitModel.getAddresses()))
			{
				LOG.debug("Comparing " + model.getPartnerNumber() + " with " + shipToId);
				if (shipToId.equals(model.getPartnerNumber()))
				{
					LOG.debug("Found default shipto ");
					return model;
				}
			}
		}
		return null;
	}

	private boolean persistDefaultShipTo(final B2BUnitModel b2bUnitModel)
	{
		int totalShipto = 0;
		AddressModel addressModel = null;
		for (final AddressModel model : CollectionUtils.emptyIfNull(b2bUnitModel.getAddresses()))
		{
			if (StringUtils.isNotBlank(model.getPartnerNumber()))
			{
				totalShipto++;
				addressModel = model;
			}
		}
		final boolean hasOnly1Shipto = (totalShipto == 1);
		if (hasOnly1Shipto)
		{
			b2bUnitModel.setDefaultShipTo(addressModel);
		}
		return hasOnly1Shipto;
	}

}
