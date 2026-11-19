package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
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
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.SurplusRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation.SurplusValidatorService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@ExtendWith(MockitoExtension.class)
class AccountSurplusServiceTest {

    @InjectMocks
    private AccountSurplusService accountSurplusService;

    @Mock
    private SurplusRepository surplusRepository;
    
    @Mock
    private SurplusValidatorService validatorService;
    
    @Test
    void getResourceType_shouldReturnAccount() {
        PerformanceDataResourceType result = accountSurplusService.getResourceType();

        assertThat(result).isEqualTo(PerformanceDataResourceType.ACCOUNT);
    }

    @Test
    void getSurplusGainedWithHistoryFlag() {
        final long accountId = 999L;
        final SurplusGainedDTO surplusGainedDTO = SurplusGainedDTO.builder()
                .surplusGained(BigDecimal.TEN)
                .hasHistory(false)
                .schemeVersion(SchemeVersion.CCA_2)
                .build();
        final SurplusEntity surplusEntity = SurplusEntity.builder()
                .surplusGained(BigDecimal.TEN)
                .surplusHistoryList(List.of())
                .performanceDataResourceType(PerformanceDataResourceType.ACCOUNT)
                .schemeVersion(SchemeVersion.CCA_2)
                .resourceId(accountId)
                .build();

        when(surplusRepository.findByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2))
                .thenReturn(Optional.of(surplusEntity));

        final SurplusGainedDTO result = accountSurplusService.getSurplusGainedWithHistoryFlag(accountId);

        Assertions.assertEquals(result, surplusGainedDTO);
        verify(surplusRepository, times(1)).findByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2);
    }

    @Test
    void getSurplus() {
        final Long resourceId = 1L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;

        final SurplusEntity entity = SurplusEntity.builder()
                .id(11L)
                .resourceId(1L)
                .schemeVersion(schemeVersion)
                .surplusGained(BigDecimal.TEN)
                .build();
        final SurplusDTO expected = SurplusDTO.builder()
                .id(11L)
                .resourceId(1L)
                .schemeVersion(schemeVersion)
                .surplusGained(BigDecimal.TEN)
                .build();

        when(surplusRepository.findByResourceIdAndSchemeVersion(resourceId, schemeVersion))
                .thenReturn(Optional.of(entity));

        // Invoke
        final Optional<SurplusDTO> result = accountSurplusService.getSurplus(resourceId);

        // Verify
        assertThat(result).isNotEmpty().contains(expected);
    }

    @Test
    void getSurplusHistory() {
        long accountId = 999L;
        final SurplusEntity entity = SurplusEntity.builder()
                .surplusHistoryList(List.of(
                        SurplusHistory.builder()
                                .newSurplusGained(BigDecimal.TEN)
                                .submitter("Regulator")
                                .comments("comments")
                                .build()
                ))
                .build();

        when(surplusRepository.findByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2))
                .thenReturn(Optional.of(entity));


        final List<SurplusHistoryDTO> result = accountSurplusService.getSurplusHistory(accountId);

        Assertions.assertEquals(entity.getSurplusHistoryList().getFirst().getNewSurplusGained(), result.getFirst().getSurplusGained());
        Assertions.assertEquals(entity.getSurplusHistoryList().getFirst().getComments(), result.getFirst().getComments());
        Assertions.assertEquals(entity.getSurplusHistoryList().getFirst().getSubmitter(), result.getFirst().getSubmitter());
        verify(surplusRepository, times(1)).findByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2);
    }

    @Test
    void getSurplusHistory_empty() {
        long accountId = 999L;

        when(surplusRepository.findByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2))
                .thenReturn(Optional.empty());

        final List<SurplusHistoryDTO> result = accountSurplusService.getSurplusHistory(accountId);

        assertThat(result).isEmpty();
        verify(surplusRepository, times(1)).findByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2);
    }
    
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
        accountSurplusService.bankSurplus(dto);

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
        accountSurplusService.bankSurplus(dto);

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
        accountSurplusService.updateSurplusGained(surplusUpdateDTO, accountId, appUser);

        // verify
        verify(surplusRepository, times(1))
                .findByResourceIdAndSchemeVersion(surplusCreate.getResourceId(), SchemeVersion.CCA_2);
        assertThat(surplusEntity.getSurplusGained()).isEqualTo(BigDecimal.TEN);
        assertThat(surplusEntity.getSurplusHistoryList()).isNotEmpty();
        assertThat(surplusEntity.getSurplusHistoryList()).hasSize(2);
        final SurplusHistory persistHistory = surplusEntity.getSurplusHistoryList().getFirst();
        assertThat(persistHistory.getNewSurplusGained()).isEqualTo(BigDecimal.TWO);
    }
}