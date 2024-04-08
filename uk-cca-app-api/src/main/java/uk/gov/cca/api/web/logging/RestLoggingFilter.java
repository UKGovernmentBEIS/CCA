package uk.gov.cca.api.web.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Filter used to log Rest API requests/responses.
 */
@Component
@RequiredArgsConstructor
public class RestLoggingFilter extends OncePerRequestFilter {
    private final RestLoggingService restLoggingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        LocalDateTime requestTimestamp = LocalDateTime.now();

        MultiReadHttpServletRequestWrapper wrappedRequest = new MultiReadHttpServletRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        }

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        String requestUri = ObjectUtils.isEmpty(request.getQueryString()) ?
                request.getRequestURI() :
                request.getRequestURI().concat(request.getQueryString());
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());

        restLoggingService.logRestRequestResponse(wrappedRequest, requestUri, wrappedResponse, httpStatus, requestTimestamp);
        wrappedResponse.copyBodyToResponse();
    }
}
