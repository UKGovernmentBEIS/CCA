package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PerformanceAccountTemplateDataSubmissionType {

	INTERIM("Interim"), 
	FINAL("Final");

    private final String description;
    
}
