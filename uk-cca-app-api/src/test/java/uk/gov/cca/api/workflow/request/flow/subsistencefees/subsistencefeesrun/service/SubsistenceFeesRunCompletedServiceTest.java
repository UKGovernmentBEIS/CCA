package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunUpdateService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaReport;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.Year;
import java.util.Map;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunCompletedServiceTest {

    @InjectMocks
    private SubsistenceFeesRunCompletedService service;

    @Mock
    private RequestService requestService;

    @Mock
    private SubsistenceFeesRunUpdateService subsistenceFeesRunUpdateService;

    @Test
    void subsistenceFeesRunCompleted() {
        String requestId = "requestId";
        String submitterId = "submitterId";
        Long runId = 1L;
        SubsistenceFeesRunRequestPayload payload = SubsistenceFeesRunRequestPayload.builder().runId(runId).submitterId(submitterId).build();
        SubsistenceFeesRunRequestMetadata metadata = SubsistenceFeesRunRequestMetadata.builder()
                .chargingYear(Year.of(2025))
                .accountsReports(Map.of())
                .sectorsReports(Map.of(1L, MoaReport.builder().succeeded(true).build()))
                .build();
        Request request = Request.builder().id("S2501").payload(payload).metadata(metadata).build();
        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.completeSubsistenceFeesRun(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, never()).updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
        verify(subsistenceFeesRunUpdateService, times(1)).finalizeSubsistenceFeesRun(runId);
        verify(requestService, times(1)).addActionToRequest(request,
                SubsistenceFeesRunCompletedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.SUBSISTENCE_FEES_RUN_COMPLETED_PAYLOAD)
                        .paymentRequestId("S2501")
                        .chargingYear(Year.of(2025))
                        .sentInvoices(1L)
                        .failedInvoices(0L)
                        .status("COMPLETED")
                        .build(),
                CcaRequestActionType.SUBSISTENCE_FEES_RUN_COMPLETED, submitterId);
    }

    @Test
    void subsistenceFeesRunCompleted_withFailures() {
        String requestId = "requestId";
        String submitterId = "submitterId";
        Long runId = 1L;
        SubsistenceFeesRunRequestPayload payload = SubsistenceFeesRunRequestPayload.builder().runId(runId).submitterId(submitterId).build();
        SubsistenceFeesRunRequestMetadata metadata = SubsistenceFeesRunRequestMetadata.builder()
                .chargingYear(Year.of(2025))
                .accountsReports(Map.of(1L, MoaReport.builder().succeeded(true).build()))
                .sectorsReports(Map.of(1L, MoaReport.builder().succeeded(false).build()))
                .build();
        Request request = Request.builder().id("S2501").payload(payload).metadata(metadata).build();
        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.completeSubsistenceFeesRun(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
        verify(subsistenceFeesRunUpdateService, times(1)).finalizeSubsistenceFeesRun(runId);
        verify(requestService, times(1)).addActionToRequest(request,
                SubsistenceFeesRunCompletedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.SUBSISTENCE_FEES_RUN_COMPLETED_PAYLOAD)
                        .paymentRequestId("S2501")
                        .chargingYear(Year.of(2025))
                        .sentInvoices(1L)
                        .failedInvoices(1L)
                        .status("COMPLETED_WITH_FAILURES")
                        .build(),
                CcaRequestActionType.SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES, submitterId);
    }
}
