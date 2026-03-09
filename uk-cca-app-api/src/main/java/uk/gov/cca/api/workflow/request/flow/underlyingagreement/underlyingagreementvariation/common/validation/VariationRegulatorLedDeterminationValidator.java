package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationRegulatorLedDetermination;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VariationRegulatorLedDeterminationValidator {

    private final DataValidator<VariationRegulatorLedDetermination> variationRegulatorLedDeterminationDataValidator;

    public BusinessValidationResult validate(final VariationRegulatorLedDetermination determination) {
        List<UnderlyingAgreementVariationViolation> violations = new ArrayList<>();

        // Validate data
        if (ObjectUtils.isEmpty(determination)) {
            violations.add(new UnderlyingAgreementVariationViolation(VariationRegulatorLedDetermination.class.getName(),
                    UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_VARIATION_REGULATOR_LED_DETERMINATION));
        } else {
            variationRegulatorLedDeterminationDataValidator.validate(determination)
                    .map(businessViolation ->
                            new UnderlyingAgreementVariationViolation(VariationRegulatorLedDetermination.class.getName(),
                                    UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_VARIATION_REGULATOR_LED_DETERMINATION,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
