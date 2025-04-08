package uk.gov.cca.api.underlyingagreement.domain.baselinetargets;

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
@SpELExpression(expression = "{" +
        "(((T(java.lang.Boolean).TRUE.equals(#isTwelveMonths) && #baselineDate != null && T(java.time.LocalDate).parse(#baselineDate) != T(java.time.LocalDate).of(2018, 1, 1)) " +
        "|| T(java.lang.Boolean).FALSE.equals(#isTwelveMonths)) == (#explanation != null)) " +
        "}", message = "underlyingagreement.baselineData.explanation")
@SpELExpression(expression = "{(T(java.lang.Boolean).TRUE.equals(#isTwelveMonths) && (#greenfieldEvidences?.size() <= 0)) " +
        "|| T(java.lang.Boolean).FALSE.equals(#isTwelveMonths)" +
        "}", message = "underlyingagreement.baselineData.greenfieldEvidences")
public class BaselineData {

    @NotNull
    private Boolean isTwelveMonths;

    @NotNull
    private LocalDate baselineDate;

    private String explanation;

    @Builder.Default
    private Set<UUID> greenfieldEvidences = new HashSet<>();

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal energy;

    private Boolean usedReportingMechanism;

    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal throughput;

    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal performance;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal energyCarbonFactor;
}