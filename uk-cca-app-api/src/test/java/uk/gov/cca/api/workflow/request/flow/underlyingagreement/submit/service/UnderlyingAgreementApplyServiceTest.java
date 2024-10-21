package uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementApplySavePayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementApplySaveTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementApplyServiceTest {

    @InjectMocks
    private UnderlyingAgreementApplyService underlyingAgreementApplyService;

    @Test
    void applySaveAction() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");

        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementSaveRequestTaskActionPayload taskActionPayload =
                UnderlyingAgreementSaveRequestTaskActionPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementApplySavePayload.builder()
                                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementApplySaveTargetUnitDetails.builder()
                                        .operatorName("operatorName")
                                        .operatorAddress(AccountAddressDTO.builder().build())
                                        .responsiblePersonDetails(UnderlyingAgreementTargetUnitResponsiblePerson.builder().build())
                                        .build())
                                .authorisationAndAdditionalEvidence(underlyingAgreement.getAuthorisationAndAdditionalEvidence())
                                .build())
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        final UnderlyingAgreementPayload expected = UnderlyingAgreementPayload.builder()
                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder()
                        .operatorName("operatorName")
                        .operatorAddress(AccountAddressDTO.builder().build())
                        .responsiblePersonDetails(UnderlyingAgreementTargetUnitResponsiblePerson.builder().build())
                        .build())
                .underlyingAgreement(underlyingAgreement)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(UnderlyingAgreementSubmitRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().build())
                                .build())
                        .build())
                .build();

        // Invoke
        underlyingAgreementApplyService.applySaveAction(taskActionPayload, requestTask);

        // Verify
        UnderlyingAgreementSubmitRequestTaskPayload actual =
                (UnderlyingAgreementSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(actual.getUnderlyingAgreement()).isEqualTo(expected);
    }
}
