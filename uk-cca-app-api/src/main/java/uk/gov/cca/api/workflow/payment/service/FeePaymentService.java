package uk.gov.cca.api.workflow.payment.service;

import uk.gov.cca.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.cca.api.workflow.request.core.domain.Request;

import java.math.BigDecimal;

public interface FeePaymentService {

    BigDecimal getAmount(Request request);

    FeeMethodType getFeeMethodType();
}
