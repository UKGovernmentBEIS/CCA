package uk.gov.cca.api.workflow.request.flow;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestCreateActionPayload;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class TestRequestCreateActionPayload extends RequestCreateActionPayload {
	
	private boolean prop1;
	
}
