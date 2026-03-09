package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationRegulatorLedDetermination;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmitAttachmentsExistValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmitAttachmentsExistValidatorService service;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate() {
        final Set<UUID> files = Set.of(UUID.randomUUID());
        final Map<UUID, String> regulatorLedSubmitAttachments = Map.of(UUID.randomUUID(), "attachment");
        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .determination(VariationRegulatorLedDetermination.builder()
                                .variationImpactsAgreement(true)
                                .files(files)
                                .build())
                        .regulatorLedSubmitAttachments(regulatorLedSubmitAttachments)
                        .build();

        when(fileAttachmentsExistenceValidator.valid(files, regulatorLedSubmitAttachments.keySet()))
                .thenReturn(true);

        // Invoke
        BusinessValidationResult result = service.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(files, regulatorLedSubmitAttachments.keySet());
    }

    @Test
    void validate_not_valid() {
        final Set<UUID> files = Set.of(UUID.randomUUID());
        final Map<UUID, String> regulatorLedSubmitAttachments = Map.of(UUID.randomUUID(), "attachment");
        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .determination(VariationRegulatorLedDetermination.builder()
                                .variationImpactsAgreement(true)
                                .files(files)
                                .build())
                        .regulatorLedSubmitAttachments(regulatorLedSubmitAttachments)
                        .build();

        when(fileAttachmentsExistenceValidator.valid(files, regulatorLedSubmitAttachments.keySet()))
                .thenReturn(false);

        // Invoke
        BusinessValidationResult result = service.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(files, regulatorLedSubmitAttachments.keySet());
    }
}
