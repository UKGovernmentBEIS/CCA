package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.transform.TargetUnitDetailsMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationSubmitInitializer implements InitializeRequestTaskHandler {

    private final AccountReferenceDetailsService accountReferenceDetailsService;

    private static final TargetUnitDetailsMapper TARGET_UNIT_DETAILS_MAPPER = Mappers.getMapper(TargetUnitDetailsMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final AccountReferenceData accountReferenceData =
                accountReferenceDetailsService.getAccountReferenceData(request.getAccountId());
        final TargetUnitAccountDetails accountDetails = accountReferenceData.getTargetUnitAccountDetails();
        final String subsectorAssociationName = accountReferenceData.getSectorAssociationDetails().getSubsectorAssociationName();
        final UnderlyingAgreementVariationRequestPayload payload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        final UnderlyingAgreementContainer originalUnderlyingAgreementContainer = payload.getOriginalUnderlyingAgreementContainer();
        return UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT_PAYLOAD)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(TARGET_UNIT_DETAILS_MAPPER
                                .toUnderlyingAgreementTargetUnitDetails(accountDetails, subsectorAssociationName))
                        .underlyingAgreement(originalUnderlyingAgreementContainer.getUnderlyingAgreement())
                        .build())
                .accountReferenceData(accountReferenceData)
                .originalUnderlyingAgreementContainer(originalUnderlyingAgreementContainer)
                .sectionsCompleted(originalUnderlyingAgreementContainer.getSectionsCompleted())
                .underlyingAgreementAttachments(originalUnderlyingAgreementContainer.getUnderlyingAgreementAttachments())
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT);
    }
}
