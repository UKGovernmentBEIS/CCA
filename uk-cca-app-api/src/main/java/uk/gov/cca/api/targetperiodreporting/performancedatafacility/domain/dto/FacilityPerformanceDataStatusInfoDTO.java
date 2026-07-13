package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto;

import java.time.LocalDate;
import java.time.Year;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityPerformanceDataStatusInfoDTO {

	private TargetPeriodType targetPeriodType;

    private String targetPeriodName;
    
    private Year targetPeriodYear;
    
    private boolean variationIndicator;

    private boolean locked;

    private int reportVersion;
    
    private LocalDate submissionDate;

    private boolean lockEditable;
    
    private boolean variationIndicatorEditable; 
}
