package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.underlyingagreement.utils.UnderlyingAgreementContainerUtil;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityReferenceData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.transform.PerformanceDataFacilityReferenceDataMapper;
import uk.gov.netz.api.common.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Year;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityReferenceDataService {

    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final TargetPeriodService targetPeriodService;
    private static final PerformanceDataFacilityReferenceDataMapper MAPPER = Mappers.getMapper(PerformanceDataFacilityReferenceDataMapper.class);

    public PerformanceDataFacilityBaselineAndTargets getFacilityOriginalBaselineAndTargets(Long accountId, String facilityBusinessId, Year targetPeriodYear) {
        final UnderlyingAgreementContainer una = underlyingAgreementQueryService
                .getUnderlyingAgreementContainerByAccountId(accountId);

        final Cca3FacilityBaselineAndTargets originalBaselineData = UnderlyingAgreementContainerUtil
                .getFacilityBaselineAndTargets(facilityBusinessId, una)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return MAPPER.toPerformanceDataFacilityBaselineAndTargets(originalBaselineData, targetPeriodYear);
    }

    public PerformanceDataFacilityReferenceData getReferenceData(Long accountId, String facilityBusinessId, Year targetPeriodYear,
                                                                 TargetPeriodType targetPeriodType) {
        final TargetPeriodDetailsDTO targetPeriod = targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);

        return PerformanceDataFacilityReferenceData.builder()
                .baselineAndTargets(getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear))
                .tpMultiplier(BigDecimal.valueOf(targetPeriod.getTargetPeriodYearsContainer().getTargetPeriodYears().size()))
                .build();
    }
}
