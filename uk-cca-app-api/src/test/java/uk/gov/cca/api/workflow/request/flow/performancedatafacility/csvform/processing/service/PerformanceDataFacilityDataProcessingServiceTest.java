package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;
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

        final Map<Long, UnderlyingAgreementContainer> underlyingAgreementAccountMap = Map.of(
                1L, UnderlyingAgreementContainer.builder().build()
        );

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(underlyingAgreementQueryService.getUnderlyingAgreementContainersByAccounts(Set.of(1L)))
                .thenReturn(underlyingAgreementAccountMap);

        // Invoke
        performanceDataFacilityDataProcessingService.setAdditionalRequestPayloadData(requestId, facilityReports);

        // Verify
        assertThat(payload.getUnderlyingAgreementAccountMap()).containsExactlyEntriesOf(underlyingAgreementAccountMap);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementContainersByAccounts(Set.of(1L));
    }
}
