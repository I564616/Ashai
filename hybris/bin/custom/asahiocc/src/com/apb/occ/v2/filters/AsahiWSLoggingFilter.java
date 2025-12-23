package com.apb.occ.v2.filters;

import com.apb.core.service.config.AsahiConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;

public class AsahiWSLoggingFilter extends OncePerRequestFilter implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsahiWSLoggingFilter.class);
    private static final String NEW_LINE = "\n";

    @Resource
    private AsahiConfigurationService asahiConfigurationService;

    @Override
    public final void afterPropertiesSet()
    {
        if (this.asahiConfigurationService == null) {
            throw new IllegalArgumentException("Property 'asahiConfigurationService' must be set");
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if(asahiConfigurationService.getBoolean("log.ws.incoming.message", false)) {
            AsahiRequestWrapper requestWrapper = new AsahiRequestWrapper(httpServletRequest);
            try {
                final BufferedReader br = requestWrapper.getReader();
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line).append(NEW_LINE);
                }
                LOGGER.info(String.format("Logging payload message for URI %s, payload - %s", httpServletRequest.getRequestURI(), sb.toString()));
            } catch (IOException ex) {
                LOGGER.error("Error in message -> ", ex);
            }
            filterChain.doFilter(requestWrapper, httpServletResponse);
        }
        else
            filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
