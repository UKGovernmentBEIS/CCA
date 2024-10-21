package uk.gov.cca.api.workflow.request.flow;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestActionPayload;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class TestRequestActionPayload extends RequestActionPayload {
	
	private boolean prop1;
	
}
