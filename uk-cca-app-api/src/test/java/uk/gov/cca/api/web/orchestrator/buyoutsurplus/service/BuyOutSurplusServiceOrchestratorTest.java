package uk.gov.cca.api.web.orchestrator.buyoutsurplus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.service.SchemeTerminationHelper;
import uk.gov.cca.api.web.orchestrator.buyoutsurplus.dto.AvailableTargetPeriodsBuyOutDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.FacilityBuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodBuyOutDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusServiceOrchestratorTest {

    @InjectMocks
    private BuyOutSurplusServiceOrchestrator orchestrator;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Spy
    private ArrayList<BuyOutSurplusQueryService> buyOutSurplusQueryServices;

    @Mock
    private FacilityBuyOutSurplusQueryService facilityBuyOutSurplusQueryService;

    @Mock
    private SchemeTerminationHelper schemeTerminationHelper;

    @BeforeEach
    void setUp() {
        buyOutSurplusQueryServices.add(facilityBuyOutSurplusQueryService);
    }

    @Test
    void getExcludedAccountsForBuyOutSurplusRun() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;

        final TargetUnitAccountBusinessInfoDTO eligibleAccount = TargetUnitAccountBusinessInfoDTO.builder()
                .name("accountName")
                .build();

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType))
                .thenReturn(TargetPeriodInfoDTO.builder().schemeVersion(SchemeVersion.CCA_3).build());
        when(facilityBuyOutSurplusQueryService.getResourceType())
                .thenReturn(PerformanceDataResourceType.FACILITY);
        when(facilityBuyOutSurplusQueryService.getExcludedEligibleAccountsForBuyOutSurplusByTargetPeriod(targetPeriodType))
                .thenReturn(List.of(eligibleAccount));

        // Invoke
        List<TargetUnitAccountBusinessInfoDTO> results = orchestrator
                .getExcludedAccountsForBuyOutSurplusRun(targetPeriodType);

        // Verify
        assertThat(results).containsExactly(eligibleAccount);
        verify(targetPeriodService).getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(facilityBuyOutSurplusQueryService).getResourceType();
        verify(facilityBuyOutSurplusQueryService).getExcludedEligibleAccountsForBuyOutSurplusByTargetPeriod(targetPeriodType);
    }

    @Test
    void getExcludedAccountsForBuyOutSurplusRun_not_found() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType))
                .thenReturn(TargetPeriodInfoDTO.builder().schemeVersion(SchemeVersion.CCA_2).build());
        when(facilityBuyOutSurplusQueryService.getResourceType())
                .thenReturn(PerformanceDataResourceType.FACILITY);

        // Invoke
        List<TargetUnitAccountBusinessInfoDTO> results = orchestrator
                .getExcludedAccountsForBuyOutSurplusRun(targetPeriodType);

        // Verify
        assertThat(results).isEmpty();
        verify(targetPeriodService).getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(facilityBuyOutSurplusQueryService).getResourceType();
        verifyNoMoreInteractions(facilityBuyOutSurplusQueryService);
    }

    @Test
    void getAvailableBuyOutTargetPeriods() {
        LocalDate date = LocalDate.of(2026, 1, 1);

        TargetPeriod tp6 = TargetPeriod.builder()
                .id(1L)
                .businessId(TargetPeriodType.TP6)
                .schemeVersion(SchemeVersion.CCA_2)
                .buyOutStartDate(LocalDate.of(2023, 1, 1))
                .build();

        TargetPeriod cca3Current = TargetPeriod.builder()
                .id(2L)
                .schemeVersion(SchemeVersion.CCA_3)
                .buyOutStartDate(LocalDate.of(2025, 1, 1))
                .build();

        TargetPeriod cca3Previous = TargetPeriod.builder()
                .id(3L)
                .schemeVersion(SchemeVersion.CCA_3)
                .buyOutStartDate(LocalDate.of(2024, 1, 1))
                .build();

        when(targetPeriodService.getTargetPeriodBuyOutCurrentAndPrevious(date))
                .thenReturn(List.of(tp6, cca3Current, cca3Previous));
        when(schemeTerminationHelper.isAfterCca2TerminationDate(date)).thenReturn(false);

        AvailableTargetPeriodsBuyOutDTO result = orchestrator.getAvailableBuyOutTargetPeriods(date);

        assertThat(result.getCurrentTargetPeriods())
                .containsExactly(
                        TargetPeriodBuyOutDetailsDTO.builder().id(1L).businessId(TargetPeriodType.TP6).build(),
                        TargetPeriodBuyOutDetailsDTO.builder().id(2L).build());
        assertThat(result.getPreviousTargetPeriods())
                .containsExactly(
                        TargetPeriodBuyOutDetailsDTO.builder().id(3L).build());
        verify(targetPeriodService).getTargetPeriodBuyOutCurrentAndPrevious(date);
        verify(schemeTerminationHelper).isAfterCca2TerminationDate(date);
    }

    @Test
    void getAvailableBuyOutTargetPeriods_cca2NotActive_singleCca3Period() {
        LocalDate date = LocalDate.of(2026, 1, 1);

        TargetPeriod tp6 = TargetPeriod.builder()
                .id(1L)
                .businessId(TargetPeriodType.TP6)
                .schemeVersion(SchemeVersion.CCA_2)
                .buyOutStartDate(LocalDate.of(2024, 1, 1))
                .build();

        TargetPeriod cca3 = TargetPeriod.builder()
                .id(2L)
                .schemeVersion(SchemeVersion.CCA_3)
                .buyOutStartDate(LocalDate.of(2025, 1, 1))
                .build();

        when(targetPeriodService.getTargetPeriodBuyOutCurrentAndPrevious(date))
                .thenReturn(List.of(tp6, cca3));
        when(schemeTerminationHelper.isAfterCca2TerminationDate(date)).thenReturn(true);

        AvailableTargetPeriodsBuyOutDTO result = orchestrator.getAvailableBuyOutTargetPeriods(date);

        assertThat(result.getCurrentTargetPeriods())
                .containsExactly(
                        TargetPeriodBuyOutDetailsDTO.builder()
                                .id(2L)
                                .build());
        assertThat(result.getPreviousTargetPeriods()).isEmpty();
        verify(targetPeriodService).getTargetPeriodBuyOutCurrentAndPrevious(date);
        verify(schemeTerminationHelper).isAfterCca2TerminationDate(date);
    }
}
