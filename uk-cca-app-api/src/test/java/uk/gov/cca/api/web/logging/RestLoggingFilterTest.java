package uk.gov.cca.api.web.logging;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;
import uk.gov.cca.api.web.logging.MultiReadHttpServletRequestWrapper;
import uk.gov.cca.api.web.logging.RestLoggingFilter;
import uk.gov.cca.api.web.logging.RestLoggingService;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class RestLoggingFilterTest {
    private static final String REQUEST_URI = "/api/test";

    @InjectMocks
    private RestLoggingFilter restLoggingFilter;

    @Mock
    private RestLoggingService restLoggingService;

    private MockFilterChain filterChain;

    @BeforeEach
    public void setUp() {
        filterChain = Mockito.spy(new MockFilterChain());
    }

    @Test
    void doFilterInternalLog() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.POST.name(), REQUEST_URI);

        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(HttpStatus.ACCEPTED.value());

        restLoggingFilter.doFilterInternal(request, response, filterChain);
        Mockito.verify(restLoggingService, Mockito.times(1)).logRestRequestResponse(any(MultiReadHttpServletRequestWrapper.class),
                eq(REQUEST_URI), any(ContentCachingResponseWrapper.class), eq(HttpStatus.ACCEPTED), any(LocalDateTime.class));
        Mockito.verify(filterChain, Mockito.times(1)).doFilter(any(), any());
    }
}