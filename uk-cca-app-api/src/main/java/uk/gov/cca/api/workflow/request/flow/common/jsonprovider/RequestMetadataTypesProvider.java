package uk.gov.cca.api.workflow.request.flow.common.jsonprovider;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestMetadata;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.TARGET_UNIT_ACCOUNT_CREATION;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType.UNDERLYING_AGREEMENT;

@Component
public class RequestMetadataTypesProvider implements JsonSubTypesProvider {

	@Override
	public List<NamedType> getTypes() {
		return List.of(
				new NamedType(TargetUnitAccountCreationRequestPayload.class, TARGET_UNIT_ACCOUNT_CREATION),
				new NamedType(UnderlyingAgreementRequestMetadata.class, UNDERLYING_AGREEMENT)
				//ADD MORE
				);
	}

}
