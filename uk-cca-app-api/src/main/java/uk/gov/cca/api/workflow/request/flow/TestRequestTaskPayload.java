package uk.gov.cca.api.workflow.request.flow;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class TestRequestTaskPayload extends RequestTaskPayload {
	
	private boolean prop1;
	
}
