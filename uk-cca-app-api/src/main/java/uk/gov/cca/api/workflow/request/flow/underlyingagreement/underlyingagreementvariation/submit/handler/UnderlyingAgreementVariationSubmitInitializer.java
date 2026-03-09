package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeVersionsHelperService;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform.UnderlyingAgreementTargetUnitDetailsMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationSubmitInitializer implements InitializeRequestTaskHandler {

	private final AccountReferenceDetailsService accountReferenceDetailsService;
	private final UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;
	
	private static final UnderlyingAgreementTargetUnitDetailsMapper TARGET_UNIT_DETAILS_MAPPER = Mappers.getMapper(UnderlyingAgreementTargetUnitDetailsMapper.class);

	@Override
	public RequestTaskPayload initializePayload(Request request) {
		final AccountReferenceData accountReferenceData = accountReferenceDetailsService.getAccountReferenceData(request.getAccountId());

		final TargetUnitAccountDetails accountDetails = accountReferenceData.getTargetUnitAccountDetails();
		final String subsectorAssociationName = accountReferenceData.getSectorAssociationDetails().getSubsectorAssociationName();
		final UnderlyingAgreementVariationRequestPayload payload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
		final UnderlyingAgreementContainer originalUnderlyingAgreementContainer = payload.getOriginalUnderlyingAgreementContainer();
		final UnderlyingAgreement originalUnderlyingAgreement = originalUnderlyingAgreementContainer.getUnderlyingAgreement();
		final boolean showTp5Tp6 = underlyingAgreementSchemeVersionsHelperService.shouldShowTp5Tp6(
				originalUnderlyingAgreementContainer, request.getCreationDate().toLocalDate());

		return UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
				.payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT_PAYLOAD)
				.workflowSchemeVersion(payload.getWorkflowSchemeVersion())
				.underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
						.underlyingAgreementTargetUnitDetails(TARGET_UNIT_DETAILS_MAPPER
								.toUnderlyingAgreementTargetUnitDetails(accountDetails, subsectorAssociationName))
						.underlyingAgreement(UnderlyingAgreement.builder()
								.facilities(originalUnderlyingAgreement.getFacilities())
								.authorisationAndAdditionalEvidence(originalUnderlyingAgreement.getAuthorisationAndAdditionalEvidence())
								.targetPeriod5Details(showTp5Tp6 ? originalUnderlyingAgreement.getTargetPeriod5Details() : null)
								.targetPeriod6Details(showTp5Tp6 ? originalUnderlyingAgreement.getTargetPeriod6Details() : null)
								.build()
						)
						.build())
				.accountReferenceData(accountReferenceData)
				.originalUnderlyingAgreementContainer(originalUnderlyingAgreementContainer)
				.underlyingAgreementAttachments(originalUnderlyingAgreementContainer.getUnderlyingAgreementAttachments())
				.build();
	}

	@Override
	public Set<String> getRequestTaskTypes() {
		return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT);
	}
}
