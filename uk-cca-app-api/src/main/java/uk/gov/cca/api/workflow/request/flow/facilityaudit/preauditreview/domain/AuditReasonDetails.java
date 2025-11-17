package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditReasonDetails {

    @NotEmpty
    @Builder.Default
    private List<FacilityAuditReasonType> reasonsForAudit = new ArrayList<>();

    @NotNull
    @Size(max = 10000)
    private String comment;
}
