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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataProcessingServiceTest {

    @InjectMocks
    private PerformanceDataProcessingService performanceDataProcessingService;

    @Mock
    private RequestService requestService;

    @Test
    void getAccountReports() {
        final String requestId = "requestId";
        final Map<Long, TargetUnitAccountUploadReport> accountReports = Map.of(
                1L, TargetUnitAccountUploadReport.builder().accountId(1L).build()
        );
        final Request request = Request.builder()
                .id(requestId)
                .payload(PerformanceDataProcessingRequestPayload.builder()
                        .accountReports(accountReports)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // invoke
        Map<Long, TargetUnitAccountUploadReport> result = performanceDataProcessingService
                .getAccountReports(requestId);

        // verify
        assertThat(result).isEqualTo(accountReports);
        verify(requestService, times(1)).findRequestById(requestId);
    }
}
