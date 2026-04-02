package uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain;

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
public class NonComplianceCloseJustification {

    @NotNull
    @Size(max = 10000)
    private String reason;

    @Builder.Default
    private Set<UUID> files = new HashSet<>();
}
