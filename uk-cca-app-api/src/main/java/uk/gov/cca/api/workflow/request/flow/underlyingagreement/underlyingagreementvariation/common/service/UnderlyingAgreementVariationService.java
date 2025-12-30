package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountUpdateDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDataCreationDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDataUpdateDTO;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.UnderlyingAgreementFacilityCertificationTransferService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform.UnderlyingAgreementAccountReferenceDataMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.account.service.AccountSearchAdditionalKeywordService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationService {

    private final TargetUnitAccountUpdateService targetUnitAccountUpdateService;
    private final FacilityDataUpdateService facilityDataUpdateService;
    private final AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;
    private final UnderlyingAgreementFacilityCertificationTransferService facilityCertificationTransferService;
    private final UnderlyingAgreementService underlyingAgreementService;

    private static final UnderlyingAgreementAccountReferenceDataMapper ACCOUNT_MAPPER = Mappers
            .getMapper(UnderlyingAgreementAccountReferenceDataMapper.class);

    public void updateFacilitiesAndAccount(final Long accountId, final UnderlyingAgreementContainer unaContainerFinal,
                                           final UnderlyingAgreementVariationRequestPayload requestPayload) {
        final UnderlyingAgreement proposedUnderlyingAgreement = requestPayload
                .getUnderlyingAgreementProposed().getUnderlyingAgreement();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = requestPayload
                .getUnderlyingAgreementProposed().getUnderlyingAgreementTargetUnitDetails();

        // Save facility data
        saveFacilityData(accountId, requestPayload.getFacilitiesReviewGroupDecisions(), proposedUnderlyingAgreement.getFacilities());

        // Save search keywords
        saveKeywords(accountId, targetUnitDetails, unaContainerFinal);

        final TargetUnitAccountUpdateDTO targetUnitAccountUpdateDTO = ACCOUNT_MAPPER.toTargetUnitAccountUpdateDTO(targetUnitDetails);

        // Update account status and details
        targetUnitAccountUpdateService.updateTargetUnitAccountUponUnderlyingAgreementVariation(accountId, targetUnitAccountUpdateDTO);
    }

    private void saveFacilityData(final Long accountId,
                                  final Map<String, UnderlyingAgreementVariationFacilityReviewDecision> facilitiesReviewGroupDecisions,
                                  final Set<Facility> facilities) {
        // Save new facilities
        saveFacilityDataForNewFacilities(accountId, facilities, facilitiesReviewGroupDecisions);

        // Update existing facilities
        updateFacilityDataForExistingFacilities(facilities);
    }

    private void saveKeywords(final Long accountId, final UnderlyingAgreementTargetUnitDetails underlyingAgreementTargetUnitDetails,
                              final UnderlyingAgreementContainer unaContainer) {
        // Create search keywords
        final Map<String, String> searchKeywordsForAccount = underlyingAgreementService
                .createSearchKeywordsForAccount(underlyingAgreementTargetUnitDetails.getOperatorName(), unaContainer);

        // Save new search keywords
        accountSearchAdditionalKeywordService.storeKeywordsForAccount(accountId, searchKeywordsForAccount);
    }

    private void saveFacilityDataForNewFacilities(final Long accountId, final Set<Facility> facilities, final Map<String, UnderlyingAgreementVariationFacilityReviewDecision> facilitiesReviewGroupDecisions) {
        Map<FacilityItem, LocalDate> chargeStartDateByNewFacilityItem = facilities.stream()
                .filter(facility -> facility.getStatus().equals(FacilityStatus.NEW))
                .map(Facility::getFacilityItem)
                .collect(HashMap::new, (map, facility) -> map.put(facility, Optional.ofNullable(facilitiesReviewGroupDecisions.get(facility.getFacilityId()))
                        .map(UnderlyingAgreementVariationFacilityReviewDecision::getStartDate).orElse(null)), HashMap::putAll);

        // Create new facilities
        if (!chargeStartDateByNewFacilityItem.isEmpty()) {
            Set<FacilityBaseInfoDTO> createdFacilities = facilityDataUpdateService
                    .createFacilitiesData(buildFacilitiesData(accountId, chargeStartDateByNewFacilityItem));

            facilityCertificationTransferService
                    .processFacilityCertificationsForNewFacilities(createdFacilities, chargeStartDateByNewFacilityItem.keySet());
        }
    }

    private void updateFacilityDataForExistingFacilities(final Set<Facility> facilities) {
        List<FacilityDataUpdateDTO> existingFacilities = facilities.stream()
                .filter(facility -> !facility.getStatus().equals(FacilityStatus.NEW))
                .map(this::buildFacilitiesData)
                .toList();

        // Update facility data for excluded/active facilities
        if (!existingFacilities.isEmpty()) {
            facilityDataUpdateService.updateFacilitiesData(existingFacilities);
        }
    }

    /**
     * Build FacilityData for the new facilities
     *
     * @param accountId                    account id
     * @param chargeStartDatePerFacilityItem map of NEW facilities with/without changeStartDate
     * @return List of FacilityDataCreationDTO
     */
    private List<FacilityDataCreationDTO> buildFacilitiesData(Long accountId, Map<FacilityItem, LocalDate> chargeStartDatePerFacilityItem) {
        LocalDateTime createdDate = LocalDateTime.now();
        return chargeStartDatePerFacilityItem.entrySet().stream()
                .map(entry -> {
                    FacilityItem facility = entry.getKey();
                    LocalDate chargeStartDate = entry.getValue();
                    String facilityBusinessId = facility.getFacilityId();
                    String siteName = facility.getFacilityDetails().getName();
                    FacilityAddressDTO facilityAddress = facility.getFacilityDetails().getFacilityAddress();

                    return FacilityDataCreationDTO.builder()
                            .accountId(accountId)
                            .facilityBusinessId(facilityBusinessId)
                            .siteName(siteName)
                            .createdDate(createdDate)
                            .chargeStartDate(chargeStartDate)
                            .address(facilityAddress)
                            .participatingSchemeVersions(facility.getFacilityDetails().getParticipatingSchemeVersions())
                            .build();
                })
                .toList();
    }

    /**
     * Build FacilityData for the excluded/live facility
     *
     * @param facility not NEW facility
     * @return The facility to be updated
     */
    private FacilityDataUpdateDTO buildFacilitiesData(Facility facility) {
        return FacilityDataUpdateDTO.builder()
                .facilityBusinessId(facility.getFacilityItem().getFacilityId())
                .siteName(facility.getFacilityItem().getFacilityDetails().getName())
                .facilityAddress(facility.getFacilityItem().getFacilityDetails().getFacilityAddress())
                .closedDate(facility.getExcludedDate())
                .participatingSchemeVersions(
                        facility.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions())
                .build();
    }
}
