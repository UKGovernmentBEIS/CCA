package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRegulatorLedSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {CcaRequestActionPayloadType.class})
public interface UnderlyingAgreementVariationRegulatorLedSubmitMapper {

    @Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMITTED_PAYLOAD)")
    @Mapping(target = "determination", source = "payload.regulatorLedDetermination")
    @Mapping(target = "facilityChargeStartDateMap", source = "payload.regulatorLedFacilityChargeStartDateMap")
    @Mapping(target = "underlyingAgreement", source = "payload.underlyingAgreementProposed")
    @Mapping(target = "underlyingAgreementAttachments", ignore = true)
    @Mapping(target = "regulatorLedSubmitAttachments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    UnderlyingAgreementVariationRegulatorLedSubmittedRequestActionPayload toUnderlyingAgreementVariationRegulatorLedSubmittedRequestActionPayload(
            UnderlyingAgreementVariationRequestPayload payload, Map<String, RequestActionUserInfo> usersInfo, List<DefaultNoticeRecipient> defaultContacts);

    @AfterMapping
    default void setAttachments(@MappingTarget UnderlyingAgreementVariationRegulatorLedSubmittedRequestActionPayload actionPayload,
                                                           UnderlyingAgreementVariationRequestPayload payload) {
        actionPayload.setUnderlyingAgreementAttachments(payload.getUnderlyingAgreementAttachments());
        actionPayload.setRegulatorLedSubmitAttachments(payload.getRegulatorLedSubmitAttachments());
    }

    @Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_COMPLETED_PAYLOAD)")
    @Mapping(target = "determination", source = "payload.regulatorLedDetermination")
    @Mapping(target = "facilityChargeStartDateMap", source = "payload.regulatorLedFacilityChargeStartDateMap")
    @Mapping(target = "underlyingAgreement", source = "payload.underlyingAgreementProposed")
    @Mapping(target = "underlyingAgreementAttachments", ignore = true)
    @Mapping(target = "regulatorLedSubmitAttachments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    UnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload toUnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload(
            UnderlyingAgreementVariationRequestPayload payload, Map<String, RequestActionUserInfo> usersInfo, List<DefaultNoticeRecipient> defaultContacts);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "determination", source = "payload.regulatorLedDetermination")
    @Mapping(target = "facilityChargeStartDateMap", source = "payload.regulatorLedFacilityChargeStartDateMap")
    @Mapping(target = "underlyingAgreement", source = "payload.underlyingAgreementProposed")
    UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload toUnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload(String payloadType, UnderlyingAgreementVariationRequestPayload payload);
}
