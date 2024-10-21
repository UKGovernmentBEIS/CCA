package uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UnderlyingAgreementSubmitMapper {

	@Mapping(target = "payloadType", source = "payloadType")
	@Mapping(target = "fileDocuments", ignore = true)
	@Mapping(target = "businessId", ignore = true)
	@Mapping(target = "underlyingAgreementAttachments", ignore = true)
	@Mapping(target = "attachments", ignore = true)
	UnderlyingAgreementSubmittedRequestActionPayload toUnderlyingAgreementSubmittedRequestActionPayload(
			UnderlyingAgreementSubmitRequestTaskPayload taskPayload, String payloadType);
	
	@AfterMapping
    default void setUnderlyingAgreementAttachments(@MappingTarget UnderlyingAgreementSubmittedRequestActionPayload requestActionPayload,
    		UnderlyingAgreementSubmitRequestTaskPayload taskPayload) {
        requestActionPayload.setUnderlyingAgreementAttachments(taskPayload.getUnderlyingAgreementAttachments());
    }
}
