package uk.gov.cca.api.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.config.Cca2TerminationConfig;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityHeaderInfoDTO;
import uk.gov.cca.api.facility.repository.FacilityDataRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class FacilityDataQueryServiceTest {

    @InjectMocks
    private FacilityDataQueryService service;

    @Mock
    private FacilityDataRepository repository;
    
    @Mock
    private Cca2TerminationConfig cca2TerminationConfig;
    
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
    void getActiveFacilityParticipatingSchemeVersions_CCA2_only_after_CCA2_end_date() {
        final String facilityBusinessId = "facilityId";
        final FacilityData fd = FacilityData.builder()
                .id(999L)
                .facilityBusinessId(facilityBusinessId)
                .schemeExitDate(null)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("siteName")
                .accountId(2L)
                .build();

        when(repository.findByFacilityBusinessIdAndClosedDateIsNull(facilityBusinessId))
                .thenReturn(Optional.ofNullable(fd));
        when(cca2TerminationConfig.getTerminationDate()).thenReturn(LocalDate.now().minusDays(1));

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> service.getActiveFacilityParticipatingSchemeVersions(facilityBusinessId));

        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(repository, times(1))
                .findByFacilityBusinessIdAndClosedDateIsNull(facilityBusinessId);
        verify(cca2TerminationConfig, times(1)).getTerminationDate();
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
    
    @Test
    void findLiveAccountsWithAtLeastOneFacilityForSchemeVersionOnly() {
    	List<TargetUnitAccountBusinessInfoDTO> accounts = List.of(TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).build(),
    			TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).build());
		SchemeVersion version = SchemeVersion.CCA_2;

		when(repository.findLiveAccountsWithAtLeastOneFacilityForSchemeVersionOnly(version.name()))
				.thenReturn(accounts);

		List<TargetUnitAccountBusinessInfoDTO> result = 
				service.findLiveAccountsWithAtLeastOneFacilityForSchemeVersionOnly(version.name());
		
		assertThat(result).isEqualTo(accounts);
		
		verify(repository, times(1)).findLiveAccountsWithAtLeastOneFacilityForSchemeVersionOnly(version.name());
    }

    @Test
    void getResourceHeaderInfo() {
        Long facilityId = 1L;
        FacilityData facilityData = FacilityData.builder()
                .facilityBusinessId("facilityId")
                .siteName("siteName")
                .accountId(2L)
                .build();

        when(repository.findById(facilityId)).thenReturn(Optional.ofNullable(facilityData));

        // invoke
        FacilityHeaderInfoDTO requestTaskHeaderInfo = service.getResourceHeaderInfo(String.valueOf(facilityId));

        // verify
        assertThat(requestTaskHeaderInfo).isNotNull();
        assertThat(requestTaskHeaderInfo.getName()).isEqualTo(Objects.requireNonNull(facilityData).getSiteName());
        assertThat(requestTaskHeaderInfo.getBusinessId()).isEqualTo(facilityData.getFacilityBusinessId());
        assertThat(requestTaskHeaderInfo.getStatus()).isEqualTo(FacilityDataStatus.LIVE);
        verify(repository, times(1)).findById(facilityId);
    }

    @Test
    void getFacilitiesByAccountId() {
        final Long accountId = 1L;
        final String businessId = "facilityData";
        final String siteName = "siteName";

        final FacilityData facilityData = FacilityData.builder()
                .facilityBusinessId(businessId)
                .siteName(siteName)
                .accountId(accountId)
                .closedDate(null)
                .createdDate(LocalDateTime.now())
                .build();

        final List<FacilityData> facilities = List.of(facilityData);

        final List<FacilityBaseInfoDTO> expected = List.of(FacilityBaseInfoDTO.builder()
                .facilityBusinessId(businessId)
                .siteName(siteName)
                .build());

        when(repository.findAllByAccountId(accountId)).thenReturn(facilities);

        // invoke
        List<FacilityBaseInfoDTO> actual = service.getFacilitiesByAccountId(accountId);

        // verify
        assertThat(actual).isEqualTo(expected);
        verify(repository, times(1)).findAllByAccountId(accountId);
    }

    @Test
    void getResourceType() {
        assertThat(service.getResourceType()).isEqualTo(CcaResourceType.FACILITY);
    }
}
