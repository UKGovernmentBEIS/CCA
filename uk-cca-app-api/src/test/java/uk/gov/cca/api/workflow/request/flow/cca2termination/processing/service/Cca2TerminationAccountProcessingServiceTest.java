package uk.gov.cca.api.workflow.request.flow.cca2termination.processing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.config.Cca2TerminationConfig;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeService;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2termination.processing.domain.Cca2TerminationAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.TerminateAccountAndOpenWorkflowsService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationAccountProcessingServiceTest {
	
	@InjectMocks
    private Cca2TerminationAccountProcessingService cca2TerminationAccountProcessingService;

    @Mock
    private RequestService requestService;

    @Mock
    private TerminateAccountAndOpenWorkflowsService terminateAccountAndOpenWorkflowsService;
    
    @Mock
	private Cca2TerminationConfig cca2TerminationConfig;
    
    @Mock
    private FacilityDataQueryService facilityDataQueryService;
    
    @Mock
    private FacilityDataUpdateService facilityDataUpdateService;
    
    @Mock
    private UnderlyingAgreementSchemeService underlyingAgreementSchemeService;

    @Test
    void processAccountWithCca2AndOtherFacilities() throws Exception {
        final Long accountId = 1L;
        final String requestId = "requestId";
        final FacilityData facility1 = FacilityData.builder().id(1L).participatingSchemeVersions(Set.of(SchemeVersion.CCA_2)).build();
        final FacilityData facility2 = FacilityData.builder().id(2L).participatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3)).build();
        
        final Cca2TerminationAccountState accountState = Cca2TerminationAccountState.builder()
        		.accountId(accountId)
        		.facilityIds(List.of(1L, 2L))
        		.build();
        final Request request = Request.builder()
                .id(requestId)
                .metadata(Cca2TerminationRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_TERMINATION_RUN)
                        .cca2TerminationAccountStates(Map.of(accountId, accountState))
                        .build())
                .build();
        
        RequestResource accountResource = RequestResource.builder()
				.resourceType(ResourceType.ACCOUNT)
				.resourceId("1")
				.request(request)
				.build();

        request.getRequestResources().add(accountResource);
        
        final Cca2TerminationAccountProcessingSubmittedRequestActionPayload actionPayload = Cca2TerminationAccountProcessingSubmittedRequestActionPayload.builder()
				.payloadType(CcaRequestActionPayloadType.CCA2_TERMINATION_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)
				.excludedFacilities(List.of(FacilityBaseInfoDTO.builder().id(1L).build()))
				.build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(cca2TerminationConfig.getTerminationDate()).thenReturn(LocalDate.now());
        when(facilityDataQueryService.getFacilityDataByIds(List.of(1L, 2L))).thenReturn(List.of(facility1, facility2));

        // Invoke
        cca2TerminationAccountProcessingService.doProcess(requestId, accountState);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(facilityDataQueryService, times(1)).getFacilityDataByIds(List.of(1L, 2L));
        verify(facilityDataUpdateService, times(1)).terminateFacilities(
        		any(LocalDateTime.class), eq(List.of(facility1)));
        verify(underlyingAgreementSchemeService, times(1)).terminateUnaForSchemeVersion(
        		eq(1L), eq(SchemeVersion.CCA_2), any(LocalDateTime.class));
        verify(requestService, times(1)).addActionToRequest(
        		request, actionPayload, CcaRequestActionType.CCA2_TERMINATION_ACCOUNT_PROCESSING_SUBMITTED, null);
        verifyNoInteractions(terminateAccountAndOpenWorkflowsService);
    }
    
    @Test
    void processAccountWithOnlyCca2Facilities() throws Exception {
        final Long accountId = 1L;
        final String requestId = "requestId";
        final FacilityData facility1 = FacilityData.builder().id(1L).participatingSchemeVersions(Set.of(SchemeVersion.CCA_2)).build();
        
        final Cca2TerminationAccountState accountState = Cca2TerminationAccountState.builder()
        		.accountId(accountId)
        		.facilityIds(List.of(1L))
        		.build();
        final Request request = Request.builder()
                .id(requestId)
                .metadata(Cca2TerminationRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_TERMINATION_RUN)
                        .cca2TerminationAccountStates(Map.of(accountId, accountState))
                        .build())
                .build();
        
        final Cca2TerminationAccountProcessingSubmittedRequestActionPayload actionPayload = Cca2TerminationAccountProcessingSubmittedRequestActionPayload.builder()
				.payloadType(CcaRequestActionPayloadType.CCA2_TERMINATION_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)
				.excludedFacilities(List.of(FacilityBaseInfoDTO.builder().id(1L).build()))
				.build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(cca2TerminationConfig.getTerminationDate()).thenReturn(LocalDate.now());
        when(facilityDataQueryService.getFacilityDataByIds(List.of(1L))).thenReturn(List.of(facility1));

        // Invoke
        cca2TerminationAccountProcessingService.doProcess(requestId, accountState);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(facilityDataQueryService, times(1)).getFacilityDataByIds(List.of(1L));
        verify(terminateAccountAndOpenWorkflowsService, times(1)).terminateAccountAndOpenWorkflows(
        		eq(request), any(LocalDateTime.class), isNull());
        verify(requestService, times(1)).addActionToRequest(
        		request, actionPayload, CcaRequestActionType.CCA2_TERMINATION_ACCOUNT_PROCESSING_SUBMITTED, null);
        verifyNoInteractions(facilityDataUpdateService, underlyingAgreementSchemeService);
    }
    
    @Test
    void doProcess_no_facilities() {
        final Long accountId = 1L;
        final String requestId = "requestId";
        
        final Cca2TerminationAccountState accountState = Cca2TerminationAccountState.builder()
        		.accountId(accountId)
        		.facilityIds(List.of())
        		.build();

        // Invoke
        BpmnExecutionException ex = Assertions.assertThrows(BpmnExecutionException.class, () ->
        cca2TerminationAccountProcessingService.doProcess(requestId, accountState));

		// Verify
		assertThat(ex.getErrors().get(0)).isEqualTo("No facilities found for account");
		verifyNoInteractions(requestService, facilityDataQueryService, 
				terminateAccountAndOpenWorkflowsService, facilityDataUpdateService, underlyingAgreementSchemeService);
    }
}
