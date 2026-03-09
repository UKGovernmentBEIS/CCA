package uk.gov.cca.api.workflow.request.flow.cca2termination.processing.service;

import java.time.LocalDateTime;
import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.common.config.Cca2TerminationConfig;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.facility.transform.FacilityDetailsMapper;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeService;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2termination.processing.domain.Cca2TerminationAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.TerminateAccountAndOpenWorkflowsService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Log4j2
@Service
@RequiredArgsConstructor
public class Cca2TerminationAccountProcessingService {

	private final RequestService requestService;
	private final FacilityDataQueryService facilityDataQueryService;
	private final FacilityDataUpdateService facilityDataUpdateService;
	private final UnderlyingAgreementSchemeService underlyingAgreementSchemeService;
	private final TerminateAccountAndOpenWorkflowsService terminateAccountAndOpenWorkflowsService;
	private final Cca2TerminationConfig cca2TerminationConfig;
	private static final FacilityDetailsMapper FACILITY_DETAILS_MAPPER = Mappers.getMapper(FacilityDetailsMapper.class);
	
	@Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
    public void doProcess(String requestId, Cca2TerminationAccountState accountState) throws BpmnExecutionException {
        try {
        	
            if(accountState.getFacilityIds().isEmpty()) {
                throw new BusinessException(CcaErrorCode.NO_FACILITIES_FOR_ACCOUNT);
            }

            final Request request = requestService.findRequestById(requestId);
            final LocalDateTime terminationDate = cca2TerminationConfig.getTerminationDate().atStartOfDay();

            // Get CCA2-only facilities
            List<FacilityData> cca2OnlyFacilities = facilityDataQueryService
            		.getFacilityDataByIds(accountState.getFacilityIds()).stream()
            		.filter(facility -> !facility.getParticipatingSchemeVersions().contains(SchemeVersion.CCA_3))
            		.toList();
            
            // Process
            if (cca2OnlyFacilities.size() == accountState.getFacilityIds().size()) {
            	processAccountWithOnlyCca2Facilities(request, cca2OnlyFacilities, terminationDate);
            }
            else {
            	processAccountWithCca2AndOtherFacilities(request, cca2OnlyFacilities, terminationDate);
            }
            
            // Set number of excluded facilities in account state
            accountState.setFacilitiesExcluded(Long.valueOf(cca2OnlyFacilities.size()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BpmnExecutionException(e.getMessage(), List.of(e.getMessage()));
        }
    }

	private void processAccountWithOnlyCca2Facilities(
			Request request, List<FacilityData> cca2OnlyFacilities, LocalDateTime terminationDate) {
		// Terminate account and open workflows
		terminateAccountAndOpenWorkflowsService.terminateAccountAndOpenWorkflows(request, terminationDate, null);
		
		// Create request action
        createSubmittedRequestAction(request, cca2OnlyFacilities);		
	}

	private void processAccountWithCca2AndOtherFacilities(
			Request request, List<FacilityData> cca2OnlyFacilities, LocalDateTime terminationDate) {		
		// Terminate CCA2-only facilities
		facilityDataUpdateService.terminateFacilities(terminationDate, cca2OnlyFacilities);
		
		// Terminate CCA2 UNA document and remove CCA2 facilities from BO
		underlyingAgreementSchemeService.terminateUnaForSchemeVersion(request.getAccountId(), SchemeVersion.CCA_2, terminationDate);
		
		// Create request action
        createSubmittedRequestAction(request, cca2OnlyFacilities);	
		
	}

	private void createSubmittedRequestAction(Request request, List<FacilityData> cca2OnlyFacilities) {
		Cca2TerminationAccountProcessingSubmittedRequestActionPayload actionPayload = 
				Cca2TerminationAccountProcessingSubmittedRequestActionPayload.builder()
				.payloadType(CcaRequestActionPayloadType.CCA2_TERMINATION_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)
				.excludedFacilities(cca2OnlyFacilities.stream()
						.map(FACILITY_DETAILS_MAPPER::toFacilityBaseInfo)
						.toList())
				.build();

        requestService.addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.CCA2_TERMINATION_ACCOUNT_PROCESSING_SUBMITTED,
                null);
		
	}
}
