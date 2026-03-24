package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestCreateValidatorService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataDownloadCreateValidatorTest {

    @InjectMocks
    private PerformanceDataDownloadCreateValidator validator;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private CcaRequestCreateValidatorService ccaRequestCreateValidatorService;

    @Test
    void validateAction() {
        final Long sectorId = 1L;

        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder().targetYear(Year.of(2020)).reportingEndDate(LocalDate.of(2020, 1, 1)).build(),
                                TargetPeriodYear.builder().targetYear(Year.of(2021)).reportingEndDate(LocalDate.of(2021, 1, 1)).build(),
                                TargetPeriodYear.builder().targetYear(Year.of(2026)).build()
                        ))
                        .build())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(TargetPeriodType.TP6))
                .thenReturn(targetPeriodDetails);
        when(ccaRequestCreateValidatorService
                .validate(sectorId, CcaResourceType.SECTOR_ASSOCIATION, Set.of(CcaRequestType.PERFORMANCE_DATA_DOWNLOAD)))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(sectorId);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(TargetPeriodType.TP6);
        verify(ccaRequestCreateValidatorService, times(1))
                .validate(sectorId, CcaResourceType.SECTOR_ASSOCIATION, Set.of(CcaRequestType.PERFORMANCE_DATA_DOWNLOAD));
    }

    @Test
    void validateAction_not_available() {
        final Long sectorId = 1L;

        final TargetPeriodDetailsDTO targetPeriodDetails = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder().targetYear(Year.of(2020)).reportingEndDate(LocalDate.of(2020, 1, 1)).build(),
                                TargetPeriodYear.builder().targetYear(Year.of(2021)).reportingEndDate(LocalDate.of(2021, 1, 1)).build()
                        ))
                        .build())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(TargetPeriodType.TP6))
                .thenReturn(targetPeriodDetails);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(sectorId);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().isAvailable(false).build());
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodType(TargetPeriodType.TP6);
        verifyNoInteractions(ccaRequestCreateValidatorService);
    }

    @Test
    void getMutuallyExclusiveRequests() {
        assertThat(validator.getMutuallyExclusiveRequests())
                .containsExactly(CcaRequestType.PERFORMANCE_DATA_DOWNLOAD);
    }

    @Test
    void getRequestType() {
        assertThat(validator.getRequestType())
                .isEqualTo(CcaRequestType.PERFORMANCE_DATA_DOWNLOAD);
    }
}
