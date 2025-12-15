package com.sabmiller.staff.singlesignon.webutils;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.sabmiller.staff.singlesignon.constants.SabmsinglesignonConstants;


public class SABMContextRedirectStrategy extends DefaultRedirectStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(SABMContextRedirectStrategy.class);

	 private static final String backofficeContext = "/backoffice";

	 private static final String logoutUrl = "/logout.jsp";

	 private LogoutHandler sabmlogoutHandler;

    private String context;

	private String asahiContext;

    @Override public void sendRedirect(final HttpServletRequest request, final HttpServletResponse response, final String url) throws IOException {

   	 final String referer = request.getHeader("referer");
   	 LOGGER.info("SABM SSO Redirect URL :"+url+" and referer:"+referer);

		if (StringUtils.isEmpty(getContext()) || StringUtils.isEmpty(getAsahiContext()) || referer.contains("backoffice"))
		{

   		 if(referer.contains("backoffice")) {
   			  String redirectUrl = this.calculateRedirectUrl(backofficeContext, logoutUrl);
   	        redirectUrl = response.encodeRedirectURL(redirectUrl);
   	        if(LOGGER.isDebugEnabled()) {
   	            LOGGER.debug("Redirecting to '" + redirectUrl + "'");
   	        }
   	      //Logout From IDP
   	        processLogoutFromIDP(request,response);
   	        response.sendRedirect(redirectUrl);
   	        return;
   		 }
   		 else {
           super.sendRedirect(request, response, url);
            return;
   		 }
        }
		String context = getContext();
		if (StringUtils.isNotBlank(referer) && referer.contains(SabmsinglesignonConstants.ASAHI_STAFF_PORTAL))
		{
			context = getAsahiContext();
		}
		//TO-DO condition to decide context for ALB
		String redirectUrl = this.calculateRedirectUrl(context, url);
        redirectUrl = response.encodeRedirectURL(redirectUrl);
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Redirecting to '" + redirectUrl + "'");
        }
        response.sendRedirect(redirectUrl);
    }

    private void processLogoutFromIDP(final HttpServletRequest request,final HttpServletResponse response) {
   	 try {
   		 final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
   		 LOGGER.info("SABM SSO Auth IS Null:"+auth);
   		 sabmlogoutHandler.logout(request, response, auth);
   		 LOGGER.info("SABM SSO Session logout success!!");
   	 }
		 catch (final Exception e)
		 {
   		 LOGGER.error("Error initializing global logout", e);
      }
    }

    protected String getContext() {
        return context;
    }

    public void setContext(final String context) {
        this.context = context;
    }



	/**
	 * @param sabmlogoutHandler the sabmlogoutHandler to set
	 */
	@Autowired
	public void setSabmlogoutHandler(final LogoutHandler sabmlogoutHandler)
	{
		this.sabmlogoutHandler = sabmlogoutHandler;
	}

	/**
	 * @return the asahiContext
	 */
	public String getAsahiContext()
	{
		return asahiContext;
	}

	/**
	 * @param asahiContext
	 *           the asahiContext to set
	 */
	public void setAsahiContext(final String asahiContext)
	{
		this.asahiContext = asahiContext;
	}

}
