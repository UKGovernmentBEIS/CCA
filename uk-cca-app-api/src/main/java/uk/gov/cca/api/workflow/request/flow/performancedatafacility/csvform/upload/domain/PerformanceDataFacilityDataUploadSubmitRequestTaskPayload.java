package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataUploadProcessingStatus;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDataFacilityDataUploadSubmitRequestTaskPayload extends RequestTaskPayload {

    private SectorAssociationInfo sectorAssociationInfo;
    private PerformanceDataFacilityUpload performanceDataUpload;
    private PerformanceDataFacilityDataUploadProcessingStatus processingStatus;
    private PerformanceDataFacilityUploadResults results;
    private PerformanceDataFacilityUploadErrorType errorMessage;

    @Builder.Default
    private Map<Long, FacilityUploadReport> facilityReports = new HashMap<>();

    @Builder.Default
    private List<PerformanceDataFacilityCsvErrorEntry> csvRowErrors = new ArrayList<>();

    @Builder.Default
    private Map<UUID, String> uploadAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getUploadAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        Set<UUID> uploadedFiles = new HashSet<>(Optional.ofNullable(getPerformanceDataUpload())
                .map(PerformanceDataFacilityUpload::getFiles)
                .orElse(Collections.emptySet()));

        if (results != null) {
            Optional.ofNullable(results.getUploadSummaryFile()).ifPresent(uploadedFiles::add);
        }

        return uploadedFiles;
    }
}
