package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataContainer;

import java.time.LocalDateTime;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityPerformanceAccountTemplateDataReportDetailsDTO {

    private Year targetPeriodYear;
    private LocalDateTime submissionDate;
    private FacilityPerformanceAccountTemplateDataContainer data;
}
