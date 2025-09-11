package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;

@ExtendWith(MockitoExtension.class)
class TargetPeriodEligibleAccountsQueryServiceTest {

	
	@InjectMocks
    private TargetPeriodEligibleAccountsQueryService cut;
	
	@Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;
	
	@Test
	void getEligibleAccountsForPerformanceAccountTemplateReporting() {
		Long sectorAssociationId = 1L;
		
		Year targetPeriodYear = Year.of(2024);
		
		List<TargetUnitAccountBusinessInfoDTO> accounts = List.of(
				TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("1").build()
				);
		
		when(targetUnitAccountQueryService
				.findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedDuringActivatedYearOrTerminatedBetween(
						sectorAssociationId, 
						targetPeriodYear.atMonth(Month.DECEMBER).atDay(31).atTime(LocalTime.MAX),
						targetPeriodYear.plusYears(1).atMonth(Month.JANUARY).atDay(1).atTime(LocalTime.MIN),
						MonthDay.of(5, 1).atYear(targetPeriodYear.getValue() + 1).atTime(LocalTime.MIN)
						))
				.thenReturn(accounts);	
		
		var result = cut.getEligibleAccountsForPerformanceAccountTemplateReporting(sectorAssociationId, targetPeriodYear);
		
		assertThat(result).containsExactlyElementsOf(accounts);
		
		verify(targetUnitAccountQueryService, times(1)).findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedDuringActivatedYearOrTerminatedBetween(sectorAssociationId, 
				targetPeriodYear.atMonth(Month.DECEMBER).atDay(31).atTime(LocalTime.MAX),
				targetPeriodYear.plusYears(1).atMonth(Month.JANUARY).atDay(1).atTime(LocalTime.MIN),
				MonthDay.of(5, 1).atYear(targetPeriodYear.getValue() + 1).atTime(LocalTime.MIN)
				);
		
	}
}
