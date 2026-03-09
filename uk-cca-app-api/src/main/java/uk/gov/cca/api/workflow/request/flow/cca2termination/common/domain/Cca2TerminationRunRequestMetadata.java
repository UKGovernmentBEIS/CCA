package uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cca2TerminationRunRequestMetadata extends RequestMetadata {
		
	@Builder.Default
    private Map<Long, Cca2TerminationAccountState> cca2TerminationAccountStates = new HashMap<>();
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Integer getTotalAccounts() {
        return cca2TerminationAccountStates.size();
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getFailedAccounts() {
        return cca2TerminationAccountStates.values().stream()
        		.filter(acc -> Boolean.FALSE.equals(acc.getSucceeded()))
                .count();
    }
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getAccountsWithExcludedFacilities() {
        return cca2TerminationAccountStates.values().stream()
        		.filter(acc -> acc.getFacilitiesExcluded() != 0L
        				&& acc.getFacilitiesExcluded() < acc.getFacilityIds().size())
                .count();
    }
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getTerminatedAccounts() {
        return cca2TerminationAccountStates.values().stream()
        		.filter(acc -> acc.getFacilitiesExcluded() == acc.getFacilityIds().size())
                .count();
    }
}
