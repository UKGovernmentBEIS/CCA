package uk.gov.cca.api.workflow.request.flow.common.domain.review;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementReviewDecisionDetails {

	@Size(max = 10000)
    private String notes;
	
	@Builder.Default
    @JsonInclude(Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();
}
