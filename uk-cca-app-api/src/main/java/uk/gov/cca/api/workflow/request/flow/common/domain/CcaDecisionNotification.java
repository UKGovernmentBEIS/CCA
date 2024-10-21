package uk.gov.cca.api.workflow.request.flow.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CcaDecisionNotification {

    @JsonUnwrapped
    @Valid
    private DecisionNotification decisionNotification;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<String> sectorUsers = new HashSet<>();
}
