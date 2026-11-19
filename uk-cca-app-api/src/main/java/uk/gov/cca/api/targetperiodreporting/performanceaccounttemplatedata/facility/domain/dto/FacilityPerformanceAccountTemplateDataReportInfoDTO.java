package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityPerformanceAccountTemplateDataReportInfoDTO {

    private Year targetPeriodYear;
    private int reportVersion;
    private LocalDateTime submissionDate;
}
