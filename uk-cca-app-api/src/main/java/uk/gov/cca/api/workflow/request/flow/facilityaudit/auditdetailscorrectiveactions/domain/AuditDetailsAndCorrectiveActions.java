package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain;


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
public class AuditDetailsAndCorrectiveActions {

    @NotNull
    @Valid
    private AuditDetails auditDetails;

    @NotNull
    @Valid
    private CorrectiveActions correctiveActions;
}
