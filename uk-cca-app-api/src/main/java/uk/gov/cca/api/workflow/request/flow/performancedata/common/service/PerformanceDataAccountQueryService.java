package uk.gov.cca.api.workflow.request.flow.performancedata.common.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;

@Service
@RequiredArgsConstructor
public class PerformanceDataAccountQueryService {

    private final TargetPeriodService targetPeriodService;
    private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

    @Transactional(readOnly = true)
    public List<TargetUnitAccountBusinessInfoDTO> getCandidateAccountsForPerformanceDataReportingBySector(
            Long sectorAssociationId, TargetPeriodType targetPeriodType) {
        TargetPeriodInfoDTO targetPeriod = targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType);

        return accountPerformanceDataStatusQueryService.getAccountsForPerformanceDataReportingBySector(
                sectorAssociationId, targetPeriod.getId());
    }
}
