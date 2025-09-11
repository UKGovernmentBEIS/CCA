package uk.gov.cca.api.web.orchestrator.sectorassociation.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.netz.api.common.domain.PagingRequest;

@ExtendWith(MockitoExtension.class)
class SectorAssociationAccountPerformanceDataReportServiceOrchestratorTest {

	@Mock
	private AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

	@InjectMocks
	private SectorAssociationAccountPerformanceDataReportServiceOrchestrator orchestrator;

	@Test
	void testGetSectorAccountPerformanceDataReportList() {
		final long sectorAssociationId = 1L;
		final int page = 0;
		final int pageSize = 30;
		PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
		SectorAccountPerformanceDataReportSearchCriteria criteria = SectorAccountPerformanceDataReportSearchCriteria.builder()
				.paging(pagingRequest).targetPeriodType(TargetPeriodType.TP6).build();

		// Invoke
		orchestrator.getSectorAccountPerformanceDataReportList(sectorAssociationId, criteria);

		// Verify
		verify(accountPerformanceDataStatusQueryService, times(1))
				.getSectorAccountPerformanceDataReportList(sectorAssociationId, criteria);
	}

}
