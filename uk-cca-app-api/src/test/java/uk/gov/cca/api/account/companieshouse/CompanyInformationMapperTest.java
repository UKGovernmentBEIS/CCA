package uk.gov.cca.api.account.companieshouse;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.account.companieshouse.CompanyAddress;
import uk.gov.cca.api.account.companieshouse.CompanyInformationMapper;
import uk.gov.cca.api.account.companieshouse.CompanyProfile;
import uk.gov.cca.api.account.companieshouse.CompanyProfileDTO;
import uk.gov.cca.api.account.companieshouse.CountyAddressDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompanyInformationMapperTest {

    private static final CompanyInformationMapper mapper = Mappers.getMapper(CompanyInformationMapper.class);

    @Test
    void toCompanyProfileDTO() {
        String name = "companyName";
        String registrationNumber = "GR909087";
        List<String> sicCodes = List.of("12345", "98765");
        CompanyProfile companyProfile = CompanyProfile.builder()
                .name(name)
                .registrationNumber(registrationNumber)
                .status("active")
                .jurisdiction("scotland")
                .address(CompanyAddress.builder()
                        .line1("line1")
                        .line2("line2")
                        .city("city")
                        .country("country")
                        .county("county")
                        .postcode("code")
                        .build())
                .sicCodes(sicCodes)
                .build();

        CompanyProfileDTO expected = CompanyProfileDTO.builder()
                .name(name)
                .registrationNumber(registrationNumber)
                .sicCodes(sicCodes)
                .address(CountyAddressDTO.builder()
                        .line1("line1")
                        .line2("line2")
                        .county("county")
                        .city("city")
                        .postcode("code")
                        .build())
                .build();

        CompanyProfileDTO actual = mapper.toCompanyProfileDTO(companyProfile);

        assertEquals(expected, actual);
    }

}