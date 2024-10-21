package uk.gov.cca.api.authorization.ccaauth.core.domain;

import lombok.Getter;

@Getter
public enum ContactType {

    SECTOR_ASSOCIATION("Sector Association", "SECTOR_USER"),
    OPERATOR("Operator", "OPERATOR"),
    CONSULTANT("Consultant", "SECTOR_USER", "OPERATOR");

    private String name;
    /**
     * The roleTypes.
     */
    private final String[] roleTypes;

    ContactType(String name, String... roleTypes) {
        this.name = name;
        this.roleTypes = roleTypes;
    }
}
