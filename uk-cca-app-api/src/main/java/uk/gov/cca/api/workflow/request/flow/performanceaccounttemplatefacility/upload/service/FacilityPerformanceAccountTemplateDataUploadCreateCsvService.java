package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadErrorType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.utils.FacilityPerformanceAccountTemplateDataUploadUtility;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.Map;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataUploadCreateCsvService {

    private final CcaFileAttachmentService ccaFileAttachmentService;

    public void createCsvFile(RequestTask requestTask, Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports) {

        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload =
                (FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        try {
            // Write CSV
            if (!facilityReports.isEmpty() || !requestTaskPayload.getCsvRowErrors().isEmpty()) {
                final FileDTO csvFileDTO = FacilityPerformanceAccountTemplateDataUploadUtility
                        .createCsvFile(facilityReports.values().stream().toList(), requestTaskPayload.getCsvRowErrors(), requestTask.getAssignee());

                // Save to DB
                final String uuid = ccaFileAttachmentService.createSystemFileAttachment(csvFileDTO, FileStatus.PENDING);

                // Save to task
                requestTaskPayload.getResults().setUploadSummaryFile(UUID.fromString(uuid));
                requestTaskPayload.getAttachments().put(UUID.fromString(uuid), csvFileDTO.getFileName());
            }
        } catch (Exception e) {
            log.error("Cannot generate csv for task {}", requestTask.getId(), e);
            requestTaskPayload.setErrorMessage(FacilityPerformanceAccountTemplateDataUploadErrorType.SUBMISSION_RESULTS_CSV_FAILED);
        }
    }
}
