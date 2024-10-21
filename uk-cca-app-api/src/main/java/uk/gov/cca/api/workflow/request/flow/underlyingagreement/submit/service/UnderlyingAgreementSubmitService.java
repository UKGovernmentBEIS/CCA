package uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.validation.UnderlyingAgreementPayloadValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.transform.UnderlyingAgreementSubmitMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementSubmitService {
	
	private final RequestService requestService;
	private final UnderlyingAgreementPayloadValidatorService underlyingAgreementPayloadValidatorService;
	private static final UnderlyingAgreementSubmitMapper UNDERLYING_AGREEMENT_SUBMIT_MAPPER = Mappers.getMapper(UnderlyingAgreementSubmitMapper.class);
	
	@Transactional
	public void submitUnderlyingAgreement(RequestTask requestTask, AppUser user) {
		UnderlyingAgreementSubmitRequestTaskPayload taskPayload = 
				(UnderlyingAgreementSubmitRequestTaskPayload) requestTask.getPayload();
		Request request = requestTask.getRequest();
		
		// Validate underlying agreement payload
		underlyingAgreementPayloadValidatorService.validate(requestTask);

		// Save underlying agreement to request payload
		UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();
		requestPayload.setUnderlyingAgreement(taskPayload.getUnderlyingAgreement());
		requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
		requestPayload.setUnderlyingAgreementAttachments(taskPayload.getAttachments());
		requestPayload.setAccountReferenceData(taskPayload.getAccountReferenceData());
		
		// Add request action
		addUnderlyingAgreementSubmittedRequestAction(user, taskPayload, request);
	}

	private void addUnderlyingAgreementSubmittedRequestAction(AppUser user,
			UnderlyingAgreementSubmitRequestTaskPayload taskPayload, Request request) {		
		
		UnderlyingAgreementSubmittedRequestActionPayload actionPayload = 
				UNDERLYING_AGREEMENT_SUBMIT_MAPPER.toUnderlyingAgreementSubmittedRequestActionPayload(
						taskPayload, CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_SUBMITTED_PAYLOAD);
		
        requestService.addActionToRequest(
        		request, 
        		actionPayload, 
        		CcaRequestActionType.UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED, 
        		user.getUserId());
	}
}
