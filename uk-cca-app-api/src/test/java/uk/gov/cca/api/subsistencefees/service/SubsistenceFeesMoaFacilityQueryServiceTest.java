package uk.gov.cca.api.subsistencefees.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

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

import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaFacilityRepository;
import uk.gov.netz.api.common.domain.PagingRequest;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaFacilityQueryServiceTest {

	@InjectMocks
    private SubsistenceFeesMoaFacilityQueryService subsistenceFeesMoaFacilityQueryService;

    @Mock
    private SubsistenceFeesMoaFacilityRepository subsistenceFeesMoaFacilityRepository;

    @Test
    void getSubsistenceFeesMoaFacilities() {
    	final int page = 0;
        final int pageSize = 30;
        final LocalDate date = LocalDate.now();
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        SubsistenceFeesSearchCriteria criteria = SubsistenceFeesSearchCriteria.builder()
        		.paging(pagingRequest)
        		.build();
        final Pageable pageable = getPageable(criteria);
        final SubsistenceFeesMoaFacilitySearchResultInfoDTO resultInfoDto =
                new SubsistenceFeesMoaFacilitySearchResultInfoDTO(1L, "ADS-0001", "name", FacilityPaymentStatus.IN_PROGRESS, date, false);
        final SubsistenceFeesMoaFacilitySearchResults expectedResults = SubsistenceFeesMoaFacilitySearchResults.builder()
                .subsistenceFeesMoaFacilities(List.of(resultInfoDto))
                .total(1L)
                .build();
        final Page<SubsistenceFeesMoaFacilitySearchResultInfoDTO> pageResult = new PageImpl<>(List.of(resultInfoDto));

        when(subsistenceFeesMoaFacilityRepository.findBySearchCriteria(pageable, 1L, "", null)).thenReturn(pageResult);

        // invoke
        final SubsistenceFeesMoaFacilitySearchResults results = 
        		subsistenceFeesMoaFacilityQueryService.getSubsistenceFeesMoaFacilities(1L, criteria);

        // verify
        verify(subsistenceFeesMoaFacilityRepository, times(1)).findBySearchCriteria(pageable, 1L, "", null);
        assertThat(results).isEqualTo(expectedResults);
    }
    
    private Pageable getPageable(SubsistenceFeesSearchCriteria criteria) {
        return PageRequest.of(
                criteria.getPaging().getPageNumber(),
                criteria.getPaging().getPageSize(),
                Sort.by("fd.facilityBusinessId"));
    }
}
