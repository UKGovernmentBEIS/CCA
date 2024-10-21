package uk.gov.cca.api.workflow.request.flow.common.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class CcaReviewDecision {

	@NotNull
	private CcaReviewDecisionType type;
}
