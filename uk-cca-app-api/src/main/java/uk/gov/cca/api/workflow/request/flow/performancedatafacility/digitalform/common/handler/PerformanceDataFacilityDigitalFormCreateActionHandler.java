package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.util.PerformanceDataFacilityUtil;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.RequestFacilityCreateActionHandler;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateFacilityAndAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormCreateActionHandler
        implements RequestFacilityCreateActionHandler<PerformanceDataFacilityDigitalFormRequestCreateActionPayload> {

    private final TargetPeriodService targetPeriodService;
    private final RequestCreateFacilityAndAccountAndSectorResourcesService requestCreateFacilityAndAccountAndSectorResourcesService;
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long facilityId, PerformanceDataFacilityDigitalFormRequestCreateActionPayload payload, AppUser appUser) {
        final TargetPeriodDetailsDTO targetPeriod = targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(payload.getTargetPeriodType());
        final TargetPeriodYear targetPeriodYear = PerformanceDataFacilityUtil.getTargetPeriodYearBySubmissionDate(targetPeriod, LocalDate.now())
                .orElseThrow(() -> new BusinessException(CcaErrorCode.TARGET_PERIOD_YEAR_NOT_FOUND));

        CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM)
                .requestResources(requestCreateFacilityAndAccountAndSectorResourcesService.createRequestResources(facilityId))
                .requestPayload(PerformanceDataFacilityDigitalFormRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REQUEST_PAYLOAD)
                        .targetPeriodType(payload.getTargetPeriodType())
                        .reportType(payload.getReportType())
                        .targetPeriodYear(targetPeriodYear.getTargetYear())
                        .build())
                .requestMetadata(PerformanceDataFacilityDigitalFormRequestMetadata.builder()
                        .type(CcaRequestMetadataType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM)
                        .targetPeriodType(payload.getTargetPeriodType())
                        .reportType(payload.getReportType())
                        .build())
                .build();

        Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM;
    }
}
