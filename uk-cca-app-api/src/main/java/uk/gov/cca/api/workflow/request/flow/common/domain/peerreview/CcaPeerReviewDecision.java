package uk.gov.cca.api.workflow.request.flow.common.domain.peerreview;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewDecision;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CcaPeerReviewDecision {

    @NotNull
    @JsonUnwrapped
    @Valid
    private PeerReviewDecision decision;

    @Builder.Default
    private Set<UUID> files = new HashSet<>();
}
