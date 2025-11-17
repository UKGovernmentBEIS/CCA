package uk.gov.cca.api.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchCriteria;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResultInfoDTO;
import uk.gov.cca.api.facility.repository.FacilityDataRepository;
import uk.gov.cca.api.facility.transform.FacilitySearchResultsMapper;
import uk.gov.netz.api.common.domain.PagingRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FacilitySearchServiceTest {

    @InjectMocks
    private FacilitySearchService facilitySearchService;

    @Mock
    private FacilityDataRepository facilityDataRepository;

    @Mock
    private FacilitySearchResultsMapper facilitySearchResultsMapper;

    @Test
    void searchFacilities() {
        final Long accountId = 1L;
        final String facilityBusinessId = "SA-F00001";
        final String siteName = "site1";
        final String term = "SA-";
        final int pageNum = 0;
        final int pageSize = 30;

        final FacilitySearchResultInfoDTO facilitySearchResultInfoDTO =
                new FacilitySearchResultInfoDTO(1L, facilityBusinessId, siteName, null, FacilityDataStatus.LIVE);

        final FacilitySearchCriteria facilitySearchCriteria = FacilitySearchCriteria.builder()
                .term(term)
                .paging(PagingRequest.builder().pageNumber(pageNum).pageSize(pageSize).build())
                .build();

        final FacilityData facilityData = FacilityData.builder()
        		.id(1L)
                .facilityBusinessId(facilityBusinessId)
                .siteName(siteName)
                .build();

        final Page<FacilitySearchResultInfoDTO> expectedResults = new PageImpl<>(List.of(facilitySearchResultInfoDTO));

        final Page<FacilityData> page = new PageImpl<>(List.of(facilityData));

        final Pageable pageable = getPageable(facilitySearchCriteria);

        final String searchTerm = getSearchTerm(facilitySearchCriteria);

        when(facilityDataRepository.searchFacilityDataByAccountIdAndTerm(pageable, accountId, searchTerm)).thenReturn(page);
        when(facilitySearchResultsMapper.toFacilitySearchResultInfo(facilityData)).thenReturn(facilitySearchResultInfoDTO);

        // invoke
        final Page<FacilitySearchResultInfoDTO> results = facilitySearchService.searchFacilities(accountId, facilitySearchCriteria);

        // verify
        verify(facilityDataRepository, times(1)).searchFacilityDataByAccountIdAndTerm(pageable, accountId, searchTerm);
        verify(facilitySearchResultsMapper, times(1)).toFacilitySearchResultInfo(facilityData);
        assertThat(results).isEqualTo(expectedResults);
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
