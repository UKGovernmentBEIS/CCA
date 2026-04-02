package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormSubmitInitializer implements InitializeRequestTaskHandler {

    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final UnderlyingAgreementContainer una = underlyingAgreementQueryService
                .getUnderlyingAgreementContainerByAccountId(request.getAccountId());
        final PerformanceDataFacilityDigitalFormRequestPayload payload =
                (PerformanceDataFacilityDigitalFormRequestPayload) request.getPayload();
        final FacilityDTO facility = payload.getFacility();

        final Cca3FacilityBaselineAndTargets originalBaselineData = Stream.of(
                    una.getUnderlyingAgreement().getFacilities(),
                    una.getExcludedFacilities()
                ).flatMap(Collection::stream)
                .filter(f -> f.getFacilityItem().getFacilityId().equals(facility.getFacilityBusinessId()))
                .findFirst().map(f -> f.getFacilityItem().getCca3BaselineAndTargets())
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        // TODO transform data from previous performance data

        return PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_PAYLOAD)
                .targetPeriodType(payload.getTargetPeriodType())
                .reportType(payload.getReportType())
                .targetPeriodYear(payload.getTargetPeriodYear())
                .facility(facility)
                .originalBaselineData(originalBaselineData)
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT);
    }
}
