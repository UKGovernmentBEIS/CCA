package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.flow.common.service.CalculateWorkingDaysService;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusPaymentDeadlineServiceTest {

    @InjectMocks
    private BuyOutSurplusPaymentDeadlineService service;

    @Mock
    private CalculateWorkingDaysService calculateWorkingDaysService;

    private static final CompetentAuthorityEnum CA_ENGLAND = CompetentAuthorityEnum.ENGLAND;
    private static final LocalDate CURRENT_DATE = LocalDate.of(2026, 10, 2);
    private static final LocalDate SECONDARY_DEADLINE = LocalDate.of(2026, 11, 15);

    @Test
    void shouldReturnPrimaryPaymentDeadlineForPrimarySingleTargetPeriod() {
        final LocalDate primaryDeadline = LocalDate.of(2026, 11, 30);
        TargetPeriodInfoDTO targetPeriodInfo = TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP6)
                .buyOutPrimaryPaymentDeadline(primaryDeadline)
                .build();

        LocalDate result = service.getBuyOutPaymentDeadline(
                CA_ENGLAND,
                PerformanceDataSubmissionType.PRIMARY,
                targetPeriodInfo,
                CURRENT_DATE
        );

        assertThat(result).isEqualTo(primaryDeadline);
        verifyNoInteractions(calculateWorkingDaysService);
    }

    @Test
    void shouldCalculateSecondaryPaymentDeadlineForSecondarySingleTargetPeriod() {
        final LocalDate primaryDeadline = LocalDate.of(2026, 11, 30);
        TargetPeriodInfoDTO targetPeriodInfo = TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP6)
                .buyOutPrimaryPaymentDeadline(primaryDeadline)
                .build();

        when(calculateWorkingDaysService.addWorkingDays(CURRENT_DATE, 30, CA_ENGLAND))
                .thenReturn(SECONDARY_DEADLINE);

        LocalDate result = service.getBuyOutPaymentDeadline(
                CA_ENGLAND,
                PerformanceDataSubmissionType.SECONDARY,
                targetPeriodInfo,
                CURRENT_DATE
        );

        assertThat(result).isEqualTo(SECONDARY_DEADLINE);
        verify(calculateWorkingDaysService)
                .addWorkingDays(CURRENT_DATE, 30, CA_ENGLAND);
    }

    @Test
    void getBuyOutPaymentDeadlinePerTargetPeriodWhenPrimaryRulesApply() {
        TargetPeriodInfoDTO tp7 = TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP7)
                .buyOutPrimaryPaymentDeadline(LocalDate.of(2026, 11, 30))
                .build();
        TargetPeriodInfoDTO tp8 = TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP8)
                .buyOutPrimaryPaymentDeadline(LocalDate.of(2027, 11, 30))
                .build();
        TargetPeriodInfoDTO tp9 = TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP9)
                .buyOutPrimaryPaymentDeadline(LocalDate.of(2027, 11, 30))
                .build();

        when(calculateWorkingDaysService.addWorkingDays(CURRENT_DATE, 30, CA_ENGLAND))
                .thenReturn(SECONDARY_DEADLINE);

        Map<TargetPeriodType, LocalDate> result =
                service.getBuyOutPaymentDeadlinePerTargetPeriod(
                        CA_ENGLAND,
                        true,
                        List.of(tp7, tp8, tp9),
                        tp9,
                        CURRENT_DATE
                );

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                TargetPeriodType.TP7, SECONDARY_DEADLINE,
                TargetPeriodType.TP8, SECONDARY_DEADLINE,
                TargetPeriodType.TP9, LocalDate.of(2027, 11, 30)
        ));
        verify(calculateWorkingDaysService, times(1))
                .addWorkingDays(CURRENT_DATE, 30, CA_ENGLAND);
    }

    @Test
    void getBuyOutPaymentDeadlinePerTargetPeriodWhenPrimaryRulesDoNotApply() {
        TargetPeriodInfoDTO tp7 = TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP7)
                .buyOutPrimaryPaymentDeadline(LocalDate.of(2026, 11, 30))
                .build();
        TargetPeriodInfoDTO tp8 = TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP8)
                .buyOutPrimaryPaymentDeadline(LocalDate.of(2027, 11, 30))
                .build();
        TargetPeriodInfoDTO tp9 = TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP9)
                .buyOutPrimaryPaymentDeadline(LocalDate.of(2027, 11, 30))
                .build();

        when(calculateWorkingDaysService.addWorkingDays(CURRENT_DATE, 30, CA_ENGLAND))
                .thenReturn(SECONDARY_DEADLINE);

        Map<TargetPeriodType, LocalDate> result =
                service.getBuyOutPaymentDeadlinePerTargetPeriod(
                        CA_ENGLAND,
                        false,
                        List.of(tp7, tp8, tp9),
                        tp9,
                        CURRENT_DATE
                );

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                TargetPeriodType.TP7, SECONDARY_DEADLINE,
                TargetPeriodType.TP8, SECONDARY_DEADLINE,
                TargetPeriodType.TP9, SECONDARY_DEADLINE
        ));
        verify(calculateWorkingDaysService, times(1))
                .addWorkingDays(CURRENT_DATE, 30, CA_ENGLAND);
    }
}

