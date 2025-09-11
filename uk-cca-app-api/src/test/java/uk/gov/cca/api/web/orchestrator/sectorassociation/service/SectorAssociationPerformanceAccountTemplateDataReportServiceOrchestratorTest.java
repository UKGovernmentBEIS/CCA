package uk.gov.cca.api.web.orchestrator.sectorassociation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataQueryService;

@ExtendWith(MockitoExtension.class)
class SectorAssociationPerformanceAccountTemplateDataReportServiceOrchestratorTest {

	@InjectMocks
	private SectorAssociationPerformanceAccountTemplateDataReportServiceOrchestrator cut;
	
	@Mock
	private PerformanceAccountTemplateDataQueryService performanceAccountTemplateDataQueryService;

	@Test
	void getSectorPerformanceAccountTemplateDataReportListDTO() {
		Long sectorAssociationId = 1L;
		SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria
				.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.build();
		
		SectorPerformanceAccountTemplateDataReportListDTO listDTO = SectorPerformanceAccountTemplateDataReportListDTO.builder()
				.items(List.of(SectorPerformanceAccountTemplateDataReportItemDTO.builder()
						.operatorName("dfd")
						.build()))
				.total(1L)
				.build();
		
		when(performanceAccountTemplateDataQueryService.getSectorAccountsDataReportList(sectorAssociationId, criteria,
				Year.of(2024))).thenReturn(listDTO);
		
		var result = cut.getSectorPerformanceAccountTemplateDataReportListDTO(sectorAssociationId, criteria);
		
		assertThat(result).isEqualTo(listDTO);

		verify(performanceAccountTemplateDataQueryService, times(1))
				.getSectorAccountsDataReportList(sectorAssociationId, criteria,
						Year.of(2024));
	}
	
}
