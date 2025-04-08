package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataSpreadsheetGenerateCompletedServiceTest {

    @InjectMocks
    private PerformanceDataSpreadsheetGenerateCompletedService performanceDataSpreadsheetGenerateCompletedService;

    @Mock
    private RequestService requestService;

    @Test
    void completed() {
        final String requestId = "requestId";
        final long accountId = 1L;
        final TargetUnitAccountReport accountReport = TargetUnitAccountReport.builder().accountId(accountId).build();

        Request request = Request.builder()
                .id(requestId)
                .payload(PerformanceDataGenerateRequestPayload.builder()
                        .accountsReports(new HashMap<>())
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        performanceDataSpreadsheetGenerateCompletedService.completed(requestId, accountId, accountReport);

        // Verify
        assertThat(((PerformanceDataGenerateRequestPayload) request.getPayload()).getAccountsReports())
                .hasSize(1)
                .containsExactlyEntriesOf(Map.of(accountId, accountReport));
        verify(requestService, times(1)).findRequestById(requestId);
    }
}
