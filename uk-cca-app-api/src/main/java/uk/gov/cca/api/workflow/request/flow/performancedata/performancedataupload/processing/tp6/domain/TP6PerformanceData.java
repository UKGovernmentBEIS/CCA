package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceData;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TP6PerformanceData extends PerformanceData {

    // Section 2: Target Unit Details, Targets and Previous Performance
    @NotNull(message = "{performanceData.targetUnitDetails.notEmpty}")
    @Valid
    @Builder.Default
    private PerformanceDataTargetUnitDetails targetUnitDetails = new PerformanceDataTargetUnitDetails();

    // Section 3: Actual Target Period Performance for Target Facility
    @NotNull(message = "{performanceData.actualTargetPeriodPerformance.notEmpty}")
    @Valid
    @Builder.Default
    private ActualTargetPeriodPerformance actualTargetPeriodPerformance = new ActualTargetPeriodPerformance();

    // Section 4: Target Period Performance Result
    @NotNull(message = "{performanceData.performanceResult.notEmpty}")
    @Valid
    @Builder.Default
    private TargetPeriodPerformanceResult performanceResult = new TargetPeriodPerformanceResult();

    // Section 5: Carbon Surplus or Buy-Out Determination
    @NotNull(message = "{performanceData.primaryDetermination.notEmpty}")
    @Valid
    @Builder.Default
    private PrimaryDetermination primaryDetermination = new PrimaryDetermination();

    // Section 5: Supplementary MOA Surplus and Buy-Out Determination
    @NotNull(message = "{performanceData.secondaryDetermination.notEmpty}")
    @Valid
    @Builder.Default
    private SecondaryDetermination secondaryDetermination = new SecondaryDetermination();
}
