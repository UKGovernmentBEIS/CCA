package uk.gov.cca.api.web.orchestrator.facility.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchCriteria;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResultInfoDTO;
import uk.gov.cca.api.facility.service.FacilitySearchService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityCertificationSearchResultInfoDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilitySearchResults;
import uk.gov.cca.api.web.orchestrator.facility.transform.FacilityInfoMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilitySearchServiceOrchestrator {

    private final FacilitySearchService facilitySearchService;
    private final FacilityCertificationService facilityCertificationService;
    private final CertificationPeriodService certificationPeriodService;

    private static final FacilityInfoMapper FACILITY_INFO_MAPPER = Mappers.getMapper(FacilityInfoMapper.class);

    public FacilitySearchResults searchFacilities(Long accountId, FacilitySearchCriteria facilitySearchCriteria) {

        Page<FacilitySearchResultInfoDTO> results = facilitySearchService.searchFacilities(accountId, facilitySearchCriteria);

        if (ObjectUtils.isEmpty(results)) {
            return FacilitySearchResults.emptyFacilitySearchResults();
        }

        Set<Long> facilityIds = results.stream()
                .map(FacilitySearchResultInfoDTO::getId)
                .collect(Collectors.toSet());

        Map<Long, FacilityCertificationStatus> certificationStatusByFacilityId = certificationPeriodService.getCurrentCertificationPeriodOptional()
                .map(certificationPeriod -> facilityCertificationService.getFacilityCertifications(facilityIds, certificationPeriod.getId()))
                .orElse(Map.of());

        List<FacilityCertificationSearchResultInfoDTO> facilities = results
                .stream()
                .map(facilityInfo -> FACILITY_INFO_MAPPER.toFacilityCertificationSearchResultInfo(facilityInfo, certificationStatusByFacilityId.get(facilityInfo.getId())))
                .toList();

        return FacilitySearchResults.builder()
                .facilities(facilities)
                .total(results.getTotalElements())
                .build();
    }

}
