package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.utils.PerformanceAccountTemplateUtils;

@Service
@RequiredArgsConstructor
public class TargetPeriodEligibleAccountsQueryService {

	private final TargetUnitAccountQueryService targetUnitAccountQueryService;

	@Transactional(readOnly = true)
	public List<TargetUnitAccountBusinessInfoDTO> getEligibleAccountsForPerformanceAccountTemplateReporting(
			Long sectorAssociationId, Year targetPeriodYear) {
		final Year nextTargetPeriodYear = targetPeriodYear.plusYears(1);
		final LocalDateTime acceptedDate = targetPeriodYear.atMonth(Month.DECEMBER).atDay(31).atTime(LocalTime.MAX);
		final LocalDateTime terminatedDateFrom = nextTargetPeriodYear.atMonth(Month.JANUARY).atDay(1).atTime(LocalTime.MIN);
		final LocalDateTime terminatedDateTo = PerformanceAccountTemplateUtils.TERMINATED_END_DATE_FOR_ELIGIBLE_ACCOUNTS_MONTH_DAY
				.atYear(nextTargetPeriodYear.getValue()).atTime(LocalTime.MIN);

		return targetUnitAccountQueryService
				.findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedDuringActivatedYearOrTerminatedBetween(
						sectorAssociationId, acceptedDate, terminatedDateFrom, terminatedDateTo);
	}
}
