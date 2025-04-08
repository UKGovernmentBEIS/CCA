package uk.gov.cca.api.workflow.request.flow.common.domain.review;

public enum UnderlyingAgreementPayloadType {
    PROPOSED,
    EDITED;

    @Override
    public String toString() {
        return "["+super.name()+"]";
    }
}
