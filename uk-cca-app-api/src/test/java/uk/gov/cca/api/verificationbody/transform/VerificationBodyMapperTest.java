package uk.gov.cca.api.verificationbody.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.verificationbody.domain.Address;
import uk.gov.cca.api.verificationbody.domain.dto.AddressDTO;
import uk.gov.cca.api.common.EmissionTradingScheme;
import uk.gov.cca.api.verificationbody.domain.VerificationBody;
import uk.gov.cca.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.cca.api.verificationbody.transform.VerificationBodyMapper;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class VerificationBodyMapperTest {

    private VerificationBodyMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(VerificationBodyMapper.class);
    }

    @Test
    void toVerificationBody() {
        String name = "name";
        String accreditationRefNum = "accreditationRefNum";
        AddressDTO address = AddressDTO.builder().line1("line1").line2("line2").city("city").country("country").postcode("code").build();
        EmissionTradingScheme emissionTradingScheme1 = mock(EmissionTradingScheme.class);
        EmissionTradingScheme emissionTradingScheme2 = mock(EmissionTradingScheme.class);
        Set<EmissionTradingScheme> emissionTradingSchemes = Set.of(emissionTradingScheme1, emissionTradingScheme2);

        VerificationBodyEditDTO verificationBodyEditDTO = VerificationBodyEditDTO.builder()
            .name(name)
            .accreditationReferenceNumber(accreditationRefNum)
            .address(address)
            .emissionTradingSchemes(emissionTradingSchemes)
            .build();

        //invoke
        VerificationBody verificationBody = mapper.toVerificationBody(verificationBodyEditDTO);

        //assertions
        assertThat(verificationBody).isNotNull();
        assertEquals(name, verificationBody.getName());
        assertEquals(accreditationRefNum, verificationBody.getAccreditationReferenceNumber());

        Address verificationBodyAddress = verificationBody.getAddress();
        assertThat(verificationBodyAddress).isNotNull();
        assertEquals(address.getLine1(), verificationBodyAddress.getLine1());
        assertEquals(address.getLine2(), verificationBodyAddress.getLine2());
        assertEquals(address.getCity(), verificationBodyAddress.getCity());
        assertEquals(address.getCountry(), verificationBodyAddress.getCountry());
        assertEquals(address.getPostcode(), verificationBodyAddress.getPostcode());

        assertThat(verificationBody.getEmissionTradingSchemes())
                .hasSize(2)
                .containsExactlyInAnyOrder(emissionTradingScheme1, emissionTradingScheme2);
    }
}