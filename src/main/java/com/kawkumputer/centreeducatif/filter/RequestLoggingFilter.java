package com.kawkumputer.centreeducatif.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        try {
            logRequest(requestWrapper);
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            logResponse(responseWrapper, System.currentTimeMillis() - startTime);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String queryString = request.getQueryString();
        String path = queryString != null ? request.getRequestURI() + "?" + queryString : request.getRequestURI();
        
        log.info("=========================== Request ===========================");
        log.info("Method: {}", request.getMethod());
        log.info("Path: {}", path);
        log.info("Headers: {}", Collections.list(request.getHeaderNames())
            .stream()
            .map(headerName -> headerName + ": " + Collections.list(request.getHeaders(headerName)))
            .reduce((header1, header2) -> header1 + ", " + header2)
            .orElse(""));
        log.info("===========================================================");
    }

    private void logResponse(ContentCachingResponseWrapper response, long timeElapsed) {
        log.info("=========================== Response ==========================");
        log.info("Status: {}", response.getStatus());
        log.info("Time elapsed: {} ms", timeElapsed);
        log.info("============================================================");
    }
}
