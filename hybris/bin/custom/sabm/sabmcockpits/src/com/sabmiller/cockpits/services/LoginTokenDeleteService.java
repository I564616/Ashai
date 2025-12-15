package com.sabmiller.cockpits.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LoginTokenDeleteService {

    void deleteCookies(final HttpServletRequest request, final HttpServletResponse response, final String contextPath);
}
