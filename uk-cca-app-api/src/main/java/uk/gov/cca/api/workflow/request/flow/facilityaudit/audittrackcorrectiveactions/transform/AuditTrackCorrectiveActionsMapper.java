package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AuditTrackCorrectiveActionsMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED_PAYLOAD)")
    AuditTrackCorrectiveActionsSubmittedRequestActionPayload toAuditTrackCorrectiveActionsSubmittedRequestActionPayload(AuditTrackCorrectiveActionsRequestTaskPayload taskPayload);
}
