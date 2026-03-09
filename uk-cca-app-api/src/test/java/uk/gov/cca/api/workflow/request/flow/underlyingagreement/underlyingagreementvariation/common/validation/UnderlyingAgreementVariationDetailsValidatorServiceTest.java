package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationModificationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationDetailsValidatorServiceTest {

    @InjectMocks
    private EditedUnderlyingAgreementVariationDetailsValidatorService service;

    @Mock
    private DataValidator<UnderlyingAgreementVariationDetails> validator;

    @Test
    void validate() {
        UUID att1UUID = UUID.randomUUID();

        UnderlyingAgreementVariationPayload unav = UnderlyingAgreementVariationPayload
                .builder()
                .underlyingAgreementVariationDetails(UnderlyingAgreementVariationDetails.builder()
                        .modifications(List.of(UnderlyingAgreementVariationModificationType.ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT))
                        .reason("bla bla bla bla")
                        .build())
                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().operatorName("name").build())
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                        .build())
                .build();

        UnderlyingAgreementVariationSubmitRequestTaskPayload requestTaskPayload = UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                .underlyingAgreement(unav)
                .sectionsCompleted(Map.of("section1", "completed"))
                .underlyingAgreementAttachments(Map.of(att1UUID, "att1"))
                .build();

        final UnderlyingAgreementVariationDetails underlyingAgreementVariationDetails =
                requestTaskPayload.getUnderlyingAgreement().getUnderlyingAgreementVariationDetails();

        final BusinessValidationResult validationResult = service.validate(requestTaskPayload);

        // Verify
        verify(validator, times(1)).validate(underlyingAgreementVariationDetails);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    void validate_empty_details() {
        UUID att1UUID = UUID.randomUUID();

        UnderlyingAgreementVariationPayload unav = UnderlyingAgreementVariationPayload
                .builder()
                .underlyingAgreementVariationDetails(null)
                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().operatorName("name").build())
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                        .build())
                .build();

        UnderlyingAgreementVariationSubmitRequestTaskPayload requestTaskPayload = UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                .underlyingAgreement(unav)
                .sectionsCompleted(Map.of("section1", "completed"))
                .underlyingAgreementAttachments(Map.of(att1UUID, "att1"))
                .build();

        final UnderlyingAgreementVariationDetails underlyingAgreementVariationDetails =
                requestTaskPayload.getUnderlyingAgreement().getUnderlyingAgreementVariationDetails();

        final BusinessValidationResult validationResult = service.validate(requestTaskPayload);

        // Verify
        verify(validator, times(0)).validate(underlyingAgreementVariationDetails);
        assertThat(validationResult.isValid()).isFalse();
    }

    @Test
    void validate_no_reason() {
        UUID att1UUID = UUID.randomUUID();

        UnderlyingAgreementVariationPayload unav = UnderlyingAgreementVariationPayload
                .builder()
                .underlyingAgreementVariationDetails(UnderlyingAgreementVariationDetails.builder()
                        .modifications(List.of(UnderlyingAgreementVariationModificationType.ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT))
                        .build())
                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().operatorName("name").build())
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                        .build())
                .build();

        UnderlyingAgreementVariationSubmitRequestTaskPayload requestTaskPayload = UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                .underlyingAgreement(unav)
                .sectionsCompleted(Map.of("section1", "completed"))
                .underlyingAgreementAttachments(Map.of(att1UUID, "att1"))
                .build();

        final UnderlyingAgreementVariationDetails underlyingAgreementVariationDetails =
                requestTaskPayload.getUnderlyingAgreement().getUnderlyingAgreementVariationDetails();

        when(validator.validate(underlyingAgreementVariationDetails)).thenReturn(Optional.of(new BusinessViolation()));

        final BusinessValidationResult validationResult = service.validate(requestTaskPayload);

        // Verify
        verify(validator, times(1)).validate(underlyingAgreementVariationDetails);
        assertThat(validationResult.isValid()).isFalse();
    }
}
