/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.facades.b2bunit;

import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.customer.data.AsahiB2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;

import java.util.List;


/**
 * The Interface ApbB2BUnitFacade.
 *
 *  Kuldeep.Singh1
 */
public interface ApbB2BUnitFacade{

	/**
	 * Import apb B 2 B unit.
	 *
	 * @param b2bUnitData the b 2 b unit data
	 */
	public boolean importApbB2BUnit(AsahiB2BUnitData b2bUnitData);

	/**
	 * Import apb B 2 B unit address.
	 *
	 * @param addressData the address data
	 */
	public void importApbB2BUnitAddress(AddressData addressData);

	/**
	 * Sets the given unit as default for current customer/user
	 * @param b2bUnit
	 * @return boolean
	 */
	public boolean setCurrentUnit(String b2bUnit);

	/**
	 * Gets the sam access type
	 * @param customerUid
	 * @param payerB2bUnit
	 * @return access type
	 */
	public String getSamAccessType(final String customerUid, final String payerB2bUnit);

	/**
	 * returns the sam access flag
	 * @param customerUid
	 * @param payerB2bUnit
	 * @return boolean
	 */
	public boolean isSamAccessApprovalPending(final String customerUid, final String payerB2bUnit) ;

	/**
	 * returns the approval pending flag
	 * @param customerUid
	 * @param payerB2bUnit
	 * @return boolean
	 */
	public boolean isSamAccessDenied(final String customerUid, final String payerB2bUnit);

	/**
	 * Import failed customer data
	 * @param b2bUnitData
	 *
	 */
	public boolean custImportFailed(AsahiB2BUnitData b2bUnitData);

	/**
	 *
	 * @param userId
	 * @return
	 */
	public List<B2BUnitData> getB2BUnitsByCustomer(String userId);

}