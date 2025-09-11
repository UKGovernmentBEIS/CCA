package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementReviewDecisionDataValidatorTest {

    @InjectMocks
    private UnderlyingAgreementReviewDecisionDataValidator underlyingAgreementReviewDecisionDataValidator;

    @Mock
    private DataValidator<?> dataValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validateReviewDecisionData() {
        final UnderlyingAgreementReviewDecision decision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementFacilityReviewDecision facilityReviewDecision = UnderlyingAgreementFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                .build();
        final Determination determination = Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(Determination.builder().build())
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, decision))
                        .facilitiesReviewGroupDecisions(Map.of("1", facilityReviewDecision))
                        .determination(determination)
                        .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        when(dataValidator.validate(any())).thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getAttachments().keySet()))
                .thenReturn(true);

        BusinessValidationResult result = underlyingAgreementReviewDecisionDataValidator
                .validateReviewDecisionData(requestTask);

        assertThat(result.isValid()).isTrue();
        verify(dataValidator, times(3)).validate(any());
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getAttachments().keySet());
    }

    @Test
    void validateReviewDecisionData_not_valid() {
        final UnderlyingAgreementReviewDecision decision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementFacilityReviewDecision facilityReviewDecision = UnderlyingAgreementFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                .build();
        final Determination determination = Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(Determination.builder().build())
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementReviewGroup.TARGET_UNIT_DETAILS, decision))
                        .facilitiesReviewGroupDecisions(Map.of("1", facilityReviewDecision))
                        .determination(determination)
                        .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        when(dataValidator.validate(any())).thenReturn(Optional.of(new BusinessViolation()));
        when(fileAttachmentsExistenceValidator.valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getAttachments().keySet()))
                .thenReturn(true);

        BusinessValidationResult result = underlyingAgreementReviewDecisionDataValidator
                .validateReviewDecisionData(requestTask);

        assertThat(result.isValid()).isFalse();
        verify(dataValidator, times(3)).validate(any());
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getAttachments().keySet());
    }
}
