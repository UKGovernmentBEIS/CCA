package uk.gov.cca.api.targetperiodreporting.targetperiod.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodYearDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.repository.TargetPeriodRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetPeriodServiceTest {

    @InjectMocks
    private TargetPeriodService service;

    @Mock
    private TargetPeriodRepository repository;

    @Test
    void getTargetPeriodDetailsByTargetPeriodType() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

        final TargetPeriodYearsContainer container = TargetPeriodYearsContainer.builder()
                .targetPeriodYears(List.of(
                        TargetPeriodYear.builder()
                                .startDate(LocalDate.of(2024, 1, 1))
                                .endDate(LocalDate.of(2100, 1, 1))
                                .reportingStartDate(LocalDate.of(2025, 1, 1))
                                .build(),
                        TargetPeriodYear.builder()
                                .startDate(LocalDate.of(2020, 1, 1))
                                .endDate(LocalDate.of(2023, 12, 31))
                                .reportingStartDate(LocalDate.of(2024, 1, 1))
                                .reportingEndDate(LocalDate.of(2024, 12, 31))
                                .build()))
                .build();
        final TargetPeriod targetPeriod = TargetPeriod.builder()
                .id(1L)
                .businessId(targetPeriodType)
                .name("Test")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2100, 1, 1))
                .performanceDataTemplateVersion("6.0")
                .targetPeriodYearsContainer(container)
                .buyOutStartDate(LocalDate.of(2021, 1, 1))
                .buyOutPrimaryPaymentDeadline(LocalDate.of(2022, 1, 1))
                .secondaryReportingStartDate(LocalDate.of(2023, 1, 1))
                .build();
        final TargetPeriodDetailsDTO expected = TargetPeriodDetailsDTO.builder()
                .id(1L)
                .businessId(targetPeriodType)
                .name("Test")
                .targetPeriodYearsContainer(container)
                .buyOutStartDate(LocalDate.of(2021, 1, 1))
                .buyOutPrimaryPaymentDeadline(LocalDate.of(2022, 1, 1))
                .secondaryReportingStartDate(LocalDate.of(2023, 1, 1))
                .build();

        when(repository.findByBusinessId(targetPeriodType)).thenReturn(Optional.of(targetPeriod));

        // Invoke
        TargetPeriodDetailsDTO result = service.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);

        // Verify
        assertThat(result).isEqualTo(expected);
        verify(repository).findByBusinessId(targetPeriodType);
    }

    @Test
    void getTargetPeriodInfoByTargetPeriodType() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final TargetPeriod targetPeriod = TargetPeriod.builder()
                .id(1L)
                .businessId(targetPeriodType)
                .name("Test")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2100, 1, 1))
                .buyOutStartDate(LocalDate.of(2021, 1, 1))
                .buyOutPrimaryPaymentDeadline(LocalDate.of(2022, 1, 1))
                .secondaryReportingStartDate(LocalDate.of(2023, 1, 1))
                .build();
        final TargetPeriodInfoDTO expected = TargetPeriodInfoDTO.builder()
                .id(1L)
                .businessId(targetPeriodType)
                .name("Test")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2100, 1, 1))
                .buyOutStartDate(LocalDate.of(2021, 1, 1))
                .buyOutPrimaryPaymentDeadline(LocalDate.of(2022, 1, 1))
                .secondaryReportingStartDate(LocalDate.of(2023, 1, 1))
                .build();

        when(repository.findByBusinessId(targetPeriodType)).thenReturn(Optional.of(targetPeriod));

        // Invoke
        TargetPeriodInfoDTO result = service.getTargetPeriodInfoByTargetPeriodType(targetPeriodType);

        // Verify
        assertThat(result).isEqualTo(expected);
        verify(repository).findByBusinessId(targetPeriodType);
    }

    @Test
    void getTargetPeriodByTargetPeriodTypeAndTargetYear() {
        final TargetPeriodType businessId = TargetPeriodType.TP6;

        final TargetPeriod targetPeriod = TargetPeriod.builder()
                .id(1L)
                .businessId(businessId)
                .name("Test")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2100, 1, 1))
                .performanceDataTemplateVersion("6.0")
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2024))
                                        .startDate(LocalDate.of(2024, 1, 1))
                                        .endDate(LocalDate.of(2100, 1, 1))
                                        .reportingStartDate(LocalDate.of(2025, 1, 1))
                                        .build(),
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2020))
                                        .startDate(LocalDate.of(2020, 1, 1))
                                        .endDate(LocalDate.of(2023, 12, 31))
                                        .reportingStartDate(LocalDate.of(2024, 1, 1))
                                        .reportingEndDate(LocalDate.of(2024, 12, 31))
                                        .build()
                        ))
                        .build())
                .buyOutStartDate(LocalDate.of(2021, 1, 1))
                .buyOutPrimaryPaymentDeadline(LocalDate.of(2022, 1, 1))
                .secondaryReportingStartDate(LocalDate.of(2023, 1, 1))
                .build();
        final TargetPeriodYearDTO expected = TargetPeriodYearDTO.builder()
                .id(1L)
                .businessId(businessId)
                .name("Test")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2100, 1, 1))
                .performanceDataTemplateVersion("6.0")
                .performanceDataStartDate(LocalDate.of(2025, 1, 1))
                .buyOutStartDate(LocalDate.of(2021, 1, 1))
                .buyOutEndDate(LocalDate.of(2022, 1, 1))
                .isCurrent(false)
                .secondaryReportingStartDate(LocalDate.of(2023, 1, 1))
                .build();

        when(repository.findByBusinessId(businessId)).thenReturn(Optional.of(targetPeriod));

        // Invoke
        TargetPeriodYearDTO result = service.getTargetPeriodByTargetPeriodTypeAndTargetYear(businessId, Year.of(2024));

        // Verify
        assertThat(result).isEqualTo(expected);
        verify(repository).findByBusinessId(businessId);
    }

    @Test
    void getTargetPeriodByTargetPeriodTypeAndTargetYear_notFound() {
        final TargetPeriodType businessId = TargetPeriodType.TP6;

        when(repository.findByBusinessId(businessId)).thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrowsExactly(BusinessException.class,
                () -> service.getTargetPeriodByTargetPeriodTypeAndTargetYear(businessId, Year.of(2024)));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(repository).findByBusinessId(businessId);
    }

    @Test
    void findTargetPeriodNameById() {
        final TargetPeriodType businessId = TargetPeriodType.TP6;
        final String name = "name";
        final TargetPeriod targetPeriod = TargetPeriod.builder()
                .name(name)
                .businessId(businessId)
                .build();

        when(repository.findByBusinessId(businessId)).thenReturn(Optional.of(targetPeriod));

        // Invoke
        String resultName = service.findTargetPeriodNameByTargetPeriodType(businessId);

        // Verify
        assertThat(resultName).isEqualTo(name);
        verify(repository).findByBusinessId(businessId);
    }

    @Test
    void findTargetPeriodNameById_not_found() {
        final TargetPeriodType businessId = TargetPeriodType.TP6;

        when(repository.findByBusinessId(businessId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrowsExactly(BusinessException.class,
                () -> service.findTargetPeriodNameByTargetPeriodType(businessId));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(repository).findByBusinessId(businessId);
    }
}

