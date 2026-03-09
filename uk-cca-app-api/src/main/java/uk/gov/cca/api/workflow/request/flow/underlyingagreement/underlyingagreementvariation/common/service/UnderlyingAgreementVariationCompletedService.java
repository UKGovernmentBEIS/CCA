package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.transform.UnderlyingAgreementVariationContainerDeepCloneMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationCompletedService {

    private final RequestService requestService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final UnderlyingAgreementService underlyingAgreementService;
    private final UnderlyingAgreementVariationService underlyingAgreementVariationService;

    private static final UnderlyingAgreementVariationContainerDeepCloneMapper UNA_VARIATION_CONTAINER_DEEP_CLONE_MAPPER =
            Mappers.getMapper(UnderlyingAgreementVariationContainerDeepCloneMapper.class);

    public void completeUnderlyingAgreementVariation(String requestId) {
        Request request = requestService.findRequestById(requestId);
        UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        Long accountId = request.getAccountId();
        AccountReferenceData accountReferenceData = accountReferenceDetailsService.getAccountReferenceData(accountId);

        // Construct active underlying agreement, only live facilities should be included
        UnderlyingAgreementContainer unaContainerFinal = UNA_VARIATION_CONTAINER_DEEP_CLONE_MAPPER
                .toUnderlyingAgreementContainer(requestPayload, accountReferenceData);

        // Update underlying agreement without document update
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(request.getCreationDate())
                .schemeVersion(requestPayload.getWorkflowSchemeVersion())
                .build();
        underlyingAgreementService.updateUnderlyingAgreement(unaContainerFinal, accountId, underlyingAgreementValidationContext, false);

        // Update facility data and account
        underlyingAgreementVariationService.updateFacilitiesAndAccount(accountId, unaContainerFinal, requestPayload);
    }
}
