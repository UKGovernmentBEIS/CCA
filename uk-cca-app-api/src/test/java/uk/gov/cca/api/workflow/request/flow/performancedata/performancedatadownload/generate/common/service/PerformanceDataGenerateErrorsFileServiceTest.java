package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class PerformanceDataGenerateErrorsFileServiceTest {

    @InjectMocks
    private PerformanceDataGenerateErrorsFileService performanceDataGenerateErrorsFileService;

    @Mock
    private RequestService requestService;

    @Mock
    private CcaFileAttachmentService ccaFileAttachmentService;

    @Test
    void generateErrorsFile() throws IOException {
        final String requestId = "requestId";
        final String sectorUserAssignee = "sector";
        final String fileCsv = "fileCsv";

        final Request request = Request.builder()
                .id(requestId)
                .payload(PerformanceDataGenerateRequestPayload.builder()
                        .targetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .sectorAssociationInfo(SectorAssociationInfo.builder()
                                .acronym("acronym")
                                .build())
                        .accountsReports(Map.of(
                                1L, TargetUnitAccountReport.builder().succeeded(false).accountId(1L).errors(List.of("error1")).build(),
                                2L, TargetUnitAccountReport.builder().succeeded(true).build()
                        ))
                        .sectorUserAssignee(sectorUserAssignee)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaFileAttachmentService.createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED), eq(sectorUserAssignee)))
                .thenReturn(fileCsv);

        // Invoke
        Optional<FileInfoDTO> result = performanceDataGenerateErrorsFileService.generateErrorsFile(requestId);

        // Verify
        assertThat(result)
                .isPresent()
                .contains(FileInfoDTO.builder().uuid(fileCsv).name("acronym_TP6_download_errors.csv").build());
        verify(requestService, times(1)).findRequestById(requestId);
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED), eq(sectorUserAssignee));
    }

    @Test
    void generateErrorsFile_empty() {
        final String requestId = "requestId";
        final String sectorUserAssignee = "sector";

        final Request request = Request.builder()
                .id(requestId)
                .payload(PerformanceDataGenerateRequestPayload.builder()
                        .targetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .sectorAssociationInfo(SectorAssociationInfo.builder()
                                .acronym("acronym")
                                .build())
                        .accountsReports(Map.of(
                                1L, TargetUnitAccountReport.builder().succeeded(true).build(),
                                2L, TargetUnitAccountReport.builder().succeeded(true).build()
                        ))
                        .sectorUserAssignee(sectorUserAssignee)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        Optional<FileInfoDTO> result = performanceDataGenerateErrorsFileService.generateErrorsFile(requestId);

        // Verify
        assertThat(result).isNotPresent();
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoInteractions(ccaFileAttachmentService);
    }
}
