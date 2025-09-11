package uk.gov.cca.api.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SchemeVersion {
    CCA_2(2),
    CCA_3(3);

    private final int version;
}