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
import uk.gov.cca.api.facilityaudit.service.FacilityAuditService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilitySearchResultExtendedDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilitySearchResults;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

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

	@Mock
	private FacilityAuditService facilityAuditService;

    @Test
    void searchFacilities_REGULATOR() {
        final Long accountId = 1L;
        final Long currentCertificationPeriodId = 2L;
        final String facilityBusinessId = "SA-F00001";
        final String siteName = "site1";
        final String term = "SA-";
        final int pageNum = 0;
        final int pageSize = 30;
        final long certificationPeriodId = 2L;
	    final AppUser user = AppUser.builder().roleType(REGULATOR).build();

        final CertificationPeriodInfoDTO certificationPeriod = CertificationPeriodInfoDTO.builder()
                .id(certificationPeriodId)
                .build();

        final FacilitySearchResultInfoDTO facilitySearchResultInfoDTO =
                new FacilitySearchResultInfoDTO(1L, facilityBusinessId, siteName, null, FacilityDataStatus.LIVE);

        final FacilitySearchCriteria facilitySearchCriteria = FacilitySearchCriteria.builder()
                .term(term)
                .paging(PagingRequest.builder().pageNumber(pageNum).pageSize(pageSize).build())
                .build();

        final Page<FacilitySearchResultInfoDTO> page = new PageImpl<>(List.of(facilitySearchResultInfoDTO));

        FacilitySearchResultExtendedDTO facilitySearchResultExtendedDTO =
                new FacilitySearchResultExtendedDTO(1L, facilityBusinessId, siteName, null, FacilityDataStatus.LIVE, FacilityCertificationStatus.CERTIFIED, true);

        FacilitySearchResults searchResultsDTO = FacilitySearchResults.builder()
                .facilities(List.of(facilitySearchResultExtendedDTO))
                .total(1L)
                .build();

        when(facilitySearchService.searchFacilities(accountId, facilitySearchCriteria)).thenReturn(page);
        when(certificationPeriodService.getCurrentCertificationPeriodOptional()).thenReturn(Optional.ofNullable(certificationPeriod));
        when(facilityCertificationService.getFacilityCertifications(Set.of(1L), currentCertificationPeriodId))
                .thenReturn(Map.of(1L, FacilityCertificationStatus.CERTIFIED));
		when(facilityAuditService.getAuditRequiredFacilityIds(Set.of(1L)))
				.thenReturn(Set.of(1L));

        // invoke
        FacilitySearchResults results =
                facilitySearchServiceOrchestrator.searchFacilities(accountId, user, facilitySearchCriteria);

        // verify
        verify(facilitySearchService, times(1)).searchFacilities(accountId, facilitySearchCriteria);
        verify(certificationPeriodService, times(1)).getCurrentCertificationPeriodOptional();
        verify(facilityCertificationService, times(1)).getFacilityCertifications(Set.of(1L), currentCertificationPeriodId);
		verify(facilityAuditService, times(1)).getAuditRequiredFacilityIds(Set.of(1L));
        assertThat(results).isEqualTo(searchResultsDTO);
    }

	@Test
	void searchFacilities_OPERATOR() {
		final Long accountId = 1L;
		final Long currentCertificationPeriodId = 2L;
		final String facilityBusinessId = "SA-F00001";
		final String siteName = "site1";
		final String term = "SA-";
		final int pageNum = 0;
		final int pageSize = 30;
		final long certificationPeriodId = 2L;
		final AppUser user = AppUser.builder().roleType(OPERATOR).build();

		final CertificationPeriodInfoDTO certificationPeriod = CertificationPeriodInfoDTO.builder()
				.id(certificationPeriodId)
				.build();

		final FacilitySearchResultInfoDTO facilitySearchResultInfoDTO =
				new FacilitySearchResultInfoDTO(1L, facilityBusinessId, siteName, null, FacilityDataStatus.LIVE);

		final FacilitySearchCriteria facilitySearchCriteria = FacilitySearchCriteria.builder()
				.term(term)
				.paging(PagingRequest.builder().pageNumber(pageNum).pageSize(pageSize).build())
				.build();

		final Page<FacilitySearchResultInfoDTO> page = new PageImpl<>(List.of(facilitySearchResultInfoDTO));

		FacilitySearchResultExtendedDTO facilitySearchResultExtendedDTO =
				new FacilitySearchResultExtendedDTO(1L, facilityBusinessId, siteName, null, FacilityDataStatus.LIVE, FacilityCertificationStatus.CERTIFIED, true);

		FacilitySearchResults searchResultsDTO = FacilitySearchResults.builder()
				.facilities(List.of(facilitySearchResultExtendedDTO))
				.total(1L)
				.build();

		when(facilitySearchService.searchFacilities(accountId, facilitySearchCriteria)).thenReturn(page);
		when(certificationPeriodService.getCurrentCertificationPeriodOptional()).thenReturn(Optional.ofNullable(certificationPeriod));
		when(facilityCertificationService.getFacilityCertifications(Set.of(1L), currentCertificationPeriodId))
				.thenReturn(Map.of(1L, FacilityCertificationStatus.CERTIFIED));
		when(facilityAuditService.getAuditRequiredFacilityIds(Set.of(1L)))
				.thenReturn(Set.of(1L));

		// invoke
		FacilitySearchResults results =
				facilitySearchServiceOrchestrator.searchFacilities(accountId, user, facilitySearchCriteria);

		// verify
		verify(facilitySearchService, times(1)).searchFacilities(accountId, facilitySearchCriteria);
		verify(certificationPeriodService, times(1)).getCurrentCertificationPeriodOptional();
		verify(facilityCertificationService, times(1)).getFacilityCertifications(Set.of(1L), currentCertificationPeriodId);
		verify(facilityAuditService, times(1)).getAuditRequiredFacilityIds(Set.of(1L));
		assertThat(results).isEqualTo(searchResultsDTO);
	}

	@Test
	void searchFacilities_SECTOR_USER() {
		final Long accountId = 1L;
		final Long currentCertificationPeriodId = 2L;
		final String facilityBusinessId = "SA-F00001";
		final String siteName = "site1";
		final String term = "SA-";
		final int pageNum = 0;
		final int pageSize = 30;
		final long certificationPeriodId = 2L;
		final AppUser user = AppUser.builder().roleType(SECTOR_USER).build();

		final CertificationPeriodInfoDTO certificationPeriod = CertificationPeriodInfoDTO.builder()
				.id(certificationPeriodId)
				.build();

		final FacilitySearchResultInfoDTO facilitySearchResultInfoDTO =
				new FacilitySearchResultInfoDTO(1L, facilityBusinessId, siteName, null, FacilityDataStatus.LIVE);

		final FacilitySearchCriteria facilitySearchCriteria = FacilitySearchCriteria.builder()
				.term(term)
				.paging(PagingRequest.builder().pageNumber(pageNum).pageSize(pageSize).build())
				.build();

		final Page<FacilitySearchResultInfoDTO> page = new PageImpl<>(List.of(facilitySearchResultInfoDTO));

		FacilitySearchResultExtendedDTO facilitySearchResultExtendedDTO =
				new FacilitySearchResultExtendedDTO(1L, facilityBusinessId, siteName, null, FacilityDataStatus.LIVE, FacilityCertificationStatus.CERTIFIED, null);

		FacilitySearchResults searchResultsDTO = FacilitySearchResults.builder()
				.facilities(List.of(facilitySearchResultExtendedDTO))
				.total(1L)
				.build();

		when(facilitySearchService.searchFacilities(accountId, facilitySearchCriteria)).thenReturn(page);
		when(certificationPeriodService.getCurrentCertificationPeriodOptional()).thenReturn(Optional.ofNullable(certificationPeriod));
		when(facilityCertificationService.getFacilityCertifications(Set.of(1L), currentCertificationPeriodId))
				.thenReturn(Map.of(1L, FacilityCertificationStatus.CERTIFIED));

		// invoke
		FacilitySearchResults results =
				facilitySearchServiceOrchestrator.searchFacilities(accountId, user, facilitySearchCriteria);

		// verify
		verify(facilitySearchService, times(1)).searchFacilities(accountId, facilitySearchCriteria);
		verify(certificationPeriodService, times(1)).getCurrentCertificationPeriodOptional();
		verify(facilityCertificationService, times(1)).getFacilityCertifications(Set.of(1L), currentCertificationPeriodId);
		verify(facilityAuditService, times(0)).getAuditRequiredFacilityIds(Set.of(1L));
		assertThat(results).isEqualTo(searchResultsDTO);
	}

}
