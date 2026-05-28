package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNotice;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NonComplianceEnforcementResponseNoticeSubmitInitializerTest {

    @InjectMocks
    private NonComplianceEnforcementResponseNoticeSubmitInitializer nonComplianceEnforcementResponseNoticeSubmitInitializer;

    @Test
    void initializePayload_first_time() {
        final String accountId = "1";
        final Request request = Request.builder()
                .id("ADS_1-T00003-NCOM-1")
                .payload(NonComplianceRequestPayload.builder()
                        .build())
                .requestResources(List.of(RequestResource.builder()
                        .resourceId(accountId)
                        .resourceType(ResourceType.ACCOUNT)
                        .build()))
                .build();

        NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload expected = NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PAYLOAD)
                .penaltyReissue(false)
                .build();

        // Invoke
        RequestTaskPayload actual = nonComplianceEnforcementResponseNoticeSubmitInitializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.class).isEqualTo(expected);
    }

    @Test
    void initializePayload_penalty_reissue() {
        final String accountId = "1";
        final Request request = Request.builder()
                .id("ADS_1-T00003-NCOM-1")
                .payload(NonComplianceRequestPayload.builder()
                        .enforcementResponseNotice(null)
                        .penaltyReissueNeeded(true)
                        .build())
                .requestResources(List.of(RequestResource.builder()
                        .resourceId(accountId)
                        .resourceType(ResourceType.ACCOUNT)
                        .build()))
                .build();

        NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload expected = NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PAYLOAD)
                .penaltyReissue(true)
                .build();

        // Invoke
        RequestTaskPayload actual = nonComplianceEnforcementResponseNoticeSubmitInitializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.class).isEqualTo(expected);
    }

    @Test
    void initializePayload_from_peer_review() {
        final String accountId = "1";
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceEnforcementResponseNotice enforcementResponseNotice = NonComplianceEnforcementResponseNotice.builder()
                .type(NonComplianceEnforcementResponseNoticeType.PENALTY)
                .file(fileUuid)
                .comments("bla bla bla")
                .build();
        final Request request = Request.builder()
                .id("ADS_1-T00003-NCOM-1")
                .payload(NonComplianceRequestPayload.builder()
                        .enforcementResponseNotice(enforcementResponseNotice)
                        .penaltyReissueNeeded(true)
                        .build())
                .requestResources(List.of(RequestResource.builder()
                        .resourceId(accountId)
                        .resourceType(ResourceType.ACCOUNT)
                        .build()))
                .build();

        NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload expected = NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PAYLOAD)
                .enforcementResponseNotice(enforcementResponseNotice)
                .penaltyReissue(true)
                .build();

        // Invoke
        RequestTaskPayload actual = nonComplianceEnforcementResponseNoticeSubmitInitializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.class).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(nonComplianceEnforcementResponseNoticeSubmitInitializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT);
    }
}
