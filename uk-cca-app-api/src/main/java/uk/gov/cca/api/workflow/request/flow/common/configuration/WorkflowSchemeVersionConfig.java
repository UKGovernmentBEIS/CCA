package uk.gov.cca.api.workflow.request.flow.common.configuration;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import uk.gov.cca.api.common.domain.SchemeVersion;

@ConfigurationProperties(prefix = "workflow-scheme-version")
@Getter
@Setter
public class WorkflowSchemeVersionConfig {

    @NotNull
    private SchemeVersion una;

    @NotNull
    private SchemeVersion unaVariation;
}
