package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload extends RequestTaskPayload {

    private SectorAssociationInfo sectorAssociationInfo;
    private FacilityPerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload;
    private FacilityPerformanceAccountTemplateDataUploadProcessingStatus processingStatus;
    private FacilityPerformanceAccountTemplateDataUploadErrorType errorMessage;

    @Builder.Default
    private Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> uploadAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getUploadAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        Set<UUID> uploadedFiles = new HashSet<>(Optional.ofNullable(getPerformanceAccountTemplateDataUpload())
                .map(FacilityPerformanceAccountTemplateDataUpload::getFiles)
                .orElse(Collections.emptySet()));

//        if (results != null) {
//            Optional.ofNullable(results.getUploadSummaryFile()).ifPresent(uploadedFiles::add);
//        }

        return uploadedFiles;
    }
    //TODO: enhance
}
