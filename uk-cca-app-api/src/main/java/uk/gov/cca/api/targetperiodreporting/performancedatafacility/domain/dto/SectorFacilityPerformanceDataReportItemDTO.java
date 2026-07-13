package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.utils.ConversionUtils;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorFacilityPerformanceDataReportItemDTO {
	
	public SectorFacilityPerformanceDataReportItemDTO(
	        Long facilityId,
	        String facilityBusinessId,
	        String siteName,
	        Long accountId) {

	    this(facilityId, facilityBusinessId, siteName, accountId, null, 0, PerformanceDataFacilityTargetPeriodResultType.OUTSTANDING,
    		null, null, null, null, null, null, null, null ,null, null, null, null, null);
	}
	
	private Long facilityId;
	private String facilityBusinessId;
	private String siteName;
	private Long accountId;
	private LocalDateTime submissionDate;
	private Integer reportVersion;
	private PerformanceDataFacilityTargetPeriodResultType reportStatus;
	private PerformanceDataSubmissionType submissionType;
	private Boolean locked;
	private Boolean variationIndicator;
	// Data
	private Boolean atLeastSeventyPercentEnergyUsed;
	@JsonIgnore
	private String actualImprovementStr;
	@JsonIgnore
	private String actualEnergyCarbonStr;
	@JsonIgnore
	private String targetEnergyCarbonStr;
	@JsonIgnore
	private String energyCarbonDifferenceStr;
	@JsonIgnore
	private String actualCo2EmissionsStr;
	@JsonIgnore
	private String targetCo2EmissionsStr;
	@JsonIgnore
	private String co2EmissionsDifferenceStr;
	@JsonIgnore
	private String buyOutRequiredStr;
	@JsonIgnore
	private String surplusGainedStr;
	
	@JsonProperty("actualImprovement")
	public BigDecimal getActualImprovement() {
		return ConversionUtils.toBigDecimal(actualImprovementStr);
	}
	@JsonProperty("actualEnergyCarbon")
	public BigDecimal getActualEnergyCarbon() {
		return ConversionUtils.toBigDecimal(actualEnergyCarbonStr);
	}
	@JsonProperty("targetEnergyCarbon")
	public BigDecimal getTargetEnergyCarbon() {
		return ConversionUtils.toBigDecimal(targetEnergyCarbonStr);
	}
	@JsonProperty("energyCarbonDifference")
	public BigDecimal getEnergyCarbonDifference() {
		return ConversionUtils.toBigDecimal(energyCarbonDifferenceStr);
	}
	@JsonProperty("actualCo2Emissions")
	public BigDecimal getActualCo2Emissions() {
		return ConversionUtils.toBigDecimal(actualCo2EmissionsStr);
	}
	@JsonProperty("targetCo2Emissions")
	public BigDecimal getTargetCo2Emissions() {
		return ConversionUtils.toBigDecimal(targetCo2EmissionsStr);
	}
	@JsonProperty("co2EmissionsDifference")
	public BigDecimal getCo2EmissionsDifference() {
		return ConversionUtils.toBigDecimal(co2EmissionsDifferenceStr);
	}
	@JsonProperty("buyOutRequired")
	public BigDecimal getBuyOutRequired() {
		return ConversionUtils.toBigDecimal(buyOutRequiredStr);
	}
	@JsonProperty("surplusGained")
	public BigDecimal getSurplusGained() {
		return ConversionUtils.toBigDecimal(surplusGainedStr);
	}

}
