package uk.gov.cca.api.authorization.ccaauth.core.service;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;

@ExtendWith(MockitoExtension.class)
class ContactTypeValidatorTest {
    private static final ContactType OPERATOR_CONTACT_TYPE = ContactType.OPERATOR;
    private static final ContactType SECTOR_CONTACT_TYPE = ContactType.SECTOR_ASSOCIATION;

    @InjectMocks
    private ContactTypeValidator contactTypeValidator;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(contactTypeValidator, "roleType", OPERATOR);
    }

    @Test
    void isValid() {
        assertTrue(contactTypeValidator.isValid(OPERATOR_CONTACT_TYPE, constraintValidatorContext));
    }

    @Test
    void isValidFalse() {
        assertFalse(contactTypeValidator.isValid(SECTOR_CONTACT_TYPE, constraintValidatorContext));
    }
}
