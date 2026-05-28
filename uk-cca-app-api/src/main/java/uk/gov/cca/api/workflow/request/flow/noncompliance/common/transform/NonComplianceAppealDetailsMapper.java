package uk.gov.cca.api.workflow.request.flow.noncompliance.common.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceAppealDetailsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface NonComplianceAppealDetailsMapper {
    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.NON_COMPLIANCE_APPEAL_DETAILS_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "nonComplianceAttachments", ignore = true)
    NonComplianceAppealDetailsSubmittedRequestActionPayload toNonComplianceAppealDetailsSubmittedRequestActionPayload(NonComplianceRequestPayload requestPayload);

    @AfterMapping
    default void setNonComplianceAttachments(@MappingTarget NonComplianceAppealDetailsSubmittedRequestActionPayload requestActionPayload, NonComplianceRequestPayload requestPayload) {
        requestActionPayload.setNonComplianceAttachments(requestPayload.getNonComplianceAttachments());
    }
}