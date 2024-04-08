package uk.gov.cca.api.workflow.request.flow.payment.domain;

public interface RequestPayloadPayable {

    RequestPaymentInfo getRequestPaymentInfo();

    void setRequestPaymentInfo(RequestPaymentInfo requestPaymentInfo);
}
