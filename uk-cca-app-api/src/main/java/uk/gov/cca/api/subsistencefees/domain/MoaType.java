package uk.gov.cca.api.subsistencefees.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MoaType {

    SECTOR_MOA("Sector MoA", "C"),
    TARGET_UNIT_MOA("Target Unit MoA", "T");

    private final String description;
    private final String moaTypeIdentifier;

}
