package uk.gov.cca.api.workflow.request.flow.cca2termination.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cca2TerminationAccountProcessingRequestMetadata extends RequestMetadata {

	private String accountBusinessId;
	private String parentRequestId;
}
