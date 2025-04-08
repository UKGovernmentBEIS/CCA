package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationReviewInitializerTest {

    @InjectMocks
    private UnderlyingAgreementVariationReviewInitializer initializer;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Test
    void initializePayload() {
        final String requestId = "1";
        final Long accountId = 1L;

        UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
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
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(UnderlyingAgreementVariationReviewRequestTaskPayload.class);

        UnderlyingAgreementVariationReviewRequestTaskPayload reviewRequestTaskPayload =
                (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTaskPayload;

        assertThat(reviewRequestTaskPayload.getAccountReferenceData()).isEqualTo(expectedAccountData);
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW));
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
