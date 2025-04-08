package uk.gov.cca.api.migration.underlyingagreement.facilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountAddressVO {
    
    private String line1;
    private String line2;
    private String city;
    private String county;
    private String postcode;
    private Long country;
}
