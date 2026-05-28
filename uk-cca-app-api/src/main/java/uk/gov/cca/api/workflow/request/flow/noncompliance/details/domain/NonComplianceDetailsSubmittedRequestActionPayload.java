package uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceDetailsSubmittedRequestActionPayload extends CcaRequestActionPayload {

    @NotNull
    @Valid
    private NonComplianceDetails nonComplianceDetails;

    @Builder.Default
    private Map<String, String> allRelevantWorkflows = new HashMap<>();

    @Builder.Default
    private Map<String, String> allRelevantFacilities = new HashMap<>();
}
