package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmissionDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingResults;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.workflow.request.core.domain.Request;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {CcaRequestActionPayloadType.class})
public interface PerformanceDataFacilityProcessingSubmittedMapper {

    @Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.PERFORMANCE_DATA_FACILITY_SUBMITTED_PAYLOAD)")
    @Mapping(target = "details", expression = "java(toPerformanceDataFacilitySubmissionDetails(request, requestPayload, processingResults.getReportVersion()))")
    @Mapping(target = "performanceData", source = "processingResults.container")
    PerformanceDataFacilitySubmittedRequestActionPayload toPerformanceDataFacilitySubmittedRequestActionPayload(Request request, PerformanceDataFacilityProcessingRequestPayload requestPayload, PerformanceDataFacilityProcessingResults processingResults);

    @Mapping(target = "targetPeriodYear", source = "requestPayload.targetPeriodYear.targetYear")
    @Mapping(target = "submissionDate", source = "requestPayload.submissionDate")
    PerformanceDataFacilitySubmissionDetails toPerformanceDataFacilitySubmissionDetails(Request request, PerformanceDataFacilityProcessingRequestPayload requestPayload, int reportVersion);

    default PerformanceDataFacilitySubmittedRequestActionPayload toPerformanceDataFacilitySubmittedRequestActionPayload(Request request, PerformanceDataFacilityProcessingResults processingResults) {
        PerformanceDataFacilityProcessingRequestPayload requestPayload =
                (PerformanceDataFacilityProcessingRequestPayload) request.getPayload();

        return toPerformanceDataFacilitySubmittedRequestActionPayload(request, requestPayload, processingResults);
    }
}
