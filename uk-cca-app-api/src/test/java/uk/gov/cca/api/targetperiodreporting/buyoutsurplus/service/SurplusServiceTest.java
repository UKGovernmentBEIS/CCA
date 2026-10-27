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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.SurplusRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation.SurplusValidatorService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@ExtendWith(MockitoExtension.class)
class SurplusServiceTest {

    @InjectMocks
    private SurplusService surplusService;

    @Mock
    private SurplusRepository surplusRepository;
    
    @Mock
    private SurplusValidatorService validatorService;

    @Test
    void bankSurplus_new() {
        final long resourceId = 1L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        
        final SurplusCreate dto = SurplusCreate.builder()
                .resourceId(resourceId)
                .surplusGained(BigDecimal.TEN)
                .build();

        final SurplusEntity entity = SurplusEntity.builder()
                .resourceId(resourceId)
                .schemeVersion(schemeVersion)
                .performanceDataResourceType(PerformanceDataResourceType.ACCOUNT)
                .surplusGained(BigDecimal.TEN)
                .build();

        when(surplusRepository.findByResourceIdAndSchemeVersion(resourceId, schemeVersion))
                .thenReturn(Optional.empty());

        // Invoke
        surplusService.bankSurplus(dto, schemeVersion);

        // Verify
        verify(surplusRepository, times(1))
                .findByResourceIdAndSchemeVersion(resourceId, schemeVersion);
        verify(surplusRepository, times(1)).save(entity);
    }

    @Test
    void bankSurplus_update() {
        final long resourceId = 1L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        final SurplusCreate dto = SurplusCreate.builder()
                .resourceId(resourceId)
                .surplusGained(BigDecimal.TEN)
                .history(SurplusCreateHistory.builder()
                        .submitter("submitter")
                        .submitterId("submitterId")
                        .comments("comments")
                        .build())
                .build();
        final SurplusEntity entity = SurplusEntity.builder()
                .resourceId(resourceId)
                .surplusGained(BigDecimal.ONE)
                .build();

        when(surplusRepository.findByResourceIdAndSchemeVersion(resourceId, schemeVersion))
                .thenReturn(Optional.of(entity));

        // Invoke
        surplusService.bankSurplus(dto, schemeVersion);

        // Verify
        assertThat(entity.getSurplusGained()).isEqualTo(BigDecimal.TEN);
        assertThat(entity.getSurplusHistoryList()).isNotEmpty();
        final SurplusHistory persistHistory = entity.getSurplusHistoryList().getFirst();
        assertThat(persistHistory.getSubmitter()).isEqualTo("submitter");
        assertThat(persistHistory.getSubmitterId()).isEqualTo("submitterId");
        assertThat(persistHistory.getNewSurplusGained()).isEqualTo(BigDecimal.TEN);
        assertThat(persistHistory.getComments()).isEqualTo("comments");

        verify(surplusRepository, times(1))
                .findByResourceIdAndSchemeVersion(resourceId, SchemeVersion.CCA_2);
        verifyNoMoreInteractions(surplusRepository);
    }

    @Test
    void updateAccountSurplusGained() {
        final Long accountId = 123L;
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
                .resourceId(accountId)
                .surplusGained(BigDecimal.TEN)
                .history(SurplusCreateHistory.builder()
                        .submitter("sub mitter")
                        .submitterId("submitterId")
                        .comments("comments")
                        .build())
                .build();

        SurplusEntity surplusEntity = SurplusEntity.builder().resourceId(accountId)
                .surplusGained(BigDecimal.TWO)
                .schemeVersion(SchemeVersion.CCA_3)
                .build();

        surplusEntity.addHistory(SurplusHistory.builder()
                .surplus(surplusEntity)
                .newSurplusGained(BigDecimal.TWO)
                .submitter("submitter")
                .submitterId("submitterId")
                .build());

        when(surplusRepository
                .findByResourceIdAndSchemeVersion(surplusCreate.getResourceId(), SchemeVersion.CCA_2)).thenReturn(Optional.of(surplusEntity));

        // invoke
        surplusService.updateAccountSurplusGained(surplusUpdateDTO, accountId, appUser);

        // verify
        verify(surplusRepository, times(1))
                .findByResourceIdAndSchemeVersion(surplusCreate.getResourceId(), SchemeVersion.CCA_2);
        assertThat(surplusEntity.getSurplusGained()).isEqualTo(BigDecimal.TEN);
        assertThat(surplusEntity.getSurplusHistoryList()).isNotEmpty();
        assertThat(surplusEntity.getSurplusHistoryList()).hasSize(2);
        final SurplusHistory persistHistory = surplusEntity.getSurplusHistoryList().getFirst();
        assertThat(persistHistory.getNewSurplusGained()).isEqualTo(BigDecimal.TWO);
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
        surplusService.updateFacilitySurplusGained(surplusUpdateDTO, facilityId, appUser);

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

        surplusService.bankSurplus(List.of(surplusCreateExisting, surplusCreateExisting2), schemeVersion);

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

        surplusService.bankSurplus(surplusEntries, schemeVersion);

        assertEquals(BigDecimal.valueOf(150), existingEntity1.getSurplusGained());
        assertEquals(BigDecimal.valueOf(50), existingEntity2.getSurplusGained());
        verify(surplusRepository).findAllByResourceIdInAndSchemeVersion(resourceIds,schemeVersion);
        verify(validatorService).validateNoDuplicateResourceIds(surplusEntries);
        verify(surplusRepository, times(1)).saveAll(List.of(entity));
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
                () -> surplusService.bankSurplus(surplusEntries, schemeVersion));

        verify(validatorService)
                .validateNoDuplicateResourceIds(surplusEntries);

        verifyNoInteractions(surplusRepository);
    }
}
