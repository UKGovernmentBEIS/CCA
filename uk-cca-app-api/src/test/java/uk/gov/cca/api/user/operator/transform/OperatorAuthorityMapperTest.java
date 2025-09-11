package uk.gov.cca.api.user.operator.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.operator.domain.OperatorAuthorityDTO;
import uk.gov.cca.api.user.operator.domain.OperatorAuthorityInfoDTO;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import static org.junit.Assert.assertEquals;

class OperatorAuthorityMapperTest {

    private OperatorAuthorityMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(OperatorAuthorityMapper.class);
    }

    @Test
    void toOperatorAuthoritiesInfoDto() {
        String firstName = "John";
        String lastName = "Rambo";
        String roleCode = "operator_basic_user";
        String roleName = "Operator";
        String userId = "user_id";
        String contactType = "Operator";

        UserInfoDTO userInfo = UserInfoDTO.builder()
                .userId(userId)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        final OperatorAuthorityDTO operatorAuthorityDTO = OperatorAuthorityDTO.builder()
        		.userId(userId)
        		.roleName(roleName)
        		.roleCode(roleCode)
        		.authorityStatus(AuthorityStatus.PENDING)
        		.authorityCreationDate(null)
        		.contactType(ContactType.OPERATOR)
        		.build();

        // Invoke
        final OperatorAuthorityInfoDTO operatorAuthorityInfoDto = mapper.toOperatorAuthorityInfoDto(operatorAuthorityDTO, userInfo);

        // Assert
        assertEquals(roleCode, operatorAuthorityInfoDto.getRoleCode());
        assertEquals(contactType, operatorAuthorityInfoDto.getContactType());
        assertEquals(firstName, operatorAuthorityInfoDto.getFirstName());
        assertEquals(lastName, operatorAuthorityInfoDto.getLastName());
    }
}
