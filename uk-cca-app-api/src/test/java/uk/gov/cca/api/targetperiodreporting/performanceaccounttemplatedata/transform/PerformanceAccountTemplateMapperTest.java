package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Year;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportInfoDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

class PerformanceAccountTemplateMapperTest {

	private final PerformanceAccountTemplateMapper cut = Mappers.getMapper(PerformanceAccountTemplateMapper.class);

	@Test
	void toReportInfoDTO() {
		PerformanceAccountTemplateDataEntity from = PerformanceAccountTemplateDataEntity.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriod(TargetPeriod.builder().businessId(TargetPeriodType.TP6).name("test").build())
				.build();
		
		var result = cut.toReportInfoDTO(from);
		
		assertThat(result).isEqualTo(AccountPerformanceAccountTemplateDataReportInfoDTO.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriodType(TargetPeriodType.TP6)
				.targetPeriodName("test")
				.build());
	}
	
	@Test
	void toReportDetailsDTO() {
		PerformanceAccountTemplateDataContainer patContainer = PerformanceAccountTemplateDataContainer.builder()
				.file(FileInfoDTO.builder().name("filename").uuid("uuid").build())
				.build();
		
		PerformanceAccountTemplateDataEntity patEntity = PerformanceAccountTemplateDataEntity.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriod(TargetPeriod.builder().businessId(TargetPeriodType.TP6).name("test").build())
				.data(patContainer)
				.build();
		
		var result = cut.toReportDetailsDTO(patEntity);
		
		assertThat(result).isEqualTo(AccountPerformanceAccountTemplateDataReportDetailsDTO.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriodType(TargetPeriodType.TP6)
				.targetPeriodName("test")
				.data(patContainer)
				.build());
	}
}
