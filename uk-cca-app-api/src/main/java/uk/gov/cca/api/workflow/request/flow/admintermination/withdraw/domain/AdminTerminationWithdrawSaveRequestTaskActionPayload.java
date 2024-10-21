package uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AdminTerminationWithdrawSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    private AdminTerminationWithdrawReasonDetails adminTerminationWithdrawReasonDetails;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();
}
