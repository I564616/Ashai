package com.apb.facades.b2bunit;

import com.apb.facades.b2bunit.data.AccountGroupsData;
import com.apb.facades.b2bunit.data.AccountTypeData;
import com.apb.facades.b2bunit.data.BannerGroupsData;
import com.apb.facades.b2bunit.data.ChannelData;
import com.apb.facades.b2bunit.data.LicenceClassData;
import com.apb.facades.b2bunit.data.LicenseTypesData;
import com.apb.facades.b2bunit.data.SubChannelData;

/**
 * The Class AsahiB2BUnitReferenceFacade.
 * 
 * @author Kuldeep.Singh1
 */
public interface AsahiB2BUnitReferenceFacade {
	
	/**
	 * Import account group.
	 *
	 * @param accountGroupsData the account groups data
	 */
	public void importAccountGroup(AccountGroupsData accountGroupsData);
	
	/**
	 * Import account type.
	 *
	 * @param accountTypeData the account type data
	 */
	public void importAccountType(AccountTypeData accountTypeData);
	
	/**
	 * Import banner group.
	 *
	 * @param bannerGroupsData the banner groups data
	 */
	public void importBannerGroup(BannerGroupsData bannerGroupsData);
	
	/**
	 * Import sub channel.
	 *
	 * @param subChannelData the sub channel data
	 */
	public void importSubChannel(SubChannelData subChannelData);
	
	/**
	 * Import channel.
	 *
	 * @param channelData the channel data
	 */
	public void importChannel(ChannelData channelData);
	
	/**
	 * Import license type.
	 *
	 * @param licenseTypesData the license types data
	 */
	public void importLicenseType(LicenseTypesData licenseTypesData);
	
	/**
	 * Import licence class.
	 *
	 * @param licenceClassData the licence class data
	 */
	public void importLicenceClass(LicenceClassData licenceClassData);
}
