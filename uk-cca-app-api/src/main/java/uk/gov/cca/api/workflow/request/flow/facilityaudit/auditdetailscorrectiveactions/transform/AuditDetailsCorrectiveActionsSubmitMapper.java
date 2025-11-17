package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AuditDetailsCorrectiveActionsSubmitMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMITTED_PAYLOAD)")
    AuditDetailsCorrectiveActionsSubmittedRequestActionPayload toAuditDetailsCorrectiveActionsSubmittedRequestActionPayload(AuditDetailsCorrectiveActionsSubmitRequestTaskPayload taskPayload);
}
