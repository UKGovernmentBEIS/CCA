package uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain;

import java.util.Set;
import java.util.UUID;

public interface NonComplianceRequestTaskAppealable extends NonComplianceRequestTaskAttachable {

    NonComplianceAppealDetails getAppealDetails();

    void setAppealDetails(NonComplianceAppealDetails appealDetails);

    Set<UUID> getReferencedAttachmentIds();
}