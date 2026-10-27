package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.netz.api.workflow.request.core.domain.RequestCreateActionPayload;

import java.time.Year;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#targetYear == null) " +
        "|| (!T(java.time.Year).parse(#targetYear).isBefore(T(java.time.Year).of(2026)) && !T(java.time.Year).parse(#targetYear).isAfter(T(java.time.Year).of(2030)))}",
        message = "emissionstradingschemedata.targetYear")
public class EmissionsTradingSchemeDataRequestCreateActionPayload extends RequestCreateActionPayload {

    @NotNull
    private Year targetYear;
}
