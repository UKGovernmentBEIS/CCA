package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PerformanceAccountTemplateProcessingMapper {

	@Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED_PAYLOAD)")
	@Mapping(target = "businessId", source = "metadata.accountBusinessId")
	PerformanceAccountTemplateProcessingSubmittedRequestActionPayload toSubmittedAction(
			PerformanceAccountTemplateProcessingRequestMetadata metadata,
			PerformanceAccountTemplateDataContainer data);
}
