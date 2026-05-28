package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityReferenceData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.service.PerformanceDataFacilityReferenceDataService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.transform.PerformanceDataFacilityDigitalFormSubmitMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormSubmitInitializer implements InitializeRequestTaskHandler {

    private final PerformanceDataFacilityReferenceDataService performanceDataFacilityDigitalFormReferenceDataService;
    private final PerformanceDataFacilityStatusQueryService performanceDataFacilityStatusQueryService;
    private static final PerformanceDataFacilityDigitalFormSubmitMapper MAPPER = Mappers
            .getMapper(PerformanceDataFacilityDigitalFormSubmitMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final PerformanceDataFacilityDigitalFormRequestPayload payload =
                (PerformanceDataFacilityDigitalFormRequestPayload) request.getPayload();
        final FacilityBaseInfoDTO facility = payload.getFacility();

        // Get original data
        final PerformanceDataFacilityReferenceData referenceData = performanceDataFacilityDigitalFormReferenceDataService
                .getReferenceData(request.getAccountId(), facility.getFacilityBusinessId(), payload.getTargetPeriodYear(), payload.getTargetPeriodType());

        // Get stored performance data if exists
        final PerformanceDataFacilityInputData performanceData = performanceDataFacilityStatusQueryService
                .getLastUploadedPerformanceDataContainer(facility.getId(), payload.getTargetPeriodYear())
                .map(MAPPER::toPerformanceDataFacilityInputData)
                .orElse(null);

        return PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_PAYLOAD)
                .targetPeriodType(payload.getTargetPeriodType())
                .reportType(payload.getReportType())
                .targetPeriodYear(payload.getTargetPeriodYear())
                .facility(facility)
                .referenceData(referenceData)
                .performanceData(performanceData)
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT);
    }
}
