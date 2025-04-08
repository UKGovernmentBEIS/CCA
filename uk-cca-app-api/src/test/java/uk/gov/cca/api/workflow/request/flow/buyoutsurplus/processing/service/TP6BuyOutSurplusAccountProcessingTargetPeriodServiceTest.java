package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TP6BuyOutSurplusAccountProcessingTargetPeriodServiceTest {

    @InjectMocks
    private TP6BuyOutSurplusAccountProcessingTargetPeriodService tp6BuyOutSurplusAccountProcessingTargetPeriodService;

    @Mock
    private AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

    @Test
    void doProcess() throws Exception {
        final long accountId = 1L;
        final BuyOutSurplusAccountState accountState = BuyOutSurplusAccountState.builder().accountId(accountId).build();

        final PerformanceBuyOutSurplusDetailsDTO performanceReportDetails = PerformanceBuyOutSurplusDetailsDTO.builder().build();

        when(accountPerformanceDataStatusQueryService.getLastPerformanceBuyOutSurplusDetails(accountId, TargetPeriodType.TP6))
                .thenReturn(performanceReportDetails);

        // Invoke
        tp6BuyOutSurplusAccountProcessingTargetPeriodService.processBuyOutSurplus(accountState);

        // Verify
        verify(accountPerformanceDataStatusQueryService, times(1))
                .getLastPerformanceBuyOutSurplusDetails(accountId, TargetPeriodType.TP6);
    }

    @Test
    void getType() {
        assertThat(tp6BuyOutSurplusAccountProcessingTargetPeriodService.getType()).isEqualTo(TargetPeriodType.TP6);
    }
}
