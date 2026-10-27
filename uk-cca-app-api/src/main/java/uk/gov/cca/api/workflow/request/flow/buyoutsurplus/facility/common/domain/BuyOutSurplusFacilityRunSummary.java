package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusFacilityRunSummary {

	@NotNull
    @PositiveOrZero
    private Long totalAccounts;

    @NotNull
    @PositiveOrZero
    private Long failedAccounts;
    
    @NotNull
    @PositiveOrZero
    private Long totalFacilities;

    @NotNull
    @PositiveOrZero
    private Long failedFacilities;
    
    private List<@Valid BuyOutSurplusFacilityRunSummaryEntry> runSummaryEntries;
}
