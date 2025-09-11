package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto;

import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SectorPerformanceAccountTemplateDataReportListDTO {

	private List<SectorPerformanceAccountTemplateDataReportItemDTO> items;
	private Long total;

	public static SectorPerformanceAccountTemplateDataReportListDTO emptyList() {
		return SectorPerformanceAccountTemplateDataReportListDTO.builder().items(Collections.emptyList()).total(0L)
				.build();
	}

}
