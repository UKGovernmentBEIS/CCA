package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusEntity;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreateHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.SurplusRepository;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurplusServiceTest {

    @InjectMocks
    private SurplusService surplusService;

    @Mock
    private SurplusRepository surplusRepository;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Test
    void bankSurplus_new() {
        final long accountId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final SurplusCreate dto = SurplusCreate.builder()
                .accountId(accountId)
                .targetPeriodType(targetPeriodType)
                .surplusGained(BigDecimal.TEN)
                .build();

        final TargetPeriod targetPeriod = TargetPeriod.builder().businessId(targetPeriodType).build();
        final SurplusEntity entity = SurplusEntity.builder()
                .accountId(accountId)
                .targetPeriod(targetPeriod)
                .surplusGained(BigDecimal.TEN)
                .build();

        when(surplusRepository.findByAccountIdAndTargetPeriod_BusinessId(accountId, targetPeriodType))
                .thenReturn(Optional.empty());
        when(targetPeriodService.findByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);

        // Invoke
        surplusService.bankSurplus(dto);

        // Verify
        verify(surplusRepository, times(1))
                .findByAccountIdAndTargetPeriod_BusinessId(accountId, targetPeriodType);
        verify(targetPeriodService, times(1))
                .findByTargetPeriodType(targetPeriodType);
        verify(surplusRepository, times(1)).save(entity);
    }

    @Test
    void bankSurplus_update() {
        final long accountId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final SurplusCreate dto = SurplusCreate.builder()
                .accountId(accountId)
                .targetPeriodType(targetPeriodType)
                .surplusGained(BigDecimal.TEN)
                .history(SurplusCreateHistory.builder()
                        .submitter("submitter")
                        .submitterId("submitterId")
                        .comments("comments")
                        .build())
                .build();
        final SurplusEntity entity = SurplusEntity.builder()
                .accountId(accountId)
                .surplusGained(BigDecimal.ONE)
                .build();

        when(surplusRepository.findByAccountIdAndTargetPeriod_BusinessId(accountId, targetPeriodType))
                .thenReturn(Optional.of(entity));

        // Invoke
        surplusService.bankSurplus(dto);

        // Verify
        assertThat(entity.getSurplusGained()).isEqualTo(BigDecimal.TEN);
        assertThat(entity.getSurplusHistoryList()).isNotEmpty();
        final SurplusHistory persistHistory = entity.getSurplusHistoryList().getFirst();
        assertThat(persistHistory.getSubmitter()).isEqualTo("submitter");
        assertThat(persistHistory.getSubmitterId()).isEqualTo("submitterId");
        assertThat(persistHistory.getNewSurplusGained()).isEqualTo(BigDecimal.TEN);
        assertThat(persistHistory.getComments()).isEqualTo("comments");

        verify(surplusRepository, times(1))
                .findByAccountIdAndTargetPeriod_BusinessId(accountId, targetPeriodType);
        verifyNoMoreInteractions(surplusRepository);
        verifyNoInteractions(targetPeriodService);
    }

    @Test
    void updateSurplusGained() {

        final Long accountId = 123L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final SurplusUpdateDTO surplusUpdateDTO = SurplusUpdateDTO.builder()
                .newSurplusGained(BigDecimal.TEN)
                .targetPeriodType(targetPeriodType)
                .comments("comments")
                .build();
        final AppUser appUser = AppUser.builder()
                .userId("submitterId")
                .firstName("sub")
                .lastName("mitter")
                .build();

        final SurplusCreate surplusCreate = SurplusCreate.builder()
                .accountId(accountId)
                .targetPeriodType(targetPeriodType)
                .surplusGained(BigDecimal.TEN)
                .history(SurplusCreateHistory.builder()
                        .submitter("sub mitter")
                        .submitterId("submitterId")
                        .comments("comments")
                        .build())
                .build();

        surplusService.updateSurplusGained(surplusUpdateDTO, accountId, appUser);

        verify(surplusRepository, times(1))
                .findByAccountIdAndTargetPeriod_BusinessId(surplusCreate.getAccountId(), surplusCreate.getTargetPeriodType());
    }
}
