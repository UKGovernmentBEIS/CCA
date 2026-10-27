package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingResults;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {CcaRequestActionPayloadType.class})
public interface FacilityPerformanceAccountTemplateDataProcessingSubmittedMapper {

    @Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED_PAYLOAD)")
    @Mapping(target = "performanceData", source = "processingResults.container")
    @Mapping(target = "targetPeriodYear", source = "requestPayload.targetYear")
    FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload toFacilityPerformanceAccountTemplateDataFacilitySubmittedRequestActionPayload(FacilityPerformanceAccountTemplateProcessingRequestPayload requestPayload, FacilityPerformanceAccountTemplateProcessingResults processingResults);

}