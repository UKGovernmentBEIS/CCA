package uk.gov.cca.api.underlyingagreement.domain.facilities;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{!T(java.time.LocalDate).parse(#baselineDate).isBefore(T(java.time.LocalDate).of(2022, 1, 1))}", 
		message = "underlyingagreement.facilities.baselineData.baselineDate")
@SpELExpression(expression = "{" +
        "(((T(java.lang.Boolean).TRUE.equals(#isTwelveMonths) && #baselineDate != null && T(java.time.LocalDate).parse(#baselineDate) != T(java.time.LocalDate).of(2022, 1, 1)) " +
        "|| T(java.lang.Boolean).FALSE.equals(#isTwelveMonths)) == (#explanation != null)) " +
        "}", message = "underlyingagreement.facilities.baselineData.explanation")
@SpELExpression(expression = "{(T(java.lang.Boolean).TRUE.equals(#isTwelveMonths) && (#greenfieldEvidences?.size() <= 0)) " +
        "|| T(java.lang.Boolean).FALSE.equals(#isTwelveMonths)" +
        "}", message = "underlyingagreement.facilities.baselineData.greenfieldEvidences")
public class FacilityBaselineData {

    @NotNull
    private Boolean isTwelveMonths;

    @NotNull
    private LocalDate baselineDate;

    private String explanation;

    @Builder.Default
    private Set<UUID> greenfieldEvidences = new HashSet<>();

    // This field remains in the model to support the timeline events of UNAs submitted before the split-by-product functionality was implemented
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal energy;

    @NotNull
    private Boolean usedReportingMechanism;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal energyCarbonFactor;
}
