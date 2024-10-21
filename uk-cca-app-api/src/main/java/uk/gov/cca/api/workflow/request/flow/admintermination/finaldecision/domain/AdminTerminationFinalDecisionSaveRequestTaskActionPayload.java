package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain;

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
public class AdminTerminationFinalDecisionSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    private AdminTerminationFinalDecisionReasonDetails adminTerminationFinalDecisionReasonDetails;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();
}
