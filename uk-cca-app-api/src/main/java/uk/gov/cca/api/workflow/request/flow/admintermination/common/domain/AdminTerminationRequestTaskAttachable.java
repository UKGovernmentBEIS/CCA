package uk.gov.cca.api.workflow.request.flow.admintermination.common.domain;

import java.util.Map;
import java.util.UUID;

public interface AdminTerminationRequestTaskAttachable {
	Map<UUID, String> getAdminTerminationAttachments();
}
