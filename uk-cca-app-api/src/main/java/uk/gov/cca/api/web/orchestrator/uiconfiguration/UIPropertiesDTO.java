package uk.gov.cca.api.web.orchestrator.uiconfiguration;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.validation.annotation.Validated;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import uk.gov.netz.api.alert.dto.NotificationAlertDTO;

@Validated
@Data
@Builder
public class UIPropertiesDTO {
    private Map<String, Boolean> features;
    private Map<String, String> analytics;
    private String keycloakServerUrl;
    private LocalDate subsistenceFeesRunTriggerDate;
    private LocalDate underlyingAgreementSchemeParticipationFlagCutOffDate;
    
    @With
    private List<NotificationAlertDTO> notificationAlerts;
}
