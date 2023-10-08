package com.skypro.Marketplace.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A Spring filter for handling Cross-Origin Resource Sharing (CORS) headers.
 * This filter adds the "Access-Control-Allow-Credentials" header to responses
 * to enable credentials (e.g., cookies) to be sent during cross-origin requests.
 */
@Component
public class BasicAuthCorsFilter extends OncePerRequestFilter {

    /**
     * Adds the "Access-Control-Allow-Credentials" header to the HTTP response
     * and then proceeds with the filter chain.
     *
     * @param httpServletRequest  The HTTP servlet request.
     * @param httpServletResponse The HTTP servlet response to which CORS headers are added.
     * @param filterChain         The filter chain to continue processing the request.
     * @throws ServletException If a servlet-related exception occurs.
     * @throws IOException      If an I/O error occurs while processing the request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}