package uk.gov.cca.api.targetperiodreporting.facilitycertification.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.common.domain.HistoryPayload;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{((#certificationStatus == 'CERTIFIED') == (#startDate != null))}",
        message = "facilityCertification.status.startDate.invalid")
@SpELExpression(expression = "{#comments != null && !#comments.trim().isEmpty()}",
        message = "facilityCertification.status.comments.notEmpty")
public class FacilityCertificationHistoryPayload extends HistoryPayload {

    @NotNull
    private FacilityCertificationStatus certificationStatus;

    @PastOrPresent
    private LocalDate startDate;
}
