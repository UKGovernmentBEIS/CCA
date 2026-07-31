package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityPerformanceAccountTemplateDataContainer {

    // Saving Actions and Measures Implemented
    @NotEmpty
    private List<@Valid FacilityPerformanceAccountTemplateSavingAction> savingActions;
}
