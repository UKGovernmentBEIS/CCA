package uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.facility.domain.dto.FacilityDataCreationDTO;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform.UnderlyingAgreementContainerMapper;
import uk.gov.netz.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementActivatedService {

	private final RequestService requestService;
	private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final UnderlyingAgreementService underlyingAgreementService;
    private final FacilityDataUpdateService facilityDataUpdateService;
    private final AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;
    private final TargetUnitAccountUpdateService targetUnitAccountUpdateService;

    private static final UnderlyingAgreementContainerMapper UNA_CONTAINER_MAPPER = Mappers.getMapper(UnderlyingAgreementContainerMapper.class);

    public void activateUnderlyingAgreement(String requestId) {
        Request request = requestService.findRequestById(requestId);
        UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();
        Long accountId = request.getAccountId();
        AccountReferenceData accountReferenceData = accountReferenceDetailsService.getAccountReferenceData(accountId);

        UnderlyingAgreementContainer unaContainer =
        		UNA_CONTAINER_MAPPER.toUnderlyingAgreementContainer(requestPayload, accountReferenceData);

        // Save underlying agreement
        underlyingAgreementService.submitUnderlyingAgreement(unaContainer, accountId);

        // Save facility data and search keywords
        saveFacilityDataAndKeywords(accountId, unaContainer, requestPayload.getFacilitiesReviewGroupDecisions());
        
        // Update account status and details
        targetUnitAccountUpdateService.updateTargetUnitAccountUponUnderlyingAgreementActivated(
        		accountId, requestPayload.getUnderlyingAgreement().getUnderlyingAgreementTargetUnitDetails());
    }

	private void saveFacilityDataAndKeywords(Long accountId, UnderlyingAgreementContainer unaContainer, Map<String, UnderlyingAgreementFacilityReviewDecision> facilitiesReviewGroupDecisions) {
		Set<String> facilityPostCodes = unaContainer.getUnderlyingAgreement().getFacilities().stream()
				.map(facility -> facility.getFacilityItem().getFacilityDetails().getFacilityAddress().getPostcode())
				.collect(Collectors.toSet());
		Set<String> facilityIds = unaContainer.getUnderlyingAgreement().getFacilities().stream()
        		.map(facility -> facility.getFacilityItem().getFacilityId())
        		.collect(Collectors.toSet());

		Map<String, LocalDate> chargeStartDatePerFacilityId =
				facilitiesReviewGroupDecisions.entrySet().stream()
						.filter(facilityReviewDecision -> facilityReviewDecision.getValue().getStartDate() != null)
						.collect(Collectors.toMap(Map.Entry::getKey,
								facilityReviewDecision-> facilityReviewDecision.getValue().getStartDate()));

		// Save facility data
        facilityDataUpdateService.createFacilitiesData(
				buildFacilitiesData(accountId, facilityIds, chargeStartDatePerFacilityId));
        // Store facility IDs and postcodes as search keywords
        accountSearchAdditionalKeywordService.storeKeywordsForAccount(accountId, 
        		Stream.of(facilityIds, facilityPostCodes)
        		.flatMap(Set::stream)
                .collect(Collectors.toSet())
                .toArray(String[]::new));
	}

private List<FacilityDataCreationDTO> buildFacilitiesData(Long accountId, Set<String> facilityIds, Map<String, LocalDate> chargeStartDatePerFacilityId) {
		LocalDateTime createdDate = LocalDateTime.now();
		return facilityIds.stream()
				.map(facilityId -> FacilityDataCreationDTO.builder()
						.accountId(accountId)
						.facilityId(facilityId)
						.createdDate(createdDate)
						.chargeStartDate(chargeStartDatePerFacilityId.get(facilityId))
						.build())
				.toList();
	}
}
