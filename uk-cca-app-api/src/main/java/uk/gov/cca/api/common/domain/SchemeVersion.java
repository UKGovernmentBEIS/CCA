package uk.gov.cca.api.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

@Getter
@AllArgsConstructor
public enum SchemeVersion {
    CCA_2("CCA2", 2, PerformanceDataResourceType.ACCOUNT),
    CCA_3("CCA3", 3, PerformanceDataResourceType.FACILITY);

	private final String description;
    private final int version;
    private final PerformanceDataResourceType resourceType;
}