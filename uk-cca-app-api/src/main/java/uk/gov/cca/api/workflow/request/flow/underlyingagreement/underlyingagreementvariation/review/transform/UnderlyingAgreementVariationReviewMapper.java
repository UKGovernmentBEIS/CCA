package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.transform;

import java.util.List;
import java.util.Map;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationAcceptedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationRejectedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {CcaRequestActionPayloadType.class})
public interface UnderlyingAgreementVariationReviewMapper {

	@Mapping(target = "schemeDataMap", source = "accountReferenceData.sectorAssociationDetails.schemeDataMap")
    @Mapping(target = "underlyingAgreement", source = "underlyingAgreementProposed.underlyingAgreement")
    UnderlyingAgreementContainer toUnderlyingAgreementProposedContainer(
            UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "accountReferenceData", ignore = true)
    UnderlyingAgreementVariationReviewRequestTaskPayload toUnderlyingAgreementVariationReviewRequestTaskPayload(
            UnderlyingAgreementVariationRequestPayload payload, String payloadType);

    @Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PAYLOAD)")
	@Mapping(target = "underlyingAgreementAttachments", ignore = true)
	@Mapping(target = "reviewAttachments", ignore = true)
	@Mapping(target = "attachments", ignore = true)
	UnderlyingAgreementVariationAcceptedRequestActionPayload toUnderlyingAgreementVariationAcceptedRequestActionPayload(
			UnderlyingAgreementVariationRequestPayload payload, Map<String, RequestActionUserInfo> usersInfo, List<DefaultNoticeRecipient> defaultContacts);
    
    @AfterMapping
    default void setAcceptedUnderlyingAgreementAttachments(@MappingTarget UnderlyingAgreementVariationAcceptedRequestActionPayload requestActionPayload,
    		UnderlyingAgreementVariationRequestPayload payload) {
        requestActionPayload.setUnderlyingAgreementAttachments(payload.getUnderlyingAgreementAttachments());
        requestActionPayload.setReviewAttachments(payload.getReviewAttachments());
    }

    @Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_REJECTED_PAYLOAD)")
    @Mapping(target = "underlyingAgreementAttachments", ignore = true)
    @Mapping(target = "reviewAttachments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    UnderlyingAgreementVariationRejectedRequestActionPayload toUnderlyingAgreementVariationRejectedRequestActionPayload(
            UnderlyingAgreementVariationRequestPayload payload, Map<String, RequestActionUserInfo> usersInfo, List<DefaultNoticeRecipient> defaultContacts);

    @AfterMapping
    default void setRejectedUnderlyingAgreementVariationAttachments(@MappingTarget UnderlyingAgreementVariationRejectedRequestActionPayload requestActionPayload,
                                                                    UnderlyingAgreementVariationRequestPayload taskPayload) {
        requestActionPayload.setUnderlyingAgreementAttachments(taskPayload.getUnderlyingAgreementAttachments());
        requestActionPayload.setReviewAttachments(taskPayload.getReviewAttachments());
    }
}
