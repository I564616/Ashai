package com.apb.service.b2bunit;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.customer.data.AsahiB2BUnitData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

import java.util.List;
import java.util.Map;

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
 * The Interface ApbB2BUnitService.
 *
 * Kuldeep.Singh1
 */
public interface ApbB2BUnitService extends B2BUnitService<B2BUnitModel, B2BCustomerModel>
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
	 * @param abnAccountId
	 * @param abnNumber
	 * @param checkSoldTo
	 * @param checkPayer
	 * @return
	 */
	public AsahiB2BUnitModel getApbB2BUnit(String abnAccountId, String abnNumber);

	/**
	 * @param liquorLicense
	 * @return
	 */
	public AsahiB2BUnitModel findLiquorLicense(String liquorLicense);

	/**
	 * Gets the warehouse for code.
	 *
	 * @param code
	 *           the code
	 * @return the warehouse for code
	 */
	public WarehouseModel getwarehouseForCode(String code);

	/**
	 * Gets the address for address record ID.
	 *
	 * @param id
	 *           the id
	 * @param b2bUnit
	 * @return the address for address record ID
	 */
	public AddressModel getAddressForAddressRecordID(String id, B2BUnitModel b2bUnit);

	/**
	 * Gets the b 2 B unit by backend ID.
	 *
	 * @param customerRecId
	 *           the customer rec id
	 * @return the b 2 B unit by backend ID
	 */
	public B2BUnitModel getB2BUnitByBackendID(String customerRecId);

	/**
	 * Gets the b 2 B unit by accountNumber.
	 *
	 * @param accountNumber
	 *           the account number
	 * @return the b 2 B unit by account number
	 */
	public AsahiB2BUnitModel getB2BUnitByAccountNumber(String accountNumber);

	/**
	 * This method is used to get the account number for current b2b unit.
	 */
	public String getAccNumForCurrentB2BUnit();

	/**
	 * This method is used to get the current b2b unit.
	 */
	public AsahiB2BUnitModel getCurrentB2BUnit();

	/**
	 * Gets the address by record id.
	 *
	 * @param addressId
	 *           the address id
	 * @return the address by record id
	 */
	public AddressModel getAddressByRecordId(String addressId);

	/**
	 * This method is used to generate Asahi NotificationEmail
	 *
	 * @param asahiB2BUnit
	 * @param b2bUnit
	 * @param b2bUnit
	 */
	void generateAsahiNotifyProcess(AsahiB2BUnitModel asahiB2BUnit, AsahiB2BUnitData b2bUnitData);

	/**
	 * Method will return current customer's active b2b units
	 *
	 * @param userId
	 * @return list
	 */
	Map<String, List<AsahiB2BUnitModel>> getUserActiveB2BUnits(final String userId);

	/**
	 * @param samAccess
	 * @param customer
	 * @param string
	 * @return AsahiSAMAccessModel
	 */
	public AsahiSAMAccessModel createSamAccess(final String samAccess, final B2BCustomerModel customer, final String string);

	/**
	 * @param customer
	 * @param string
	 * @param samAccess
	 * @return AsahiSAMAccessModel
	 */
	public AsahiSAMAccessModel updateUserSamAccess(final B2BCustomerModel customer, final String string, final String samAccess);


	/**
	 * @param customerUid
	 * @param b2bUnit
	 * @return model
	 */
	public AsahiSAMAccessModel getSamAccessModel(final String customerUid, final String b2bUnit);

	/**
	 * @param customer
	 * @param samAccess
	 * @param accessModel
	 * @param defaultB2BUnit
	 * @return AsahiSAMAccessModel
	 */
	public AsahiSAMAccessModel updateSamAccessByUser(final B2BCustomerModel customer, final AsahiB2BUnitModel defaultB2BUnit,
			final AsahiSAMAccessModel accessModel, final String samAccess);
	
	public String getSamAccessTypeForCustomer(final B2BCustomerModel customer);
}
