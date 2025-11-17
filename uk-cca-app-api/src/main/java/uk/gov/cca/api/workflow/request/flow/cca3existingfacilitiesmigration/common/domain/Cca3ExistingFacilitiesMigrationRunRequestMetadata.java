package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cca3ExistingFacilitiesMigrationRunRequestMetadata extends RequestMetadata {

    @Builder.Default
    private Map<Long, Cca3FacilityMigrationAccountState> accountStates = new HashMap<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Integer getTotalAccounts() {
        return accountStates.size();
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getFailedAccounts() {
        return accountStates.values().stream().filter(acc -> !acc.isSucceeded())
                .count();
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getSuccessfulAccountsParticipatingInCca3Scheme() {
        return accountStates.values().stream()
                .filter(acc -> acc.isSucceeded() && acc.isCca3Participating())
                .count();
    }
}
