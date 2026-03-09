package uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetails;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceRequestPayload extends CcaRequestPayload {

    //TODO: enhance
    private NonComplianceDetails nonComplianceDetails;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();
}
