package uk.gov.cca.api.user.sectoruser.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityDetailsDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.user.core.domain.enumeration.KeycloakUserAttributes;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@Test
	void toUserRepresentation_whenNoNumbers_setsAttributesToNull() {

		SectorUserAuthorityDetailsDTO dto = new SectorUserAuthorityDetailsDTO();
		dto.setEmail("john.rambo@testDom.com");
		dto.setFirstName("John");
		dto.setLastName("Last");
		dto.setJobTitle("Rambo");
		dto.setPhoneNumber(null);
		dto.setMobileNumber(null);
		dto.setContactType(ContactType.CONSULTANT);

		UserRepresentation user = mapper.toUserRepresentation(dto);

		assertNotNull(user.getAttributes());

		assertTrue(user.getAttributes().containsKey(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()));
		assertEquals(0, user.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()).size());

		assertTrue(user.getAttributes().containsKey(KeycloakUserAttributes.PHONE_NUMBER.getName()));
		assertEquals(0, user.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).size());

		assertTrue(user.getAttributes().containsKey(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()));
		assertEquals(0, user.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()).size());

		assertTrue(user.getAttributes().containsKey(KeycloakUserAttributes.MOBILE_NUMBER.getName()));
		assertEquals(0, user.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).size());
	}

	@Test
	void toUserRepresentation_whenNoNumbers_setsAttributes() {

		PhoneNumberDTO phone = new PhoneNumberDTO();
		phone.setCountryCode("+30");
		phone.setNumber("2111000000");

		PhoneNumberDTO mobile = new PhoneNumberDTO();
		mobile.setCountryCode("+31");
		mobile.setNumber("6900000000");

		SectorUserDTO dto = new SectorUserDTO();
		dto.setEmail("user@example.com");
		dto.setFirstName("First");
		dto.setLastName("Last");
		dto.setJobTitle("Engineer");
		dto.setPhoneNumber(phone);
		dto.setMobileNumber(mobile);

		UserRepresentation user = mapper.toUserRepresentation(dto);

		assertNotNull(user.getAttributes());
		assertTrue(user.getAttributes().containsKey(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()));
		assertEquals("+30", user.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()).get(0));

		assertTrue(user.getAttributes().containsKey(KeycloakUserAttributes.PHONE_NUMBER.getName()));
		assertEquals("2111000000", user.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0));

		assertTrue(user.getAttributes().containsKey(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()));
		assertEquals("+31", user.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()).get(0));

		assertTrue(user.getAttributes().containsKey(KeycloakUserAttributes.MOBILE_NUMBER.getName()));
		assertEquals("6900000000", user.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0));
	}

}
