package uk.gov.cca.api.underlyingagreement.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementAttachmentsExistValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementAttachmentsExistValidatorService validatorService;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Test
    void validate() {
        final UUID calculatorFile = UUID.randomUUID();
        final Set<String> files = Set.of(calculatorFile.toString());
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .build())
                                .build())
                        .build())
                .underlyingAgreementAttachments(Map.of(calculatorFile, "File"))
                .build();

        when(fileAttachmentService.fileAttachmentsExist(files))
                .thenReturn(true);

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(fileAttachmentService, times(1)).fileAttachmentsExist(files);
    }

    @Test
    void validate_file_not_exists() {
        final UUID calculatorFile = UUID.randomUUID();
        final Set<String> files = Set.of(calculatorFile.toString());
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .build())
                                .build())
                        .build())
                .underlyingAgreementAttachments(Map.of(calculatorFile, "File"))
                .build();

        when(fileAttachmentService.fileAttachmentsExist(files))
                .thenReturn(false);

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.ATTACHMENT_NOT_FOUND.getMessage());
        verify(fileAttachmentService, times(1)).fileAttachmentsExist(files);
    }

    @Test
    void validate_file_not_in_attachments() {
        final UUID calculatorFile = UUID.randomUUID();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .build())
                                .build())
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.ATTACHMENT_NOT_FOUND.getMessage());
        verifyNoInteractions(fileAttachmentService);
    }
}
