package uk.gov.cca.api.targetperiodreporting.performancedatafacility.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataStatusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityStatusMapperTest {

	final PerformanceDataFacilityStatusMapper mapper = Mappers.getMapper(PerformanceDataFacilityStatusMapper.class);

    @Test
    void toFacilityPerformanceDataStatusInfoDTO_fields_editable() {
    	LocalDateTime submissionDate = LocalDateTime.now();
    	LocalDate secondaryReportingStartDate = LocalDate.now().minusDays(1);
    	PerformanceDataFacilityStatus entity = PerformanceDataFacilityStatus.builder()
    			.targetPeriod(TargetPeriod.builder()
    					.name("TP7 2027")
    					.businessId(TargetPeriodType.TP7).build())
    			.targetPeriodYear(Year.of(2027))
    			.lastPerformanceData(PerformanceDataFacilityEntity.builder()
    					.reportVersion(10)
    					.submissionDate(submissionDate)
    					.submissionType(PerformanceDataSubmissionType.PRIMARY)
    					.build())
    			.locked(true)
    			.build();

    	FacilityPerformanceDataStatusInfoDTO expected = FacilityPerformanceDataStatusInfoDTO.builder()
    			.targetPeriodName("TP7 2027")
    			.targetPeriodType(TargetPeriodType.TP7)
    			.targetPeriodYear(Year.of(2027))
    			.reportVersion(10)
    			.submissionDate(submissionDate.toLocalDate())
    			.locked(true)
    			.lockEditable(true)
    			.variationIndicator(false)
    			.variationIndicatorEditable(true)
    			.build();
        
    	FacilityPerformanceDataStatusInfoDTO result = mapper.
    			toFacilityPerformanceDataStatusInfoDTO(entity, secondaryReportingStartDate, "REGULATOR");

        assertThat(result).isEqualTo(expected);
    }
    
    @Test
    void toFacilityPerformanceDataStatusInfoDTO_fields_not_editable_sector_user() {
    	LocalDateTime submissionDate = LocalDateTime.now();
    	LocalDate secondaryReportingStartDate = LocalDate.now().minusDays(1);
    	PerformanceDataFacilityStatus entity = PerformanceDataFacilityStatus.builder()
    			.targetPeriod(TargetPeriod.builder()
    					.name("TP7 2027")
    					.businessId(TargetPeriodType.TP7).build())
    			.targetPeriodYear(Year.of(2027))
    			.lastPerformanceData(PerformanceDataFacilityEntity.builder()
    					.reportVersion(10)
    					.submissionDate(submissionDate)
    					.submissionType(PerformanceDataSubmissionType.PRIMARY)
    					.build())
    			.locked(true)
    			.variationIndicator(true)
    			.build();

    	FacilityPerformanceDataStatusInfoDTO expected = FacilityPerformanceDataStatusInfoDTO.builder()
    			.targetPeriodName("TP7 2027")
    			.targetPeriodType(TargetPeriodType.TP7)
    			.targetPeriodYear(Year.of(2027))
    			.reportVersion(10)
    			.submissionDate(submissionDate.toLocalDate())
    			.locked(true)
    			.lockEditable(false)
    			.variationIndicator(true)
    			.variationIndicatorEditable(false)
    			.build();
        
    	FacilityPerformanceDataStatusInfoDTO result = mapper.
    			toFacilityPerformanceDataStatusInfoDTO(entity, secondaryReportingStartDate, "SECTOR_USER");

        assertThat(result).isEqualTo(expected);
    }
    
    @Test
    void toFacilityPerformanceDataStatusInfoDTO_locked_not_editable_date() {
    	LocalDateTime submissionDate = LocalDateTime.now();
    	LocalDate secondaryReportingStartDate = LocalDate.now().plusDays(1);
    	PerformanceDataFacilityStatus entity = PerformanceDataFacilityStatus.builder()
    			.targetPeriod(TargetPeriod.builder()
    					.name("TP7 2027")
    					.businessId(TargetPeriodType.TP7).build())
    			.targetPeriodYear(Year.of(2027))
    			.lastPerformanceData(PerformanceDataFacilityEntity.builder()
    					.reportVersion(10)
    					.submissionDate(submissionDate)
    					.submissionType(PerformanceDataSubmissionType.PRIMARY)
    					.build())
    			.locked(true)
    			.variationIndicator(true)
    			.build();

    	FacilityPerformanceDataStatusInfoDTO expected = FacilityPerformanceDataStatusInfoDTO.builder()
    			.targetPeriodName("TP7 2027")
    			.targetPeriodType(TargetPeriodType.TP7)
    			.targetPeriodYear(Year.of(2027))
    			.reportVersion(10)
    			.submissionDate(submissionDate.toLocalDate())
    			.locked(true)
    			.lockEditable(false)
    			.variationIndicator(true)
    			.variationIndicatorEditable(true)
    			.build();
        
    	FacilityPerformanceDataStatusInfoDTO result = mapper.
    			toFacilityPerformanceDataStatusInfoDTO(entity, secondaryReportingStartDate, "REGULATOR");

        assertThat(result).isEqualTo(expected);
    }
}
