package uk.gov.cca.api.account.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TargetUnitAccountContactType {

    RESPONSIBLE_PERSON("Responsible Person"),
    ADMINISTRATIVE_CONTACT_DETAILS("Administrative Contact Details");

    private final String description;
    
    
}
