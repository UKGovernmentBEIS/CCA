package uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NonComplianceType {

    FAILURE_TO_PROVIDE_TPR("Failure to provide the Target Period Report or Interim Target Period Report"),
    FAILURE_TO_PROVIDE_PR("Failure to provide the Performance Report"),
    FAILURE_TO_PROVIDE_OTHER_INFO("Failure to provide any other information requested by the administrator"),
    FAILURE_TO_NOTIFY_OF_AN_ERROR("Failure to notify us of an error in the base year data or any Report"),
    FAILURE_TO_NOTIFY_FACILITY_NO_LONGER_ELIGIBLE("Failure to notify that a facility is no longer eligible"),
    FAILURE_TO_NOTIFY_OTHER_REQUIREMENT_UNA("Failure to notify us of any other requirement set out in the underlying agreement"),
    INACCURATE_TPR_INFO("Providing inaccurate information in a Target Period Report or Interim Target Period Report"),
    INACCURATE_PR_INFO("Providing inaccurate information in a Performance Report"),
    INACCURATE_OTHER_INFO("Providing inaccurate any other information requested by the administrator");

    private final String description;
}
