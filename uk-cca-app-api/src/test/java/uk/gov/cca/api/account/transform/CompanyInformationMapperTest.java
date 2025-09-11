package uk.gov.cca.api.account.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.CompanyProfileDTO;
import uk.gov.cca.api.account.domain.dto.CompanyProfileInfo;
import uk.gov.netz.api.companieshouse.CompanyAddress;
import uk.gov.netz.api.companieshouse.CompanyType;
import uk.gov.netz.api.companieshouse.SicCode;

class CompanyInformationMapperTest {

	private final CompanyInformationMapper mapper = Mappers.getMapper(CompanyInformationMapper.class);

    @Test
    void toCompanyProfileDTO() {
        final CompanyProfileInfo companyProfile = CompanyProfileInfo.builder()
                .name("name")
                .registrationNumber("crn")
                .companyType(CompanyType.builder().description("Limited company").build())
                .sicCodes(List.of(SicCode.builder().code("code").build()))
                .address(CompanyAddress.builder().county("county").postcode("postCode").build())
                .build();
        
        final CompanyProfileDTO expected = CompanyProfileDTO.builder()
                .name("name")
                .registrationNumber("crn")
                .operatorType("Limited company")
                .sicCodes(List.of("code"))
                .address(AccountAddressDTO.builder().county("county").postcode("postCode").build())
                .build();

        CompanyProfileDTO result = mapper.toCompanyProfileDTO(companyProfile);
        assertThat(result).isEqualTo(expected);
    }
}
