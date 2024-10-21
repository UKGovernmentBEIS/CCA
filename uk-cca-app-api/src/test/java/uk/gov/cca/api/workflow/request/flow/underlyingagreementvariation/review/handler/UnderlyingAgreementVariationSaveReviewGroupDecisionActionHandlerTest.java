package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.service.UnderlyingAgreementVariationReviewService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationSaveReviewGroupDecisionActionHandlerTest {

    @InjectMocks
    private UnderlyingAgreementVariationSaveReviewGroupDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private UnderlyingAgreementVariationReviewService underlyingAgreementVariationReviewService;

    @Test
    void process() {

        final Long requestTaskId = 1L;
        final UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload payload =
                UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD)
                        .group(UnderlyingAgreementVariationReviewGroup.TARGET_UNIT_DETAILS)
                        .decision(UnderlyingAgreementReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().notes("notes").build())
                                .build()
                        )
                        .build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        final AppUser user = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
                CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION,
                user,
                payload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(underlyingAgreementVariationReviewService, times(1)).saveReviewGroupDecision(payload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION);
    }
}
