package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform.UnderlyingAgreementTargetUnitDetailsMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementSubmitInitializer implements InitializeRequestTaskHandler {

    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private static final UnderlyingAgreementTargetUnitDetailsMapper TARGET_UNIT_DETAILS_MAPPER = Mappers.getMapper(UnderlyingAgreementTargetUnitDetailsMapper.class);


    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final AccountReferenceData accountReferenceData =
                accountReferenceDetailsService.getAccountReferenceData(request.getAccountId());
        final TargetUnitAccountDetails accountDetails = accountReferenceData.getTargetUnitAccountDetails();
        final String subsectorAssociationName = accountReferenceData.getSectorAssociationDetails().getSubsectorAssociationName();
        final UnderlyingAgreementRequestMetadata metadata = (UnderlyingAgreementRequestMetadata) request.getMetadata();

        return UnderlyingAgreementSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_SUBMIT_PAYLOAD)
                .workflowSchemeVersion(metadata.getWorkflowSchemeVersion())
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(TARGET_UNIT_DETAILS_MAPPER
                                .toUnderlyingAgreementTargetUnitDetails(accountDetails, subsectorAssociationName))
                        .build())
                .accountReferenceData(accountReferenceData)
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_APPLICATION_SUBMIT);
    }
}
