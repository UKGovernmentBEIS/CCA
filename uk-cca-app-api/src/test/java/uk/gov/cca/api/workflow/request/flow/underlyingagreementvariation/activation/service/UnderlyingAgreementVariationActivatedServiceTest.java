package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountUpdateDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.facility.domain.dto.FacilityDataCreationDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDataUpdateDTO;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationActivatedServiceTest {


    @InjectMocks
    private UnderlyingAgreementVariationActivatedService service;

    @Mock
    private TargetUnitAccountUpdateService targetUnitAccountUpdateService;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementService underlyingAgreementService;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private FacilityDataUpdateService facilityDataUpdateService;

    @Mock
    private AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;


    @Test
    void activateUnderlyingAgreementVariation() {
        final String requestId = "1";
        final String user = "user";
        final Long accountId = 2L;
        final String facilityId1 = "facilityId1";
        final String facilityId2 = "facilityId2";
        final String facilityId3 = "facilityId3";
        final String postcode = "postcode";
        final AccountReferenceData referenceData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder()
                        .measurementType(MeasurementType.ENERGY_GJ)
                        .build())
                .build();
        final Facility facility1 = Facility.builder()
                .status(FacilityStatus.LIVE)
                .facilityItem(FacilityItem.builder()
                        .facilityId(facilityId1)
                        .facilityDetails(FacilityDetails.builder().facilityAddress(AccountAddressDTO.builder().postcode(postcode).build()).build())
                        .build())
                .build();
        final Facility facility2 = Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder()
                        .facilityId(facilityId2)
                        .facilityDetails(FacilityDetails.builder().facilityAddress(AccountAddressDTO.builder().postcode(postcode).build()).build())
                        .build())
                .build();
        final Facility facility3 = Facility.builder()
                .status(FacilityStatus.EXCLUDED)
                .excludedDate(LocalDate.of(2022, 1, 1))
                .facilityItem(FacilityItem.builder()
                        .facilityId(facilityId3)
                        .facilityDetails(FacilityDetails.builder().facilityAddress(AccountAddressDTO.builder().postcode(postcode).build()).build())
                        .build())
                .build();
        final UnderlyingAgreement originalUna = UnderlyingAgreement.builder().facilities(Set.of(facility1)).build();

        final UnderlyingAgreementTargetUnitDetails unaDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final Determination determination = Determination.builder().type(DeterminationType.ACCEPTED).build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder().sectorUsers(Set.of(user)).build();
        final UnderlyingAgreementVariationRequestPayload unaRequestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_VARIATION_REQUEST_PAYLOAD)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder().facilities(Set.of(facility1, facility2, facility3)).build())
                        .underlyingAgreementTargetUnitDetails(unaDetails)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder().facilities(Set.of(facility1, facility2, facility3)).build())
                        .underlyingAgreementTargetUnitDetails(unaDetails)
                        .build())
                .originalUnderlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                        .underlyingAgreement(originalUna)
                        .build())
                .facilitiesReviewGroupDecisions(Map.of(
                        facilityId1, UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .facilityStatus(FacilityStatus.LIVE)
                                .build(),
                        facilityId2, UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .facilityStatus(FacilityStatus.NEW)
                                .startDate(LocalDate.of(2024, 11,25))
                                .build(),
                        facilityId3, UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .facilityStatus(FacilityStatus.EXCLUDED)
                                .build()
                ))
                .decisionNotification(decisionNotification)
                .determination(determination)
                .regulatorReviewer("reviewer")
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(unaRequestPayload)
                .build();
        addResourcesToRequest(accountId, request);

        final UnderlyingAgreementContainer unaContainerFinal = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().facilities(Set.of(facility1, facility2)).build())
                .sectorMeasurementType(MeasurementType.ENERGY_GJ)
                .build();

        final Map<String, String> searchKeywordPairs = Map.of(
                "facilityId1_FACILITY_ID", "facilityId1",
                "facilityId2_FACILITY_ID", "facilityId2",
                "facilityId3_FACILITY_ID", "facilityId3",
                "facilityId1_POST_CODE", "postcode",
                "facilityId2_POST_CODE", "postcode",
                "facilityId3_POST_CODE", "postcode"
        );

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(accountReferenceDetailsService.getAccountReferenceData(accountId)).thenReturn(referenceData);
        when(underlyingAgreementService.createSearchKeywordsForAccount(unaContainerFinal)).thenReturn(searchKeywordPairs);

        service.activateUnderlyingAgreementVariation(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(underlyingAgreementService, times(1)).updateUnderlyingAgreement(unaContainerFinal, request.getAccountId());
        verify(underlyingAgreementService, times(1)).createSearchKeywordsForAccount(unaContainerFinal);
        verify(accountReferenceDetailsService, times(1)).getAccountReferenceData(request.getAccountId());
        verify(facilityDataUpdateService, times(1)).createFacilitiesData(List.of(FacilityDataCreationDTO.builder()
                .accountId(accountId)
                .facilityId(facilityId2)
                .createdDate(any())
                .build()));
        verify(facilityDataUpdateService, times(1)).updateFacilitiesData(List.of(FacilityDataUpdateDTO.builder()
                .facilityId(facilityId3)
                .closedDate(any())
                .build()));
        verify(accountSearchAdditionalKeywordService, times(1)).storeKeywordsForAccount(accountId, searchKeywordPairs);
        verify(targetUnitAccountUpdateService, times(1)).updateTargetUnitAccountUponUnderlyingAgreementVariation(
                eq(request.getAccountId()), any(TargetUnitAccountUpdateDTO.class));
    }

    private void addResourcesToRequest(Long accountId, Request request) {
		RequestResource accountResource = RequestResource.builder()
				.resourceType(ResourceType.ACCOUNT)
				.resourceId(accountId.toString())
				.request(request)
				.build();

        request.getRequestResources().add(accountResource);
	}
}
