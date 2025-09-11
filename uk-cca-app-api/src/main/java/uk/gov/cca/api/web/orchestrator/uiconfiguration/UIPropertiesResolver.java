package uk.gov.cca.api.web.orchestrator.uiconfiguration;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.subsistencefees.config.SubsistenceFeesConfig;
import uk.gov.cca.api.underlyingagreement.config.UnderlyingAgreementConfig;
import uk.gov.netz.api.alert.service.NotificationAlertService;
import uk.gov.netz.api.configuration.service.ui.UIConfigurationPropertiesResolver;

@Service
@RequiredArgsConstructor
public class UIPropertiesResolver {

	private final NotificationAlertService notificationAlertService;
	private final SubsistenceFeesConfig subsistenceFeesConfig;
	private final UnderlyingAgreementConfig underlyingAgreementConfig;
	private final UIConfigurationPropertiesResolver uiConfigurationPropertiesResolver;
	private static final UIPropertiesMapper uiPropertiesMapper = Mappers.getMapper(UIPropertiesMapper.class);

	public UIPropertiesDTO resolve() {
		return uiPropertiesMapper
				.toUIPropertiesDTO(
						uiConfigurationPropertiesResolver.resolve(),
						subsistenceFeesConfig.getTriggerDate(),
						underlyingAgreementConfig.getSchemeParticipationFlagCutOffDate())
				.withNotificationAlerts(notificationAlertService.getNotificationAlerts());
	}

}
