package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDataProcessingServiceTest {

    @InjectMocks
    private PerformanceDataFacilityDataProcessingService performanceDataFacilityDataProcessingService;

    @Mock
    private RequestService requestService;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Test
    void setAdditionalRequestPayloadData() {
        final String requestId = "requestId";
        final Map<Long, FacilityUploadReport> facilityReports = Map.of(
                11L, FacilityUploadReport.builder().accountId(1L).build()
        );

        final PerformanceDataFacilityDataProcessingRequestPayload payload =
                PerformanceDataFacilityDataProcessingRequestPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .reportType(PerformanceDataReportType.FINAL)
                        .submissionDate(LocalDate.of(2026, 1, 1).atStartOfDay())
                        .build();
        final Request request = Request.builder()
                .payload(payload)
                .build();
        final TargetPeriodYear targetPeriodYear = TargetPeriodYear.builder()
                .targetYear(Year.of(2025))
                .reportingStartDate(LocalDate.of(2025, 1, 1))
                .build();
        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .businessId(TargetPeriodType.TP7)
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(targetPeriodYear))
                        .build()
                )
                .secondaryReportingStartDate(LocalDate.of(2025, 7, 1))
                .build();
        final Map<Long, UnderlyingAgreementContainer> underlyingAgreementAccountMap = Map.of(
                1L, UnderlyingAgreementContainer.builder().build()
        );

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodTypes(
                Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9))).thenReturn(List.of(targetPeriod));
        when(underlyingAgreementQueryService.getUnderlyingAgreementContainersByAccounts(Set.of(1L)))
                .thenReturn(underlyingAgreementAccountMap);

        // Invoke
        performanceDataFacilityDataProcessingService.setAdditionalRequestPayloadData(requestId, facilityReports);

        // Verify
        assertThat(payload.getSubmissionType()).isEqualTo(PerformanceDataSubmissionType.SECONDARY);
        assertThat(payload.getTargetPeriodYear()).isEqualTo(targetPeriodYear);
        assertThat(payload.getTargetPeriods()).containsExactly(targetPeriod);
        assertThat(payload.getUnderlyingAgreementAccountMap()).containsExactlyEntriesOf(underlyingAgreementAccountMap);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementContainersByAccounts(Set.of(1L));
    }
}
