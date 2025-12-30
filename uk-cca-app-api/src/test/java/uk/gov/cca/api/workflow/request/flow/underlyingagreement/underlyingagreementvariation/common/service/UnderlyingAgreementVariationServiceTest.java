package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountUpdateDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.UnderlyingAgreementFacilityCertificationTransferService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.account.service.AccountSearchAdditionalKeywordService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationService service;

    @Mock
    private TargetUnitAccountUpdateService targetUnitAccountUpdateService;

    @Mock
    private FacilityDataUpdateService facilityDataUpdateService;

    @Mock
    private AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;

    @Mock
    private UnderlyingAgreementFacilityCertificationTransferService facilityCertificationTransferService;

    @Mock
    private UnderlyingAgreementService underlyingAgreementService;

    @Test
    void updateFacilitiesAndAccount() {
        final Long accountId = 2L;
        final UnderlyingAgreementContainer unaContainerFinal = UnderlyingAgreementContainer.builder().build();

        final String facilityId1 = "facilityId1";
        final String facilityId2 = "facilityId2";
        final String facilityId3 = "facilityId3";
        final String postcode = "postcode";
        final String operatorName = "operatorName";

        final UnderlyingAgreementTargetUnitDetails unaDetails = UnderlyingAgreementTargetUnitDetails.builder().operatorName(operatorName).build();
        final FacilityItem facilityItem1 = FacilityItem.builder()
                .facilityId(facilityId1)
                .facilityDetails(FacilityDetails.builder().facilityAddress(FacilityAddressDTO.builder().postcode(postcode).build()).build())
                .build();
        final Facility facility1 = Facility.builder()
                .status(FacilityStatus.LIVE)
                .facilityItem(facilityItem1)
                .build();
        final FacilityItem facilityItem2 = FacilityItem.builder()
                .facilityId(facilityId2)
                .facilityDetails(FacilityDetails.builder().facilityAddress(FacilityAddressDTO.builder().postcode(postcode).build()).build())
                .build();
        final Facility facility2 = Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(facilityItem2)
                .build();
        FacilityItem facilityItem3 = FacilityItem.builder()
                .facilityId(facilityId3)
                .facilityDetails(FacilityDetails.builder().facilityAddress(FacilityAddressDTO.builder().postcode(postcode).build()).build())
                .build();
        final Facility facility3 = Facility.builder()
                .status(FacilityStatus.EXCLUDED)
                .excludedDate(LocalDate.of(2022, 1, 1))
                .facilityItem(facilityItem3)
                .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder().facilities(Set.of(facility1, facility2, facility3)).build())
                        .underlyingAgreementTargetUnitDetails(unaDetails)
                        .build())
                .facilitiesReviewGroupDecisions(Map.of(
                        facilityId1, UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .facilityStatus(FacilityStatus.LIVE)
                                .build(),
                        facilityId2, UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .facilityStatus(FacilityStatus.NEW)
                                .startDate(LocalDate.of(2024, 11, 25))
                                .build(),
                        facilityId3, UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .facilityStatus(FacilityStatus.EXCLUDED)
                                .build()
                ))
                .build();

        final Set<FacilityBaseInfoDTO> createdFacilities = Set.of(FacilityBaseInfoDTO.builder()
                .facilityBusinessId(facilityItem2.getFacilityId())
                .build());
        final Set<FacilityItem> facilityItems = Set.of(facilityItem2);
        final Map<String, String> searchKeywordPairs = Map.of(
                "facilityId1_FACILITY_ID", "facilityId1",
                "facilityId2_FACILITY_ID", "facilityId2",
                "facilityId3_FACILITY_ID", "facilityId3",
                "facilityId1_POST_CODE", "postcode",
                "facilityId2_POST_CODE", "postcode",
                "facilityId3_POST_CODE", "postcode"
        );

        when(facilityDataUpdateService.createFacilitiesData(anyList())).thenReturn(createdFacilities);
        when(underlyingAgreementService.createSearchKeywordsForAccount(operatorName, unaContainerFinal)).thenReturn(searchKeywordPairs);

        // Invoke
        service.updateFacilitiesAndAccount(accountId, unaContainerFinal, requestPayload);

        // Verify
        verify(facilityDataUpdateService, times(1)).createFacilitiesData(anyList());
        verify(facilityCertificationTransferService, times(1)).processFacilityCertificationsForNewFacilities(createdFacilities, facilityItems);
        verify(facilityDataUpdateService, times(1)).updateFacilitiesData(anyList());
        verify(underlyingAgreementService, times(1)).createSearchKeywordsForAccount(operatorName, unaContainerFinal);
        verify(accountSearchAdditionalKeywordService, times(1)).storeKeywordsForAccount(accountId, searchKeywordPairs);
        verify(targetUnitAccountUpdateService, times(1)).updateTargetUnitAccountUponUnderlyingAgreementVariation(
                accountId, TargetUnitAccountUpdateDTO.builder().operatorName(operatorName).build());
    }
}
