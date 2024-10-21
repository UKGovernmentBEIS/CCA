package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationDetailsValidatorService {

    private final DataValidator<UnderlyingAgreementVariationDetails> validator;

    public BusinessValidationResult validate(final RequestTask requestTask) {
        List<UnderlyingAgreementVariationViolation> violations = new ArrayList<>();

        final UnderlyingAgreementVariationRequestTaskPayload taskPayload = (UnderlyingAgreementVariationRequestTaskPayload) requestTask.getPayload();
        final UnderlyingAgreementVariationDetails underlyingAgreementVariationDetails = taskPayload.getUnderlyingAgreement().getUnderlyingAgreementVariationDetails();

        if (ObjectUtils.isEmpty(underlyingAgreementVariationDetails)) {
            violations.add(new UnderlyingAgreementVariationViolation(UnderlyingAgreementVariationDetails.class.getName(),
                    UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_SECTION_DATA));
        } else {
            validator.validate(underlyingAgreementVariationDetails).map(businessViolation -> new UnderlyingAgreementVariationViolation(
                            UnderlyingAgreementVariationDetails.class.getName(),
                            UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_SECTION_DATA,
                            businessViolation.getData()))
                    .ifPresent(violations::add);

        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
