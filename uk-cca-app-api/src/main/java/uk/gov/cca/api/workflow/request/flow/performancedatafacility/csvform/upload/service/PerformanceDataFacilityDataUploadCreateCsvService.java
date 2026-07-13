package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.utils.PerformanceDataFacilityDataUploadUtility;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUploadErrorType;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.Map;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataUploadCreateCsvService {

    private final CcaFileAttachmentService ccaFileAttachmentService;

    public void createCsvFile(RequestTask requestTask, Map<Long, FacilityUploadReport> facilityReports) {
        PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        try {
            // Write CSV
            if (!facilityReports.isEmpty() || !taskPayload.getCsvRowErrors().isEmpty()) {
                final FileDTO csvFileDTO = PerformanceDataFacilityDataUploadUtility
                        .createCsvFile(facilityReports.values().stream().toList(), taskPayload.getCsvRowErrors());

                // Save to DB
                final String uuid = ccaFileAttachmentService.createSystemFileAttachment(
                        csvFileDTO, FileStatus.PENDING, requestTask.getAssignee());

                // Save to task
                taskPayload.getResults().setUploadSummaryFile(UUID.fromString(uuid));
                taskPayload.getAttachments().put(UUID.fromString(uuid), csvFileDTO.getFileName());
            }
        } catch (Exception e) {
            log.error("Cannot generate csv for task {}", requestTask.getId(), e);
            taskPayload.setErrorMessage(PerformanceDataFacilityUploadErrorType.CSV_FAILED);
        }
    }
}
