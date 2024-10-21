package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.transform.UnderlyingAgreementVariationReviewMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewInitializer implements InitializeRequestTaskHandler {

    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private static final UnderlyingAgreementVariationReviewMapper REVIEW_MAPPER = Mappers.getMapper(UnderlyingAgreementVariationReviewMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        AccountReferenceData accountReferenceData =
                accountReferenceDetailsService.getAccountReferenceData(request.getAccountId());
        UnderlyingAgreementVariationReviewRequestTaskPayload reviewRequestTaskPayload =
                REVIEW_MAPPER.toUnderlyingAgreementVariationReviewRequestTaskPayload((UnderlyingAgreementVariationRequestPayload) request.getPayload(),
                        CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD);
        reviewRequestTaskPayload.setAccountReferenceData(accountReferenceData);
        return reviewRequestTaskPayload;
    }
    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW);
    }
}
