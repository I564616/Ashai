/*
 *
 */
package com.apb.facades.populators;

import de.hybris.platform.commercefacades.customer.data.AsahiB2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.model.AccountGroupsModel;
import com.apb.core.model.AccountTypeModel;
import com.apb.core.model.BannerGroupsModel;
import com.apb.core.model.ChannelModel;
import com.apb.core.model.CutOffDeliveryDateModel;
import com.apb.core.model.LicenceClassModel;
import com.apb.core.model.LicenseTypesModel;
import com.apb.core.model.SubChannelModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbNumberKeyGeneratorService;
import com.apb.facades.b2bunit.data.AccountGroupsData;
import com.apb.facades.b2bunit.data.AccountTypeData;
import com.apb.facades.b2bunit.data.BannerGroupsData;
import com.apb.facades.b2bunit.data.ChannelData;
import com.apb.facades.b2bunit.data.LicenceClassData;
import com.apb.facades.b2bunit.data.LicenseTypesData;
import com.apb.facades.b2bunit.data.SubChannelData;
import com.apb.facades.constants.ApbFacadesConstants;
import com.apb.facades.delivery.data.CutOffDeliveryData;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.enums.AddressType;
import com.sabmiller.core.enums.BackendCustomerType;
import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * The Class ApbB2BUnitPopulator.
 *
 * Kuldeep.Singh1
 */


public class ApbB2BUnitPopulator implements Populator<AsahiB2BUnitData, AsahiB2BUnitModel>
{
	// Creates logger
	private static final Logger LOGGER = LoggerFactory.getLogger(ApbB2BUnitPopulator.class);

	/** The Constant CUSTOMER_EMAIL_SPLIT. */
	private static final String CUSTOMER_EMAIL_SPLIT = "customer.email.split";

	/** The Constant CUSTOMER_DELIVERY_DATEPATTERN. */
	private static final String CUSTOMER_DELIVERY_DATEPATTERN = "dd-MM-yyyy";

	/** The Constant COMPANY_CODE. */
	private static final String SGA_COMPANY_CODE = "sga";

	/** The apb B 2 B unit service. */
	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	/** The apb address reverse converter. */
	private Converter<AddressData, AddressModel> apbAddressReverseConverter;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/** The flexible search service. */
	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	/** The apb number key generator service. */
	@Resource(name = "apbNumberKeyGeneratorService")
	private ApbNumberKeyGeneratorService apbNumberKeyGeneratorService;

	/** The enumeration service. */
	@Resource(name="enumerationService")
	private EnumerationService enumerationService;


	/**
	 * Populate.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 * @throws ConversionException
	 *            the conversion exception
	 */
	public void populate(final AsahiB2BUnitData source, final AsahiB2BUnitModel target) throws ConversionException
	{
		if (null != source.getAbnNumber())
		{
			source.setAbnNumber(source.getAbnNumber().replaceAll(" ", ""));
		}
		//setting basic AsahiB2BUnit attributes
		this.populatingBasicAttributes(source, target);

		//setting Account Group
		this.populatingAccountGroupAttributes(source.getEclAccountGroupId(), target);

		//setting Account Type
		this.populatingAccountTypeAttributes(source.getEclAccountTypeId(), target);

		//setting Banner Group
		this.populatingBannerGroupAttributes(source.getEclBannerGroupid(), target);

		//setting Channel
		this.populatingChannelAttributes(source.getEclChannelCode(), target);

		//setting LicenseClass
		this.populatingLicenseClassAttributes(source.getEclLicenseClassCode(), target);

		//setting Subchannel
		this.populatingSubchannelAttributes(source.getEclSubchannelCode(), target);

		//setting LicenseType
		this.populatingLicenseTypeAttributes(source.getEclLicenseTypeCode(), target);

		//setting Warehouse to b2bUnit
		this.populatingWarehouseAttributes(source.getWarehouse(), target);

		//setting other specific attributes to b2bUnit
		this.populatingOtherAttributes(source, target);

		this.modelService.save(target);

		//Populate active flag
		this.populateActiveFlag(source.getActive(), target);

		//setting address to b2bUnit
		this.populatingAddressAttributes(source, target);

		//Populate shipping address for SGA
		if(CollectionUtils.isNotEmpty(source.getShipTo())){
			this.populateShippingAddressForSGA(source.getShipTo(), target);
		}
	}

	/**
	 * Populate shipping address for SGA.
	 *
	 * @param shipTo the ship to
	 * @param target the target
	 */
	private void populateShippingAddressForSGA(List<String> shipTos,
			AsahiB2BUnitModel target) {
		List<AsahiB2BUnitModel> shipToCustomers = new ArrayList<AsahiB2BUnitModel>();
		Set<String> hs = new HashSet<>();
		hs.addAll(shipTos);
		shipTos.clear();
		shipTos.addAll(hs);

		for(String accountNumber : shipTos){
			AsahiB2BUnitModel shipToCustomer= this.apbB2BUnitService.getB2BUnitByAccountNumber(accountNumber);
			if(null!=shipToCustomer){
				shipToCustomers.add(shipToCustomer);
			}
		}
		target.setShipToAccounts(shipToCustomers);
	}

	/**
	 * Populate Other attributes to B2BUnit
	 *
	 * @param asahiB2BUnitData
	 * @param asahiB2BUnitModel
	 */
	private void populatingOtherAttributes(final AsahiB2BUnitData asahiB2BUnitData, final AsahiB2BUnitModel asahiB2BUnitModel)
	{

		if (null != asahiB2BUnitData)
		{
			asahiB2BUnitModel.setCustOrderType(asahiB2BUnitData.getCustOrderType());

			asahiB2BUnitModel.setSalesOrg(asahiB2BUnitData.getSalesOrg());

			asahiB2BUnitModel.setDistributionChannel(asahiB2BUnitData.getDistributionChannel());

			asahiB2BUnitModel.setDivision(asahiB2BUnitData.getDivision());

			asahiB2BUnitModel.setCreditControlArea(asahiB2BUnitData.getCreditControlArea());

			populateCatalogHierarchy(asahiB2BUnitModel, asahiB2BUnitData);

			asahiB2BUnitModel.setTier(asahiB2BUnitData.getTier());
			asahiB2BUnitModel.setPaymentTerm(asahiB2BUnitData.getPaymentTerm());
			asahiB2BUnitModel.setCutOffTime(asahiB2BUnitData.getCutOffTime());
			asahiB2BUnitModel.setCooDate(asahiB2BUnitData.getCooDate());
			


		}

	}


	private void populateCatalogHierarchy(final AsahiB2BUnitModel asahiB2BUnitModel, final AsahiB2BUnitData asahiB2BUnitData) {
		final List<String> catalogIds = asahiB2BUnitData.getCatalogHierarchyId();
		if(CollectionUtils.isNotEmpty(catalogIds)){
			Collection<String> catalogs = new ArrayList<String>(catalogIds);
			asahiB2BUnitModel.setCatalogHierarchy(catalogs);
		}
	}


	/**
	 * Populate DeliveryDates to B2BUnit
	 *
	 * @param asahiB2BUnitData
	 * @param existingAddress
	 */
    private void populateDeliveryDates(final AsahiB2BUnitData asahiB2BUnitData, final AddressModel existingAddress) {
        final List<CutOffDeliveryData> deliveryDates = asahiB2BUnitData.getDeliveryDate();
        List<CutOffDeliveryDateModel> cutOffDelDates = null;
        if (SGA_COMPANY_CODE.equalsIgnoreCase(asahiB2BUnitData.getCompanyCode()) && CollectionUtils.isNotEmpty(deliveryDates)) {
            modelService.removeAll(existingAddress.getCutOffDeliveryDates());
            cutOffDelDates = new ArrayList<>();
            addCutOffDeliveryDates(asahiB2BUnitData, deliveryDates, cutOffDelDates);
            existingAddress.setCutOffDeliveryDates(cutOffDelDates);
            modelService.save(existingAddress);
        }
    }


	/**
	 * @param asahiB2BUnitData
	 * @param deliveryDates
	 * @param cutOffDelDates
	 *           This method adds cut off delivery dates to the list
	 */
	private void addCutOffDeliveryDates(final AsahiB2BUnitData asahiB2BUnitData, final List<CutOffDeliveryData> deliveryDates,
			final List<CutOffDeliveryDateModel> cutOffDelDates)
	{
	    final Map dateMap = new HashMap();
		for (final CutOffDeliveryData deliveryDateStr : deliveryDates)
		{
		    final String dateKey = deliveryDateStr.getCutoffDate() + "$$" + deliveryDateStr.getDeliveryDate();
            if(!dateMap.containsKey(dateKey)) { //do not consider duplicate cutoffdate
                dateMap.put(dateKey, true); //dummy value, only map key is required

                final CutOffDeliveryDateModel cutOffDeliveryDateModel = new CutOffDeliveryDateModel();
                try
                {
                    cutOffDeliveryDateModel.setCode(apbNumberKeyGeneratorService.generateCode(asahiB2BUnitData.getAddressId()));
                    cutOffDeliveryDateModel
                            .setCutOffDate(new SimpleDateFormat(CUSTOMER_DELIVERY_DATEPATTERN).parse(deliveryDateStr.getCutoffDate()));
                    cutOffDeliveryDateModel
                            .setDeliveryDate(new SimpleDateFormat(CUSTOMER_DELIVERY_DATEPATTERN).parse(deliveryDateStr.getDeliveryDate()));

                    cutOffDelDates.add(cutOffDeliveryDateModel);
                }
                catch (final ParseException e)
                {
                    LOGGER.error("Parse Exception caught in converting Delivery Date String B2B Unit" + e.getMessage());
                }
            }
		}
		modelService.saveAll(cutOffDelDates);
	}


	/**
	 * Populate active flag.
	 *
	 * @param active
	 *           the active
	 * @param target
	 *           the target
	 */
	private void populateActiveFlag(final Boolean active, final AsahiB2BUnitModel target)
	{
		if (null != active)
		{
			if (active.equals(Boolean.FALSE))
			{
				this.apbB2BUnitService.disableUnit(target);
			}
			else
			{
				if (null == target.getActive() || !target.getActive())
				{
					this.apbB2BUnitService.enableUnit(target);
				}
			}
		}
	}

	/**
	 * Populating address attributes.
	 *
	 * @param address
	 *           the address
	 * @param target
	 *           the target
	 */
	private void populatingAddressAttributes(final AsahiB2BUnitData source, final AsahiB2BUnitModel target)
	{
		final Collection<AddressModel> addressList = new ArrayList<>();
		if (null != target.getAddresses())
		{
			addressList.addAll(target.getAddresses());
		}
		// Fetching Address for AddressRecordID
		if (null != source.getAddress())
		{
			String addressRecordId = source.getAccountNum();
			if (!SGA_COMPANY_CODE.equalsIgnoreCase(source.getCompanyCode()))
			{
				addressRecordId = ApbFacadesConstants.CUSTOMER_INVOICE_ADDRESS_PREFIX + source.getBackendRecordID();
			}

			AddressModel existingAddress = this.apbB2BUnitService.getAddressForAddressRecordID(addressRecordId, target);

			final AddressData addressData = source.getAddress();

			addressData.setAddressInterface(false);
			addressData.setCustomerRecId(source.getBackendRecordID());
			/* Check if Address already exist in hybris if yes then update otherwise create new. */
			if (null != existingAddress)
			{

				if(SGA_COMPANY_CODE.equalsIgnoreCase(addressData.getCompanyCode()) && !asahiConfigurationService.getBoolean("update.address.street.with.backend.record.sga", false)){
				    restrictStreetUpdate(addressData, existingAddress);
                }
                else if(!SGA_COMPANY_CODE.equalsIgnoreCase(addressData.getCompanyCode()) && !asahiConfigurationService.getBoolean("update.address.street.with.backend.record.apb", false)){
				    restrictStreetUpdate(addressData, existingAddress);
                }
				// update existing address
				existingAddress = this.apbAddressReverseConverter.convert(addressData, existingAddress);

				if (null == existingAddress.getOwner())
				{
					final AsahiB2BUnitModel b2bUnit = this.apbB2BUnitService.getApbB2BUnit(source.getAccountNum(),
							source.getAbnNumber());
					existingAddress.setOwner(b2bUnit);
				}
				//For APB
				if (!SGA_COMPANY_CODE.equalsIgnoreCase(source.getCompanyCode()))
				{
					existingAddress.setAddressType(AddressType.INVOICE);
				}

				existingAddress.setAddressRecordid(addressRecordId);

				this.modelService.save(existingAddress);
				if (!addressList.contains(existingAddress))
				{
					addressList.add(existingAddress);
				}
			}
			else
			{
				//create new Address in hybris
				AddressModel newAddress = this.modelService.create(AddressModel.class);

				//calling converter to populate the AddressModel
				newAddress = this.apbAddressReverseConverter.convert(addressData, newAddress);
				if (null == newAddress.getOwner())
				{
					final AsahiB2BUnitModel b2bUnit = this.apbB2BUnitService.getApbB2BUnit(source.getAccountNum(),
							source.getAbnNumber());
					newAddress.setOwner(b2bUnit);
				}
                if (!SGA_COMPANY_CODE.equalsIgnoreCase(source.getCompanyCode())) {
                    newAddress.setAddressType(AddressType.INVOICE);
                }
				newAddress.setAddressRecordid(addressRecordId);
				//saving new AddressModel into hybris
				this.modelService.save(newAddress);
				addressList.add(newAddress);
                existingAddress = apbB2BUnitService.getAddressForAddressRecordID(addressRecordId, target);  //set new address as existingaddress now
			}

			populateDeliveryDates(source, existingAddress);
			target.setAddresses(addressList);
		}
	}

	/**
	 * Populating warehouse attributes.
	 *
	 * @param warehouse
	 *           the warehouse
	 * @param target
	 *           the target
	 */
	private void populatingWarehouseAttributes(final WarehouseData warehouse, final AsahiB2BUnitModel target)
	{
		if (null != warehouse && null != warehouse.getCode())
		{
			final WarehouseModel warehouseModel = this.apbB2BUnitService.getwarehouseForCode(warehouse.getCode());
			target.setWarehouse(warehouseModel);
		}
		else
		{
			target.setWarehouse(null);
		}
	}

	/**
	 * Populating license type attributes.
	 *
	 * @param licenseTypesData
	 *           the license types data
	 * @param target
	 *           the target
	 */
	private void populatingLicenseTypeAttributes(final LicenseTypesData licenseTypesData, final AsahiB2BUnitModel target)
	{
		if (null != licenseTypesData && null != licenseTypesData.getCode())
		{
			//Getting LicenseTypes for the requested code
			final LicenseTypesModel existingLicenseTypes = this.apbB2BUnitService.getLicenseTypesForCode(licenseTypesData.getCode());

			//Check If LicenseTypes exist for the requested code
			if(null==existingLicenseTypes){
				//Creating new LicenseTypes If LicenseTypes does not exist for requested code
				LicenseTypesModel newLicenseTypes = this.modelService.create(LicenseTypesModel.class);
				newLicenseTypes.setCode(licenseTypesData.getCode());
				newLicenseTypes.setName(licenseTypesData.getName());
				this.modelService.save(newLicenseTypes);
				target.setEclLicenseTypeCode(newLicenseTypes);
			}else{
				existingLicenseTypes.setName(licenseTypesData.getName());
				this.modelService.save(existingLicenseTypes);
				target.setEclLicenseTypeCode(existingLicenseTypes);
			}
		}
		else
		{
			target.setEclLicenseTypeCode(null);
		}
	}

	/**
	 * Populating subchannel attributes.
	 *
	 * @param subChannelData
	 *           the sub channel data
	 * @param target
	 *           the target
	 */
	private void populatingSubchannelAttributes(final SubChannelData subChannelData, final AsahiB2BUnitModel target)
	{

		if (null != subChannelData && null != subChannelData.getCode())
		{
			//Getting SubChannel for the requested code
			final SubChannelModel existingSubChannel = this.apbB2BUnitService.getSubChannelForCode(subChannelData.getCode());

			//Check If SubChannel exist for the requested code
			if(null==existingSubChannel){
				//Creating new SubChannel If SubChannel does not exist for requested code
				SubChannelModel newSubChannel = this.modelService.create(SubChannelModel.class);
				newSubChannel.setCode(subChannelData.getCode());
				newSubChannel.setName(subChannelData.getName());
				this.modelService.save(newSubChannel);
				target.setEclSubchannelCode(newSubChannel);

			}else{
				existingSubChannel.setName(subChannelData.getName());
				this.modelService.save(existingSubChannel);
				target.setEclSubchannelCode(existingSubChannel);
			}
		}
		else
		{
			target.setEclSubchannelCode(null);
		}
	}

	/**
	 * Populating license class attributes.
	 *
	 * @param licenceClassData
	 *           the licence class data
	 * @param target
	 *           the target
	 */
	private void populatingLicenseClassAttributes(final LicenceClassData licenceClassData, final AsahiB2BUnitModel target)
	{
		if (null != licenceClassData && null != licenceClassData.getCode())
		{
			//Getting LicenceClass for the requested code
			final LicenceClassModel existingLicenceClass = this.apbB2BUnitService.getLicenceClassForCode(licenceClassData.getCode());

			//Check If LicenseClass exist for the requested code
			if(null==existingLicenceClass){
				//Creating new LicenseClass If LicenseClass does not exist for requested code
				LicenceClassModel newLicenseTypes = this.modelService.create(LicenceClassModel.class);
				newLicenseTypes.setCode(licenceClassData.getCode());
				newLicenseTypes.setName(licenceClassData.getName());
				this.modelService.save(newLicenseTypes);
				target.setEclLicenseClassCode(newLicenseTypes);
			}else{
				existingLicenceClass.setName(licenceClassData.getName());
				this.modelService.save(existingLicenceClass);
				target.setEclLicenseClassCode(existingLicenceClass);
			}
		}
		else
		{
			target.setEclLicenseClassCode(null);
		}
	}

	/**
	 * Populating channel attributes.
	 *
	 * @param channelData
	 *           the channel data
	 * @param target
	 *           the target
	 */
	private void populatingChannelAttributes(final ChannelData channelData, final AsahiB2BUnitModel target)
	{
		if (null != channelData && null != channelData.getCode())
		{
			//Getting Channel for the requested code
			final ChannelModel existingChannel = this.apbB2BUnitService.getChannelForCode(channelData.getCode());

			//Check If Channel exist for the requested code
			if(null==existingChannel){
				//Creating new Channel If Channel does not exist for requested code
				ChannelModel newChannel = this.modelService.create(ChannelModel.class);
				newChannel.setCode(channelData.getCode());
				newChannel.setName(channelData.getName());
				this.modelService.save(newChannel);
				target.setEclChannelCode(newChannel);
			}else{
				existingChannel.setName(channelData.getName());
				this.modelService.save(existingChannel);
				target.setEclChannelCode(existingChannel);
			}
		}
		else
		{
			target.setEclChannelCode(null);
		}
	}

	/**
	 * Populating banner group attributes.
	 *
	 * @param bannerGroupsData
	 *           the banner groups data
	 * @param target
	 *           the target
	 */
	private void populatingBannerGroupAttributes(final BannerGroupsData bannerGroupsData, final AsahiB2BUnitModel target)
	{
		if (null != bannerGroupsData && null != bannerGroupsData.getCode())
		{
			//Getting BannerGroup for the requested code
			final BannerGroupsModel existingBannerGroup = this.apbB2BUnitService.getBannerGroupsForCode(bannerGroupsData.getCode());

			//Check If Banner exist for the requested code
			if(null==existingBannerGroup){
				//Creating new BannerGroup If BannerGroup does not exist for requested code
				BannerGroupsModel newBannerGroup = this.modelService.create(BannerGroupsModel.class);
				newBannerGroup.setCode(bannerGroupsData.getCode());
				newBannerGroup.setName(bannerGroupsData.getName());
				this.modelService.save(newBannerGroup);
				target.setEclBannerGroupid(newBannerGroup);
			}else{
				existingBannerGroup.setName(bannerGroupsData.getName());
				this.modelService.save(existingBannerGroup);
				target.setEclBannerGroupid(existingBannerGroup);
			}
		}
		else
		{
			target.setEclBannerGroupid(null);
		}
	}

	/**
	 * Populating account type attributes.
	 *
	 * @param accountTypeData
	 *           the account type data
	 * @param target
	 *           the target
	 */
	private void populatingAccountTypeAttributes(final AccountTypeData accountTypeData, final AsahiB2BUnitModel target)
	{
		if (null != accountTypeData && null != accountTypeData.getCode())
		{
			//Getting AccountType for the requested code
			final AccountTypeModel existingAccountType = this.apbB2BUnitService.getAccountTypeForCode(accountTypeData.getCode());

			//Check If AccountType exist for the requested code
			if(null==existingAccountType){
				//Creating new AccountType If AccountType does not exist for requested code
				AccountTypeModel newAccountType = this.modelService.create(AccountTypeModel.class);
				newAccountType.setCode(accountTypeData.getCode());
				newAccountType.setName(accountTypeData.getName());
				this.modelService.save(newAccountType);
				target.setEclAccountTypeId(newAccountType);
			}else{
				existingAccountType.setName(accountTypeData.getName());
				this.modelService.save(existingAccountType);
				target.setEclAccountTypeId(existingAccountType);
			}
		}
		else
		{
			target.setEclAccountTypeId(null);
		}
	}

	/**
	 * Populating account group attributes.
	 *
	 * @param accountGroupsData
	 *           the account groups data
	 * @param target
	 *           the target
	 */
	private void populatingAccountGroupAttributes(final AccountGroupsData accountGroupsData, final AsahiB2BUnitModel target)
	{
		if (null != accountGroupsData && null != accountGroupsData.getCode())
		{
			//Getting AccountGroup for the requested code
			final AccountGroupsModel existingAccountGroup = this.apbB2BUnitService
					.getAccountGroupsForCode(accountGroupsData.getCode());

			//Check If AccountGroup exist for the requested code
			if(null==existingAccountGroup){
				//Creating new AccountGroup If AccountGroup does not exist for requested code
				AccountGroupsModel newAccountGroup = this.modelService.create(AccountGroupsModel.class);
				newAccountGroup.setCode(accountGroupsData.getCode());
				newAccountGroup.setName(accountGroupsData.getName());
				this.modelService.save(newAccountGroup);
				target.setEclAccountGroupId(newAccountGroup);
			}else{
				existingAccountGroup.setName(accountGroupsData.getName());
				this.modelService.save(existingAccountGroup);
				target.setEclAccountGroupId(existingAccountGroup);
			}
		}
		else
		{
			target.setEclAccountGroupId(null);
		}
	}

	/**
	 * Populating basic attributes.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 */
	private void populatingBasicAttributes(final AsahiB2BUnitData source, final AsahiB2BUnitModel target)
	{
		target.setAbnNumber(source.getAbnNumber());
		target.setAccountNum(source.getAccountNum());
		if (null == target.getUid())
		{
			target.setUid(source.getUid());
		}
		if (null != source.getTradingName())
		{
			target.setLocName(source.getTradingName());
		}
		else
		{
			target.setLocName(source.getName());
		}
		target.setName(source.getName());
		target.setCompanyCode(source.getCompanyCode());
		target.setCompanyUid(source.getCompanyCode());
		target.setBackendRecordID(source.getBackendRecordID());
		target.setPurposeCode(source.getPurposeCode());
		target.setPhone(source.getPhone());
		target.setCellularPhone(source.getCellularPhone());
		target.setSalesRepCode(source.getSalesRepCode());
		target.setSalesRepName(source.getSalesRepName());
		target.setSalesRepEmailID(source.getSalesRepEmailID());
		target.setSalesRepPhone(source.getSalesRepPhone());
		target.setEmailAddress(source.getEmail());
		target.setTeleFax(source.getTeleFax());
		target.setCooDate(source.getCooDate());
		/*
		 * if (null != source.getEmail()) { final String emailAddresses = source.getEmail(); final String[] email =
		 * emailAddresses.split(this.asahiConfigurationService.getString(CUSTOMER_EMAIL_SPLIT, ","));
		 * target.setEmailAddress(email[0]); }
		 */
		target.setLiquorLicensenumber(source.getLiquorLicensenumber());

		//setting Customer Account Type
		if(null!=source.getCustomerType()){
			target.setBackendCustomerType(this.enumerationService.getEnumerationValue(BackendCustomerType.class,source.getCustomerType()));
		}
		modelService.save(target);
		//setting Payer Customer Account Type
		if(null!=source.getPayerAccount()){
			target.setPayerAccount(this.apbB2BUnitService.getB2BUnitByAccountNumber(source.getPayerAccount()));
		}
	}

	/**
	 * @return the apbAddressReverseConverter
	 */
	public Converter<AddressData, AddressModel> getApbAddressReverseConverter()
	{
		return apbAddressReverseConverter;
	}

	/**
	 * @param apbAddressReverseConverter
	 *           the apbAddressReverseConverter to set
	 */
	public void setApbAddressReverseConverter(final Converter<AddressData, AddressModel> apbAddressReverseConverter)
	{
		this.apbAddressReverseConverter = apbAddressReverseConverter;
	}

	private void restrictStreetUpdate(final AddressData addressData, final AddressModel existingAddress){
        addressData.setStreetname(existingAddress.getStreetname());
        addressData.setStreetnumber(existingAddress.getStreetnumber());
    }
}
