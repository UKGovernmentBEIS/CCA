package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public abstract class UnderlyingAgreementVariationDetailsValidatorService<T extends UnderlyingAgreementVariationRequestTaskPayload> {
    private final DataValidator<UnderlyingAgreementVariationDetails> validator;

    public abstract UnderlyingAgreementVariationPayload getUnderlyingAgreementPayload(T taskPayload);

    public abstract String getPayloadType();

    public BusinessValidationResult validate(T taskPayload) {
        List<UnderlyingAgreementVariationViolation> violations = new ArrayList<>();
        final UnderlyingAgreementVariationDetails underlyingAgreementVariationDetails = this.getUnderlyingAgreementPayload(taskPayload).getUnderlyingAgreementVariationDetails();
        if (ObjectUtils.isEmpty(underlyingAgreementVariationDetails)) {
            violations.add(new UnderlyingAgreementVariationViolation(this.getPayloadType() + UnderlyingAgreementVariationDetails.class.getName(), UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_SECTION_DATA));
        } else {
            validator.validate(underlyingAgreementVariationDetails).map(businessViolation -> new UnderlyingAgreementVariationViolation(this.getPayloadType() + UnderlyingAgreementVariationDetails.class.getName(), UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_SECTION_DATA, businessViolation.getData())).ifPresent(violations::add);
        }
        return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
    }
}
