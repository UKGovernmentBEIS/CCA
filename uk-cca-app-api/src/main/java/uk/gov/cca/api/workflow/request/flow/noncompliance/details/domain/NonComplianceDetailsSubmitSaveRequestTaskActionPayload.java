package uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceDetailsSubmitSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    private NonComplianceDetails nonComplianceDetails;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();
}
