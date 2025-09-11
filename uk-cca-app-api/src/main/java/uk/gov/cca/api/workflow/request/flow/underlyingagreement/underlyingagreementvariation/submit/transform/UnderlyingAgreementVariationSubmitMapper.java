package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UnderlyingAgreementVariationSubmitMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "fileDocuments", ignore = true)
    @Mapping(target = "businessId", ignore = true)
    @Mapping(target = "underlyingAgreementAttachments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    UnderlyingAgreementVariationSubmittedRequestActionPayload toUnderlyingAgreementVariationSubmittedRequestActionPayload(
            UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload, String payloadType);

    @AfterMapping
    default void setUnderlyingAgreementAttachments(@MappingTarget UnderlyingAgreementVariationSubmittedRequestActionPayload requestActionPayload,
                                                   UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload) {
        requestActionPayload.setUnderlyingAgreementAttachments(taskPayload.getUnderlyingAgreementAttachments());
    }
}
