package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PerformanceAccountTemplateDataStatus {

	SUBMITTED("Submitted"), 
	OUTSTANDING("Outstanding")
	;

    private final String description;
    
}
