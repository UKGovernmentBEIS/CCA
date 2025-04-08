package uk.gov.cca.api.facility.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.facility.domain.FacilityAddress;

import static org.assertj.core.api.Assertions.assertThat;

public class FacilityAddressMapperTest {

    private final FacilityAddressMapper mapper = Mappers.getMapper(FacilityAddressMapper.class);

    @Test
    void toFacilityAddress() {

        FacilityAddress facilityAddress = FacilityAddress.builder()
                .line1("line1")
                .line2("line2")
                .city("city")
                .country("country")
                .postcode("postcode")
                .build();

        AccountAddressDTO accountAddressDTO = AccountAddressDTO.builder()
                .line1("line1")
                .line2("line2")
                .city("city")
                .country("country")
                .postcode("postcode")
                .build();

        // invoke
        FacilityAddress mappedAddress = mapper.toFacilityAddress(accountAddressDTO);

        // verify
        assertThat(mappedAddress).isEqualTo(facilityAddress);
    }

    @Test
    void setAddress() {

        FacilityAddress facilityAddress = FacilityAddress.builder()
                .line1("line1")
                .line2("line2")
                .city("city")
                .country("country")
                .postcode("postcode")
                .build();

        AccountAddressDTO accountAddressDTO = AccountAddressDTO.builder()
                .line1("line1")
                .line2("line2")
                .city("city2")
                .country("country2")
                .postcode("postcode2")
                .build();

        // invoke
        mapper.setAddress(facilityAddress, accountAddressDTO);

        // verify
        assertThat(facilityAddress.getCity()).isEqualTo("city2");
        assertThat(facilityAddress.getCountry()).isEqualTo("country2");
        assertThat(facilityAddress.getPostcode()).isEqualTo("postcode2");
    }
}
