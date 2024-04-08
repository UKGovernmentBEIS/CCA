package uk.gov.cca.api.web.logging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static uk.gov.cca.api.web.logging.CorrelationIdHeaderWriter.CORRELATION_ID_HEADER;

@Log4j2
@Service
public class RestLoggingService {
    private final Level logLevel;
    private final RestLoggingProperties restLoggingProperties;
    private final CorrelationIdHeaderWriter correlationIdHeaderWriter;
    private final ObjectMapper objectMapper;

    public RestLoggingService(RestLoggingProperties restLoggingProperties, CorrelationIdHeaderWriter correlationIdHeaderWriter, ObjectMapper objectMapper) {
        this.correlationIdHeaderWriter = correlationIdHeaderWriter;
        this.restLoggingProperties = restLoggingProperties;
        this.objectMapper = objectMapper;
        logLevel = Level.valueOf(restLoggingProperties.getLevel().name());
    }

    public void logRestRequestResponse(MultiReadHttpServletRequestWrapper request,
                                       String requestUri,
                                       ContentCachingResponseWrapper response,
                                       HttpStatus httpStatus,
                                       LocalDateTime requestTimestamp) {
        correlationIdHeaderWriter.writeHeaders(request, response);
        String correlationId = response.getHeader(CORRELATION_ID_HEADER);

        RestLoggingEntry requestLog;
        RestLoggingEntry responseLog;

        if (httpStatus.isError()) {
            requestLog = getRequestRestLoggingEntry(request, requestUri, correlationId, requestTimestamp);
            responseLog = getResponseRestLoggingEntry(response, httpStatus, correlationId, request, requestUri, requestTimestamp);
            log.log(Level.ERROR, requestLog);
            log.log(Level.ERROR, responseLog);
        } else if (log.isEnabled(logLevel) && !isUriExcluded(requestUri)) {
            requestLog = getRequestRestLoggingEntry(request, requestUri, correlationId, requestTimestamp);
            responseLog = getResponseRestLoggingEntry(response, httpStatus, correlationId, request, requestUri, requestTimestamp);
            log.log(logLevel, requestLog);
            log.log(logLevel, responseLog);
        }
    }

    private boolean isUriExcluded(String uri) {
        List<Pattern> excludedUriPatterns = this.restLoggingProperties.getExcludedUriPatterns().stream()
                .map(Pattern::compile)
                .toList();

        for (Pattern pattern : excludedUriPatterns) {
            if (pattern.matcher(uri).find()) {
                return true;
            }
        }
        return false;
    }

    private RestLoggingEntry getRequestRestLoggingEntry(HttpServletRequest request, String requestUri, String correlationId, LocalDateTime requestTimestamp) {
        String user = request.getRemoteUser();

        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());

        Map<String, Object> payload = getRequestPayloadAsMap(request);

        Map<String, String> headers = new HashMap<>();
        Collections.list(request.getHeaderNames()).stream()
                .filter(header -> !header.equalsIgnoreCase(HttpHeaders.AUTHORIZATION))
                .forEach(header -> headers.put(header, request.getHeader(header)));


        return RestLoggingEntry.builder()
                .type(RestLoggingEntry.RestLoggingEntryType.REQUEST)
                .headers(headers)
                .payload(payload)
                .uri(requestUri)
                .userId(user)
                .httpMethod(httpMethod.name())
                .correlationId(correlationId)
                .timestamp(requestTimestamp)
                .build();
    }

    private RestLoggingEntry getResponseRestLoggingEntry(ContentCachingResponseWrapper response,
                                                         HttpStatus httpStatus,
                                                         String correlationId,
                                                         MultiReadHttpServletRequestWrapper request,
                                                         String requestUri,
                                                         LocalDateTime requestTimestamp) {
        String user = request.getRemoteUser();

        Map<String, Object> payload = getResponsePayloadAsMap(response);

        Map<String, String> headers = new HashMap<>();
        response.getHeaderNames()
                .forEach(header -> headers.put(header, response.getHeader(header)));

        return RestLoggingEntry.builder()
                .type(RestLoggingEntry.RestLoggingEntryType.RESPONSE)
                .headers(headers)
                .payload(payload)
                .uri(requestUri)
                .userId(user)
                .httpStatus(httpStatus.value())
                .correlationId(correlationId)
                .responseTimeInMillis(ChronoUnit.MILLIS.between(requestTimestamp, LocalDateTime.now()))
                .build();
    }

    private Map<String, Object> getRequestPayloadAsMap(HttpServletRequest request) {
        try {
            if (request.getContentType() != null && request.getContentType().contains(MULTIPART_FORM_DATA_VALUE)) {
                Optional<Part> requestJsonPart = request.getParts().stream()
                        .filter(part -> part.getContentType().equals(APPLICATION_JSON_VALUE))
                        .findFirst();

                if (requestJsonPart.isPresent()) {
                    Part part = requestJsonPart.get();
                    if (part.getSize() > 0) {
                        return getPayloadAsMap(part.getInputStream().readAllBytes());
                    }
                }

            } else {
                return getPayloadAsMap(request.getInputStream().readAllBytes());
            }
        } catch (IOException | ServletException ex) {
            return Map.of("body", "[unknownContent]");
        }
        return Map.of("body", "[unknownContent]");
    }

    private Map<String, Object> getResponsePayloadAsMap(ContentCachingResponseWrapper response) {
        if (response.getHeader(CONTENT_DISPOSITION) != null) {
            return Map.of("body", "[fileContent]");
        }
        return getPayloadAsMap(response.getContentAsByteArray());
    }

    private Map<String, Object> getPayloadAsMap(byte[] buffer) {
        if (buffer != null && buffer.length > 0) {
            try {
                return objectMapper.readValue(buffer, new TypeReference<>() {
                });
            } catch (IOException ex) {
                return Map.of();
            }
        }
        return Map.of();
    }
}
