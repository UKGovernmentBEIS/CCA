package uk.gov.cca.api.migration.underlyingagreement.facilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityItemVO {

    //Account
    private String businessId;
    
    private String facilityId;
    private LocalDateTime createdDate;

    //FacilityDetails
    private String name;
    private String uketsId;
    private String applicationReason;
    private String previousFacilityId;
    private AccountAddressVO facilityAddress;

    //FacilityContact
    private String email;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private AccountAddressVO address;
    private String phoneNumber;

    //EligibilityDetailsAndAuthorisation;
    private String adjacentFacilityId;
    private String agreementType;
    private Boolean erpAuthorisationExists;
    private String authorisationNumber;
    private String regulatorName;

    //FacilityExtent

    //Apply70Rule
    private BigDecimal energyConsumed;// %
    private BigDecimal energyConsumedProvision;// %
}
