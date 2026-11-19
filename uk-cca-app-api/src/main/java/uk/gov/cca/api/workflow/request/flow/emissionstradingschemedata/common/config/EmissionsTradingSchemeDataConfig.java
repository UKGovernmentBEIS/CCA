package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDate;

@ConfigurationProperties(prefix = "emissions-trading-scheme-data")
@Getter
@Setter
public class EmissionsTradingSchemeDataConfig {
    private LocalDate submissionDate;
}
