package uk.gov.cca.api.web.orchestrator.uiconfiguration;

import java.time.LocalDate;

import org.mapstruct.Mapper;

import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.configuration.domain.ui.UIConfigurationPropertiesDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UIPropertiesMapper {

	UIPropertiesDTO toUIPropertiesDTO(UIConfigurationPropertiesDTO uiConfigurationProperties, LocalDate subsistenceFeesRunTriggerDate, LocalDate underlyingAgreementSchemeParticipationFlagCutOffDate);
	
}
