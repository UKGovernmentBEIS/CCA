package uk.gov.cca.api.underlyingagreement.domain.facilities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import uk.gov.cca.api.common.domain.SchemeVersion;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum TargetImprovementType {
    TP7(SchemeVersion.CCA_3, 7),
    TP8(SchemeVersion.CCA_3, 8),
    TP9(SchemeVersion.CCA_3, 9);

    private final SchemeVersion schemeVersion;
    private final Integer targetPeriodNumber;

    public static Set<TargetImprovementType> getImprovementsBySchemeVersion(SchemeVersion schemeVersion) {
        return Arrays.stream(TargetImprovementType.values())
                .filter(improvement -> improvement.getSchemeVersion().equals(schemeVersion))
                .collect(Collectors.toSet());
    }

    public static Optional<TargetImprovementType> getTargetImprovementTypeByTargetPeriodNumber(int number) {
        return Arrays.stream(TargetImprovementType.values())
                .filter(improvement -> improvement.getTargetPeriodNumber().equals(number))
                .findFirst();
    }
}
