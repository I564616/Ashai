/**
 *
 */
package com.apb.storefront.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 *
 *
 */
public interface ImpersonateUserLoginStrategy
{


	/**
	 * @param username
	 * @param request
	 * @param response
	 * @return
	 */
	boolean loginAsBDECustomer(String username, HttpServletRequest request, HttpServletResponse response);

}
