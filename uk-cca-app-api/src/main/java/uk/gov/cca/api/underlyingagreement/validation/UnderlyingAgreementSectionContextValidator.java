package uk.gov.cca.api.underlyingagreement.validation;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;

public interface UnderlyingAgreementSectionContextValidator {

    BusinessValidationResult validate(UnderlyingAgreementContainer container);
}
