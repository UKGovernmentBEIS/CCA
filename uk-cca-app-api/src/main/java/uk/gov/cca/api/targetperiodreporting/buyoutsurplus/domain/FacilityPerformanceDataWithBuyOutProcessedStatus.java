package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityPerformanceDataWithBuyOutProcessedStatus {
	
	public FacilityPerformanceDataWithBuyOutProcessedStatus(
	        Long facilityId,
			String facilityBusinessId,
	        String siteName,
	        PerformanceDataFacilityEntity performanceData,
	        boolean locked,
	        boolean processed) {

		this.facilityId = facilityId;
		this.facilityBusinessId = facilityBusinessId;
		this.siteName = siteName;
		this.targetPeriodId = performanceData.getTargetPeriod().getId();
	    this.targetPeriodType = performanceData.getTargetPeriod().getBusinessId();
	    this.performanceDataId = performanceData.getId();
	    this.submissionDate = performanceData.getSubmissionDate();
	    this.submissionType = performanceData.getSubmissionType();
	    this.reportVersion = performanceData.getReportVersion();
	    this.surplusGained = performanceData.getData().getCalculatedResults().getSurplusGained();
	    this.buyOutRequired = performanceData.getData().getCalculatedResults().getBuyOutRequired();
	    this.performanceOutcome = performanceData.getPerformanceOutcome();
	    this.locked = locked;
	    this.processed = processed;
	}

	private Long facilityId;
	private String facilityBusinessId;
	private String siteName;
    private Long targetPeriodId;
	private TargetPeriodType targetPeriodType;
	private Long performanceDataId;
	private int reportVersion;
    private PerformanceDataSubmissionType submissionType;
    private PerformanceDataFacilityTargetPeriodResultType performanceOutcome;
    private BigDecimal surplusGained;
    private BigDecimal buyOutRequired;
    private LocalDateTime submissionDate;
    private boolean locked;
    private boolean processed;
}
