package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.transform;

import java.util.List;
import java.util.Map;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementAcceptedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementRejectedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {CcaRequestActionPayloadType.class})
public interface UnderlyingAgreementReviewMapper {

	@Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "accountReferenceData", ignore = true)
	UnderlyingAgreementReviewRequestTaskPayload toUnderlyingAgreementReviewRequestTaskPayload(
			UnderlyingAgreementRequestPayload payload, String payloadType);

	@Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_ACCEPTED_PAYLOAD)")
	@Mapping(target = "underlyingAgreementAttachments", ignore = true)
	@Mapping(target = "reviewAttachments", ignore = true)
	@Mapping(target = "attachments", ignore = true)
	UnderlyingAgreementAcceptedRequestActionPayload toUnderlyingAgreementAcceptedRequestActionPayload(
			UnderlyingAgreementRequestPayload payload, Map<String, RequestActionUserInfo> usersInfo, List<DefaultNoticeRecipient> defaultContacts);

	@AfterMapping
    default void setAcceptedUnderlyingAgreementAttachments(@MappingTarget UnderlyingAgreementAcceptedRequestActionPayload requestActionPayload,
    		UnderlyingAgreementRequestPayload taskPayload) {
        requestActionPayload.setUnderlyingAgreementAttachments(taskPayload.getUnderlyingAgreementAttachments());
        requestActionPayload.setReviewAttachments(taskPayload.getReviewAttachments());
    }

	@Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_REJECTED_PAYLOAD)")
	@Mapping(target = "underlyingAgreementAttachments", ignore = true)
	@Mapping(target = "reviewAttachments", ignore = true)
	@Mapping(target = "attachments", ignore = true)
	UnderlyingAgreementRejectedRequestActionPayload toUnderlyingAgreementRejectedRequestActionPayload(
			UnderlyingAgreementRequestPayload payload, Map<String, RequestActionUserInfo> usersInfo, List<DefaultNoticeRecipient> defaultContacts);

	@AfterMapping
	default void setRejectedUnderlyingAgreementAttachments(@MappingTarget UnderlyingAgreementRejectedRequestActionPayload requestActionPayload,
												   UnderlyingAgreementRequestPayload taskPayload) {
		requestActionPayload.setUnderlyingAgreementAttachments(taskPayload.getUnderlyingAgreementAttachments());
		requestActionPayload.setReviewAttachments(taskPayload.getReviewAttachments());
	}
}
