package uk.gov.cca.api.workflow.request.core.domain;

public interface RequestTaskPayloadVerifiable {

    boolean isVerificationPerformed();

    void setVerificationPerformed(boolean verificationPerformed);
}
