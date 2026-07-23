package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDate;
import java.time.Year;

@ConfigurationProperties(prefix = "performance-account-template")
@Getter
@Setter
public class PerformanceAccountTemplateConfig {

    private Year targetYear;
    private LocalDate submissionDate;
}
