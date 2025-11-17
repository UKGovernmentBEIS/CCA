package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain;

import java.util.Map;
import java.util.UUID;

public interface FacilityAuditRequestTaskAttachable {
    Map<UUID, String> getFacilityAuditAttachments();
}
