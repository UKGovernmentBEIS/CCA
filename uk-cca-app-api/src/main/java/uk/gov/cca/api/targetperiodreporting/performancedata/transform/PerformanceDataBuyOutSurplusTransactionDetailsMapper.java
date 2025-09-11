package uk.gov.cca.api.targetperiodreporting.performancedata.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataDetailsInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PerformanceDataBuyOutSurplusTransactionDetailsMapper {
    
    @Mapping(target = "accountBusinessId", source = "account.businessId")
    @Mapping(target = "operatorName", source = "account.name")
    @Mapping(target = "targetPeriodType", source = "performanceData.targetPeriod.businessId")
    @Mapping(target = "targetPeriodResultType", source = "performanceData.performanceOutcome")
    @Mapping(target = "reportVersion", source = "performanceData.reportVersion")
    @Mapping(target = "submissionType", source = "performanceData.submissionType")
    @Mapping(target = "priBuyOutCarbon", source = "performanceData.data.surplusBuyOutDetermination.priBuyOutCarbon")
    PerformanceDataDetailsInfoDTO toPerformanceDataDetailsInfoDTO(
            PerformanceDataEntity performanceData,
            TargetUnitAccountBusinessInfoDTO account);
}
