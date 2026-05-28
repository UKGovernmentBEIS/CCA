package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface NonComplianceAppealOutcomeMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.NON_COMPLIANCE_APPEAL_OUTCOME_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "nonComplianceAttachments", ignore = true)
    NonComplianceAppealOutcomeSubmittedRequestActionPayload toNonComplianceAppealOutcomeSubmittedRequestActionPayload(NonComplianceRequestPayload requestPayload);

    @AfterMapping
    default void setNonComplianceAttachments(@MappingTarget NonComplianceAppealOutcomeSubmittedRequestActionPayload requestActionPayload, NonComplianceRequestPayload requestPayload) {
        requestActionPayload.setNonComplianceAttachments(requestPayload.getNonComplianceAttachments());
    }
}
