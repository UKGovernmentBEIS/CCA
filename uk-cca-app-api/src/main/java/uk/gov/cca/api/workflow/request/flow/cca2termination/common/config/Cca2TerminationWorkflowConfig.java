package uk.gov.cca.api.workflow.request.flow.cca2termination.common.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cca2-termination-workflow")
@Getter
@Setter
public class Cca2TerminationWorkflowConfig {

	private LocalDate triggerDate;
	private List<String> accountBusinessIds;

}
