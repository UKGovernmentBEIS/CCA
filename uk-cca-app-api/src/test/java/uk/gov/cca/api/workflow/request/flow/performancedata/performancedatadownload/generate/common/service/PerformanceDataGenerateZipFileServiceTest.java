package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataGenerateZipFileServiceTest {

    @InjectMocks
    private PerformanceDataGenerateZipFileService performanceDataGenerateZipFileService;

    @Mock
    private RequestService requestService;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private CcaFileAttachmentService ccaFileAttachmentService;

    @Test
    void generateZipFile() throws IOException {
        final String requestId = "requestId";
        final String sectorUserAssignee = "sector";
        final String file1 = "file1";
        final String file2 = "file2";
        final String fileZip = "fileZip";
        final Request request = Request.builder()
                .id(requestId)
                .payload(PerformanceDataGenerateRequestPayload.builder()
                        .targetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .sectorAssociationInfo(SectorAssociationInfo.builder()
                                .acronym("acronym")
                                .build())
                        .accountsReports(Map.of(
                                1L, TargetUnitAccountReport.builder().succeeded(true).fileInfo(FileInfoDTO.builder().uuid(file1).build()).build(),
                                2L, TargetUnitAccountReport.builder().succeeded(true).fileInfo(FileInfoDTO.builder().uuid(file2).build()).build()
                        ))
                        .sectorUserAssignee(sectorUserAssignee)
                        .build())
                .build();

        final Stream<FileDTO> fileStream = Stream.of(
                FileDTO.builder().fileName("report1").fileContent("dummy-content1".getBytes()).build(),
                FileDTO.builder().fileName("report2").fileContent("dummy-content2".getBytes()).build()
        );

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(fileAttachmentService.getFilesAsStream(Set.of(file1, file2))).thenReturn(fileStream);
        when(ccaFileAttachmentService.createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED), eq(sectorUserAssignee)))
                .thenReturn(fileZip);

        // Invoke
        FileInfoDTO result = performanceDataGenerateZipFileService.generateZipFile(requestId);

        // Verify
        assertThat(result).isEqualTo(FileInfoDTO.builder().uuid(fileZip).name("acronym_TP6_reporting_templates.zip").build());
        verify(requestService, times(1)).findRequestById(requestId);
        verify(fileAttachmentService, times(1)).getFilesAsStream(Set.of(file1, file2));
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED), eq(sectorUserAssignee));
    }
}
