package com.apb.facades.b2bunit.impl;

import jakarta.annotation.Resource;

import com.apb.core.model.AccountGroupsModel;
import com.apb.core.model.AccountTypeModel;
import com.apb.core.model.BannerGroupsModel;
import com.apb.core.model.ChannelModel;
import com.apb.core.model.LicenceClassModel;
import com.apb.core.model.LicenseTypesModel;
import com.apb.core.model.SubChannelModel;
import com.apb.facades.b2bunit.AsahiB2BUnitReferenceFacade;
import com.apb.facades.b2bunit.data.AccountGroupsData;
import com.apb.facades.b2bunit.data.AccountTypeData;
import com.apb.facades.b2bunit.data.BannerGroupsData;
import com.apb.facades.b2bunit.data.ChannelData;
import com.apb.facades.b2bunit.data.LicenceClassData;
import com.apb.facades.b2bunit.data.LicenseTypesData;
import com.apb.facades.b2bunit.data.SubChannelData;
import com.apb.service.b2bunit.ApbB2BUnitService;

import de.hybris.platform.servicelayer.model.ModelService;

/**
 * The Class AsahiB2BUnitReferenceFacadeImpl.
 * 
 * @author Kuldeep.Singh1
 */
public class AsahiB2BUnitReferenceFacadeImpl implements AsahiB2BUnitReferenceFacade{

	/** The apb B 2 B unit service. */
	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;
	
	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;
	
	/**
	 * Import account group.
	 *
	 * @param accountGroupsData the account groups data
	 */
	@Override
	public void importAccountGroup(AccountGroupsData accountGroupsData) {
		if(null!=accountGroupsData){
			//Getting AccountGroup for the requested code
			AccountGroupsModel existingAccountGroup = this.apbB2BUnitService.getAccountGroupsForCode(accountGroupsData.getCode());
			
			//Check If AccountGroup exist for the requested code
			if(null==existingAccountGroup){
				//Creating new AccountGroup If AccountGroup does not exist for requested code
				AccountGroupsModel newAccountGroup = this.modelService.create(AccountGroupsModel.class);
				newAccountGroup.setCode(accountGroupsData.getCode());
				newAccountGroup.setName(accountGroupsData.getName());
				this.modelService.save(newAccountGroup);
			}else{
				existingAccountGroup.setName(accountGroupsData.getName());
				this.modelService.save(existingAccountGroup);
			}
		}
	}

	/**
	 * Import account type.
	 *
	 * @param accountTypeData the account type data
	 */
	@Override
	public void importAccountType(AccountTypeData accountTypeData) {
		
		if(null!=accountTypeData){
			//Getting AccountType for the requested code
			AccountTypeModel existingAccountType = this.apbB2BUnitService.getAccountTypeForCode(accountTypeData.getCode());
		
			//Check If AccountType exist for the requested code
			if(null==existingAccountType){
				//Creating new AccountType If AccountType does not exist for requested code
				AccountTypeModel newAccountType = this.modelService.create(AccountTypeModel.class);
				newAccountType.setCode(accountTypeData.getCode());
				newAccountType.setName(accountTypeData.getName());
				this.modelService.save(newAccountType);
			}else{
				existingAccountType.setName(accountTypeData.getName());
				this.modelService.save(existingAccountType);
			}
		}
	}

	/**
	 * Import banner group.
	 *
	 * @param bannerGroupsData the banner groups data
	 */
	@Override
	public void importBannerGroup(BannerGroupsData bannerGroupsData) {
		
		if(null!=bannerGroupsData){
			//Getting BannerGroup for the requested code
			BannerGroupsModel existingBannerGroup = this.apbB2BUnitService.getBannerGroupsForCode(bannerGroupsData.getCode());
			
			//Check If Banner exist for the requested code
			if(null==existingBannerGroup){
				//Creating new BannerGroup If BannerGroup does not exist for requested code
				BannerGroupsModel newBannerGroup = this.modelService.create(BannerGroupsModel.class);
				newBannerGroup.setCode(bannerGroupsData.getCode());
				newBannerGroup.setName(bannerGroupsData.getName());
				this.modelService.save(newBannerGroup);
			}else{
				existingBannerGroup.setName(bannerGroupsData.getName());
				this.modelService.save(existingBannerGroup);
			}
		}
	}
	
	/**
	 * Import sub channel.
	 *
	 * @param subChannelData the sub channel data
	 */
	@Override
	public void importSubChannel(SubChannelData subChannelData) {
		if(null!=subChannelData){
			//Getting SubChannel for the requested code
			SubChannelModel existingSubChannel = this.apbB2BUnitService.getSubChannelForCode(subChannelData.getCode());
			
			//Check If SubChannel exist for the requested code
			if(null==existingSubChannel){
				//Creating new SubChannel If SubChannel does not exist for requested code
				SubChannelModel newSubChannel = this.modelService.create(SubChannelModel.class);
				newSubChannel.setCode(subChannelData.getCode());
				newSubChannel.setName(subChannelData.getName());
				this.modelService.save(newSubChannel);
				
			}else{
				existingSubChannel.setName(subChannelData.getName());
				this.modelService.save(existingSubChannel);
			}
		}
	}

	/**
	 * Import channel.
	 *
	 * @param channelData the channel data
	 */
	@Override
	public void importChannel(ChannelData channelData) {
		if(null!=channelData){
			//Getting Channel for the requested code
			ChannelModel existingChannel = this.apbB2BUnitService.getChannelForCode(channelData.getCode());
			
			//Check If Channel exist for the requested code
			if(null==existingChannel){
				//Creating new Channel If Channel does not exist for requested code
				ChannelModel newChannel = this.modelService.create(ChannelModel.class);
				newChannel.setCode(channelData.getCode());
				newChannel.setName(channelData.getName());
				this.modelService.save(newChannel);
			}else{
				existingChannel.setName(channelData.getName());
				this.modelService.save(existingChannel);
			}
		}
	}

	/**
	 * Import license type.
	 *
	 * @param licenseTypesData the license types data
	 */
	@Override
	public void importLicenseType(LicenseTypesData licenseTypesData) {
		if(null!=licenseTypesData){
			//Getting LicenseTypes for the requested code
			LicenseTypesModel existingLicenseTypes = this.apbB2BUnitService.getLicenseTypesForCode(licenseTypesData.getCode());
			
			//Check If LicenseTypes exist for the requested code
			if(null==existingLicenseTypes){
				//Creating new LicenseTypes If LicenseTypes does not exist for requested code
				LicenseTypesModel newLicenseTypes = this.modelService.create(LicenseTypesModel.class);
				newLicenseTypes.setCode(licenseTypesData.getCode());
				newLicenseTypes.setName(licenseTypesData.getName());
				this.modelService.save(newLicenseTypes);
			}else{
				existingLicenseTypes.setName(licenseTypesData.getName());
				this.modelService.save(existingLicenseTypes);
			}
		}
	}

	/**
	 * Import licence class.
	 *
	 * @param licenceClassData the licence class data
	 */
	@Override
	public void importLicenceClass(LicenceClassData licenceClassData) {
		if(null!=licenceClassData){
			//Getting LicenceClass for the requested code
			LicenceClassModel existingLicenceClass = this.apbB2BUnitService.getLicenceClassForCode(licenceClassData.getCode());
			
			//Check If LicenseClass exist for the requested code
			if(null==existingLicenceClass){
				//Creating new LicenseClass If LicenseClass does not exist for requested code
				LicenceClassModel newLicenseTypes = this.modelService.create(LicenceClassModel.class);
				newLicenseTypes.setCode(licenceClassData.getCode());
				newLicenseTypes.setName(licenceClassData.getName());
				this.modelService.save(newLicenseTypes);
			}else{
				existingLicenceClass.setName(licenceClassData.getName());
				this.modelService.save(existingLicenceClass);
			}
		}
	}

}
