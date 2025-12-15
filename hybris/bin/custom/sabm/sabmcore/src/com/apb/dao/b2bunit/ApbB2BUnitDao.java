package com.apb.dao.b2bunit;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import com.apb.core.model.AccountGroupsModel;
import com.apb.core.model.AccountTypeModel;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiSAMAccessModel;
import com.apb.core.model.BannerGroupsModel;
import com.apb.core.model.ChannelModel;
import com.apb.core.model.LicenceClassModel;
import com.apb.core.model.LicenseTypesModel;
import com.apb.core.model.SubChannelModel;


/**
 * The Interface ApbB2BUnitDao.
 *
 * Kuldeep.Singh1
 */
public interface ApbB2BUnitDao extends Dao
{

	/**
	 * Gets the apb B 2 B unit by.
	 *
	 * @param abnNumber
	 *           the abn number
	 * @return the apb B 2 B unit by
	 */
	public AsahiB2BUnitModel getApbB2BUnitByAbn(String abnNumber);

	/**
	 * Gets the license types for code.
	 *
	 * @param code
	 *           the code
	 * @return the license types for code
	 */
	public LicenseTypesModel getLicenseTypesForCode(String code);

	/**
	 * Gets the sub channel for code.
	 *
	 * @param code
	 *           the code
	 * @return the sub channel for code
	 */
	public SubChannelModel getSubChannelForCode(String code);

	/**
	 * Gets the licence class for code.
	 *
	 * @param code
	 *           the code
	 * @return the licence class for code
	 */
	public LicenceClassModel getLicenceClassForCode(String code);

	/**
	 * Gets the channel for code.
	 *
	 * @param code
	 *           the code
	 * @return the channel for code
	 */
	public ChannelModel getChannelForCode(String code);

	/**
	 * Gets the banner groups for code.
	 *
	 * @param code
	 *           the code
	 * @return the banner groups for code
	 */
	public BannerGroupsModel getBannerGroupsForCode(String code);

	/**
	 * Gets the account type for code.
	 *
	 * @param code
	 *           the code
	 * @return the account type for code
	 */
	public AccountTypeModel getAccountTypeForCode(String code);

	/**
	 * Gets the account groups for code.
	 *
	 * @param code
	 *           the code
	 * @return the account groups for code
	 */
	public AccountGroupsModel getAccountGroupsForCode(String code);

	/**
	 * @param abpAccountNo
	 * @param abnNumber
	 * @param soldTo
	 *  @param checkPayer
	 * @return asahi b2b unit for abp and abn number
	 */
	public AsahiB2BUnitModel getApbB2BUnit(String abpAccountNo, String abnNumber);

	/**
	 * @param licenseNumber
	 * @return asahi b2b unit for liquor license number
	 */
	public AsahiB2BUnitModel findLiquorLicense(String licenseNumber);

	/**
	 * Gets the warehouse for code.
	 *
	 * @param code the code
	 * @return the warehouse for code
	 */
	public WarehouseModel getwarehouseForCode(String code);

	/**
	 * Gets the address for address record ID.
	 *
	 * @param id the id
	 * @param b2bUnit 
	 * @return the address for address record ID
	 */
	public AddressModel getAddressForAddressRecordID(String id, B2BUnitModel b2bUnit);

	/**
	 * Gets the b 2 B unit by backend ID.
	 *
	 * @param customerRecId the customer rec id
	 * @return the b 2 B unit by backend ID
	 */
	public B2BUnitModel getB2BUnitByBackendID(String customerRecId);
	
	/**
	 * Gets the b 2 B unit by account number.
	 *
	 * @param accountNumber the account number
	 * @return the b 2 B unit by account number
	 */
	public AsahiB2BUnitModel getB2BUnitByAccountNumber(String accountNumber);

	/**
	 * Gets the address by record id.
	 *
	 * @param addressId the address id
	 * @return the address by record id
	 */
	public AddressModel getAddressByRecordId(String addressId);

	/**
	 * @param customer
	 * @param b2bUnit
	 * @return
	 */
	public AsahiSAMAccessModel getAccessModel(B2BCustomerModel customer, AsahiB2BUnitModel b2bUnit);
	
	/**
	 * @param uid
	 * @return
	 */
	public AsahiB2BUnitModel getB2BUnitByUID(final String uid);
	
}
