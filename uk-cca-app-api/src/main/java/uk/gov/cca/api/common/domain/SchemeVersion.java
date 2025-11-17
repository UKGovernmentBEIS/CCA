package uk.gov.cca.api.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SchemeVersion {
    CCA_2("CCA2", 2),
    CCA_3("CCA3", 3);

	private final String description;
    private final int version;
}