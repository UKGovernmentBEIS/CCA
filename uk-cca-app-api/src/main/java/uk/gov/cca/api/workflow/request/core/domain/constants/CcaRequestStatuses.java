package uk.gov.cca.api.workflow.request.core.domain.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CcaRequestStatuses {

    public final String CANCELLED = "CANCELLED";
    public final String MIGRATED = "MIGRATED";
    public final String COMPLETED_WITH_FAILURES = "COMPLETED_WITH_FAILURES";

}
