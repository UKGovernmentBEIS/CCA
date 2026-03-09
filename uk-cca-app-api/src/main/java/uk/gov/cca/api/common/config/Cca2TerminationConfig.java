package uk.gov.cca.api.common.config;

import java.time.LocalDate;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cca2-termination")
@Getter
@Setter
public class Cca2TerminationConfig {

	private LocalDate terminationDate;
}
