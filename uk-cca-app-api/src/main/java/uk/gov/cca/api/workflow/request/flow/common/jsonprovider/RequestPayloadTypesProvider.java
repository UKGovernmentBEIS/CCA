package uk.gov.cca.api.workflow.request.flow.common.jsonprovider;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.ADMIN_TERMINATION_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.TARGET_UNIT_ACCOUNT_CREATION_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.UNDERLYING_AGREEMENT_REQUEST_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType.UNDERLYING_AGREEMENT_VARIATION_REQUEST_PAYLOAD;

@Component
public class RequestPayloadTypesProvider implements JsonSubTypesProvider {

	@Override
	public List<NamedType> getTypes() {
		return List.of(
				new NamedType(TargetUnitAccountCreationRequestPayload.class, TARGET_UNIT_ACCOUNT_CREATION_REQUEST_PAYLOAD),
				new NamedType(UnderlyingAgreementRequestPayload.class, UNDERLYING_AGREEMENT_REQUEST_PAYLOAD),
				new NamedType(UnderlyingAgreementVariationRequestPayload.class, UNDERLYING_AGREEMENT_VARIATION_REQUEST_PAYLOAD),
				new NamedType(AdminTerminationRequestPayload.class, ADMIN_TERMINATION_REQUEST_PAYLOAD)
				);
	}

}
