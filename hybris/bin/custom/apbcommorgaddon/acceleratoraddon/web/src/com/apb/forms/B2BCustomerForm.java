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
package com.apb.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateProfileForm;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.user.data.CustomerData;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.apb.facades.product.data.AsahiRoleData;


/**
 * Pojo for 'B2BCustomer' form.
 */
public class B2BCustomerForm extends UpdateProfileForm
{
	private boolean active;
	private String uid;
	private String parentB2BUnit;
	private String role;
	private Collection<CustomerData> approvers;
	private Collection<B2BUserGroupData> approverGroups;
	private Collection<B2BUserGroupData> permissionGroups;
	private Collection<B2BPermissionData> permissions;
	private String email;
	private AsahiRoleData asahiRole;
	private Collection<String> asahiCustomRoles = new ArrayList<String>();
	private String samAccess; 
	private boolean accessDenied;
	private boolean pendingApproval;
	private String mobileNumber;


	private static final String REGEXPATTERN = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";


	public String getUid()
	{
		return uid;
	}

	public void setUid(final String uid)
	{
		this.uid = uid;
	}

	@NotEmpty(message = "{profile.roles.invalid}")
	public Collection<String> getAsahiCustomRoles()
	{
		return asahiCustomRoles;
	}

	public void setAsahiCustomRoles(final Collection<String> asahiCustomRoles)
	{
		this.asahiCustomRoles = asahiCustomRoles;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(final boolean active)
	{
		this.active = active;
	}

	public Collection<CustomerData> getApprovers()
	{
		return approvers;
	}

	public void setApprovers(final Collection<CustomerData> approvers)
	{
		this.approvers = approvers;
	}

	public Collection<B2BUserGroupData> getApproverGroups()
	{
		return approverGroups;
	}

	public void setApproverGroups(final Collection<B2BUserGroupData> approverGroups)
	{
		this.approverGroups = approverGroups;
	}

	public Collection<B2BUserGroupData> getPermissionGroups()
	{
		return permissionGroups;
	}

	public void setPermissionGroups(final Collection<B2BUserGroupData> permissionGroups)
	{
		this.permissionGroups = permissionGroups;
	}

	public Collection<B2BPermissionData> getPermissions()
	{
		return permissions;
	}

	public void setPermissions(final Collection<B2BPermissionData> permissions)
	{
		this.permissions = permissions;
	}

	public String getRole()
	{
		return role;
	}

	public void setRole(final String role)
	{
		this.role = role;
	}

	public String getParentB2BUnit()
	{
		return parentB2BUnit;
	}

	public void setParentB2BUnit(final String parentB2BUnit)
	{
		this.parentB2BUnit = parentB2BUnit;
	}

	public AsahiRoleData getAsahiRole()
	{
		return asahiRole;
	}

	public void setAsahiRole(final AsahiRoleData asahiRole)
	{
		this.asahiRole = asahiRole;
	}

	@NotNull(message = "{profile.email.invalid}")
	//@Size(min = 1, max = 255, message = "{profile.email.invalid}")
	/* @Email(message = "{profile.email.invalid}") */
	//@Pattern(regexp = REGEXPATTERN, message = "{profile.email.invalid}")
	public String getEmail()
	{
		return email;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}

	public String getSamAccess()
	{
		return samAccess;
	}

	public void setSamAccess(String samAccess)
	{
		this.samAccess = samAccess;
	}

	public boolean getAccessDenied()
	{
		return accessDenied;
	}

	public void setAccessDenied(boolean accessDenied)
	{
		this.accessDenied = accessDenied;
	}

	public boolean getPendingApproval()
	{
		return pendingApproval;
	}

	public void setPendingApproval(boolean pendingApproval)
	{
		this.pendingApproval = pendingApproval;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	
}
