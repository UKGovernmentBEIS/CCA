package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UnderlyingAgreementVariationActivationMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "underlyingAgreementAttachments", ignore = true)
    @Mapping(target = "reviewAttachments", ignore = true)
    @Mapping(target = "underlyingAgreementActivationAttachments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    UnderlyingAgreementVariationActivatedRequestActionPayload toUnderlyingAgreementVariationActivatedRequestActionPayload(
            UnderlyingAgreementVariationRequestPayload payload, String payloadType, Map<String, RequestActionUserInfo> usersInfo, List<DefaultNoticeRecipient> defaultContacts);

    @AfterMapping
    default void setUnderlyingAgreementAttachments(@MappingTarget UnderlyingAgreementVariationActivatedRequestActionPayload requestActionPayload,
                                                   UnderlyingAgreementVariationRequestPayload payload) {
        requestActionPayload.setUnderlyingAgreementAttachments(payload.getUnderlyingAgreementAttachments());
        requestActionPayload.setReviewAttachments(payload.getReviewAttachments());
        requestActionPayload.setUnderlyingAgreementActivationAttachments(payload.getUnderlyingAgreementActivationAttachments());
    }
}
