package uk.gov.cca.api.workflow.request.flow.common.validation.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeterminationDataValidator {

    private final DataValidator<Determination> dataValidator;

    public BusinessValidationResult validateDetermination(final Determination determination) {
        List<ReviewDeterminationViolation> violations = new ArrayList<>();

        // Validate determination data
        dataValidator.validate(determination)
                .map(businessViolation ->
                        new ReviewDeterminationViolation(Determination.class.getName(),
                                ReviewDeterminationViolation.ReviewDeterminationViolationMessage.INVALID_DETERMINATION_DATA,
                                businessViolation.getData()))
                .ifPresent(violations::add);

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
