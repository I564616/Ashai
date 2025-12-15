package com.sabmiller.staff.storefront.filters;

import com.sabmiller.staff.singlesignon.constants.SabmsinglesignonConstants;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.CookieBasedLoginToken;
import de.hybris.platform.jalo.user.LoginToken;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by wei.yang.ng on 27/05/2016.
 */
public class SABMSSOFilter extends OncePerRequestFilter {
    private static final Logger LOG = Logger.getLogger(SABMSSOFilter.class);

    private GUIDCookieStrategy guidCookieStrategy;
    private UserDetailsService userDetailsService;
    private ConfigurationService configurationService;

    @Override protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {
        LOG.info("Logging in user using SAML cookie");
        final Cookie cookie = WebUtils.getCookie(request, getCookieName());

        if(null != cookie) {
            final LoginToken token = new CookieBasedLoginToken(cookie);
            loginUser(token.getUser().getUid(), request, response);
        }

        filterChain.doFilter(request, response);
    }

    private String getCookieName() {
        return configurationService.getConfiguration()
                .getString(SabmsinglesignonConstants.SSO_COOKIE_NAME, SabmsinglesignonConstants.SSO_DEFAULT_COOKIE_NAME);
    }

    private void loginUser(final String userName, final HttpServletRequest request, final HttpServletResponse response) {
        final UserDetails userDetails = getUserDetailsService().loadUserByUsername(userName);
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, userDetails.getAuthorities());
        token.setDetails(new WebAuthenticationDetails(request));
        final User user = UserManager.getInstance().getUserByLogin(userName);
        JaloSession.getCurrentSession().setUser(user);
        SecurityContextHolder.getContext().setAuthentication(token);
        getGuidCookieStrategy().setCookie(request, response);
        eraseSamlCookie(response);

    }

    private void eraseSamlCookie(final HttpServletResponse response) {

        final Cookie cookie = new Cookie(getCookieName(), "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    public GUIDCookieStrategy getGuidCookieStrategy() {
        return guidCookieStrategy;
    }

    public void setGuidCookieStrategy(GUIDCookieStrategy guidCookieStrategy) {
        this.guidCookieStrategy = guidCookieStrategy;
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
