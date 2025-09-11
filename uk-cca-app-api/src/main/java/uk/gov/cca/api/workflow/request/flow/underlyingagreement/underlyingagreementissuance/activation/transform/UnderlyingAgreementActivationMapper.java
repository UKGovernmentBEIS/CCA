package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.transform;

import java.util.List;
import java.util.Map;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.domain.UnderlyingAgreementActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UnderlyingAgreementActivationMapper {

	@Mapping(target = "payloadType", source = "payloadType")
	@Mapping(target = "underlyingAgreementAttachments", ignore = true)
	@Mapping(target = "reviewAttachments", ignore = true)
	@Mapping(target = "underlyingAgreementActivationAttachments", ignore = true)
	@Mapping(target = "attachments", ignore = true)
	UnderlyingAgreementActivatedRequestActionPayload toUnderlyingAgreementActivatedRequestActionPayload(
			UnderlyingAgreementRequestPayload payload, String payloadType, Map<String, RequestActionUserInfo> usersInfo, List<DefaultNoticeRecipient> defaultContacts);

	@AfterMapping
    default void setUnderlyingAgreementAttachments(@MappingTarget UnderlyingAgreementActivatedRequestActionPayload requestActionPayload,
    		UnderlyingAgreementRequestPayload payload) {
        requestActionPayload.setUnderlyingAgreementAttachments(payload.getUnderlyingAgreementAttachments());
        requestActionPayload.setReviewAttachments(payload.getReviewAttachments());
        requestActionPayload.setUnderlyingAgreementActivationAttachments(payload.getUnderlyingAgreementActivationAttachments());
    }
}
