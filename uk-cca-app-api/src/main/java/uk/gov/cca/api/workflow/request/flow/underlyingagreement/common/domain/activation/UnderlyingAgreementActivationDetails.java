package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.activation;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementActivationDetails {

	@NotEmpty
	@Builder.Default
    @JsonInclude(Include.NON_EMPTY)
    private Set<UUID> evidenceFiles = new HashSet<>();
    
    @Size(max = 10000)
    private String comments;
}
