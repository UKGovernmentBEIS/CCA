package uk.gov.cca.api.migration.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetUnitAccountVO {
    
    private Long originalTuPk;
    private String targetUnitId;
    private String operatorName;
    private String companyRegistrationNumber;
    private String sicCode;
    private String sectorAcronym;
    private String subsectorName;
    private String operatorType;
    private Boolean financiallyIndependent;

    //Operator Address
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String county;
    private String postcode;
    private Long country;
    
    private TargetUnitAccountContactVO responsiblePerson;
    private TargetUnitAccountContactVO administrativeContact;
    
}
