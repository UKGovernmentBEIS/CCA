package uk.gov.cca.api.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.repository.FacilityDataRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityDataQueryServiceTest {

    @InjectMocks
    private FacilityDataQueryService service;

    @Mock
    private FacilityDataRepository repository;

    @Test
    void getFacilityData() {
        FacilityData facility = FacilityData.builder().facilityId("facilityId").build();
        when(repository.findByFacilityId("facilityId")).thenReturn(Optional.ofNullable(facility));

        FacilityDataDetailsDTO facilityDetailsDTO = service.getFacilityData("facilityId");

        assertThat(facilityDetailsDTO.getFacilityId()).isEqualTo("facilityId");
        verify(repository, times(1)).findByFacilityId("facilityId");
    }

    @Test
    void isExistingFacilityId() {
        String facilityId = "facilityId";
        when(repository.existsByFacilityId(facilityId)).thenReturn(false);

        boolean result = service.isExistingFacilityId(facilityId);

        assertThat(result).isFalse();
        verify(repository, times(1)).existsByFacilityId(facilityId);
    }

    @Test
    void isActiveFacility() {
        String facilityId = "facilityId";
        when(repository.existsByFacilityIdAndClosedDateIsNull(facilityId)).thenReturn(false);

        boolean result = service.isActiveFacility(facilityId);

        assertThat(result).isFalse();
        verify(repository, times(1)).existsByFacilityIdAndClosedDateIsNull(facilityId);
    }

    @Test
    void findActiveFacilitiesByAccountId() {
        Long accountId = 1L;

        FacilityData activeFacility = FacilityData.builder()
                .facilityId("activeFacility").accountId(accountId).closedDate(null).createdDate(LocalDateTime.now()).build();

        List<FacilityData> facilities = List.of(activeFacility);

        when(repository.findFacilityDataByAccountIdAndClosedDateIsNull(accountId)).thenReturn(facilities);

        List<FacilityData> results = service.findActiveFacilitiesByAccountId(accountId);

        assertThat(results).contains(activeFacility);
    }

    @Test
    void getAllActiveFacilityIdsByAccount() {
        long accountId = 1L;

        when(repository.findFacilityIdsByAccountIdAndClosedDateIsNull(accountId)).thenReturn(List.of(1L, 2L, 3L));

        // Invoke
        List<Long> result = service.getAllActiveFacilityIdsByAccount(accountId);

        // Verify
        assertThat(result).containsExactly(1L, 2L, 3L);
        verify(repository, times(1)).findFacilityIdsByAccountIdAndClosedDateIsNull(accountId);
    }

    @Test
    void getAccountIdByFacilityId() {
        long accountId = 1L;
        FacilityData facility = FacilityData.builder().accountId(accountId).facilityId("facilityId").build();
        when(repository.findByFacilityId("facilityId")).thenReturn(Optional.ofNullable(facility));

        Long result = service.getAccountIdByFacilityId("facilityId");

        assertThat(result).isEqualTo(accountId);
        verify(repository, times(1)).findByFacilityId("facilityId");
    }

    @Test
    void getFacilityBaseInfo() {
        Long facilityId = 1L;
        FacilityData facilityData = FacilityData.builder()
                .facilityId("facilityId")
                .siteName("siteName")
                .accountId(2L)
                .build();

        when(repository.findById(facilityId)).thenReturn(Optional.ofNullable(facilityData));

        // invoke
        service.getFacilityBaseInfo(facilityId);

        verify(repository, times(1)).findById(facilityId);
    }

    @Test
    void getIdByFacilityId() {

        when(repository.findIdByFacilityId("facilityBusinessId")).thenReturn(Optional.of(999L));

        final Long facilityId = service.getIdByFacilityId("facilityBusinessId");

        assertThat(facilityId).isEqualTo(999L);
        verify(repository, times(1))
                .findIdByFacilityId("facilityBusinessId");

    }

    @Test
    void getActiveFacilityParticipatingSchemeVersions() {
        final String facilityId = "facilityId";
        final FacilityData fd = FacilityData.builder()
                .id(999L)
                .facilityId(facilityId)
                .schemeExitDate(null)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                .siteName("siteName")
                .accountId(2L)
                .build();

        when(repository.findByFacilityIdAndClosedDateIsNull(facilityId))
                .thenReturn(Optional.ofNullable(fd));

        Set<SchemeVersion> result = service.getActiveFacilityParticipatingSchemeVersions(facilityId);

        assertEquals(fd.getParticipatingSchemeVersions(), result);
        verify(repository, times(1))
                .findByFacilityIdAndClosedDateIsNull(facilityId);
    }

    @Test
    void getParticipatingFacilitySchemeVersions() {
        final String facilityId =  "facilityId";
        final Set<SchemeVersion> schemeVersions = Set.of(SchemeVersion.CCA_2);

        when(repository.findByFacilityId(facilityId))
                .thenReturn(Optional.of(FacilityData.builder().participatingSchemeVersions(schemeVersions).build()));

        final Set<SchemeVersion> result = service.getParticipatingFacilitySchemeVersions(facilityId);

        assertThat(result).isEqualTo(schemeVersions);
        verify(repository, times(1)).findByFacilityId(facilityId);

    }

    @Test
    void getParticipatingFacilitySchemeVersions_empty() {
        final String facilityId =  "facilityId";

        when(repository.findByFacilityId(facilityId)).thenReturn(Optional.empty());

        final Set<SchemeVersion> result = service.getParticipatingFacilitySchemeVersions(facilityId);

        assertThat(result).isEmpty();
        verify(repository, times(1)).findByFacilityId(facilityId);

    }
}
