package uk.gov.cca.api.underlyingagreement.domain.authorisation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AuthorisationAndAdditionalEvidenceTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testAuthorisationAttachmentIdsNotEmpty() {

        AuthorisationAndAdditionalEvidence authorisationAndAdditionalEvidence = AuthorisationAndAdditionalEvidence.builder()
                .authorisationAttachmentIds(Set.of())
                .build();

        Set<ConstraintViolation<AuthorisationAndAdditionalEvidence>> violations = validator.validate(authorisationAndAdditionalEvidence);

        assertFalse(violations.isEmpty(), "authorisationAttachmentIds should not be empty");
    }
}