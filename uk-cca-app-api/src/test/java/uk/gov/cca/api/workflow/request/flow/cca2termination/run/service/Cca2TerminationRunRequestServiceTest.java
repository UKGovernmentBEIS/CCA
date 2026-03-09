package uk.gov.cca.api.workflow.request.flow.cca2termination.run.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskType;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationRunRequestServiceTest {

	@InjectMocks
    private Cca2TerminationRunRequestService cca2TerminationRunRequestService;

    @Mock
    private RequestService requestService;
    
    @Mock
    private RequestQueryService requestQueryService;
	
    @Mock
    private WorkflowService workflowService;
    
    private static final String TERMINATE_REASON = "Workflow terminated by the CCA2 end date workflow, because it contains CCA2-only facilities.";
	
    @Test
    void terminateVariationRequests() {
    	String requestId1 = "1";
    	String requestId2 = "2";
    	String requestId3 = "3";
		final Request requestContainsCca2Only = Request.builder()
				.id(requestId1)
				.processInstanceId("processInstanceId1")
				.type(RequestType.builder().code(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION).build())
				.status(RequestStatuses.IN_PROGRESS)
				.payload(UnderlyingAgreementVariationRequestPayload.builder()
						.underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
								.underlyingAgreement(UnderlyingAgreement.builder()
										.facilities(Set.of(Facility.builder()
												.facilityItem(FacilityItem.builder()
														.facilityDetails(FacilityDetails.builder()
																.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
																.build())
														.build())
												.build()))
										.build())
								.build())
						.build())
				.build();
		addCaResourceToRequest(requestContainsCca2Only);
		
		final Request requestDoesNotContainCca2Only = Request.builder()
				.id(requestId2)
				.processInstanceId("processInstanceId2")
				.type(RequestType.builder().code(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION).build())
				.status(RequestStatuses.IN_PROGRESS)
				.payload(UnderlyingAgreementVariationRequestPayload.builder()
						.underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
								.underlyingAgreement(UnderlyingAgreement.builder()
										.facilities(Set.of(Facility.builder()
												.facilityItem(FacilityItem.builder()
														.facilityDetails(FacilityDetails.builder()
																.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3))
																.build())
														.build())
												.build()))
										.build())
								.build())
						.build())
				.build();
		addCaResourceToRequest(requestDoesNotContainCca2Only);
		
		final RequestTask task = RequestTask.builder()
				.type(RequestTaskType.builder().code(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT).build())
				.payload(UnderlyingAgreementVariationRequestTaskPayload.builder()
						.underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
								.underlyingAgreement(UnderlyingAgreement.builder()
										.facilities(Set.of(Facility.builder()
												.facilityItem(FacilityItem.builder()
														.facilityDetails(FacilityDetails.builder()
																.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
																.build())
														.build())
												.build()))
										.build())
								.build())
						.build())
				.build();
		final Request requestTaskContainsCca2Only = Request.builder()
				.id(requestId3)
				.processInstanceId("processInstanceId3")
				.type(RequestType.builder().code(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION).build())
				.status(RequestStatuses.IN_PROGRESS)
				.payload(UnderlyingAgreementVariationRequestPayload.builder().build())
				.requestTasks(List.of(task))
				.build();
		addCaResourceToRequest(requestTaskContainsCca2Only);
		
		when(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
				CcaRequestType.UNDERLYING_AGREEMENT_VARIATION, ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()))
				.thenReturn(List.of(requestContainsCca2Only, requestDoesNotContainCca2Only, requestTaskContainsCca2Only));
		
		cca2TerminationRunRequestService.terminateVariationRequests();
		
		verify(requestService, times(1)).addActionToRequest(requestContainsCca2Only, null, CcaRequestActionType.REQUEST_TERMINATED, null);
		verify(requestService, times(1)).addActionToRequest(requestTaskContainsCca2Only, null, CcaRequestActionType.REQUEST_TERMINATED, null);
		verify(requestService, never()).addActionToRequest(requestDoesNotContainCca2Only, null, CcaRequestActionType.REQUEST_TERMINATED, null);
		
		verify(workflowService, times(1)).deleteProcessInstance("processInstanceId1", TERMINATE_REASON);
		verify(workflowService, times(1)).deleteProcessInstance("processInstanceId3", TERMINATE_REASON);
    }
    
    @Test
    void getNumberOfAccountsCompleted() {
    	String requestId = "1";
		Request request = Request.builder()
				.id(requestId)
				.metadata(Cca2TerminationRunRequestMetadata.builder()
						.cca2TerminationAccountStates(Map.of(
								1L, Cca2TerminationAccountState.builder().succeeded(true).build(),
								2L, Cca2TerminationAccountState.builder().build(),
								3L, Cca2TerminationAccountState.builder().succeeded(false).build()
								))
						.build())
				.build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		
		long result = cca2TerminationRunRequestService.getNumberOfAccountsCompleted(requestId);
		
		assertThat(result).isEqualTo(2);
		
		verify(requestService, times(1)).findRequestById(requestId);
    }
    
    @Test
    void completeCca2TerminationRun() {
        final String requestId = "requestId";

        final Map<Long, Cca2TerminationAccountState> accountStates = Map.of(
                1L, Cca2TerminationAccountState.builder().succeeded(false).build(),
                2L, Cca2TerminationAccountState.builder().succeeded(true).build()
        );

        final Cca2TerminationRunRequestMetadata metadata = Cca2TerminationRunRequestMetadata.builder()
        		.cca2TerminationAccountStates(accountStates)
        		.build();
        final Request request = Request.builder()
                .id(requestId)
                .metadata(metadata)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        cca2TerminationRunRequestService.completeCca2TerminationRun(requestId);

        // Verify
        assertThat(metadata.getCca2TerminationAccountStates()).isEqualTo(accountStates);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
    }
    
    private void addCaResourceToRequest(Request request) {
		RequestResource caResource = RequestResource.builder()
				.resourceType(ResourceType.CA)
				.resourceId(CompetentAuthorityEnum.ENGLAND.name())
				.request(request)
				.build();

        request.getRequestResources().add(caResource);
	}
}
