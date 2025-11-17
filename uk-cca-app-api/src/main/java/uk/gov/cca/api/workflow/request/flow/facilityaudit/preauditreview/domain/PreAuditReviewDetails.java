package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreAuditReviewDetails {

    @NotNull
    @Valid
    private AuditReasonDetails auditReasonDetails;

    @NotNull
    @Valid
    private RequestedDocuments requestedDocuments;

    @NotNull
    @Valid
    private AuditDetermination auditDetermination;
}
