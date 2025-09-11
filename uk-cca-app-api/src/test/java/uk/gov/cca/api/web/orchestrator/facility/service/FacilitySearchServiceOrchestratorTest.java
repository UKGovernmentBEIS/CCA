package uk.gov.cca.api.web.orchestrator.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchCriteria;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchResultInfoDTO;
import uk.gov.cca.api.facility.service.FacilitySearchService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityCertificationSearchResultInfoDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilitySearchResults;
import uk.gov.netz.api.common.domain.PagingRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilitySearchServiceOrchestratorTest {

    @InjectMocks
    private FacilitySearchServiceOrchestrator facilitySearchServiceOrchestrator;

    @Mock
    private FacilitySearchService facilitySearchService;

    @Mock
    private FacilityCertificationService facilityCertificationService;

    @Mock
    private CertificationPeriodService certificationPeriodService;

    @Test
    void searchFacilities() {
        final Long accountId = 1L;
        final Long currentCertificationPeriodId = 2L;
        final String facilityId = "SA-F00001";
        final String siteName = "site1";
        final String term = "SA-";
        final int pageNum = 0;
        final int pageSize = 30;
        final long certificationPeriodId = 2L;

        final CertificationPeriodInfoDTO certificationPeriod = CertificationPeriodInfoDTO.builder()
                .id(certificationPeriodId)
                .build();

        final FacilitySearchResultInfoDTO facilitySearchResultInfoDTO =
                new FacilitySearchResultInfoDTO(1L, facilityId, siteName, null, FacilityDataStatus.LIVE);

        final FacilitySearchCriteria facilitySearchCriteria = FacilitySearchCriteria.builder()
                .term(term)
                .paging(PagingRequest.builder().pageNumber(pageNum).pageSize(pageSize).build())
                .build();

        final Page<FacilitySearchResultInfoDTO> page = new PageImpl<>(List.of(facilitySearchResultInfoDTO));

        FacilityCertificationSearchResultInfoDTO facilityCertificationSearchResultInfoDTO =
                new FacilityCertificationSearchResultInfoDTO(facilityId, siteName, null, FacilityDataStatus.LIVE, FacilityCertificationStatus.CERTIFIED);

        FacilitySearchResults searchResultsDTO = FacilitySearchResults.builder()
                .facilities(List.of(facilityCertificationSearchResultInfoDTO))
                .total(1L)
                .build();

        when(facilitySearchService.searchFacilities(accountId, facilitySearchCriteria)).thenReturn(page);
        when(certificationPeriodService.getCurrentCertificationPeriodOptional()).thenReturn(Optional.ofNullable(certificationPeriod));
        when(facilityCertificationService.getFacilityCertifications(Set.of(1L), currentCertificationPeriodId))
                .thenReturn(Map.of(1L, FacilityCertificationStatus.CERTIFIED));

        // invoke
        FacilitySearchResults results =
                facilitySearchServiceOrchestrator.searchFacilities(accountId, facilitySearchCriteria);

        // verify
        verify(facilitySearchService, times(1)).searchFacilities(accountId, facilitySearchCriteria);
        verify(certificationPeriodService, times(1)).getCurrentCertificationPeriodOptional();
        verify(facilityCertificationService, times(1)).getFacilityCertifications(Set.of(1L), currentCertificationPeriodId);
        assertThat(results).isEqualTo(searchResultsDTO);
    }
}
