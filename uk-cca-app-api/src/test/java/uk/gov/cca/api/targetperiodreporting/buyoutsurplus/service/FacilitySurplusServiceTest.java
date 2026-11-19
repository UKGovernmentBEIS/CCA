package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreateHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusEntity;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.SurplusRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation.SurplusValidatorService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class FacilitySurplusServiceTest {

    @InjectMocks
    private FacilitySurplusService facilitySurplusService;

    @Mock
    private SurplusRepository surplusRepository;
    
    @Mock
    private SurplusValidatorService validatorService;
    
    
    @Test
    void getResourceType_shouldReturnAccount() {
        PerformanceDataResourceType result = facilitySurplusService.getResourceType();

        assertThat(result).isEqualTo(PerformanceDataResourceType.FACILITY);
    }

    @Test
    void getSurplusGainedWithHistoryFlag() {
        final long facilityId = 999L;
        final SurplusGainedDTO surplusGainedDTO = SurplusGainedDTO.builder()
                .surplusGained(BigDecimal.TEN)
                .hasHistory(false)
                .schemeVersion(SchemeVersion.CCA_3)
                .build();
        final SurplusEntity surplusEntity = SurplusEntity.builder()
                .surplusGained(BigDecimal.TEN)
                .surplusHistoryList(List.of())
                .performanceDataResourceType(PerformanceDataResourceType.FACILITY)
                .schemeVersion(SchemeVersion.CCA_3)
                .resourceId(facilityId)
                .build();
        when(surplusRepository.findByResourceIdAndSchemeVersion(facilityId, SchemeVersion.CCA_3))
                .thenReturn(Optional.of(surplusEntity));

        final SurplusGainedDTO result = facilitySurplusService.getSurplusGainedWithHistoryFlag(facilityId);

        Assertions.assertEquals(result, surplusGainedDTO);
        verify(surplusRepository, times(1)).findByResourceIdAndSchemeVersion(facilityId, SchemeVersion.CCA_3);
    }

    @Test
    void getFacilitySurplusHistory() {
        long facilityId = 999L;
        final SurplusEntity entity = SurplusEntity.builder()
                .surplusHistoryList(List.of(
                        SurplusHistory.builder()
                                .newSurplusGained(BigDecimal.TEN)
                                .submitter("Regulator")
                                .comments("comments")
                                .build()
                ))
                .build();

        when(surplusRepository.findByResourceIdAndSchemeVersion(facilityId, SchemeVersion.CCA_3))
                .thenReturn(Optional.of(entity));


        final List<SurplusHistoryDTO> result = facilitySurplusService.getSurplusHistory(facilityId);

        Assertions.assertEquals(entity.getSurplusHistoryList().getFirst().getNewSurplusGained(), result.getFirst().getSurplusGained());
        Assertions.assertEquals(entity.getSurplusHistoryList().getFirst().getComments(), result.getFirst().getComments());
        Assertions.assertEquals(entity.getSurplusHistoryList().getFirst().getSubmitter(), result.getFirst().getSubmitter());
        verify(surplusRepository, times(1)).findByResourceIdAndSchemeVersion(facilityId, SchemeVersion.CCA_3);
    }

    @Test
    void getFacilitySurplusHistory_empty() {
        long facilityId = 999L;

        when(surplusRepository.findByResourceIdAndSchemeVersion(facilityId, SchemeVersion.CCA_3))
                .thenReturn(Optional.empty());

        final List<SurplusHistoryDTO> result = facilitySurplusService.getSurplusHistory(facilityId);

        assertThat(result).isEmpty();
        verify(surplusRepository, times(1)).findByResourceIdAndSchemeVersion(facilityId, SchemeVersion.CCA_3);
    }
    
    @Test
    void getFacilityPositiveSurplusMap_shouldReturnOnlyPositiveSurplus() {
        // Given
        List<Long> facilityIds = List.of(1L, 2L, 3L, 4L);
        SchemeVersion schemeVersion = SchemeVersion.CCA_3;

        SurplusEntity positiveSurplus1 = createSurplusEntity(1L, new BigDecimal("100"));
        SurplusEntity zeroSurplus = createSurplusEntity(2L, BigDecimal.ZERO);
        SurplusEntity positiveSurplus2 = createSurplusEntity(3L, new BigDecimal("15"));
        SurplusEntity positiveSurplus3 = createSurplusEntity(4L, new BigDecimal("25"));

        when(surplusRepository.findAllByResourceIdInAndSchemeVersion(facilityIds, schemeVersion
        )).thenReturn(List.of(
                positiveSurplus1,
                zeroSurplus,
                positiveSurplus2,
                positiveSurplus3
        ));

        Map<Long, BigDecimal> result = facilitySurplusService.getPositiveSurplusMap(facilityIds, schemeVersion);

        assertThat(result)
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        1L, new BigDecimal("100"),
                        3L, new BigDecimal("15"),
                        4L, new BigDecimal("25")
                ));

        verify(surplusRepository).findAllByResourceIdInAndSchemeVersion(facilityIds, schemeVersion);
    }
    
    @Test
    void getFacilityPositiveSurplusMap_invalidSchemeVersion() {
        // Given
        List<Long> facilityIds = List.of(1L, 2L, 3L, 4L);
        
        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> facilitySurplusService.getPositiveSurplusMap(facilityIds, SchemeVersion.CCA_2));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verifyNoMoreInteractions(surplusRepository);
    }
    
    @Test
    void updateFacilitySurplusGained() {
        final Long facilityId = 123L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final SurplusUpdateDTO surplusUpdateDTO = SurplusUpdateDTO.builder()
                .newSurplusGained(BigDecimal.TEN)
                .comments("comments")
                .build();
        final AppUser appUser = AppUser.builder()
                .userId("submitterId")
                .firstName("sub")
                .lastName("mitter")
                .build();

        final SurplusCreate surplusCreate = SurplusCreate.builder()
                .resourceId(facilityId)
                .surplusGained(BigDecimal.TEN)
                .history(SurplusCreateHistory.builder()
                        .submitter("sub mitter")
                        .submitterId("submitterId")
                        .comments("comments")
                        .build())
                .build();

        SurplusEntity surplusEntity = SurplusEntity.builder().resourceId(facilityId)
                .surplusGained(BigDecimal.TWO)
                .schemeVersion(schemeVersion)
                .build();

        surplusEntity.addHistory(SurplusHistory.builder()
                .surplus(surplusEntity)
                .newSurplusGained(BigDecimal.TWO)
                .submitter("submitter")
                .submitterId("submitterId")
                .build());

        when(surplusRepository
                .findByResourceIdAndSchemeVersion(surplusCreate.getResourceId(), schemeVersion)).thenReturn(Optional.of(surplusEntity));

        // invoke
        facilitySurplusService.updateSurplusGained(surplusUpdateDTO, facilityId, appUser);

        // verify
        verify(surplusRepository, times(1))
                .findByResourceIdAndSchemeVersion(surplusCreate.getResourceId(), schemeVersion);
        assertThat(surplusEntity.getSurplusGained()).isEqualTo(BigDecimal.TEN);
        assertThat(surplusEntity.getSurplusHistoryList()).isNotEmpty();
        assertThat(surplusEntity.getSurplusHistoryList()).hasSize(2);
        final SurplusHistory persistHistory = surplusEntity.getSurplusHistoryList().getFirst();
        assertThat(persistHistory.getNewSurplusGained()).isEqualTo(BigDecimal.TWO);
    }
    
    @Test
    void shouldUpdateExistingSurplusAndAddHistory() {
        SchemeVersion schemeVersion = SchemeVersion.CCA_3;

        SurplusCreate surplusCreateExisting = SurplusCreate.builder()
                .resourceId(100L)
                .surplusGained(BigDecimal.valueOf(150))
                .build();

        SurplusCreate surplusCreateExisting2 = SurplusCreate.builder()
                .resourceId(103L)
                .surplusGained(BigDecimal.valueOf(50))
                .build();

        SurplusEntity existingEntity1 = new SurplusEntity();
        existingEntity1.setResourceId(surplusCreateExisting.getResourceId());
        existingEntity1.setSchemeVersion(schemeVersion);
        existingEntity1.setSurplusGained(BigDecimal.valueOf(50));
        existingEntity1.setPerformanceDataResourceType(PerformanceDataResourceType.FACILITY);

        SurplusEntity existingEntity2 = new SurplusEntity();
        existingEntity2.setResourceId(surplusCreateExisting2.getResourceId());
        existingEntity2.setSchemeVersion(schemeVersion);
        existingEntity2.setSurplusGained(BigDecimal.valueOf(10));
        existingEntity2.setPerformanceDataResourceType(PerformanceDataResourceType.FACILITY);

        when(surplusRepository
                .findAllByResourceIdInAndSchemeVersion(
                        List.of(surplusCreateExisting.getResourceId(), surplusCreateExisting2.getResourceId()),
                        schemeVersion))
                .thenReturn(List.of(existingEntity1, existingEntity2));

        facilitySurplusService.bankSurplus(List.of(surplusCreateExisting, surplusCreateExisting2), schemeVersion);

        assertEquals(BigDecimal.valueOf(150), existingEntity1.getSurplusGained());
        assertEquals(BigDecimal.valueOf(50), existingEntity2.getSurplusGained());
        verify(surplusRepository).findAllByResourceIdInAndSchemeVersion(
                List.of(surplusCreateExisting.getResourceId(), surplusCreateExisting2.getResourceId()),
                schemeVersion);

        verify(surplusRepository, never()).saveAll(anyList());
    }
    
    @Test
    void shouldUpdateExistingAndCreateNewSurplus() {
        SchemeVersion schemeVersion = SchemeVersion.CCA_3;

        SurplusCreate surplusCreateExisting = SurplusCreate.builder()
                .resourceId(100L)
                .surplusGained(BigDecimal.valueOf(150))
                .build();

        SurplusCreate surplusCreateExisting2 = SurplusCreate.builder()
                .resourceId(103L)
                .surplusGained(BigDecimal.valueOf(50))
                .build();

        SurplusCreate surplusCreateNew = SurplusCreate.builder()
                .resourceId(105L)
                .surplusGained(BigDecimal.valueOf(50))
                .build();
        
        List<Long> resourceIds = List.of(surplusCreateExisting.getResourceId(), surplusCreateExisting2.getResourceId(), surplusCreateNew.getResourceId());
        List<SurplusCreate> surplusEntries = List.of(surplusCreateExisting, surplusCreateExisting2, surplusCreateNew);

        SurplusEntity existingEntity1 = new SurplusEntity();
        existingEntity1.setResourceId(surplusCreateExisting.getResourceId());
        existingEntity1.setSchemeVersion(schemeVersion);
        existingEntity1.setSurplusGained(BigDecimal.valueOf(50));

        SurplusEntity existingEntity2 = new SurplusEntity();
        existingEntity2.setResourceId(surplusCreateExisting2.getResourceId());
        existingEntity2.setSchemeVersion(schemeVersion);
        existingEntity2.setSurplusGained(BigDecimal.valueOf(10));
        
        final SurplusEntity entity = SurplusEntity.builder()
                .resourceId(surplusCreateNew.getResourceId())
                .performanceDataResourceType(PerformanceDataResourceType.FACILITY)
                .surplusGained(surplusCreateNew.getSurplusGained())
                .schemeVersion(schemeVersion)
                .build();

        doNothing()
        .when(validatorService)
        .validateNoDuplicateResourceIds(surplusEntries);
        
        when(surplusRepository
                .findAllByResourceIdInAndSchemeVersion(resourceIds,schemeVersion))
                .thenReturn(List.of(existingEntity1, existingEntity2));

        facilitySurplusService.bankSurplus(surplusEntries, schemeVersion);

        assertEquals(BigDecimal.valueOf(150), existingEntity1.getSurplusGained());
        assertEquals(BigDecimal.valueOf(50), existingEntity2.getSurplusGained());
        verify(surplusRepository).findAllByResourceIdInAndSchemeVersion(resourceIds,schemeVersion);
        verify(validatorService).validateNoDuplicateResourceIds(surplusEntries);
        verify(surplusRepository, times(1)).saveAll(List.of(entity));
    }
    
    @Test
    void bankSurplus_shouldThrowException_whenInvalidScheme() {
        
        SurplusCreate surplusCreate = SurplusCreate.builder()
                .resourceId(100L)
                .surplusGained(BigDecimal.valueOf(150))
                .build();

        SurplusCreate surplusCreate2 = SurplusCreate.builder()
                .resourceId(103L)
                .surplusGained(BigDecimal.valueOf(50))
                .build();
        
        List<SurplusCreate> surplusEntries = List.of(surplusCreate, surplusCreate2);
        
        BusinessException businessException = assertThrows(
                BusinessException.class,
                () -> facilitySurplusService.bankSurplus(surplusEntries, SchemeVersion.CCA_2));

        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verifyNoInteractions(validatorService);
        verifyNoInteractions(surplusRepository);
    }
    
    @Test
    void bankSurplus_shouldThrowException_whenDuplicateResourceIdsExist() {
        SchemeVersion schemeVersion = SchemeVersion.CCA_3;

        SurplusCreate surplusCreateExisting = SurplusCreate.builder()
                .resourceId(100L)
                .surplusGained(BigDecimal.valueOf(150))
                .build();

        SurplusCreate surplusCreateExisting2 = SurplusCreate.builder()
                .resourceId(103L)
                .surplusGained(BigDecimal.valueOf(50))
                .build();

        SurplusCreate surplusCreateNew = SurplusCreate.builder()
                .resourceId(100L)
                .surplusGained(BigDecimal.valueOf(50))
                .build();
        
        List<SurplusCreate> surplusEntries = List.of(surplusCreateExisting, surplusCreateExisting2, surplusCreateNew);


        doThrow(new RuntimeException("Duplicate resource IDs"))
        .when(validatorService)
        .validateNoDuplicateResourceIds(surplusEntries);
        
        assertThrows(
                RuntimeException.class,
                () -> facilitySurplusService.bankSurplus(surplusEntries, schemeVersion));

        verify(validatorService)
                .validateNoDuplicateResourceIds(surplusEntries);

        verifyNoInteractions(surplusRepository);
    }
    
    private SurplusEntity createSurplusEntity(Long facilityId, BigDecimal surplusGained) {
        return SurplusEntity.builder()
                .resourceId(facilityId)
                .schemeVersion(SchemeVersion.CCA_3)
                .surplusGained(surplusGained)
                .build();
    }
}