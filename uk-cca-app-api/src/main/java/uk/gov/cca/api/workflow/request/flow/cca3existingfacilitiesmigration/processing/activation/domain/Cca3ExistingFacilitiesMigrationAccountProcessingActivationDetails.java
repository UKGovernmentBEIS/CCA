package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotEmpty;
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
public class Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails {

	@NotEmpty
	@Builder.Default
    @JsonInclude(Include.NON_EMPTY)
    private Set<UUID> evidenceFiles = new HashSet<>();
    
    @Size(max = 10000)
    private String comments;
}
