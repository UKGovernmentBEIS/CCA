package uk.gov.cca.api.web.logging;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Facilitator class for logging rest api request/response.
 */
@Data
@Builder
public class RestLoggingEntry {
    private RestLoggingEntryType type;
    @Builder.Default
    private final Map<String, String> headers = new ConcurrentHashMap<>();
    @Builder.Default
    private Map<String, Object> payload = new HashMap<>();
    private String uri;
    private String userId;
    private String httpMethod;
    private int httpStatus;
    private String correlationId;
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private LocalDateTime timestamp = LocalDateTime.now();

    long responseTimeInMillis;

    public enum RestLoggingEntryType {
        REQUEST,
        RESPONSE
    }
}
