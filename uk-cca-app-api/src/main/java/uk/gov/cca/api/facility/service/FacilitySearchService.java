package uk.gov.cca.api.facility.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchCriteria;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResultInfoDTO;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResults;
import uk.gov.cca.api.facility.repository.FacilityDataRepository;
import uk.gov.cca.api.facility.transform.FacilitySearchResultsMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilitySearchService {

    private final FacilityDataRepository facilityDataRepository;
    private final FacilitySearchResultsMapper facilitySearchResultsMapper;

    public FacilitySearchResults searchFacilities(Long accountId, FacilitySearchCriteria facilitySearchCriteria) {

        final String term = getSearchTerm(facilitySearchCriteria);
        final Pageable pageable = getPageable(facilitySearchCriteria);

        Page<FacilityData> results =
                facilityDataRepository.searchFacilityDataByAccountIdAndTerm(pageable, accountId, term);

        if (ObjectUtils.isEmpty(results)) {
            return FacilitySearchResults.emptyFacilitySearchResults();
        }

        List<FacilitySearchResultInfoDTO> facilities = results.stream()
                .map(facilitySearchResultsMapper::toFacilitySearchResultInfo)
                .collect(Collectors.toList());

        return FacilitySearchResults.builder()
                .facilities(facilities)
                .total(results.getTotalElements())
                .build();
    }

    private String getSearchTerm(FacilitySearchCriteria facilitySearchCriteria) {
        return facilitySearchCriteria.getTerm() != null ? facilitySearchCriteria.getTerm().toLowerCase().trim() : "";
    }

    private Pageable getPageable(FacilitySearchCriteria facilitySearchCriteria) {
        return PageRequest.of(
                facilitySearchCriteria.getPaging().getPageNumber().intValue(),
                facilitySearchCriteria.getPaging().getPageSize().intValue(),
                Sort.by("facilityId"));
    }


}
