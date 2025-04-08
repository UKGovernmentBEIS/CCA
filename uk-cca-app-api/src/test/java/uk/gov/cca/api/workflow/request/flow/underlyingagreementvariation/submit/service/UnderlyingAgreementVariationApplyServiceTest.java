package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationApplySavePayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationApplySaveTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationModificationType.AMEND_OPERATOR_OR_ORGANISATION_NAME;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationApplyServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationApplyService underlyingAgreementVariationApplyService;

    @Test
    void applySaveAction() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final Map<String, String> reviewSectionsCompleted = Map.of("subtask", "ACCEPTED");

        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final Map<UnderlyingAgreementVariationReviewGroup, UnderlyingAgreementReviewDecision> reviewGroups = Map.of(
                UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS, UnderlyingAgreementReviewDecision.builder().build()
        );
        final Map<String, UnderlyingAgreementVariationFacilityReviewDecision> facilityGroups = Map.of(
                "facilityId", UnderlyingAgreementVariationFacilityReviewDecision.builder().build()
        );
        final UnderlyingAgreementVariationSaveRequestTaskActionPayload taskActionPayload =
                UnderlyingAgreementVariationSaveRequestTaskActionPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationApplySavePayload.builder()
                                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementVariationApplySaveTargetUnitDetails.builder()
                                        .operatorName("operatorName")
                                        .operatorAddress(AccountAddressDTO.builder().build())
                                        .responsiblePersonDetails(UnderlyingAgreementTargetUnitResponsiblePerson.builder().build())
                                        .build())
                                .underlyingAgreementVariationDetails(UnderlyingAgreementVariationDetails.builder()
                                        .reason("reason").modifications(Collections.singletonList(AMEND_OPERATOR_OR_ORGANISATION_NAME)).build())
                                .authorisationAndAdditionalEvidence(underlyingAgreement.getAuthorisationAndAdditionalEvidence())
                                .build())
                        .sectionsCompleted(sectionsCompleted)
                        .reviewGroupDecisions(reviewGroups)
                        .facilitiesReviewGroupDecisions(facilityGroups)
                        .reviewSectionsCompleted(reviewSectionsCompleted)
                        .build();

        final UnderlyingAgreementVariationPayload expected = UnderlyingAgreementVariationPayload.builder()
                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder()
                        .operatorName("operatorName")
                        .operatorAddress(AccountAddressDTO.builder().build())
                        .responsiblePersonDetails(UnderlyingAgreementTargetUnitResponsiblePerson.builder().build())
                        .build())
                .underlyingAgreementVariationDetails(UnderlyingAgreementVariationDetails.builder()
                        .reason("reason").modifications(Collections.singletonList(AMEND_OPERATOR_OR_ORGANISATION_NAME)).build())
                .underlyingAgreement(underlyingAgreement)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().build())
                                .build())
                        .build())
                .build();

        // Invoke
        underlyingAgreementVariationApplyService.applySaveAction(taskActionPayload, requestTask);

        // Verify
        UnderlyingAgreementVariationSubmitRequestTaskPayload actual =
                (UnderlyingAgreementVariationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(actual.getUnderlyingAgreement()).isEqualTo(expected);
        assertThat(actual.getReviewSectionsCompleted()).isEqualTo(reviewSectionsCompleted);
        assertThat(actual.getReviewGroupDecisions()).isEqualTo(reviewGroups);
        assertThat(actual.getFacilitiesReviewGroupDecisions()).isEqualTo(facilityGroups);
    }
}