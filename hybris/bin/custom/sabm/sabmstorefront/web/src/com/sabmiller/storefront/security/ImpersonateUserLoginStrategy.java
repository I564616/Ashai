/**
 *
 */
package com.sabmiller.storefront.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 *
 * @author xiaowu.a.zhang
 * @date 07/06/2016
 *
 */
public interface ImpersonateUserLoginStrategy
{

	/**
	 * Impersonate a user. SABMC-1101
	 *
	 * @param username
	 *           the user'name need to be impersonated
	 * @param request
	 *           the request
	 * @param response
	 *           the response
	 * @return if impersonate success return true. otherwise return false
	 */
	boolean loginAsCustomer(String username,String b2bUnitId, HttpServletRequest request, HttpServletResponse response);

	/**
	 * @param request
	 * @param response
	 * @param uid 
	 * @return 
	 */
	boolean loginAsEmployee(HttpServletRequest request, HttpServletResponse response, String uid);

	/**
	 * @param username
	 * @param request
	 * @param response
	 * @return
	 */
	boolean loginAsBDECustomer(String username, HttpServletRequest request, HttpServletResponse response);

}
