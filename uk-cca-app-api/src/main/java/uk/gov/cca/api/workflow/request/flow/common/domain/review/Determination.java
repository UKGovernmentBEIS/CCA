package uk.gov.cca.api.workflow.request.flow.common.domain.review;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#type == null) || ( ((#type eq 'REJECTED') == (#reason != null)) && ((#type eq 'ACCEPTED') == (#reason == null)) )}",
        message = "underlyingagreement.review.determination.reason.typeMismatch")
public class Determination {

    @NotNull
    private DeterminationType type;

    @Size(max = 10000)
    private String reason;

    @Size(max = 10000)
    private String additionalInformation;

    @Builder.Default
    private Set<UUID> files = new HashSet<>();
}
