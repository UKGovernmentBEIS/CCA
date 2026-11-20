package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;

import java.time.LocalDateTime;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissionsTradingSchemeDataReportDetailsDTO {

    @NotNull
    private Year targetPeriodYear;

    @NotNull
    private LocalDateTime submissionDate;

    @NotNull
    private EmissionsTradingSchemeDataContainer data;
}
