package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestMetadata;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TerminateOpenFacilityWorkflowsServiceTest {

    @InjectMocks
    private TerminateOpenFacilityWorkflowsService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private WorkflowService workflowService;

    private static final String TERMINATE_REASON = "Workflow terminated by the system due to successful CSV update for the target period";

    @Test
    void terminateOpenWorkflows() {
        final String requestId1 = "ADS_12-F00001-TPR-3";
        final String requestId2 = "ADS_12-F00001-TPR-4";
        final Long facilityId = 999L;
        final String sectorUser = "sectorUser";
        final String csvFormProcessInstanceId = "csvFormProcessInstanceId";
        final String digitalFormProcessInstanceId = "digitalFormProcessInstanceId";

        final Request requestCsvForm = Request.builder()
                .id(requestId1)
                .processInstanceId(csvFormProcessInstanceId)
                .payload(PerformanceDataFacilityProcessingRequestPayload.builder()
                        .facility(FacilityDTO.builder()
                                .id(facilityId)
                                .build())
                        .targetPeriodType(TargetPeriodType.TP7)
                        .sectorUserAssignee(sectorUser)
                        .build())
                .metadata(PerformanceDataFacilityProcessingRequestMetadata.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .build())
                .type(RequestType.builder()
                        .code(CcaRequestType.PERFORMANCE_DATA_FACILITY_PROCESSING)
                        .build())
                .status(RequestStatuses.IN_PROGRESS)
                .build();

        final Request requestDigitalForm = Request.builder()
                .id(requestId2)
                .processInstanceId(digitalFormProcessInstanceId)
                .payload(PerformanceDataFacilityProcessingRequestPayload.builder()
                        .facility(FacilityDTO.builder()
                                .id(facilityId)
                                .build())
                        .sectorUserAssignee(sectorUser).build())
                .metadata(PerformanceDataFacilityDigitalFormRequestMetadata.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .build())
                .type(RequestType.builder()
                        .code(CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM)
                        .build())
                .status(RequestStatuses.IN_PROGRESS)
                .build();

        when(requestService.findRequestById(requestId1)).thenReturn(requestCsvForm);
        when(requestQueryService.findInProgressRequestsByResource(facilityId, CcaResourceType.FACILITY)).thenReturn(List.of(requestDigitalForm));

        // invoke
        service.terminateOpenWorkflows(requestId1);

        // verify
        verify(workflowService, times(1)).deleteProcessInstance("digitalFormProcessInstanceId", TERMINATE_REASON);
        verify(requestService, times(1)).addActionToRequest(eq(requestDigitalForm), any(), eq(CcaRequestActionType.REQUEST_TERMINATED_DUE_TO_PERFORMANCE_DATA_CSV_UPDATE), eq(sectorUser));
        // never invoked
        verify(requestService, never()).addActionToRequest(eq(requestCsvForm), any(), eq(CcaRequestActionType.REQUEST_TERMINATED_DUE_TO_PERFORMANCE_DATA_CSV_UPDATE), eq(sectorUser));
        verify(workflowService, never()).deleteProcessInstance(csvFormProcessInstanceId, TERMINATE_REASON);
    }
}