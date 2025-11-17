package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.handler;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class Cca3ExistingFacilitiesMigrationAccountProcessingActivationInitializer implements InitializeRequestTaskHandler {
	
	@Override
    public RequestTaskPayload initializePayload(Request request) {
        return Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload.builder()
        		.payloadType(CcaRequestTaskPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_PAYLOAD)
        		.build();
    }
	@Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION);
    }
}
