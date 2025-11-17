package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#hasActions) == (#actions?.size() > 0)}",
        message = "facilityAudit.auditdetailscorrectiveactions.hasActions")
public class CorrectiveActions {

    @NotNull
    private Boolean hasActions;

    @Size(max = 10)
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@Valid CorrectiveAction> actions;

}
