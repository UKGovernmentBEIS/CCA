package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementReviewInitializerTest {

	@InjectMocks
    private UnderlyingAgreementReviewInitializer initializer;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Test
    void initializePayload() {
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final String requestId = "1";
        final Long accountId = 1L;

        UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                .workflowSchemeVersion(workflowSchemeVersion)
                .build();

        Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .build();
        addResourcesToRequest(accountId, request);

        AccountReferenceData expectedAccountData = AccountReferenceData.builder()
        		.targetUnitAccountDetails(TargetUnitAccountDetails.builder().operatorName("name").build())
        		.build();

        Mockito.when(accountReferenceDetailsService.getAccountReferenceData(request.getAccountId()))
            .thenReturn(expectedAccountData);

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(UnderlyingAgreementReviewRequestTaskPayload.class);

        UnderlyingAgreementReviewRequestTaskPayload reviewRequestTaskPayload =
            (UnderlyingAgreementReviewRequestTaskPayload) requestTaskPayload;

        assertThat(reviewRequestTaskPayload.getAccountReferenceData()).isEqualTo(expectedAccountData);
        assertThat(reviewRequestTaskPayload.getWorkflowSchemeVersion()).isEqualTo(workflowSchemeVersion);
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW));
    }
    
    private void addResourcesToRequest(Long accountId, Request request) {
		RequestResource accountResource = RequestResource.builder()
				.resourceType(ResourceType.ACCOUNT)
				.resourceId(accountId.toString())
				.request(request)
				.build();

        request.getRequestResources().add(accountResource);
	}
}
