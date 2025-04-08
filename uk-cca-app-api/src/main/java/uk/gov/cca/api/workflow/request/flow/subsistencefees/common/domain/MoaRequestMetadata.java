package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain;

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
public class MoaRequestMetadata extends RequestMetadata {

	private String parentRequestId;
	private String transactionId;
}
