package uk.gov.cca.api.underlyingagreement.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementAuthorisationAndAdditionalEvidenceContextValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementAuthorisationAndAdditionalEvidenceContextValidatorService validatorService;

    @Mock
    private DataValidator<AuthorisationAndAdditionalEvidence> validator;


    @Test
    void validateSection_NoViolations() {
        UUID attachmentId = UUID.randomUUID();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder().authorisationAttachmentIds(Set.of(attachmentId)).build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getAuthorisationAndAdditionalEvidence()))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));


        assertThat(result.isValid()).isTrue();
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getAuthorisationAndAdditionalEvidence());
    }

    @Test
    void getSectionName() {
        assertThat(validatorService.getSectionName()).isEqualTo(AuthorisationAndAdditionalEvidence.class.getName());
    }

    @Test
    void testValidate_EmptySection() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        assertThat(result.isValid()).isFalse();

    }
}
