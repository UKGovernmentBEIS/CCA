package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateFacilityAndAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormCreateActionHandlerTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormCreateActionHandler handler;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private RequestCreateFacilityAndAccountAndSectorResourcesService requestCreateFacilityAndAccountAndSectorResourcesService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {
        final long facilityId = 1L;
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final PerformanceDataFacilityDigitalFormRequestCreateActionPayload payload =
                PerformanceDataFacilityDigitalFormRequestCreateActionPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .build();

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2025))
                                        .reportingStartDate(LocalDate.of(2026, 1, 1))
                                        .build()
                        ))
                        .build())
                .build();
        final Map<String, String> resources = Map.of("facility", "1");
        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM)
                .requestResources(resources)
                .requestPayload(PerformanceDataFacilityDigitalFormRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REQUEST_PAYLOAD)
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(Year.of(2025))
                        .build())
                .requestMetadata(PerformanceDataFacilityDigitalFormRequestMetadata.builder()
                        .type(CcaRequestMetadataType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM)
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .build())
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);
        when(requestCreateFacilityAndAccountAndSectorResourcesService.createRequestResources(facilityId))
                .thenReturn(resources);
        when(startProcessRequestService.startProcess(requestParams))
                .thenReturn(Request.builder().id("request-id").build());

        // Invoke
        String result = handler.process(facilityId, payload, appUser);

        // Verify
        assertThat(result).isEqualTo("request-id");
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(requestCreateFacilityAndAccountAndSectorResourcesService, times(1))
                .createRequestResources(facilityId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void getRequestType() {
        assertThat(handler.getRequestType()).isEqualTo(CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM);
    }
}
