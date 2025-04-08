package uk.gov.cca.api.targetperiodreporting.performancedata.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataContainer {
	
    /** Section 2: Target Unit Details, Targets and Previous Performance */
	@NotNull
    @Valid
	private TargetsPreviousPerformance targetsPreviousPerformance;

    /** Section 3: Actual Target Period Performance for Target Facility */
    @NotNull
    @Valid
    private ActualPerformance actualPerformance;

    /** Section 4: Target Period Performance Result */
    @NotNull
    @Valid
    private PerformanceResult performanceResult;

    /** Section 5: Surplus and Buy-Out Determination */
    @NotNull
    @Valid
    private SurplusBuyOutDetermination surplusBuyOutDetermination;

    @NotNull
    FileInfoDTO targetPeriodReport;
}
