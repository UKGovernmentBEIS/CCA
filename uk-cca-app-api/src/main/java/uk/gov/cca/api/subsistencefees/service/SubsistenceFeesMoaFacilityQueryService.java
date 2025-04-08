package uk.gov.cca.api.subsistencefees.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaFacilityRepository;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResults;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesMoaFacilityQueryService {
	
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

    private String getSearchTerm(SubsistenceFeesSearchCriteria criteria) {
        return criteria.getTerm() != null ? criteria.getTerm().toLowerCase().trim() : "";
    }

    private Pageable getPageable(SubsistenceFeesSearchCriteria criteria) {
        return PageRequest.of(
                criteria.getPaging().getPageNumber().intValue(),
                criteria.getPaging().getPageSize().intValue());
    }

}
