package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PerformanceAccountTemplateDataContainer {
    /** Section 1: TU Identity and Performance & Totals */
    @NotNull
    private TargetUnitIdentityAndPerformance targetUnitIdentityAndPerformance;
    
    /** Energy/Carbon Saving Actions and Measures Implemented */
    private List<EnergyOrCarbonSavingActionsAndMeasuresImplementedItem> energyOrCarbonSavingActionsAndMeasuresImplementedItems;

    @NotNull
    private FileInfoDTO file;
}
