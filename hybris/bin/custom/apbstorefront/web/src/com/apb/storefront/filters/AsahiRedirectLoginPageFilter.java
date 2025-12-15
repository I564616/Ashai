package com.apb.storefront.filters;

import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.filter.OncePerRequestFilter;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;

import static com.apb.storefront.constant.ApbStoreFrontContants.ASAHI_USER_TIMEOFFSET_COOKIE;


/**
 * Redirect to login page on session time out for Ajax request.
 */
public class AsahiRedirectLoginPageFilter extends OncePerRequestFilter
{


	public static final Logger LOG = LoggerFactory.getLogger(AsahiRedirectLoginPageFilter.class);
	/**
	 * ajax request
	 */
	public static final String AJAX_REQUEST_HEADER_NAME = "X-Requested-With";
	/**
	 * Generic redirect prefix
	 */
	public static final String REDIRECT_PREFIX = "redirect:";
	/**
	 * login page redirect
	 */
	public static final String REDIRECT_TO_LOGIN = "/login";

	private static final String ANONYMOUS = "anonymous";

	private SessionService sessionService;

	private UserService userService;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		final HttpSession session = request.getSession(false);// don't create if it doesn't exist

		if (getUserService().getCurrentUser().getUid().equalsIgnoreCase(ANONYMOUS)
				&& "XMLHttpRequest".equalsIgnoreCase(request.getHeader(AJAX_REQUEST_HEADER_NAME)))
		{

			//Setting the header and redirect using the header.
			response.setHeader("REQUIRES_AUTH", "1");
			filterChain.doFilter(request, response);

		}
		else if(checkIfUserDisabled())
		{
			if(getSessionService().hasCurrentSession()){
				getSessionService().closeCurrentSession();
				filterChain.doFilter(request, response);
			}
		}

		else
		{
			if(null == sessionService.getAttribute("asahiUserTimeOffset"))
			{
				setUserTimeInSession(request);
			}
			filterChain.doFilter(request, response);
		}

	}

	private boolean checkIfUserDisabled() {
		UserModel userModel = getUserService().getCurrentUser();
		if(null != userModel && userModel instanceof B2BCustomerModel){
			if(!(((B2BCustomerModel)userModel).getActive().booleanValue()) || null == ((B2BCustomerModel)userModel).getDefaultB2BUnit() || !((B2BCustomerModel)userModel).getDefaultB2BUnit().getActive())
			{
				return true;
			}
		}
		return false;
	}
	
	private void setUserTimeInSession(HttpServletRequest request)
	{
		if (request.getCookies() != null)
		{
			for (final Cookie cookie : request.getCookies())
			{
				if (null != cookie && null != cookie.getName() && ASAHI_USER_TIMEOFFSET_COOKIE.equals(cookie.getName()))
				{
					sessionService.setAttribute(ASAHI_USER_TIMEOFFSET_COOKIE, cookie.getValue());
					break;
				}
			}
		}
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
