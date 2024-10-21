package uk.gov.cca.api.user.sectoruser.transform;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserRegistrationWithCredentialsDTO;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;

class SectorUserRegistrationMapperTest {

    private SectorUserRegistrationMapper mapper;

    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(SectorUserRegistrationMapper.class);
    }

    @Test
    void toUserRepresentation() {
        String email = "email";
        String firstName = "fn";
        String lastName = "ln";
        String userId = "userId";
        SectorUserRegistrationWithCredentialsDTO userRegistrationDTO = SectorUserRegistrationWithCredentialsDTO.
                builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(PhoneNumberDTO.builder().build())
                .mobileNumber(PhoneNumberDTO.builder().build())
                .build();

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(userRegistrationDTO, email, userId);

        // Assert
        Assertions.assertEquals(email, userRepresentation.getEmail());
        Assertions.assertEquals(email, userRepresentation.getUsername());
        Assertions.assertEquals(firstName, userRepresentation.getFirstName());
        Assertions.assertEquals(lastName, userRepresentation.getLastName());
        Assertions.assertEquals(userId, userRepresentation.getId());
    }
}
