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
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationModificationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationDetermination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationReviewDecisionDataValidatorTest {

    @InjectMocks
    private UnderlyingAgreementVariationReviewDecisionDataValidator underlyingAgreementVariationReviewDecisionDataValidator;

    @Mock
    private DataValidator<?> dataValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validateReviewDecisionData() {
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .facilities(Set.of(Facility.builder().status(FacilityStatus.LIVE).facilityItem(FacilityItem.builder().facilityId("1").build()).build()))
                .build();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final UnderlyingAgreementVariationDetails underlyingAgreementVariationDetails = UnderlyingAgreementVariationDetails.builder()
                .modifications(List.of(UnderlyingAgreementVariationModificationType.ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT))
                .reason("bla bla bla")
                .build();
        final UnderlyingAgreementReviewDecision decision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementVariationFacilityReviewDecision facilityReviewDecision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(null)
                .facilityStatus(FacilityStatus.LIVE)
                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                .build();
        final VariationDetermination determination = VariationDetermination.builder()
                .determination(Determination.builder()
                        .type(DeterminationType.ACCEPTED)
                        .additionalInformation("text")
                        .build())
                .build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementVariationDetails(underlyingAgreementVariationDetails)
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, decision))
                        .facilitiesReviewGroupDecisions(Map.of("1", facilityReviewDecision))
                        .determination(determination)
                        .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        when(dataValidator.validate(any())).thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getAttachments().keySet()))
                .thenReturn(true);

        BusinessValidationResult result = underlyingAgreementVariationReviewDecisionDataValidator
                .validateReviewDecisionData(requestTask);

        assertThat(result.isValid()).isTrue();
        verify(dataValidator, times(3)).validate(any());
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getAttachments().keySet());
    }

    @Test
    void validateReviewDecisionData_not_valid() {
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final UnderlyingAgreementVariationDetails underlyingAgreementVariationDetails = UnderlyingAgreementVariationDetails.builder()
                .modifications(List.of(UnderlyingAgreementVariationModificationType.ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT))
                .reason("bla bla bla")
                .build();
        final UnderlyingAgreementReviewDecision decision = UnderlyingAgreementReviewDecision.builder()
                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                .type(CcaReviewDecisionType.ACCEPTED).build();
        final UnderlyingAgreementVariationFacilityReviewDecision facilityReviewDecision = UnderlyingAgreementVariationFacilityReviewDecision.builder()
                .type(CcaReviewDecisionType.ACCEPTED)
                .changeStartDate(Boolean.TRUE)
                .startDate(LocalDate.now())
                .details(UnderlyingAgreementReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                .build();
        final VariationDetermination determination = VariationDetermination.builder()
                .determination(Determination.builder()
                        .type(DeterminationType.ACCEPTED)
                        .additionalInformation("text")
                        .build())
                .build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementVariationDetails(underlyingAgreementVariationDetails)
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .reviewGroupDecisions(Map.of(
                                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, decision))
                        .facilitiesReviewGroupDecisions(Map.of("1", facilityReviewDecision))
                        .determination(determination)
                        .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        when(dataValidator.validate(any())).thenReturn(Optional.of(new BusinessViolation()));
        when(fileAttachmentsExistenceValidator.valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getAttachments().keySet()))
                .thenReturn(true);

        BusinessValidationResult result = underlyingAgreementVariationReviewDecisionDataValidator
                .validateReviewDecisionData(requestTask);

        assertThat(result.isValid()).isFalse();
        verify(dataValidator, times(3)).validate(any());
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getAttachments().keySet());
    }
}
