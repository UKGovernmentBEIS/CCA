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
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class DataValidator<T> {

    private final Validator validator;

    public Optional<BusinessViolation> validate(T section) {
        return this.validate(section, this::constructViolationData);
    }

    public Optional<BusinessViolation> validate(T section, Function<Set<ConstraintViolation<T>>, List<String>> constructViolationData) {
        if (ObjectUtils.isEmpty(section)) {
            return Optional.empty();
        }

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(section);

        if (!constraintViolations.isEmpty()) {
            BusinessViolation violation = new BusinessViolation(
                    section.getClass().getName(),
                    constructViolationData.apply(constraintViolations).toArray());

            return Optional.of(violation);
        }

        return Optional.empty();
    }

    private List<String> constructViolationData(Set<ConstraintViolation<T>> constraintViolations) {
        List<String> violationData = new ArrayList<>();

        constraintViolations.forEach(constraintViolation -> {
            String violationMessage = Optional.ofNullable(constraintViolation.getPropertyPath())
                    .filter(path -> !path.toString().isBlank())
                    .map(path -> "%s - %s".formatted(path, constraintViolation.getMessage()))
                    .orElse(constraintViolation.getMessage());
            violationData.add(violationMessage);
        });

        return violationData;
    }
}
