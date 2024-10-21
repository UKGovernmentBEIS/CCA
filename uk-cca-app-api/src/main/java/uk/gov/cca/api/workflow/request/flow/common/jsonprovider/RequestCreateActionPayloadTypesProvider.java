package uk.gov.cca.api.workflow.request.flow.common.jsonprovider;

import java.util.List;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationSubmitApplicationCreateActionPayload;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestCreateActionPayloadType.TARGET_UNIT_ACCOUNT_CREATION_SUBMIT_PAYLOAD;

@Component
public class RequestCreateActionPayloadTypesProvider implements JsonSubTypesProvider {

	@Override
	public List<NamedType> getTypes() {
		return List.of(
				new NamedType(TargetUnitAccountCreationSubmitApplicationCreateActionPayload.class, TARGET_UNIT_ACCOUNT_CREATION_SUBMIT_PAYLOAD)
				//ADD MORE
				);
	}

}
