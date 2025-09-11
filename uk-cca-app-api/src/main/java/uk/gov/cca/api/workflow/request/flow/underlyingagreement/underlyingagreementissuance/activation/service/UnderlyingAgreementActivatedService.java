package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountUpdateDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDataCreationDTO;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.UnderlyingAgreementFacilityCertificationTransferService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform.UnderlyingAgreementAccountReferenceDataMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.transform.UnderlyingAgreementContainerMapper;
import uk.gov.netz.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementActivatedService {

    private final RequestService requestService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final UnderlyingAgreementService underlyingAgreementService;
    private final FacilityDataUpdateService facilityDataUpdateService;
    private final AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;
    private final TargetUnitAccountUpdateService targetUnitAccountUpdateService;
    private final UnderlyingAgreementFacilityCertificationTransferService facilityTransferService;

    private static final UnderlyingAgreementContainerMapper UNA_CONTAINER_MAPPER = Mappers.getMapper(UnderlyingAgreementContainerMapper.class);
    private static final UnderlyingAgreementAccountReferenceDataMapper ACCOUNT_DATA_MAPPER = Mappers.getMapper(UnderlyingAgreementAccountReferenceDataMapper.class);

    public void activateUnderlyingAgreement(String requestId) {
        Request request = requestService.findRequestById(requestId);
        UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();
        Long accountId = request.getAccountId();
        AccountReferenceData accountReferenceData = accountReferenceDetailsService.getAccountReferenceData(accountId);

        UnderlyingAgreementContainer unaContainerFinal =
                UNA_CONTAINER_MAPPER.toUnderlyingAgreementContainer(requestPayload, accountReferenceData);

        // Save underlying agreement
        UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(request.getCreationDate())
                .schemeVersion(requestPayload.getWorkflowSchemeVersion())
                .build();
        UnderlyingAgreementEntity underlyingAgreementEntity = underlyingAgreementService
                .submitUnderlyingAgreement(unaContainerFinal, accountId, underlyingAgreementValidationContext);

        // Save facility data
        saveFacilityDataAndCertifications(accountId, unaContainerFinal, requestPayload.getFacilitiesReviewGroupDecisions());

        // Save search keywords
        saveKeywords(accountId, unaContainerFinal);

        TargetUnitAccountUpdateDTO targetUnitAccountUpdateDTO = ACCOUNT_DATA_MAPPER.toTargetUnitAccountUpdateDTO(
                requestPayload.getUnderlyingAgreementProposed().getUnderlyingAgreementTargetUnitDetails());

        // Update account status and details
        targetUnitAccountUpdateService.activateTargetUnitAccount(
                accountId, targetUnitAccountUpdateDTO, underlyingAgreementEntity.getActivationDate());
    }

    private void saveKeywords(Long accountId, UnderlyingAgreementContainer unaContainer) {

        // Create search keywords
        final Map<String, String> searchKeywordsForAccount = underlyingAgreementService.createSearchKeywordsForAccount(unaContainer);

        // Store facility IDs and postcodes as search keywords
        accountSearchAdditionalKeywordService.storeKeywordsForAccount(accountId, searchKeywordsForAccount);
    }

    private void saveFacilityDataAndCertifications(Long accountId, UnderlyingAgreementContainer unaContainer, Map<String, UnderlyingAgreementFacilityReviewDecision> facilitiesReviewGroupDecisions) {
        Set<FacilityItem> facilities = unaContainer.getUnderlyingAgreement().getFacilities().stream()
                .map(Facility::getFacilityItem)
                .collect(Collectors.toSet());

        Map<String, LocalDate> chargeStartDateByFacilityId =
                facilitiesReviewGroupDecisions.entrySet().stream()
                        .filter(facilityReviewDecision -> facilityReviewDecision.getValue().getStartDate() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                facilityReviewDecision -> facilityReviewDecision.getValue().getStartDate()));

        // Save facility data
        Set<FacilityBaseInfoDTO> createdFacilities = facilityDataUpdateService.createFacilitiesData(
                buildFacilitiesData(accountId, facilities, chargeStartDateByFacilityId));

        facilityTransferService.processFacilityCertificationsForNewFacilities(createdFacilities, facilities);
    }

    private List<FacilityDataCreationDTO> buildFacilitiesData(Long accountId, Set<FacilityItem> facilities, Map<String, LocalDate> chargeStartDatePerFacilityId) {
        LocalDateTime createdDate = LocalDateTime.now();
        return facilities.stream()
                .map(facility -> {
                    String facilityId = facility.getFacilityId();
                    String siteName = facility.getFacilityDetails().getName();
                    AccountAddressDTO facilityAddress = facility.getFacilityDetails().getFacilityAddress();

                    return FacilityDataCreationDTO.builder()
                            .accountId(accountId)
                            .facilityId(facilityId)
                            .siteName(siteName)
                            .createdDate(createdDate)
                            .chargeStartDate(chargeStartDatePerFacilityId.get(facilityId))
                            .address(facilityAddress)
                            .participatingSchemeVersions(facility.getFacilityDetails().getParticipatingSchemeVersions())
                            .build();
                })
                .toList();
    }
}
