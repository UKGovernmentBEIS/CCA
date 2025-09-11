package uk.gov.cca.api.underlyingagreement.domain.facilities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import uk.gov.cca.api.common.domain.SchemeVersion;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum TargetImprovementType {
    TP7(SchemeVersion.CCA_3),
    TP8(SchemeVersion.CCA_3),
    TP9(SchemeVersion.CCA_3);

    private final SchemeVersion schemeVersion;

    public static Set<TargetImprovementType> getImprovementsBySchemeVersion(SchemeVersion schemeVersion) {
        return Arrays.stream(TargetImprovementType.values())
                .filter(improvement -> improvement.getSchemeVersion().equals(schemeVersion))
                .collect(Collectors.toSet());
    }
}
