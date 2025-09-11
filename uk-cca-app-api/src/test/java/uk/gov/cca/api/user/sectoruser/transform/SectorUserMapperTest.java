package uk.gov.cca.api.user.sectoruser.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;

import static org.junit.Assert.assertEquals;

class SectorUserMapperTest {

    private SectorUserMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(SectorUserMapper.class);
    }

    @Test
    void toUserRepresentation() {
        String email = "john.rambo@testDom.com";
        String firstName = "John";
        String lastName = "Rambo";

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(email, firstName, lastName);

        // Assert
        assertEquals(email, userRepresentation.getEmail());
        assertEquals(firstName, userRepresentation.getFirstName());
        assertEquals(lastName, userRepresentation.getLastName());
    }
}
