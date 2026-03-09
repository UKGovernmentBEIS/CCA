package uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceDetailsSubmitRequestTaskPayload extends RequestTaskPayload {

    @NotNull
    @Valid
    private NonComplianceDetails nonComplianceDetails;

    @Builder.Default
    private Map<String, String> allRelevantWorkflows = new HashMap<>();

    @Builder.Default
    private Map<String, String> allRelevantFacilities = new HashMap<>();

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();
}
