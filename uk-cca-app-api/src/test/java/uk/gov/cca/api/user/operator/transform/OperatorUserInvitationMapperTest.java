package uk.gov.cca.api.user.operator.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserInvitationDTO;
import uk.gov.netz.api.user.operator.domain.OperatorUserInvitationDTO;

import static org.junit.Assert.assertEquals;

class OperatorUserInvitationMapperTest {

    private OperatorUserInvitationMapper mapper;

    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(OperatorUserInvitationMapper.class);
    }

    @Test
    void toUserInvitationDTO() {
        String email = "john.rambo@testDom.com";
        String firstName = "John";
        String lastName = "Rambo";
        String roleCode = "operator_basic_user";

        final CcaOperatorUserInvitationDTO ccaOperatorUserInvitationDTO = CcaOperatorUserInvitationDTO.builder()
                .firstName(firstName)
                .roleCode(roleCode)
                .lastName(lastName)
                .contactType(ContactType.OPERATOR)
                .email(email)
                .build();

        // Invoke
        final OperatorUserInvitationDTO operatorUserInvitationDTO = mapper.toUserInvitationDTO(ccaOperatorUserInvitationDTO);

        // Assert
        assertEquals(roleCode, operatorUserInvitationDTO.getRoleCode());
        assertEquals(email, operatorUserInvitationDTO.getEmail());
        assertEquals(firstName, operatorUserInvitationDTO.getFirstName());
        assertEquals(lastName, operatorUserInvitationDTO.getLastName());
    }
}
