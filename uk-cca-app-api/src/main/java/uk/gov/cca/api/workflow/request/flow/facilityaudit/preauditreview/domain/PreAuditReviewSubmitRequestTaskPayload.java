package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestTaskAttachable;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PreAuditReviewSubmitRequestTaskPayload extends RequestTaskPayload implements FacilityAuditRequestTaskAttachable {

    @NotNull
    @Valid
    private PreAuditReviewDetails preAuditReviewDetails;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> facilityAuditAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getFacilityAuditAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        PreAuditReviewDetails preAuditReviewDetails = getPreAuditReviewDetails();
        if (preAuditReviewDetails == null) {
            return Collections.emptySet();
        }
        return Optional.ofNullable(preAuditReviewDetails.getRequestedDocuments()).map(details -> {
            Set<UUID> referencedAttachmentIds = new HashSet<>(Arrays.asList(
                    details.getEligibleProcessFile(),
                    details.getManufacturingProcessFile(),
                    details.getProcessFlowMapsFile(),
                    details.getAnnotatedSitePlansFile(),
                    details.getSeventyPerCentRuleEvidenceFile(),
                    details.getDirectlyAssociatedActivitiesFile()));
            referencedAttachmentIds.addAll(details.getBaseYearTargetPeriodEvidenceFiles());
            referencedAttachmentIds.addAll(details.getAdditionalDocuments());
            return referencedAttachmentIds.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
        }).orElse(Collections.emptySet());
    }
}
