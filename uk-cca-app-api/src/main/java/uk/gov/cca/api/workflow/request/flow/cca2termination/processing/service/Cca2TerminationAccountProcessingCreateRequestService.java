package uk.gov.cca.api.workflow.request.flow.cca2termination.processing.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2termination.processing.domain.Cca2TerminationAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@Log4j2
@Service
@RequiredArgsConstructor
public class Cca2TerminationAccountProcessingCreateRequestService {

	private final RequestService requestService;
	private final FacilityDataQueryService facilityDataQueryService;
    private final RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional
    public void createRequest(Long accountId, String parentRequestId, String parentRequestBusinessKey) {
    	
    	Cca2TerminationRunRequestMetadata metadata = null;
    	Cca2TerminationAccountState accountState;
    	log.info("Trigger request for account with id {} and parent request id {}", accountId, parentRequestId);
    	try {
    		final Request parentRequest = requestService.findRequestById(parentRequestId);
            metadata = (Cca2TerminationRunRequestMetadata) parentRequest.getMetadata();
            accountState = metadata.getCca2TerminationAccountStates().get(accountId);

            // Update accountState with facilityIds
            accountState.setFacilityIds(facilityDataQueryService.getAllActiveFacilityIdsByAccount(accountId));
    	} catch (Exception e) {
    		log.error(e.getMessage(), e);
    		log.error(metadata);
            throw e;
    	}
        
        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.CCA2_TERMINATION_ACCOUNT_PROCESSING)
                .requestResources(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .requestMetadata(Cca2TerminationAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_TERMINATION_ACCOUNT_PROCESSING)
                        .parentRequestId(parentRequestId)
                        .accountBusinessId(accountState.getAccountBusinessId())
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountId,
                        CcaBpmnProcessConstants.CCA2_TERMINATION_RUN_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.CCA2_TERMINATION_ACCOUNT_STATE, accountState
                ))
                .build();

        startProcessRequestService.startProcess(requestParams);
    }
}
