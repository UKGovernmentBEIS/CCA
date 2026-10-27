package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusEntity;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.SurplusRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurplusQueryServiceTest {

    @InjectMocks
    private SurplusQueryService surplusQueryService;

    @Mock
    private SurplusRepository surplusRepository;

    @Test
    void getAccountSurplusGainedWithHistoryFlag() {
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

        final SurplusGainedDTO result = surplusQueryService.getAccountSurplusGainedWithHistoryFlag(accountId);

        Assertions.assertEquals(result, surplusGainedDTO);
        verify(surplusRepository, times(1)).findByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2);
    }

    @Test
    void getFacilitySurplusGainedWithHistoryFlag() {
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

        final SurplusGainedDTO result = surplusQueryService.getFacilitySurplusGainedWithHistoryFlag(facilityId);

        Assertions.assertEquals(result, surplusGainedDTO);
        verify(surplusRepository, times(1)).findByResourceIdAndSchemeVersion(facilityId, SchemeVersion.CCA_3);
    }

    @Test
    void getSurplusByResourceIdAndSchemeVersion() {
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
        final Optional<SurplusDTO> result = surplusQueryService.getSurplusByResourceIdAndSchemeVersion(resourceId, schemeVersion);

        // Verify
        assertThat(result).isNotEmpty().contains(expected);
    }

    @Test
    void getAccountSurplusHistory() {
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


        final List<SurplusHistoryDTO> result = surplusQueryService
                .getAccountSurplusHistory(accountId);

        Assertions.assertEquals(entity.getSurplusHistoryList().getFirst().getNewSurplusGained(), result.getFirst().getSurplusGained());
        Assertions.assertEquals(entity.getSurplusHistoryList().getFirst().getComments(), result.getFirst().getComments());
        Assertions.assertEquals(entity.getSurplusHistoryList().getFirst().getSubmitter(), result.getFirst().getSubmitter());
        verify(surplusRepository, times(1)).findByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2);
    }

    @Test
    void getAccountSurplusHistory_empty() {
        long accountId = 999L;

        when(surplusRepository.findByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2))
                .thenReturn(Optional.empty());

        final List<SurplusHistoryDTO> result = surplusQueryService
                .getAccountSurplusHistory(accountId);

        assertThat(result).isEmpty();
        verify(surplusRepository, times(1)).findByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2);
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


        final List<SurplusHistoryDTO> result = surplusQueryService.getFacilitySurplusHistory(facilityId);

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

        final List<SurplusHistoryDTO> result = surplusQueryService.getFacilitySurplusHistory(facilityId);

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

        Map<Long, BigDecimal> result = surplusQueryService.getFacilityPositiveSurplusMap(facilityIds, schemeVersion);

        assertThat(result)
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        1L, new BigDecimal("100"),
                        3L, new BigDecimal("15"),
                        4L, new BigDecimal("25")
                ));

        verify(surplusRepository).findAllByResourceIdInAndSchemeVersion(facilityIds, schemeVersion);
    }
    
    private SurplusEntity createSurplusEntity(Long facilityId, BigDecimal surplusGained) {
        return SurplusEntity.builder()
                .resourceId(facilityId)
                .schemeVersion(SchemeVersion.CCA_3)
                .surplusGained(surplusGained)
                .build();
    }
}