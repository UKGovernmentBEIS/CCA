package uk.gov.cca.api.workflow.request.core.domain;

import uk.gov.cca.api.verificationbody.domain.verificationreport.VerificationReport;

public interface RequestPayloadVerifiable<T extends VerificationReport> extends Payload {

    boolean isVerificationPerformed();

    void setVerificationPerformed(boolean verificationPerformed);

    T getVerificationReport();

    void setVerificationReport(T verificationReport);
}
