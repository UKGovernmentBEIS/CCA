package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.transform.TargetUnitDetailsMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
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

    private static final TargetUnitDetailsMapper TARGET_UNIT_DETAILS_MAPPER = Mappers.getMapper(TargetUnitDetailsMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final AccountReferenceData accountReferenceData = accountReferenceDetailsService.getAccountReferenceData(request.getAccountId());

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
                .underlyingAgreementAttachments(originalUnderlyingAgreementContainer.getUnderlyingAgreementAttachments())
                .reviewGroupDecisions(initializeReviewGroups())
                .facilitiesReviewGroupDecisions(initializeFacilityReviewGroups(originalUnderlyingAgreementContainer.getUnderlyingAgreement().getFacilities()))
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT);
    }

    private Map<UnderlyingAgreementVariationReviewGroup, UnderlyingAgreementReviewDecision> initializeReviewGroups() {
        // Initialize all review groups to ACCEPTED except VARIATION_DETAILS
        return Arrays.stream(UnderlyingAgreementVariationReviewGroup.values())
                .filter(group -> !group.equals(UnderlyingAgreementVariationReviewGroup.VARIATION_DETAILS))
                .collect(Collectors.toMap(
                        group -> group,
                        group -> UnderlyingAgreementReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .build())
                );
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
