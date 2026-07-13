package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacility;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmissionDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestPayload;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {BigDecimal.class, CcaRequestActionPayloadType.class})
public interface PerformanceDataFacilityDigitalFormSubmittedMapper {

    @Mapping(target = "facilityId", source = "requestPayload.facility.id")
    PerformanceDataFacility toPerformanceDataFacility(PerformanceDataFacilityDigitalFormRequestPayload requestPayload, PerformanceDataFacilityContainer data);

    @Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.PERFORMANCE_DATA_FACILITY_SUBMITTED_PAYLOAD)")
    @Mapping(target = "details", expression = "java(toPerformanceDataFacilitySubmissionDetails(request, requestPayload))")
    @Mapping(target = "performanceData", source = "performanceData")
    PerformanceDataFacilitySubmittedRequestActionPayload toPerformanceDataFacilitySubmittedRequestActionPayload(Request request, PerformanceDataFacilityDigitalFormRequestPayload requestPayload, PerformanceDataFacilityContainer performanceData);

    PerformanceDataFacilitySubmissionDetails toPerformanceDataFacilitySubmissionDetails(Request request, PerformanceDataFacilityDigitalFormRequestPayload requestPayload);

    default PerformanceDataFacilitySubmittedRequestActionPayload toPerformanceDataFacilitySubmittedRequestActionPayload(Request request, PerformanceDataFacilityContainer performanceData) {
        PerformanceDataFacilityDigitalFormRequestPayload requestPayload =
                (PerformanceDataFacilityDigitalFormRequestPayload) request.getPayload();

        return toPerformanceDataFacilitySubmittedRequestActionPayload(request, requestPayload, performanceData);
    }
}
