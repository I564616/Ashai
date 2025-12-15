/**
 *
 */
package com.sabmiller.facades.user.converter.populator;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.services.BaseStoreService;

import com.sabmiller.core.enums.AddressType;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.apb.core.service.address.AsahiAddressService;
import com.apb.core.service.config.AsahiConfigurationService;
import de.hybris.platform.enumeration.EnumerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.apb.core.util.ApbAddressTimeUtil;
import com.apb.core.util.AsahiSiteUtil;

import org.apache.commons.lang3.StringUtils;
import com.apb.facades.constants.ApbFacadesConstants;
import de.hybris.platform.servicelayer.model.ModelService;
import com.sabmiller.core.constants.SabmCoreConstants;
import jakarta.annotation.Resource;
import de.hybris.platform.servicelayer.user.UserService;


/**
 * @author joshua.a.antony
 *
 */
public class SabmAddressReversePopulator extends AddressReversePopulator
{

	final Logger logger = LoggerFactory.getLogger(SabmAddressReversePopulator.class);
	
	/** The Constant APB_COMPANY_CODE. */
	private static final String APB_COMPANY_CODE = "apb";
	
	/** The user service. */
	@Resource(name="userService")
	private UserService userService;
	
	/** The asahi configuration service. */
	@Resource(name="asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;
	
	/** The enumeration service. */
	@Resource(name="enumerationService")
	private EnumerationService enumerationService;
	
	/** The apb B2B unit service. */
	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;
	
	@Resource(name="apbAddressService")
	private AsahiAddressService apbAddressService;
	
	@Resource
	private ModelService modelService; 
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Override
	public void populate(final AddressData addressData, final AddressModel addressModel) throws ConversionException
	{
		super.populate(addressData, addressModel);
		if(!asahiSiteUtil.isCub())
		{
			apbPopulate(addressData,addressModel);
		}
		else
		{
   		addressModel.setContactAddress(Boolean.valueOf(addressData.isContactAddress()));
   		addressModel.setPartnerNumber(addressData.getPartnerNumber());
		}
	}
	
	private void apbPopulate(final AddressData addressData, final AddressModel addressModel)
	{
		B2BUnitModel b2bUnit =null; 
		if(null==addressModel.getOwner() && addressData.getAddressInterface()){
			if(null!=addressData.getCompanyCode() && APB_COMPANY_CODE.equalsIgnoreCase(addressData.getCompanyCode())){
				
				b2bUnit = this.apbB2BUnitService.getB2BUnitByBackendID(addressData.getCustomerRecId());
			}else{
				b2bUnit = this.apbB2BUnitService.getB2BUnitByAccountNumber(addressData.getCustomerRecId());
			}
			addressModel.setOwner(b2bUnit);
		}
		
		addressModel.setCompanyCode(addressData.getCompanyCode());
		
		if(APB_COMPANY_CODE.equalsIgnoreCase(addressData.getCompanyCode())){
			
			addressModel.setAddressRecordid(addressData.getRecordId());
		}else{
			addressModel.setAddressRecordid(addressData.getCustomerRecId());
		}
		addressModel.setEclDeliveryInstruction(addressData.getDeliveryInstruction());
		addressModel.setEclDlvModeId(addressData.getDlvModeId());
		addressModel.setEmail(addressData.getEmail());
		addressModel.setStreetnumber(addressData.getStreetnumber());
		addressModel.setStreetname(addressData.getStreetname());
		if(null!=addressData.getAddressType())
		{
			addressModel.setAddressType(this.enumerationService.getEnumerationValue(AddressType.class, apbAddressService.getAddressStatusMapping(addressData.getAddressType())));
			
			if(addressData.getAddressType().equalsIgnoreCase(ApbFacadesConstants.DEFAULT_ADDRESS_CODE)){
				if(null != b2bUnit)
				{
					b2bUnit.getShippingAddresses().stream().forEach( shippingAdressModel -> {shippingAdressModel.setDefaultAddress(Boolean.FALSE);});
					modelService.saveAll(b2bUnit.getShippingAddresses()); 
				}else{
					b2bUnit = this.apbB2BUnitService.getB2BUnitByBackendID(addressData.getCustomerRecId());
					if(null!=b2bUnit){
						b2bUnit.getShippingAddresses().stream().forEach( shippingAdressModel -> {shippingAdressModel.setDefaultAddress(Boolean.FALSE);});
						modelService.saveAll(b2bUnit.getShippingAddresses()); 
					}
				}
				addressModel.setDefaultAddress(Boolean.TRUE);
			}
			
			if(addressData.getAddressType().equalsIgnoreCase(ApbFacadesConstants.DEFAULT_ADDRESS_CODE_FOR_SGA)){
				addressModel.setDefaultAddress(Boolean.TRUE);
				addressModel.setBillingAddress(true);	
				addressModel.setShippingAddress(true);
			}
			
			//For Invoice type address
			if(null!= addressData.getAddressInterface() && addressData.getAddressInterface()){
				addressModel.setShippingAddress(true);
			}else{
				addressModel.setBillingAddress(true);
			}
			addressModel.setVisibleInAddressBook(true);
		}else{
			addressModel.setBillingAddress(true);
			addressModel.setAddressType(addressData.getBackendAddressType());
		}
		
		addressModel.setDeliveryCalendar(addressData.getDeliveryCalendar());
		
		 if(StringUtils.isNotEmpty(addressData.getDeliveryTimeSlotFrom()))
		 {
			 addressModel.setEclDeliveryTimeslotFrom(ApbAddressTimeUtil.getDeliveryTimeDateObject(addressData.getDeliveryTimeSlotFrom()));
		 }
		 if(StringUtils.isNotEmpty(addressData.getDeliveryTimeSlotTo()))
		 {
			 addressModel.setEclDeliveryTimesLotto(ApbAddressTimeUtil.getDeliveryTimeDateObject(addressData.getDeliveryTimeSlotTo()));
		 }
	}
}
