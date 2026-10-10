package com.quizdesk.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

/**
 * Forces UTF-8 on every request and response.
 *
 * <p>Declared in web.xml (not with @WebFilter) so its order relative to the auth filter is
 * guaranteed: encoding must run first.</p>
 */
public class CharacterEncodingFilter implements Filter {

    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) {
        String configured = filterConfig.getInitParameter("encoding");
        if (configured != null && !configured.isBlank()) { this.encoding = configured; }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request.getCharacterEncoding() == null) { request.setCharacterEncoding(encoding); }
        response.setCharacterEncoding(encoding);
        chain.doFilter(request, response);
    }
}
