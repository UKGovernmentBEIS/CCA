package uk.gov.cca.api.underlyingagreement.domain.baselinetargets;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{" +
        "(#agreementCompositionType == 'NOVEM' && #isTargetUnitThroughputMeasured == null) || " +
        "(" +
        "(#agreementCompositionType == 'ABSOLUTE' || #agreementCompositionType == 'RELATIVE')" +
        ")" +
        "}", message = "underlyingagreement.targetComposition.isTargetUnitThroughputMeasured")
@SpELExpression(expression = "{" +
        "(#agreementCompositionType == 'NOVEM' && #throughputUnit == null) || " +
        "(#agreementCompositionType == 'ABSOLUTE' || #agreementCompositionType == 'RELATIVE') " +
        "}", message = "underlyingagreement.targetComposition.throughputUnit")
@SpELExpression(expression = "{" +
        "(#agreementCompositionType == 'NOVEM' && #conversionFactor == null) || " +
        "(" +
        "(#agreementCompositionType == 'ABSOLUTE' || #agreementCompositionType == 'RELATIVE') && " +
        "(T(java.lang.Boolean).TRUE.equals(#isTargetUnitThroughputMeasured) || (#conversionFactor == null))" +
        ")" +
        "}", message = "underlyingagreement.targetComposition.conversionFactor")
@SpELExpression(expression = "{" +
        "(#agreementCompositionType == 'NOVEM' && (#conversionEvidences?.size() <= 0)) || " +
        "(" +
        "(#agreementCompositionType == 'ABSOLUTE' || #agreementCompositionType == 'RELATIVE') && " +
        "(T(java.lang.Boolean).TRUE.equals(#isTargetUnitThroughputMeasured) == (#conversionEvidences?.size() gt 0))" +
        ")" +
        "}", message = "underlyingagreement.targetComposition.conversionEvidences")
public class TargetComposition {

    @NotNull
    private UUID calculatorFile;

    @NotNull
    private MeasurementType measurementType;

    @NotNull
    private AgreementCompositionType agreementCompositionType;

    /**
     *  Indicates whether the target unit throughput unit is inherited from sector.
     *  If it is inherited from sector then isTargetUnitThroughputMeasured is set to false.
     *  This property is not applicable for Novem agreement composition type.
     */
    private Boolean isTargetUnitThroughputMeasured;

    private String throughputUnit;

    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal conversionFactor;

    @Builder.Default
    private Set<UUID> conversionEvidences = new HashSet<>();
}
