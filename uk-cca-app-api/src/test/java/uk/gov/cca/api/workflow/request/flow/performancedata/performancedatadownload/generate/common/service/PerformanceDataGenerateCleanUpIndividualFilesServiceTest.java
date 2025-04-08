package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataGenerateCleanUpIndividualFilesServiceTest {

    @InjectMocks
    private PerformanceDataGenerateCleanUpIndividualFilesService performanceDataGenerateCleanUpIndividualFilesService;

    @Mock
    private RequestService requestService;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Test
    void cleanupIndividualAccountReports() {
        final String requestId = "requestId";
        final String file = "file";

        final Request request = Request.builder()
                .id(requestId)
                .payload(PerformanceDataGenerateRequestPayload.builder()
                        .accountsReports(Map.of(
                                1L, TargetUnitAccountReport.builder().succeeded(false).build(),
                                2L, TargetUnitAccountReport.builder().succeeded(true).fileInfo(FileInfoDTO.builder().uuid(file).build()).build()
                        ))
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        performanceDataGenerateCleanUpIndividualFilesService.cleanupIndividualAccountReports(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(fileAttachmentService, times(1))
                .deleteFileAttachmentsInBatches(Set.of(file));
    }

    @Test
    void cleanupIndividualAccountReports_empty() {
        final String requestId = "requestId";

        final Request request = Request.builder()
                .id(requestId)
                .payload(PerformanceDataGenerateRequestPayload.builder()
                        .accountsReports(Map.of(
                                1L, TargetUnitAccountReport.builder().succeeded(false).build(),
                                2L, TargetUnitAccountReport.builder().succeeded(false).build()
                        ))
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        performanceDataGenerateCleanUpIndividualFilesService.cleanupIndividualAccountReports(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoInteractions(fileAttachmentService);
    }
}
