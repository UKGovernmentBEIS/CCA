package uk.gov.cca.api.facility.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchCriteria;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResultInfoDTO;
import uk.gov.cca.api.facility.repository.FacilityDataRepository;
import uk.gov.cca.api.facility.transform.FacilitySearchResultsMapper;

@Service
@RequiredArgsConstructor
public class FacilitySearchService {

    private final FacilityDataRepository facilityDataRepository;
    private final FacilitySearchResultsMapper facilitySearchResultsMapper;

    public Page<FacilitySearchResultInfoDTO> searchFacilities(Long accountId, FacilitySearchCriteria facilitySearchCriteria) {

        final String term = getSearchTerm(facilitySearchCriteria);
        final Pageable pageable = getPageable(facilitySearchCriteria);

        Page<FacilityData> results =
                facilityDataRepository.searchFacilityDataByAccountIdAndTerm(pageable, accountId, term);

        return results.map(facilitySearchResultsMapper::toFacilitySearchResultInfo);
    }

    private String getSearchTerm(FacilitySearchCriteria facilitySearchCriteria) {
        return facilitySearchCriteria.getTerm() != null ? facilitySearchCriteria.getTerm().toLowerCase().trim() : "";
    }

    private Pageable getPageable(FacilitySearchCriteria facilitySearchCriteria) {
        return PageRequest.of(
                facilitySearchCriteria.getPaging().getPageNumber(),
                facilitySearchCriteria.getPaging().getPageSize(),
                Sort.by("facilityBusinessId"));
    }


}
