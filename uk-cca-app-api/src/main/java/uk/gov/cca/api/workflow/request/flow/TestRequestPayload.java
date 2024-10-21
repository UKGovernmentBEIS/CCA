package uk.gov.cca.api.workflow.request.flow;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestPayload;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class TestRequestPayload extends RequestPayload {
	
	private boolean prop1;
	
}
