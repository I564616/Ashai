

/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.util.AsahiSiteUtil;
import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.enums.SapServiceCallStatus;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.SalesDataModel;
import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.core.model.UnloadingPointModel;
import com.sabmiller.facades.b2bunit.data.SalesData;
import com.sabmiller.facades.b2bunit.data.ShippingCarrier;
import com.sabmiller.facades.b2bunit.data.UnloadingPoint;


/**
 * SABMB2BUnitPopulator Populate the target instance from the source instance.
 */
public class SABMB2BUnitPopulator implements Populator<B2BUnitModel, B2BUnitData>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMB2BUnitPopulator.class);
	private static final String CUB_STORE = "sabmStore";

	/** The unit service. */
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;

	private AbstractPopulatingConverter<SalesDataModel, SalesData> salesDataConverter;

	private AbstractPopulatingConverter<UnloadingPointModel, UnloadingPoint> unloadingPointConverter;

	@Resource
	private BaseStoreService baseStoreService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private CartService cartService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	/*
	 * SAB-535 Populate the target instance from the source instance.
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */

	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource
	private CMSSiteService cmsSiteService;

	@Override
	public void populate(final B2BUnitModel source, final B2BUnitData target) throws ConversionException
	{
		if(asahiSiteUtil.isCub())
		{
   		if (source.getDefaultShipTo() != null)
   		{
   			target.setDefaultShipTo(source.getDefaultShipTo().getPartnerNumber());
   			target.setIsDepositApplicable(getDepositApplicableFlag(source));
   		}
   		target.setSoldTo(source.getSoldto());
   		target.setPayerId(source.getPayerId());
   		if (source.getDefaultUnloadingPoint() != null)
   		{
   			target.setDefaultUnloadingPoint(unloadingPointConverter.convert(source.getDefaultUnloadingPoint()));
   		}

   		if (source.getDefaultCarrier() != null)
   		{
   			target.setDefaultCarrier(populateShippingCarrier(source.getDefaultCarrier()));
   		}
   		setAddresse(source, target);
   		setAddress(source, target);
   		setShippingCarriers(source, target);
   		if (source.getSalesData() != null)
   		{
   			target.setSalesData(salesDataConverter.convert(source.getSalesData()));
   		}
   		target.setUid(source.getUid());
   		target.setName(source.getName());
   		target.setContact(populateContact(source.getContact()));
   		target.setCupCallInProgress(SapServiceCallStatus.IN_PROGRESS.equals(source.getCupCallStatus()));
   		target.setBogofDealsCallInProgress(SapServiceCallStatus.IN_PROGRESS.equals(source.getBogofCallStatus()));
   		target.setDiscountDealsCallInProgress(SapServiceCallStatus.IN_PROGRESS.equals(source.getDiscountCallStatus()));
   		target.setDealsCallInProgress(target.isBogofDealsCallInProgress() || target.isDiscountDealsCallInProgress());
   		/*
   		 * Add contractAddress for SABMC-438 to check the default contract address
   		 *
   		 * @author Ross
   		 *
   		 * @date 20160423
   		 */
   		if (source.getContactAddress() != null)
   		{
   			target.setContactAddress(addressConverter.convert(source.getContactAddress()));
   		}
   		// add b2bUnit status for SABMC-1132
   		if (null != source.getB2BUnitStatus())
   		{
   			target.setB2BUnitStatus(source.getB2BUnitStatus().getCode().toLowerCase());
   		}

   		/*
   		 * Add activeUsers for SABMC-438.
   		 *
   		 * @author Ross
   		 *
   		 * @date 20160423
   		 */
   		int activeUserNumber = 0;
   		final Set<PrincipalModel> members = source.getMembers();
   		for (final PrincipalModel member : members)
   		{
   			if (member instanceof B2BCustomerModel && !(member instanceof BDECustomerModel))
   			{
   				final B2BCustomerModel customerModel = (B2BCustomerModel) member;
   				// Count the active user number of current B2bUnit
   				if (customerModel.getActive() != null && customerModel.getActive().booleanValue())
   				{
   					activeUserNumber++;
   				}
   			}

   			/**
   			 * Assume there is only one BDE customer associate to B2BUnit
   			 */
   			if (member instanceof BDECustomerModel){
   				final BDECustomerModel bdeCustomerModel = (BDECustomerModel) member;
   					if(bdeCustomerModel.getUid().startsWith("bde-")){
   						target.setBdeUserName(bdeCustomerModel.getName());
   						continue;
   					}
   			}
   		}
   		target.setActiveUsers(activeUserNumber);
		}

	}


	/**
	 * @param source
	 */
	private boolean getDepositApplicableFlag(final B2BUnitModel source)
	{
		if (source.getDefaultShipTo() != null && source.getDefaultShipTo().getRegion() != null)
		{
			final String unitState = source.getDefaultShipTo().getRegion().getIsocodeShort();
			final List<String> statesList = sabmConfigurationService.getDepositApplicableStates();
			if (statesList != null)
			{
				for (final String state : statesList)
				{
					if (StringUtils.equalsIgnoreCase(state, unitState))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * set Addresse to B2BUnitData
	 *
	 * @param b2bUnitModel
	 * @param b2bUnitData
	 */
	public void setAddresse(final B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		if (CollectionUtils.isNotEmpty(b2bUnitModel.getAddresses()))
		{
			final List<AddressData> addresses = new ArrayList();
			for (final AddressModel addressModel : b2bUnitModel.getAddresses())
			{
				if (Boolean.TRUE.equals(addressModel.getShippingAddress()))
				{
					addresses.add(getAddressConverter().convert(addressModel));
				}
			}
			b2bUnitData.setAddresses(addresses);
		}
		else
		{
			LOG.warn("Attribute ShippingAddresses is null in B2BUnitModel: {}", b2bUnitModel);
			b2bUnitData.setAddresses(null);
		}

	}

	/**
	 * set Address to B2BUnitData
	 *
	 * @param b2bUnitModel
	 * @param b2bUnitData
	 */
	public void setAddress(final B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		if (null != b2bUnitModel.getContactAddress())
		{
			b2bUnitData.setAddress(getAddressConverter().convert(b2bUnitModel.getContactAddress()));
		}
		else if (CollectionUtils.isNotEmpty(b2bUnitModel.getAddresses()))
		{
			b2bUnitData.setAddress(getAddressConverter().convert(b2bUnitModel.getAddresses().iterator().next()));
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Attribute ShippingAddresses is null in B2BUnitModel: {}", b2bUnitModel);
			}
			b2bUnitData.setAddress(null);
		}
	}





	/**
	 * set ShippingCarriers to B2BUnitData
	 *
	 * @param b2bUnitModel
	 * @param b2bUnitData
	 */
	public void setShippingCarriers(B2BUnitModel b2bUnitModel, final B2BUnitData b2bUnitData)
	{
		final B2BUnitModel b2bUnitForAlternativeAddress = getUnitBasedForAlternativeAddress();
		b2bUnitModel = null != b2bUnitForAlternativeAddress ? b2bUnitForAlternativeAddress : b2bUnitModel;
		if (CollectionUtils.isNotEmpty(b2bUnitModel.getShippingCarriers()))
		{
			final List<ShippingCarrier> shippingCarriers = new ArrayList<>();
			List<ShippingCarrierModel> allowedCarries = b2bUnitModel.getShippingCarriers();
			if (configurationService.getConfiguration().getBoolean("cub.enable.carrier.restriction", true)
					&& cmsSiteService.getCurrentSite() != null
					&& cmsSiteService.getCurrentSite().getUid().equalsIgnoreCase(CUB_STORE))
			{
				allowedCarries = b2bUnitService.getAllowedCarries(b2bUnitModel.getShippingCarriers());
			}
			for (final ShippingCarrierModel shippCarrierModel : allowedCarries)
			{
				if (null != shippCarrierModel.getCustomerOwned() && Boolean.TRUE.equals(shippCarrierModel.getCustomerOwned()))
				{
					shippingCarriers.add(populateShippingCarrier(shippCarrierModel));
				}
			}
			b2bUnitData.setShippingCarriers(shippingCarriers);
		}
		else
		{
			LOG.debug("Attribute ShippingCarriers is null in B2BUnitModel: {}", b2bUnitModel.getPk());
			b2bUnitData.setShippingCarriers(null);
		}

	}

	private B2BUnitModel getUnitBasedForAlternativeAddress()
	{
		B2BUnitModel b2bUnitModel = null;
		if (cartService.hasSessionCart())
		{
			final CartModel cartModel = cartService.getSessionCart();
			if (null != cartModel && null != cartModel.getDeliveryAddress()
					&& null != cartModel.getDeliveryAddress().getPartnerNumber())
			{
				b2bUnitModel = b2bUnitService.getUnitForUid(cartModel.getDeliveryAddress().getPartnerNumber());
			}
		}

		return b2bUnitModel;
	}

	private CustomerData populateContact(final UserModel user)
	{
		final CustomerData customerData = new CustomerData();
		if (user instanceof CustomerModel)
		{
			final CustomerModel customerModel = (CustomerModel) user;
			customerData.setName(customerModel.getName());
			customerData.setEmail(customerModel.getContactEmail());
		}
		return customerData;
	}

	private ShippingCarrier populateShippingCarrier(final ShippingCarrierModel shippCarrierModel)
	{
		final ShippingCarrier carrierData = new ShippingCarrier();
		carrierData.setCode(shippCarrierModel.getCarrierCode());
		if (StringUtils.isEmpty(shippCarrierModel.getCarrierDescription()))
		{
			carrierData.setDescription(shippCarrierModel.getCarrierCode());
		}
		else
		{
			carrierData.setDescription(shippCarrierModel.getCarrierDescription());
		}
		carrierData.setCustomerOwned(shippCarrierModel.getCustomerOwned());
		return carrierData;
	}

	/**
	 * @return the addressConverter
	 */

	public AbstractPopulatingConverter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	/**
	 * @param addressConverter
	 *           the addressConverter to set
	 */
	public void setAddressConverter(final AbstractPopulatingConverter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	/**
	 * @return the salesDataConverter
	 */
	public AbstractPopulatingConverter<SalesDataModel, SalesData> getSalesDataConverter()
	{
		return salesDataConverter;
	}

	/**
	 * @param salesDataConverter
	 *           the salesDataConverter to set
	 */
	public void setSalesDataConverter(final AbstractPopulatingConverter<SalesDataModel, SalesData> salesDataConverter)
	{
		this.salesDataConverter = salesDataConverter;
	}


	/**
	 * @return the unloadingPointConverter
	 */
	public AbstractPopulatingConverter<UnloadingPointModel, UnloadingPoint> getUnloadingPointConverter()
	{
		return unloadingPointConverter;
	}


	/**
	 * @param unloadingPointConverter
	 *           the unloadingPointConverter to set
	 */
	public void setUnloadingPointConverter(
			final AbstractPopulatingConverter<UnloadingPointModel, UnloadingPoint> unloadingPointConverter)
	{
		this.unloadingPointConverter = unloadingPointConverter;
	}

}


