/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.user.converters.populator.CustomerPopulator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.services.BaseStoreService;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.product.data.AsahiRoleData;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.enums.AsahiRole;


/**
 * Convert the CustomerModel to CustomerData
 *
 * @author xiaowu.a.zhang
 * @data 2015-12-24
 */
public class SABMCustomerPopulator extends CustomerPopulator
{

   @Resource(name="asahiRoleConverter")
	private Converter<AsahiRole, AsahiRoleData> asahiRoleConverter;

	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private AsahiCoreUtil asahiCoreUtil;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	 @Resource
    private ApbB2BUnitService apbB2BUnitService;

	@Override
	public void populate(final CustomerModel source, final CustomerData target) throws ConversionException
	{
		super.populate(source, target);

		if(!asahiSiteUtil.isCub())
		{

		//populating contact number for b2b customer
				if (source instanceof B2BCustomerModel)
				{
					target.setContactNumber(((B2BCustomerModel)source).getContactNumber());
					target.setMobileNumber(((B2BCustomerModel)source).getContactNumber());
					target.setAsahiRole(asahiRoleConverter.convert(((B2BCustomerModel) source).getAsahiRole()));
					if(asahiSiteUtil.isSga()){
						final Boolean disableEmailNotification = ((B2BCustomerModel)source).getDisableEmailNotification();
						target.setDisableEmailNotification(disableEmailNotification != null? disableEmailNotification:false);
						target.setSamAccess(asahiCoreUtil.getCurrentUserAccessType());
					}

					if(null!=((B2BCustomerModel)source).getLoggedInBefore() && (((B2BCustomerModel)source).getLoggedInBefore())){
						target.setLoggedInBefore(true);
					}else{
						target.setLoggedInBefore(false);
					}
					if(null != apbB2BUnitService.getCurrentB2BUnit() && null != apbB2BUnitService.getCurrentB2BUnit().getAdminUsers().stream().filter(user -> user.getUid().equals(source.getUid())).findFirst().orElse(null)){
					    target.setIsAdminUser(true);
		            }
		            else
		            {
		                target.setIsAdminUser(false);
		            }
				}

		}

   		if (StringUtils.isBlank(target.getFirstName()))
   		{
   			target.setFirstName(source.getFirstName());
   		}

   		if (StringUtils.isBlank(target.getLastName()))
   		{
   			target.setLastName(source.getLastName());
   		}

   		target.setMobileNumber(source.getMobileContactNumber());

   		target.setGaUid(source.getPk().toString());


         target.setMobileNumber(source.getMobileContactNumber());
   		target.setBusinessContactNumber(source.getBusinessContactPhoneNumber());

	}

}
