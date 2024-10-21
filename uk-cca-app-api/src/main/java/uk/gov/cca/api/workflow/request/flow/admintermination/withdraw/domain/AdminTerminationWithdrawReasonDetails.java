package uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTerminationWithdrawReasonDetails {

    @NotNull
    private String explanation;

    private Set<UUID> relevantFiles;

}
