package uk.gov.cca.api.web.orchestrator.sectorassociation.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;

@Service
@RequiredArgsConstructor
public class SectorAssociationAccountPerformanceDataReportServiceOrchestrator {

	private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

	public SectorAccountPerformanceDataReportListDTO getSectorAccountPerformanceDataReportList(Long sectorAssociationId,
			SectorAccountPerformanceDataReportSearchCriteria criteria) {

		return accountPerformanceDataStatusQueryService.getSectorAccountPerformanceDataReportList(sectorAssociationId,
				criteria);
	}
}
