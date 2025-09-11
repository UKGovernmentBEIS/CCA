package uk.gov.cca.api.user.operator.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserRegistrationWithCredentialsDTO;

import static org.junit.Assert.assertEquals;

class CcaOperatorUserRegistrationMapperTest {

    private CcaOperatorUserRegistrationMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(CcaOperatorUserRegistrationMapper.class);
    }

    @Test
    void toUserRepresentation() {
        String email = "email";
        String userId = "userId";

        CcaOperatorUserRegistrationWithCredentialsDTO userRegistrationDTO =
                CcaOperatorUserRegistrationWithCredentialsDTO.builder()
                .emailToken("12345678")
                .organisationName("test")
                .jobTitle("Engineer")
                .contactType(ContactType.CONSULTANT)
                .build();

        final UserRepresentation userRepresentation = mapper.toUserRepresentation(userRegistrationDTO, email, userId);

        assertEquals(email, userRepresentation.getUsername());
        assertEquals(email, userRepresentation.getEmail());
        assertEquals(userId, userRepresentation.getId());
    }
}