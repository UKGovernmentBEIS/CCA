package uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#isEnforcementResponseNoticeRequired) == (#explanation != null)}",
        message = "nonCompliance.nonComplianceDetails.explanation")
public class NonComplianceDetails {

    @NotNull
    private NonComplianceType nonComplianceType;

    @PastOrPresent
    private LocalDate nonCompliantDate;

    @PastOrPresent
    private LocalDate compliantDate;

    @Size(max = 10000)
    private String comment;

    @Builder.Default
    private Set<String> relevantWorkflows = new HashSet<>();

    @Builder.Default
    private Set<@Valid WorkflowFacilityDTO> relevantFacilities = new HashSet<>();

    @NotNull
    private Boolean isEnforcementResponseNoticeRequired;

    @Size(max = 10000)
    private String explanation;
}
