package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityPerformanceAccountTemplateDataUploadResults {
    private int totalFilesUploaded;
    private int facilitiesSucceeded;
    private int facilitiesFailed;
    private UUID uploadSummaryFile;
    private LocalDateTime submittedDate;
}