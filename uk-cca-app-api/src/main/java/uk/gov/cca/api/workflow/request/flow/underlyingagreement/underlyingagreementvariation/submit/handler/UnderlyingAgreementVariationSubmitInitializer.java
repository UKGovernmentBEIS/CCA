package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform.UnderlyingAgreementTargetUnitDetailsMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationSubmitInitializer implements InitializeRequestTaskHandler {

	private final AccountReferenceDetailsService accountReferenceDetailsService;

	private static final UnderlyingAgreementTargetUnitDetailsMapper TARGET_UNIT_DETAILS_MAPPER = Mappers.getMapper(UnderlyingAgreementTargetUnitDetailsMapper.class);

	@Override
	public RequestTaskPayload initializePayload(Request request) {
		final AccountReferenceData accountReferenceData = accountReferenceDetailsService.getAccountReferenceData(request.getAccountId());

		final TargetUnitAccountDetails accountDetails = accountReferenceData.getTargetUnitAccountDetails();
		final String subsectorAssociationName = accountReferenceData.getSectorAssociationDetails().getSubsectorAssociationName();
		final UnderlyingAgreementVariationRequestPayload payload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
		final UnderlyingAgreementContainer originalUnderlyingAgreementContainer = payload.getOriginalUnderlyingAgreementContainer();
		final UnderlyingAgreement originalUnderlyingAgreement = originalUnderlyingAgreementContainer.getUnderlyingAgreement();
		final boolean showTp5Tp6 = shouldShowTp5Tp6(originalUnderlyingAgreementContainer);

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
				.reviewGroupDecisions(initializeReviewGroups(showTp5Tp6))
				.facilitiesReviewGroupDecisions(initializeFacilityReviewGroups(originalUnderlyingAgreementContainer.getUnderlyingAgreement().getFacilities()))
				.build();
	}

	@Override
	public Set<String> getRequestTaskTypes() {
		return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT);
	}

	private Map<UnderlyingAgreementVariationReviewGroup, UnderlyingAgreementReviewDecision> initializeReviewGroups(boolean showTp5Tp6) {
		// Initialize review groups to ACCEPTED, conditionally including TP5/TP6 groups per business rules
		return Arrays.stream(UnderlyingAgreementVariationReviewGroup.values())
				.filter(group -> !group.equals(UnderlyingAgreementVariationReviewGroup.VARIATION_DETAILS))
				.filter(group -> {
					boolean isTP5orTP6 =
							group == UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD5_DETAILS ||
									group == UnderlyingAgreementVariationReviewGroup.TARGET_PERIOD6_DETAILS;
					return !isTP5orTP6 || showTp5Tp6;
				})
				.collect(Collectors.toMap(
						group -> group,
						group -> UnderlyingAgreementReviewDecision.builder()
								.type(CcaReviewDecisionType.ACCEPTED)
								.build())
				);
	}

	private boolean shouldShowTp5Tp6(UnderlyingAgreementContainer originalContainer) {

		final boolean hasLiveCca2OrBothFacility = originalContainer.getUnderlyingAgreement().getFacilities().stream()
				.anyMatch(f -> f.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions()
						.contains(SchemeVersion.CCA_2)
				);

		// All live facilities are CCA3-only
		if (!hasLiveCca2OrBothFacility) {
			return false;
		}

		final TargetPeriod5Details originalTp5 = originalContainer.getUnderlyingAgreement().getTargetPeriod5Details();
		final TargetPeriod6Details originalTp6 = originalContainer.getUnderlyingAgreement().getTargetPeriod6Details();
		// TP5/TP6 Relevant data exists
		return originalTp5 != null && originalTp6 != null;
	}

	private Map<String, UnderlyingAgreementVariationFacilityReviewDecision> initializeFacilityReviewGroups(Set<Facility> facilities) {
		// Initialize all original facility review groups to ACCEPTED
		return facilities.stream().collect(Collectors.toMap(
				facility -> facility.getFacilityItem().getFacilityId(),
				facility -> UnderlyingAgreementVariationFacilityReviewDecision.builder()
						.facilityStatus(facility.getStatus())
						.type(CcaReviewDecisionType.ACCEPTED)
						.build()
		));
	}
}
