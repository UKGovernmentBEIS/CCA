package uk.gov.cca.api.web.controller.uiconfiguration;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
@Data
@Builder
public class UIPropertiesDTO {
    private Map<String, Boolean> features;
    private Map<String, String> analytics;
    private String keycloakServerUrl;
}
