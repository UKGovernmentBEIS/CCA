package uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTerminationReasonDetails {

    @NotNull
    private AdminTerminationReason reason;

    @NotNull
    @Size(max = 10000)
    private String explanation;

    @Builder.Default
    private Set<UUID> relevantFiles = new HashSet<>();

}
