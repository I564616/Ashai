package com.sabmiller.cockpits.cmscockpit.services.impl;

import com.sabmiller.cockpits.services.LoginTokenDeleteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginServiceImpl extends de.hybris.platform.cmscockpit.services.login.impl.LoginServiceImpl {

    private LoginTokenDeleteService loginTokenDeleteService;

    @Override public void deleteLoginTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        getLoginTokenDeleteService().deleteCookies(request, response, getContextPath(request));
    }

    protected LoginTokenDeleteService getLoginTokenDeleteService() {
        return loginTokenDeleteService;
    }

    public void setLoginTokenDeleteService(LoginTokenDeleteService loginTokenDeleteService) {
        this.loginTokenDeleteService = loginTokenDeleteService;
    }
}
