package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.service.UnderlyingAgreementVariationReviewService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationReviewSaveActionHandlerTest {

    @InjectMocks
    private UnderlyingAgreementVariationReviewSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private UnderlyingAgreementVariationReviewService underlyingAgreementReviewService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload reviewSavePayload =
                UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW_PAYLOAD)
                        .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        AppUser user = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTask.getId(), CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW, user, reviewSavePayload);

        //verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(underlyingAgreementReviewService, times(1)).saveUnderlyingAgreementVariation(reviewSavePayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW);
    }
}
