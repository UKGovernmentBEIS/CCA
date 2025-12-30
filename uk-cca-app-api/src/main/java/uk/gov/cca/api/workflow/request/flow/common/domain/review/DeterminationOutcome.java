package uk.gov.cca.api.workflow.request.flow.common.domain.review;

public enum DeterminationOutcome {
    ACCEPTED,
    REJECTED,
    COMPLETED;

    public static DeterminationOutcome fromDeterminationType(DeterminationType determinationType, Boolean hasChanges) {
        if(Boolean.FALSE.equals(hasChanges)) {
            return COMPLETED;
        }

        return switch (determinationType) {
            case ACCEPTED -> ACCEPTED;
            case REJECTED -> REJECTED;
        };
    }
}
