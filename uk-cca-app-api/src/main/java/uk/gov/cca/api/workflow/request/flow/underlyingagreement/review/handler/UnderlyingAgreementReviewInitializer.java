package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.handler;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.transform.UnderlyingAgreementReviewMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementReviewInitializer implements InitializeRequestTaskHandler {

	private final AccountReferenceDetailsService accountReferenceDetailsService;
	private static final UnderlyingAgreementReviewMapper REVIEW_MAPPER = Mappers.getMapper(UnderlyingAgreementReviewMapper.class);
	
	@Override
    public RequestTaskPayload initializePayload(Request request) {
		AccountReferenceData accountReferenceData =
                accountReferenceDetailsService.getAccountReferenceData(request.getAccountId());
		UnderlyingAgreementReviewRequestTaskPayload reviewRequestTaskPayload =
				REVIEW_MAPPER.toUnderlyingAgreementReviewRequestTaskPayload((UnderlyingAgreementRequestPayload) request.getPayload(),
						CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD);
		reviewRequestTaskPayload.setAccountReferenceData(accountReferenceData);
        return reviewRequestTaskPayload;
    }
	@Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW);
    }
}
