package uk.gov.cca.api.subsistencefees.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SubsistenceFeesMoaFacilityAuthorityInfoProvider;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacility;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaFacilityRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesMoaFacilityQueryService implements SubsistenceFeesMoaFacilityAuthorityInfoProvider {

    private final SubsistenceFeesMoaFacilityRepository subsistenceFeesMoaFacilityRepository;

    public SubsistenceFeesMoaFacilitySearchResults getSubsistenceFeesMoaFacilities(Long moaTargetUnitId,
                                                                                   SubsistenceFeesSearchCriteria criteria) {

        final String term = getSearchTerm(criteria);
        final Pageable pageable = getPageable(criteria);

        Page<SubsistenceFeesMoaFacilitySearchResultInfoDTO> results = subsistenceFeesMoaFacilityRepository
                .findBySearchCriteria(pageable, moaTargetUnitId, term, criteria.getMarkFacilitiesStatus());

        if (ObjectUtils.isEmpty(results)) {
            return SubsistenceFeesMoaFacilitySearchResults.emptySubsistenceFeesFacilitiesSearchResults();
        }

        List<SubsistenceFeesMoaFacilitySearchResultInfoDTO> moaFacilities = results.stream()
                .collect(Collectors.toList());

        return SubsistenceFeesMoaFacilitySearchResults.builder()
                .subsistenceFeesMoaFacilities(moaFacilities)
                .total(results.getTotalElements())
                .build();
    }

    @Override
    public Long getAccountIdByMoaFacilityId(Long moaFacilityId) {
        SubsistenceFeesMoaFacility subsistenceFeesMoaFacility = subsistenceFeesMoaFacilityRepository.findWithMoaTargetUnit(moaFacilityId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return subsistenceFeesMoaFacility.getSubsistenceFeesMoaTargetUnit().getAccountId();
    }

    private String getSearchTerm(SubsistenceFeesSearchCriteria criteria) {
        return criteria.getTerm() != null ? criteria.getTerm().toLowerCase().trim() : "";
    }

    private Pageable getPageable(SubsistenceFeesSearchCriteria criteria) {
        return PageRequest.of(
                criteria.getPaging().getPageNumber(),
                criteria.getPaging().getPageSize(),
                Sort.by("fd.facilityId")
        		);
    }
}
