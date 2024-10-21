package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.transform.UnderlyingAgreementVariationSubmitMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.validation.UnderlyingAgreementVariationPayloadValidatorService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationSubmitService {

    private final RequestService requestService;
    private final UnderlyingAgreementVariationPayloadValidatorService underlyingAgreementVariationPayloadValidatorService;
    private static final UnderlyingAgreementVariationSubmitMapper UNDERLYING_AGREEMENT_VARIATION_SUBMIT_MAPPER = Mappers.getMapper(UnderlyingAgreementVariationSubmitMapper.class);

    @Transactional
    public void submitUnderlyingAgreementVariation(RequestTask requestTask, AppUser user) {
        UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationSubmitRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();

        // Validate underlying agreement variation payload
        underlyingAgreementVariationPayloadValidatorService.validate(requestTask);

        // Save underlying agreement to request payload
        UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        requestPayload.setUnderlyingAgreement(taskPayload.getUnderlyingAgreement());
        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setUnderlyingAgreementAttachments(taskPayload.getAttachments());
        requestPayload.setAccountReferenceData(taskPayload.getAccountReferenceData());

        // Add request action
        addUnderlyingAgreementVariationSubmittedRequestAction(user, taskPayload, request);
    }

    private void addUnderlyingAgreementVariationSubmittedRequestAction(AppUser user,
                                                                       UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload, Request request) {

        UnderlyingAgreementVariationSubmittedRequestActionPayload actionPayload =
                UNDERLYING_AGREEMENT_VARIATION_SUBMIT_MAPPER.toUnderlyingAgreementVariationSubmittedRequestActionPayload(
                        taskPayload, CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SUBMITTED_PAYLOAD);

        requestService.addActionToRequest(
                request,
                actionPayload,
                CcaRequestActionType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED,
                user.getUserId());
    }
}
