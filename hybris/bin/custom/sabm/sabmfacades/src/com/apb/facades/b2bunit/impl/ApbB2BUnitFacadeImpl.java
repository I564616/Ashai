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
package com.apb.facades.b2bunit.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.customer.data.AsahiB2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.b2bunit.ApbB2BUnitFacade;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.google.common.collect.Lists;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiSAMAccessModel;
import com.sabmiller.core.model.CustImpFailedRecordsModel;
/**
 * The Class ApbB2BUnitFacadeImpl.
 *
 * Kuldeep.Singh1
 */
public class ApbB2BUnitFacadeImpl implements ApbB2BUnitFacade {

	private static final Logger LOG = LoggerFactory.getLogger(ApbB2BUnitFacadeImpl.class);

	/** The Constant APB_COMPANY_CODE. */
	private static final String APB_COMPANY_CODE = "apb";

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	/** The apb B2B unit service. */
	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;

	/** The apb B2B unit converter. */
	private Converter<AsahiB2BUnitData, AsahiB2BUnitModel> apbB2BUnitConverter;
	@Autowired
	private Converter<AsahiB2BUnitData, CustImpFailedRecordsModel> custImportFailedConverter;

	/** The apb address reverse converter. */
	private Converter<AddressData, AddressModel> apbAddressReverseConverter;

	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private UserService userService;

	@Resource
	private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;
	/**
	 * Gets the license types for code.
	 *
	 * @param b2bUnitData
	 *            the b 2 b unit data
	 */
	@Override
	public boolean importApbB2BUnit(final AsahiB2BUnitData b2bUnitData) {

		// Fetching B2BUnit based on abnNumber
		AsahiB2BUnitModel b2bUnit = this.apbB2BUnitService.getB2BUnitByAccountNumber(b2bUnitData.getAccountNum());
		/*
		 * Check if AsahiB2BUnit already exist in hybris if yes then update
		 * otherwise create new.
		 */
		if (null != b2bUnit) {
			// update existing product
			// calling converter to populate the AsahiB2BUnitModel
			LOG.info("Customer Exists Uid: " + b2bUnit.getUid());
			b2bUnit = this.apbB2BUnitConverter.convert(b2bUnitData, b2bUnit);
			// saving existing AsahiB2BUnit into hybris database
			this.modelService.save(b2bUnit);
			if (asahiSiteUtil.isSga()) {
				this.apbB2BUnitService.generateAsahiNotifyProcess(b2bUnit, b2bUnitData);
				}
			return true;
		} else {
			if (b2bUnitData.getActive()) {
				LOG.info("New Customer Uid: " + b2bUnitData.getUid());
				// create new AsahiB2BUnit in hybris
				AsahiB2BUnitModel asahiB2BUnit = this.modelService.create(AsahiB2BUnitModel.class);

				// calling converter to populate the AsahiB2BUnitModel
				asahiB2BUnit = this.apbB2BUnitConverter.convert(b2bUnitData, asahiB2BUnit);

				// saving new AsahiB2BUnit into hybris database
				this.modelService.save(asahiB2BUnit);
				if (asahiSiteUtil.isSga()) {
					this.apbB2BUnitService.generateAsahiNotifyProcess(asahiB2BUnit, b2bUnitData);
				}
                return true;
			} else {
				this.custImportFailed(b2bUnitData);
				return false;
			}
		}
	}

	/**
	 * Import apb B2B unit address.
	 *
	 * @param addressData
	 *            the address data
	 */
	@Override
	public void importApbB2BUnitAddress(final AddressData addressData) {
		B2BUnitModel b2bUnit = null;
		if (null != addressData.getCompanyCode() && APB_COMPANY_CODE.equalsIgnoreCase(addressData.getCompanyCode())) {

			b2bUnit = this.apbB2BUnitService.getB2BUnitByBackendID(addressData.getCustomerRecId());
		} else {
			b2bUnit = this.apbB2BUnitService.getB2BUnitByAccountNumber(addressData.getCustomerRecId());
		}

		if (null != b2bUnit) {
			if (addressData.getRecordId().equalsIgnoreCase(addressData.getCustomerRecId())) {
				if (CollectionUtils.isNotEmpty(b2bUnit.getAddresses())) {
					final AddressModel address = (AddressModel) ((List) b2bUnit.getAddresses()).get(0);
					addressData.setRecordId(address.getAddressRecordid());
				}
			}

			if (null == addressData.getAddressType()) {
				addressData.setAddressType(((AsahiB2BUnitModel) b2bUnit).getBackendCustomerType().toString());
			}

			// Fetching Address for AddressRecordID
			AddressModel existingAddress = this.apbB2BUnitService
					.getAddressForAddressRecordID(addressData.getRecordId(), b2bUnit);

			/*
			 * Check if Address already exist in hybris if yes then update
			 * otherwise create new.
			 */

			addressData.setAddressInterface(true);
			if (null != existingAddress) {
				// update existing address
				existingAddress = this.apbAddressReverseConverter.convert(addressData, existingAddress);
				this.modelService.save(existingAddress);
			} else {
				// create new Address in hybris
				AddressModel newAddress = this.modelService.create(AddressModel.class);

				// calling converter to populate the AddressModel
				newAddress = this.apbAddressReverseConverter.convert(addressData, newAddress);

				// saving new AddressModel into hybris
				this.modelService.save(newAddress);
			}

		}

	}

	/**
	 * @return the apbB2BUnitConverter
	 */
	public Converter<AsahiB2BUnitData, AsahiB2BUnitModel> getApbB2BUnitConverter() {
		return apbB2BUnitConverter;
	}

	/**
	 * @param apbB2BUnitConverter
	 *            the apbB2BUnitConverter to set
	 */
	public void setApbB2BUnitConverter(final Converter<AsahiB2BUnitData, AsahiB2BUnitModel> apbB2BUnitConverter) {
		this.apbB2BUnitConverter = apbB2BUnitConverter;
	}

	/**
	 * @return the apbAddressReverseConverter
	 */
	public Converter<AddressData, AddressModel> getApbAddressReverseConverter() {
		return apbAddressReverseConverter;
	}

	/**
	 * @param apbAddressReverseConverter
	 *            the apbAddressReverseConverter to set
	 */
	public void setApbAddressReverseConverter(final Converter<AddressData, AddressModel> apbAddressReverseConverter) {
		this.apbAddressReverseConverter = apbAddressReverseConverter;
	}

	@Override
	public boolean setCurrentUnit(final String b2bUnit) {
		boolean returnVal = Boolean.FALSE;
		try {
			LOG.info("Getting current B2bunit for code " + b2bUnit);
			final B2BUnitModel unit = apbB2BUnitService.getB2BUnitByAccountNumber(b2bUnit);
			if (null != unit) {
				LOG.info("Setting current B2bunit as " + b2bUnit);
				final B2BCustomerModel b2bCust = (B2BCustomerModel) userService.getCurrentUser();
				apbB2BUnitService.setCurrentUnit(b2bCust, unit);
				updateSpringContext(b2bCust);
				returnVal = Boolean.TRUE;
			}
		} catch (final Exception ex) {
			LOG.error("Exception encountered while setting current b2bunit as " + b2bUnit + " is " + ex);
		}
		return returnVal;
	}

	/**
	 * Updating the spring context for customer
	 *
	 * @param b2bCust
	 */
	private void updateSpringContext(final B2BCustomerModel b2bCust) {

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
		final GrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_" + ApbCoreConstants.B2B_ADMIN_GROUP);
		boolean adminRoleExists = Boolean.FALSE;

		if (updatedAuthorities.contains(adminAuthority)) {
			adminRoleExists = Boolean.TRUE;
		}

		if (CollectionUtils.isNotEmpty(b2bCust.getAdminUnits()) && (null != b2bCust.getAdminUnits().stream()
				.filter(unit -> unit.getUid().equalsIgnoreCase(b2bCust.getDefaultB2BUnit().getUid())).findFirst()
				.orElse(null))) {
			/*
			 * Not adding admin role if already exists
			 */
			if (!adminRoleExists) {
				updatedAuthorities.add(adminAuthority);
			}

		} else if (adminRoleExists) {
			/*
			 * If role exists but user does not have admin rights on the current
			 * unit, remove admin role
			 */
			updatedAuthorities.remove(adminAuthority);
		}
		if (auth instanceof UsernamePasswordAuthenticationToken) {
			final Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(),
					auth.getCredentials(), updatedAuthorities);
			SecurityContextHolder.getContext().setAuthentication(newAuth);
			LOG.info("Spring context updated for b2bunit");
		}
	}

	@Override
	public String getSamAccessType(final String customerUid, final String payerB2bUnit) {
		String accessType = ApbCoreConstants.ORDER_ACCESS;
		final AsahiSAMAccessModel accessModel = apbB2BUnitService.getSamAccessModel(customerUid, payerB2bUnit);
		if (null != accessModel) {
			if (accessModel.isPayAccess() && accessModel.isOrderAccess()) {
				accessType = ApbCoreConstants.PAY_AND_ORDER_ACCESS;
			} else if (accessModel.isPayAccess()) {
				accessType = ApbCoreConstants.PAY_ACCESS;
			}
		}
		return accessType;
	}

	@Override
	public boolean isSamAccessApprovalPending(final String customerUid, final String payerB2bUnit) {
		boolean approvalPending = Boolean.TRUE;
		final AsahiSAMAccessModel accessModel = apbB2BUnitService.getSamAccessModel(customerUid, payerB2bUnit);
		if (null != accessModel) {
			approvalPending = accessModel.isPendingApproval();
		}
		return approvalPending;
	}

	@Override
	public boolean isSamAccessDenied(final String customerUid, final String payerB2bUnit) {
		boolean approvalDenied = Boolean.TRUE;
		final AsahiSAMAccessModel accessModel = apbB2BUnitService.getSamAccessModel(customerUid, payerB2bUnit);
		if (null != accessModel) {
			approvalDenied = accessModel.isApprovalDenied();
		}
		return approvalDenied;
	}

	@Override
	public boolean custImportFailed(final AsahiB2BUnitData b2bUnitData) {
		if ((null != b2bUnitData.getUid() && b2bUnitData.getUid().trim().length() > 0) || (null != b2bUnitData.getName() &&  b2bUnitData.getName().trim().length() > 0) ||(null != b2bUnitData.getAccountNum() &&  b2bUnitData.getAccountNum().trim().length() > 0)
				|| (null != b2bUnitData.getPurposeCode() && b2bUnitData.getPurposeCode().trim().length() > 0)
				|| (null != b2bUnitData.getSalesRepName() && b2bUnitData.getSalesRepName().trim().length() > 0)) {
			CustImpFailedRecordsModel custImportFailed = this.modelService.create(CustImpFailedRecordsModel.class);

			// calling converter to populate the CustImpFailedRecordsModel
			custImportFailed = this.custImportFailedConverter.convert(b2bUnitData, custImportFailed);

			// saving new CustImpFailedRecordsModel into hybris database
			this.modelService.save(custImportFailed);
			return false;
		}
		return false;
	}

	@Override
	public List<B2BUnitData> getB2BUnitsByCustomer(final String userId)
	{
		if (StringUtils.isBlank(userId))
		{
			LOG.warn("No Customer UID was provided.");
			return null;
		}
		//final CustomerModel customer = b2bCommerceUnitService.getCustomerForUid(uid);
		final CustomerModel customer = (CustomerModel) userService.getUserForUID(userId);
		final Set<PrincipalGroupModel> principalGroups = customer.getGroups();
		final List<B2BUnitData> b2bUnits = Lists.newArrayList();
		for (final PrincipalGroupModel principalGroup : SetUtils.emptyIfNull(principalGroups))
		{
			if (principalGroup instanceof AsahiB2BUnitModel)
			{
				final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) principalGroup;
				if (BooleanUtils.isTrue(b2bUnit.getActive()))
				{
					b2bUnits.add(b2bUnitConverter.convert(b2bUnit));
				}
				else
				{
					LOG.warn("This B2BUnit[{}] is not available", b2bUnit);
				}
			}
		}
		return b2bUnits;
	}
}