package com.sabmiller.cockpits.services.impl;

import com.sabmiller.cockpits.services.LoginTokenDeleteService;
import de.hybris.platform.util.Config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginTokenDeleteServiceImpl implements LoginTokenDeleteService {

    private static final String LOGIN_TOKEN_NAME_KEY = "login.token.name";
    private static final String LOGIN_TOKEN_DOMAIN = "login.token.domain";
    private static final String LOGIN_TOKEN_SECURE = "login.token.secure";
    private static final String J_SESSION_ID = "JSESSIONID";
    private static final String SSO_COOKIE = "sso_cookie__";
    private static final String DEFAULT_PATH = "/";

    @Override public void deleteCookies(HttpServletRequest request, HttpServletResponse response, String path) {
        deleteCookiesInternal(request, response, path);
    }

    /**
     * This method is a copy from @de.hybris.platform.cockpit.services.login.impl.{@link de.hybris.platform.cockpit.services.login.impl.LoginServiceImpl#deleteLoginTokenCookie(HttpServletRequest, HttpServletResponse)}
     * With the a change of:
     * if the cookie is from same as the login.token.name property, the path will be set to "/". aside from that. everything else remains the same logic wise
     * @param request
     * @param response
     * @param path
     */
    protected void deleteCookiesInternal(HttpServletRequest request, HttpServletResponse response, String path) {
        if(request != null && response != null) {
            Cookie[] cookies = request.getCookies();
            if(cookies != null) {
                for (int i = 0; i < cookies.length; ++i) {
                    if(cookies[i] != null && (cookies[i].getName().equals(J_SESSION_ID) || cookies[i].getName()
                            .equals(Config.getParameter(LOGIN_TOKEN_NAME_KEY)) || cookies[i].getName().equals(SSO_COOKIE))) {
                        cookies[i].setMaxAge(0);
                        if(cookies[i].getName().equals(SSO_COOKIE) || cookies[i].getName()
                                .equals(Config.getParameter(LOGIN_TOKEN_NAME_KEY))) {
                            cookies[i].setPath(DEFAULT_PATH);
                        } else {
                            cookies[i].setPath(path);
                            if(Config.getParameter(LOGIN_TOKEN_DOMAIN) != null
                                    && Config.getParameter(LOGIN_TOKEN_DOMAIN).trim().length() > 0) {
                                cookies[i].setDomain(Config.getParameter(LOGIN_TOKEN_DOMAIN));
                            }
                        }

                        cookies[i].setSecure(Boolean.parseBoolean(Config.getParameter(LOGIN_TOKEN_SECURE)));
                        response.addCookie(cookies[i]);
                    }
                }
            }

        }
    }
}
