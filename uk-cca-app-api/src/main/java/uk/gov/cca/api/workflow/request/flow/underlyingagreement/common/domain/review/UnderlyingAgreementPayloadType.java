package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review;

public enum UnderlyingAgreementPayloadType {
    PROPOSED,
    EDITED;

    @Override
    public String toString() {
        return "["+super.name()+"]";
    }
}
