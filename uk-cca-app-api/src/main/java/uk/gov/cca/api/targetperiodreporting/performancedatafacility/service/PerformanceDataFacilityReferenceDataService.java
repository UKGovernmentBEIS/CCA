package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityReferenceData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.transform.PerformanceDataFacilityReferenceDataMapper;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.underlyingagreement.utils.UnderlyingAgreementContainerUtil;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.Year;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityReferenceDataService {

    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private static final PerformanceDataFacilityReferenceDataMapper MAPPER = Mappers.getMapper(PerformanceDataFacilityReferenceDataMapper.class);

    public PerformanceDataFacilityBaselineAndTargets getFacilityOriginalBaselineAndTargets(Long accountId, String facilityBusinessId, Year targetPeriodYear) {
        final UnderlyingAgreementContainer una = underlyingAgreementQueryService
                .getUnderlyingAgreementContainerByAccountId(accountId);

        return getFacilityOriginalBaselineAndTargets(facilityBusinessId, targetPeriodYear, una);
    }

    public PerformanceDataFacilityBaselineAndTargets getFacilityOriginalBaselineAndTargets(String facilityBusinessId, Year targetPeriodYear,
                                                                                           final UnderlyingAgreementContainer una) {
        final Cca3FacilityBaselineAndTargets originalBaselineData = UnderlyingAgreementContainerUtil
                .getFacilityBaselineAndTargets(facilityBusinessId, una)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return MAPPER.toPerformanceDataFacilityBaselineAndTargets(originalBaselineData, targetPeriodYear);
    }

    public PerformanceDataFacilityReferenceData getReferenceData(Long accountId, String facilityBusinessId, Year targetPeriodYear) {
        return PerformanceDataFacilityReferenceData.builder()
                .baselineAndTargets(getFacilityOriginalBaselineAndTargets(accountId, facilityBusinessId, targetPeriodYear))
                .build();
    }
}
