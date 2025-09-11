package uk.gov.cca.api.underlyingagreement.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDate;

@ConfigurationProperties(prefix = "underlying-agreement")
@Getter
@Setter
public class UnderlyingAgreementConfig {
    private LocalDate schemeParticipationFlagCutOffDate;
}
