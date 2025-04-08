package uk.gov.cca.api.workflow.request.flow.performancedata.common.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;

@Service
@RequiredArgsConstructor
public class PerformanceDataAccountQueryService {

    private final TargetPeriodService targetPeriodService;
    private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

    @Transactional(readOnly = true)
    public List<TargetUnitAccountBusinessInfoDTO> getCandidateAccountsForPerformanceDataReportingBySector(
            Long sectorAssociationId, TargetPeriodType targetPeriodType, PerformanceDataSubmissionType submissionType) {

        TargetPeriodDTO targetPeriodDTO = targetPeriodService.getTargetPeriodByBusinessId(targetPeriodType);

        return accountPerformanceDataStatusQueryService.getAccountsForPerformanceDataReportingBySector(
                sectorAssociationId, targetPeriodDTO.getId(), submissionType);
    }
}
