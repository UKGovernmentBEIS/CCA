package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NoticeOfIntent;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitSaveRequestTaskActionPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class NoticeOfIntentSubmitServiceTest {

    @InjectMocks
    private NoticeOfIntentSubmitService service;

    @Test
    void applySaveAction() {
        final UUID fileUuid = UUID.randomUUID();
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final NoticeOfIntent noticeOfIntent = NoticeOfIntent.builder()
                .noticeOfIntentFile(fileUuid)
                .comments("bla bla bla")
                .build();
        final NonComplianceNoticeOfIntentSubmitSaveRequestTaskActionPayload requestTaskActionPayload = NonComplianceNoticeOfIntentSubmitSaveRequestTaskActionPayload.builder()
                .noticeOfIntent(noticeOfIntent)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder().build())
                .build();

        // invoke
        service.applySaveAction(requestTaskActionPayload, requestTask);

        // verify
        NonComplianceNoticeOfIntentSubmitRequestTaskPayload requestTaskPayload = (NonComplianceNoticeOfIntentSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(requestTaskPayload.getNoticeOfIntent()).isEqualTo(noticeOfIntent);
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
        final NoticeOfIntent noticeOfIntent = NoticeOfIntent.builder()
                .noticeOfIntentFile(fileUuid)
                .comments("bla bla bla")
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(NonComplianceRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder()
                        .noticeOfIntent(noticeOfIntent)
                        .nonComplianceAttachments(nonComplianceAttachments)
                        .sectionsCompleted(sectionsCompleted)
                        .build())
                .request(request)
                .build();

        // invoke
        service.notifyOperator(requestTask, decisionNotification);

        // verify
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        assertThat(requestPayload.getNoticeOfIntent()).isEqualTo(noticeOfIntent);
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
        final NoticeOfIntent noticeOfIntent = NoticeOfIntent.builder()
                .noticeOfIntentFile(fileUuid)
                .comments("bla bla bla")
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(NonComplianceRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder()
                        .noticeOfIntent(noticeOfIntent)
                        .nonComplianceAttachments(nonComplianceAttachments)
                        .sectionsCompleted(sectionsCompleted)
                        .build())
                .request(request)
                .build();

        // invoke
        service.requestPeerReview(requestTask, peerReviewer, appUser.getUserId());

        // verify
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        assertThat(requestPayload.getNoticeOfIntent()).isEqualTo(noticeOfIntent);
        assertThat(requestPayload.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(requestPayload.getRegulatorPeerReviewer()).isEqualTo(peerReviewer);
        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(appUser.getUserId());
    }
}
