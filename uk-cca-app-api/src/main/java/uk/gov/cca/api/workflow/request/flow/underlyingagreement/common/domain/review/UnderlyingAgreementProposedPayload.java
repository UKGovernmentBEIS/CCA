package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review;

public interface UnderlyingAgreementProposedPayload<T> {

    T getEditedUnderlyingAgreement();

    T getProposedUnderlyingAgreement();
}
