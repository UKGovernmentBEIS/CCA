package uk.gov.cca.api.account.domain.enumeration;

import java.util.Set;

public enum AccountContactType {
    
    PRIMARY,
    SECONDARY,
    SERVICE,
    FINANCIAL,
    CA_SITE,
    VB_SITE;

    public static Set<AccountContactType> getOperatorAccountContactTypes() {
        return Set.of(PRIMARY, SECONDARY, FINANCIAL, SERVICE);
    }
    
}
