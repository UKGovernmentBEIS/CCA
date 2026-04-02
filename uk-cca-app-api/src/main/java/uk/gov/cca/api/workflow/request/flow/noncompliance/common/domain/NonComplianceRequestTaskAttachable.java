package uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain;

import java.util.Map;
import java.util.UUID;

public interface NonComplianceRequestTaskAttachable {
    Map<UUID, String> getNonComplianceAttachments();
}
