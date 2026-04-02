package uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain;

import java.util.Set;
import java.util.UUID;

public interface NonComplianceRequestTaskClosable extends NonComplianceRequestTaskAttachable {

    NonComplianceCloseJustification getCloseJustification();

    void setCloseJustification(NonComplianceCloseJustification justification);

    Set<UUID> getReferencedAttachmentIds();
}
