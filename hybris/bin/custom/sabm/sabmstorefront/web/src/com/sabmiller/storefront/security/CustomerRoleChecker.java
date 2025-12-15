/**
 * 
 */
package com.sabmiller.storefront.security;

import java.util.List;

/**
 * Check the customer role.
 * 
 * As per latest requirement there're 3 roles of B2BCustomer: "b2bassistantgroup" is able to "create and edit" user; b2bordercustomer is designed to place order; b2binvoicecustomer is used to pay the bill.
 * This CustomerRoleChecker is based on these roles to check whether a customer has specified role. Beside, it use each bit in binary to mark the role, so that if the business has extended with new roles then just set them into higher bits.  
 * 
 * @author ross.hengjun.zhu
 *
 */
public class CustomerRoleChecker
{
	/* Business corresponding group name */
	private static final String B2BASSISTANTGROUP = "b2bassistantgroup";
	private static final String B2BORDERCUSTOMER = "b2bordercustomer";
	private static final String B2BINVOICECUSTOMER = "b2binvoicecustomer";
	
	/* Binary value of role */
	public static final byte ROLE_ASSISTANT = 0x1;
	public static final byte ROLE_ORDER = 0x2;
	public static final byte ROLE_PAY = 0x4;
	
	private static byte organizeRoleIntoBytes(List<String> belongingGroupIds, byte role)
	{
		byte roles = 0;
		for (String groupId : belongingGroupIds)
		{
			switch (groupId)
			{
				case B2BASSISTANTGROUP:
					roles += ROLE_ASSISTANT;
					break;
				case B2BORDERCUSTOMER:
					roles += ROLE_ORDER;
					break;
				case B2BINVOICECUSTOMER:
					roles += ROLE_PAY;
					break;
			}
		}
		
		return roles;
	}
	
	/**
	 * Check if customer has the specified role 
	 * 
	 * @param belongingGroupIds The customer belonging group Ids.
	 * @param role The specified role in enumeration in this CustomerRoleChecker.
	 * @return Whether has the specified the role
	 */
	public static boolean hasRole(List<String> belongingGroupIds, byte role)
	{
		byte roles = organizeRoleIntoBytes(belongingGroupIds, role);
		
		return (roles & role) == role;
	}
	
	/**
	 * Check if customer only has the specified role 
	 * 
	 * @param belongingGroupIds The customer belonging group Ids.
	 * @param role The specified role in enumeration in this CustomerRoleChecker.
	 * @return Whether has the specified the role
	 */
	public static boolean hasOnlyRole(List<String> belongingGroupIds, byte role)
	{
		byte roles = organizeRoleIntoBytes(belongingGroupIds, role);
		
		return roles == role;
	}
}
