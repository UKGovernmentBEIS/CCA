package uk.gov.cca.api.workflow.request.flow.common.domain.review;

public interface UnderlyingAgreementProposedPayload<T> {

    T getEditedUnderlyingAgreement();

    T getProposedUnderlyingAgreement();
}
