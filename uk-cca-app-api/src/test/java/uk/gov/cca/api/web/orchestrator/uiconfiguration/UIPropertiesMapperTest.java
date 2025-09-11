package uk.gov.cca.api.web.orchestrator.uiconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.netz.api.configuration.domain.ui.UIConfigurationPropertiesDTO;

class UIPropertiesMapperTest {
    
    private final UIPropertiesMapper cut = Mappers.getMapper(UIPropertiesMapper.class);
    
    @Test
    void toUIPropertiesDTO() {
        LocalDate subsistenceFeesTriggerDate = LocalDate.now();
        LocalDate underlyingAgreementSchemeParticipationFlagCutOffDate = LocalDate.now();
        UIConfigurationPropertiesDTO uiConfigurationPropertiesDTO = UIConfigurationPropertiesDTO.builder()
                .analytics(Map.of(
                        "key1", "val1",
                        "key2", "val2"
                ))
                .features(Map.of(
                        "key3", Boolean.TRUE,
                        "key2", Boolean.FALSE
                ))
                .keycloakServerUrl("keycloakServerUrl")
                .build();
        
        UIPropertiesDTO result = cut.toUIPropertiesDTO(uiConfigurationPropertiesDTO, subsistenceFeesTriggerDate, underlyingAgreementSchemeParticipationFlagCutOffDate);
        
        assertThat(result).isEqualTo(UIPropertiesDTO.builder()
                .analytics(uiConfigurationPropertiesDTO.getAnalytics())
                .features(uiConfigurationPropertiesDTO.getFeatures())
                .keycloakServerUrl(uiConfigurationPropertiesDTO.getKeycloakServerUrl())
                .subsistenceFeesRunTriggerDate(subsistenceFeesTriggerDate)
                .underlyingAgreementSchemeParticipationFlagCutOffDate(underlyingAgreementSchemeParticipationFlagCutOffDate)
                .build());
    }
}
