package uk.gov.cca.api.common.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DataValidator<T> {

    private final Validator validator;

    public Optional<BusinessViolation> validate(T section) {
        if(ObjectUtils.isEmpty(section)){
            return Optional.empty();
        }

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(section);

        if (!constraintViolations.isEmpty()) {
            BusinessViolation violation = new BusinessViolation(
                    section.getClass().getName(),
                    constructViolationData(constraintViolations));

            return Optional.of(violation);
        }

        return Optional.empty();
    }

    private List<String> constructViolationData(Set<ConstraintViolation<T>> constraintViolations) {
        List<String> violationData = new ArrayList<>();

        constraintViolations.forEach(constraintViolation ->
                violationData.add(String.format("%s - %s",constraintViolation.getPropertyPath(), constraintViolation.getMessage())));

        return violationData;
    }
}
