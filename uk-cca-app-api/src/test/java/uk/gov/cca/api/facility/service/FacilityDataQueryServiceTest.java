package uk.gov.cca.api.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
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
        FacilityData facility = FacilityData.builder().id(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.ofNullable(facility));

        FacilityDataDetailsDTO facilityDetailsDTO = service.getFacilityData(1L);

        assertThat(facilityDetailsDTO.getId()).isEqualTo(1L);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void isExistingFacilityBusinessId() {
        String facilityBusinessId = "facilityId";
        when(repository.existsByFacilityBusinessId(facilityBusinessId)).thenReturn(false);

        boolean result = service.isExistingFacilityBusinessId(facilityBusinessId);

        assertThat(result).isFalse();
        verify(repository, times(1)).existsByFacilityBusinessId(facilityBusinessId);
    }

    @Test
    void isActiveFacility() {
        String facilityBusinessId = "facilityId";
        when(repository.existsByFacilityBusinessIdAndClosedDateIsNull(facilityBusinessId)).thenReturn(false);

        boolean result = service.isActiveFacility(facilityBusinessId);

        assertThat(result).isFalse();
        verify(repository, times(1)).existsByFacilityBusinessIdAndClosedDateIsNull(facilityBusinessId);
    }

    @Test
    void findActiveFacilitiesByAccountId() {
        Long accountId = 1L;

        FacilityData activeFacility = FacilityData.builder()
                .facilityBusinessId("activeFacility")
                .accountId(accountId)
                .closedDate(null)
                .createdDate(LocalDateTime.now())
                .build();

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
    void getFacilityBaseInfo() {
        Long facilityId = 1L;
        FacilityData facilityData = FacilityData.builder()
                .facilityBusinessId("facilityId")
                .siteName("siteName")
                .accountId(2L)
                .build();

        when(repository.findById(facilityId)).thenReturn(Optional.ofNullable(facilityData));

        // invoke
        service.getFacilityBaseInfo(facilityId);

        verify(repository, times(1)).findById(facilityId);
    }

    @Test
    void getActiveFacilityParticipatingSchemeVersions() {
        final String facilityBusinessId = "facilityId";
        final FacilityData fd = FacilityData.builder()
                .id(999L)
                .facilityBusinessId(facilityBusinessId)
                .schemeExitDate(null)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                .siteName("siteName")
                .accountId(2L)
                .build();

        when(repository.findByFacilityBusinessIdAndClosedDateIsNull(facilityBusinessId))
                .thenReturn(Optional.ofNullable(fd));

        Set<SchemeVersion> result = service.getActiveFacilityParticipatingSchemeVersions(facilityBusinessId);

        assertEquals(fd.getParticipatingSchemeVersions(), result);
        verify(repository, times(1))
                .findByFacilityBusinessIdAndClosedDateIsNull(facilityBusinessId);
    }

    @Test
    void getParticipatingFacilitySchemeVersions() {
        final String facilityBusinessId =  "facilityId";
        final Set<SchemeVersion> schemeVersions = Set.of(SchemeVersion.CCA_2);

        when(repository.findByFacilityBusinessId(facilityBusinessId))
                .thenReturn(Optional.of(FacilityData.builder().participatingSchemeVersions(schemeVersions).build()));

        final Set<SchemeVersion> result = service.getParticipatingFacilitySchemeVersions(facilityBusinessId);

        assertThat(result).isEqualTo(schemeVersions);
        verify(repository, times(1)).findByFacilityBusinessId(facilityBusinessId);

    }

    @Test
    void getParticipatingFacilitySchemeVersions_empty() {
        final String facilityBusinessId =  "facilityId";

        when(repository.findByFacilityBusinessId(facilityBusinessId)).thenReturn(Optional.empty());

        final Set<SchemeVersion> result = service.getParticipatingFacilitySchemeVersions(facilityBusinessId);

        assertThat(result).isEmpty();
        verify(repository, times(1)).findByFacilityBusinessId(facilityBusinessId);

    }
    
    @Test
    void getFacilityIdById() {

        when(repository.findById(1L)).thenReturn(Optional.of(FacilityData.builder().facilityBusinessId("businessId").build()));

        final String businessId = service.getFacilityBusinessIdById(1L);

        assertThat(businessId).isEqualTo("businessId");
        verify(repository, times(1)).findById(1L);
    }
    
    @Test
    void getFacilityBaseInfoByIds() {

    	List<Long> facilityIds = List.of(1L, 2L);
    	List<FacilityBaseInfoDTO> expected = List.of(FacilityBaseInfoDTO.builder()
    			.id(1L)
    			.siteName("sitename")
    			.facilityBusinessId("businessId")
    			.build());
    	
        when(repository.findAllByIdIn(facilityIds)).thenReturn(List.of(FacilityData.builder()
        		.id(1L)
        		.siteName("sitename")
        		.facilityBusinessId("businessId")
        		.build()));

        List<FacilityBaseInfoDTO> result = service.getFacilityBaseInfoByIds(facilityIds);

        assertThat(result).isEqualTo(expected);
        verify(repository, times(1)).findAllByIdIn(facilityIds);
    }
    
    @Test
    void getAccountIdByFacilityId() {
        long accountId = 1L;
        FacilityData facility = FacilityData.builder().accountId(accountId).facilityBusinessId("facilityBusinessId").build();
        when(repository.findById(1L)).thenReturn(Optional.of(facility));

        Long result = service.getAccountIdByFacilityId(1L);

        assertThat(result).isEqualTo(accountId);
        verify(repository, times(1)).findById(1L);
    }
    
    @Test
    void exclusiveLockFacility() {
		FacilityData facility = FacilityData.builder().facilityBusinessId("facilityId").build();

		when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(facility));

		FacilityData result = service.exclusiveLockFacility(1L);
		
		assertThat(result).isEqualTo(facility);
		
		verify(repository, times(1)).findByIdForUpdate(1L);
    }
}
