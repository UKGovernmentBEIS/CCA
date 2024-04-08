package uk.gov.cca.api.account.companieshouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileDTO {

    private String name;

    private String registrationNumber;

    private CountyAddressDTO address;

    private List<String> sicCodes;
}
