package uk.gov.cca.api.targetperiodreporting.performancedata.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataDetailsInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.PerformanceDataRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.transform.PerformanceDataBuyOutSurplusTransactionDetailsMapper;
import uk.gov.netz.api.common.exception.BusinessException;


import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PerformanceDataQueryService {
    
    private final PerformanceDataRepository performanceDataRepository;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private static final PerformanceDataBuyOutSurplusTransactionDetailsMapper PERFORMANCE_DATA_BUY_OUT_SURPLUS_TRANSACTION_DETAILS_MAPPER = Mappers.getMapper(PerformanceDataBuyOutSurplusTransactionDetailsMapper.class);
    
    public PerformanceDataDetailsInfoDTO getPerformanceDataBuyOutSurplusTransactionDetails(Long id) {
        
        PerformanceDataEntity performanceData =
                performanceDataRepository.findPerformanceDataEntityWithTargetPeriodById(id)
                        .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        
        TargetUnitAccountBusinessInfoDTO account =
                targetUnitAccountQueryService.getTargetUnitAccountBusinessInfoDTO(performanceData.getAccountId());
        
        return PERFORMANCE_DATA_BUY_OUT_SURPLUS_TRANSACTION_DETAILS_MAPPER
                .toPerformanceDataDetailsInfoDTO(performanceData, account);
    }
}
