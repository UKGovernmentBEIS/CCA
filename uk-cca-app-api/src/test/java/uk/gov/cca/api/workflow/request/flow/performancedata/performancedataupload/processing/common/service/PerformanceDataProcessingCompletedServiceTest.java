package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.PerformanceDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataProcessingCompletedServiceTest {

    @InjectMocks
    private PerformanceDataProcessingCompletedService performanceDataProcessingCompletedService;

    @Mock
    private RequestService requestService;

    @Test
    void completed() {
        final String requestId = "requestId";
        final Long accountId = 1L;
        final TargetUnitAccountUploadReport accountReport = TargetUnitAccountUploadReport.builder()
                .accountId(accountId)
                .build();

        Request request = Request.builder()
                .id(requestId)
                .payload(PerformanceDataProcessingRequestPayload.builder()
                        .accountReports(new HashMap<>())
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        performanceDataProcessingCompletedService.completed(requestId, accountId, accountReport);

        // Verify
        assertThat(((PerformanceDataProcessingRequestPayload) request.getPayload()).getAccountReports())
                .containsExactlyEntriesOf(Map.of(accountId, accountReport));
        verify(requestService, times(1)).findRequestById(requestId);
    }
}
