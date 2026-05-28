package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNotice;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnforcementResponseNoticeSubmitServiceTest {

    @InjectMocks
    private EnforcementResponseNoticeSubmitService service;

    @Mock
    private RequestService requestService;

    @Test
    void applySaveAction() {
        final UUID fileUuid = UUID.randomUUID();
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final NonComplianceEnforcementResponseNotice enforcementResponseNotice = NonComplianceEnforcementResponseNotice.builder()
                .type(NonComplianceEnforcementResponseNoticeType.PENALTY)
                .file(fileUuid)
                .comments("bla bla bla")
                .build();
        final NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload requestTaskActionPayload = NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload.builder()
                .enforcementResponseNotice(enforcementResponseNotice)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.builder().build())
                .build();

        // invoke
        service.applySaveAction(requestTaskActionPayload, requestTask);

        // verify
        NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload requestTaskPayload = (NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(requestTaskPayload.getEnforcementResponseNotice()).isEqualTo(enforcementResponseNotice);
        assertThat(requestTaskPayload.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

    @Test
    void notifyOperator() {
        final UUID fileUuid = UUID.randomUUID();
        final String requestId = "requestId";
        final Map<UUID, String> nonComplianceAttachments = Map.of(fileUuid, "notice");
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .build();
        final NonComplianceEnforcementResponseNotice enforcementResponseNotice = NonComplianceEnforcementResponseNotice.builder()
                .type(NonComplianceEnforcementResponseNoticeType.PENALTY)
                .file(fileUuid)
                .comments("bla bla bla")
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(NonComplianceRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.builder()
                        .enforcementResponseNotice(enforcementResponseNotice)
                        .nonComplianceAttachments(nonComplianceAttachments)
                        .sectionsCompleted(sectionsCompleted)
                        .build())
                .request(request)
                .build();

        // invoke
        service.notifyOperator(requestTask, decisionNotification);

        // verify
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        assertThat(requestPayload.isPenaltyReissueNeeded()).isFalse();
        assertThat(requestPayload.getEnforcementResponseNotice()).isEqualTo(enforcementResponseNotice);
        assertThat(requestPayload.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(requestPayload.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(requestPayload.getNonComplianceAttachments()).isEqualTo(nonComplianceAttachments);
    }

    @Test
    void requestPeerReview() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final String peerReviewer = UUID.randomUUID().toString();
        final UUID fileUuid = UUID.randomUUID();
        final String requestId = "requestId";
        final Map<UUID, String> nonComplianceAttachments = Map.of(fileUuid, "notice");
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final NonComplianceEnforcementResponseNotice enforcementResponseNotice = NonComplianceEnforcementResponseNotice.builder()
                .type(NonComplianceEnforcementResponseNoticeType.PENALTY)
                .file(fileUuid)
                .comments("bla bla bla")
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(NonComplianceRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.builder()
                        .enforcementResponseNotice(enforcementResponseNotice)
                        .nonComplianceAttachments(nonComplianceAttachments)
                        .sectionsCompleted(sectionsCompleted)
                        .build())
                .request(request)
                .build();

        // invoke
        service.requestPeerReview(requestTask, peerReviewer, appUser.getUserId());

        // verify
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        assertThat(requestPayload.getEnforcementResponseNotice()).isEqualTo(enforcementResponseNotice);
        assertThat(requestPayload.isPenaltyReissueNeeded()).isFalse();
        assertThat(requestPayload.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(requestPayload.getRegulatorPeerReviewer()).isEqualTo(peerReviewer);
        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(appUser.getUserId());
    }

    @Test
    void resetForPenaltyReissue() {
        final String requestId = "requestId";
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceEnforcementResponseNotice enforcementResponseNotice = NonComplianceEnforcementResponseNotice.builder()
                .type(NonComplianceEnforcementResponseNoticeType.PENALTY)
                .file(fileUuid)
                .comments("bla bla bla")
                .build();
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
                .enforcementResponseNotice(enforcementResponseNotice)
                .penaltyReissueNeeded(true)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.resetForPenaltyReissue(requestId);

        // verify
        Assertions.assertThat(requestPayload.getEnforcementResponseNotice()).isNull();
        verify(requestService, times(1)).findRequestById(requestId);
    }
}
