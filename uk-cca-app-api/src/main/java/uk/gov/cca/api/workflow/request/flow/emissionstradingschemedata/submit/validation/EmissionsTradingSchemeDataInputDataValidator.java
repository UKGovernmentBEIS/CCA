package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.validation.EmissionsTradingSchemeDataViolation;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputDetails;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmissionsTradingSchemeDataInputDataValidator {

    private final DataValidator<EmissionsTradingSchemeDataInputDetails> dataValidator;

    public BusinessValidationResult validateInputData(EmissionsTradingSchemeDataInputDetails details) {

        List<EmissionsTradingSchemeDataViolation> violations = new ArrayList<>();

        if (ObjectUtils.isEmpty(details)) {
            EmissionsTradingSchemeDataViolation violation = new EmissionsTradingSchemeDataViolation(EmissionsTradingSchemeDataInputDetails.class.getName(),
                    EmissionsTradingSchemeDataViolation.EmissionsTradingSchemeDataViolationMessage.INVALID_EMISSIONS_TRADING_SCHEME_DATA);

            return BusinessValidationResult.builder()
                    .violations(List.of(violation))
                    .valid(false)
                    .build();
        }

        dataValidator.validate(details).map(businessViolation ->
                        new EmissionsTradingSchemeDataViolation(EmissionsTradingSchemeDataInputDetails.class.getName(),
                                EmissionsTradingSchemeDataViolation.EmissionsTradingSchemeDataViolationMessage.INVALID_EMISSIONS_TRADING_SCHEME_DATA, businessViolation.getData()))
                .ifPresent(violations::add);

        return BusinessValidationResult.builder()
                .violations(violations)
                .valid(violations.isEmpty())
                .build();
    }

}
