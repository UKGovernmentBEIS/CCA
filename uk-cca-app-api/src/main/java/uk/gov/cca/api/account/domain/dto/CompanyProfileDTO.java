package uk.gov.cca.api.account.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileDTO {

	private String name;

    private String registrationNumber;
    
    private String operatorType;

    private List<String> sicCodes;
    
    private AccountAddressDTO address;
}