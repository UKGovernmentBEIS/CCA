package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface Cca3ExistingFacilitiesMigrationAccountProcessingActivationMapper {

    UnderlyingAgreementContainer toUnderlyingAgreementContainer(UnderlyingAgreement underlyingAgreement,
                                                                Map<SchemeVersion, SchemeData> schemeDataMap,
                                                                Map<UUID, String> underlyingAgreementAttachments);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "activationAttachments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload toActivatedRequestActionPayload(
            Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload, String payloadType, Map<String, RequestActionUserInfo> usersInfo, List<DefaultNoticeRecipient> defaultContacts);

    @AfterMapping
    default void setUnderlyingAgreementAttachments(@MappingTarget Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload requestActionPayload,
                                                   Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload) {
        requestActionPayload.setActivationAttachments(payload.getActivationAttachments());
    }
}
