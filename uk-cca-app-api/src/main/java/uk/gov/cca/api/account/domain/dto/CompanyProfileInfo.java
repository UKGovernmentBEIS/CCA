package uk.gov.cca.api.account.domain.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.companieshouse.CompanyAddress;
import uk.gov.netz.api.companieshouse.CompanyType;
import uk.gov.netz.api.companieshouse.SicCode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyProfileInfo {

	private String name;

    private String registrationNumber;
    
    private CompanyType companyType;

    private List<SicCode> sicCodes;
    
    private CompanyAddress address;
}