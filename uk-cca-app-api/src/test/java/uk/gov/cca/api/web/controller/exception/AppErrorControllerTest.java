package uk.gov.cca.api.web.controller.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.ContentCachingResponseWrapper;

import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.restlogging.MultiReadHttpServletRequestWrapper;
import uk.gov.netz.api.restlogging.RestLoggingService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppErrorControllerTest {

    @InjectMocks
    private AppErrorController errorController;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private RestLoggingService restLoggingService;


    @Test
    void handleUnidentifiedError_error_status_code_401() {
        final ErrorCode expectedErrorCode = ErrorCode.UNAUTHORIZED;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(401);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorGet(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        verify(restLoggingService, times(1)).log(any(MultiReadHttpServletRequestWrapper.class),
                any(ContentCachingResponseWrapper.class), any(LocalDateTime.class), eq(null), eq(null),
                eq("/error"), eq(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void handleUnidentifiedError_error_status_code_404() {
        final ErrorCode expectedErrorCode = ErrorCode.RESOURCE_NOT_FOUND;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorPost(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        verify(restLoggingService, times(1)).log(any(MultiReadHttpServletRequestWrapper.class),
                any(ContentCachingResponseWrapper.class), any(LocalDateTime.class), eq(null), eq(null),
                eq("/error"), eq(HttpStatus.NOT_FOUND));
    }

    @Test
    void handleUnidentifiedError_error_status_code_405() {
        final ErrorCode expectedErrorCode = ErrorCode.METHOD_NOT_ALLOWED;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(405);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorPut(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        verify(restLoggingService, times(1)).log(any(MultiReadHttpServletRequestWrapper.class),
                any(ContentCachingResponseWrapper.class), any(LocalDateTime.class), eq(null), eq(null),
                eq("/error"), eq(HttpStatus.METHOD_NOT_ALLOWED));
    }

    @Test
    void handleUnidentifiedError_error_status_code_406() {
        final ErrorCode expectedErrorCode = ErrorCode.NOT_ACCEPTABLE;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(406);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorPatch(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        verify(restLoggingService, times(1)).log(any(MultiReadHttpServletRequestWrapper.class),
                any(ContentCachingResponseWrapper.class), any(LocalDateTime.class), eq(null), eq(null),
                eq("/error"), eq(HttpStatus.NOT_ACCEPTABLE));
    }

    @Test
    void handleUnidentifiedError_error_status_code_415() {
        final ErrorCode expectedErrorCode = ErrorCode.UNSUPPORTED_MEDIA_TYPE;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(415);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorDelete(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        verify(restLoggingService, times(1)).log(any(MultiReadHttpServletRequestWrapper.class),
                any(ContentCachingResponseWrapper.class), any(LocalDateTime.class), eq(null), eq(null),
                eq("/error"), eq(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    @Test
    void handleUnidentifiedError_error_status_code_500() {
        final ErrorCode expectedErrorCode = ErrorCode.INTERNAL_SERVER;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorGet(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        verify(restLoggingService, times(1)).log(any(MultiReadHttpServletRequestWrapper.class),
                any(ContentCachingResponseWrapper.class), any(LocalDateTime.class), eq(null), eq(null),
                eq("/error"), eq(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    void handleUnidentifiedError_error_status_code_null() {
        final ErrorCode expectedErrorCode = ErrorCode.RESOURCE_NOT_FOUND;
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/error");

        ResponseEntity<ErrorResponse> errorResponseEntity = errorController.handleUnidentifiedErrorPost(httpServletRequest, httpServletResponse);

        //assertions
        assertNotNull(errorResponseEntity);
        assertEquals(expectedErrorCode.getHttpStatus(), errorResponseEntity.getStatusCode());

        ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(expectedErrorCode.getCode(), errorResponse.getCode());
        assertEquals(expectedErrorCode.getMessage(), errorResponse.getMessage());
        assertThat(errorResponse.getData()).isEmpty();

        verify(restLoggingService, times(1)).log(any(MultiReadHttpServletRequestWrapper.class),
                any(ContentCachingResponseWrapper.class), any(LocalDateTime.class), eq(null), eq(null),
                eq("/error"), eq(null));
    }
}