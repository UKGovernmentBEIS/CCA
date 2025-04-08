package uk.gov.cca.api.migration.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetUnitAccountContactVO {
    
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String phoneNumber;
    
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String county;
    private String postcode;
    private Long country;

}
