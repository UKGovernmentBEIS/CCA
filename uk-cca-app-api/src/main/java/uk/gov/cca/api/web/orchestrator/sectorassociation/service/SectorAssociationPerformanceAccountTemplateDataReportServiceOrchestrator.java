package uk.gov.cca.api.web.orchestrator.sectorassociation.service;

import java.time.Year;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataQueryService;

@Service
@RequiredArgsConstructor
public class SectorAssociationPerformanceAccountTemplateDataReportServiceOrchestrator {
	
	private final PerformanceAccountTemplateDataQueryService performanceAccountTemplateDataQueryService;

	public SectorPerformanceAccountTemplateDataReportListDTO getSectorPerformanceAccountTemplateDataReportListDTO(
			Long sectorAssociationId, SectorPerformanceAccountTemplateDataReportSearchCriteria criteria) {
		final int reportYear = 2024;
		final Year targetPeriodYear = Year.of(reportYear); //TODO make it configurable
		if(Year.now().getValue() > reportYear + 1) {
			throw new RuntimeException("cannot display pat reports");
		}
		
		return performanceAccountTemplateDataQueryService
				.getSectorAccountsDataReportList(sectorAssociationId, criteria, targetPeriodYear);
		
	}
}
