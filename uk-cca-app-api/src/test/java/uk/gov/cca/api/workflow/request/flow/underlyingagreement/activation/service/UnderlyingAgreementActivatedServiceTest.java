package uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.service;

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
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
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
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementActivatedServiceTest {

    @InjectMocks
    private UnderlyingAgreementActivatedService service;

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
    void execute() {
        final String requestId = "1";
        final String user = "user";
        final Long accountId = 2L;
        final String facilityId = "facilityId";
        final String facilityId2 = "facilityId2";
        final String postcode = "postcode";
        final String postcode2 = "postcode2";

        final AccountReferenceData referenceData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder()
                        .measurementType(MeasurementType.ENERGY_GJ)
                        .build())
                .build();
        final Facility facility = Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder()
                        .facilityId(facilityId)
                        .facilityDetails(FacilityDetails.builder().facilityAddress(AccountAddressDTO.builder().postcode(postcode).build()).build())
                        .build())
                .build();
        final Facility facility2 = Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder()
                        .facilityId(facilityId2)
                        .facilityDetails(FacilityDetails.builder().facilityAddress(AccountAddressDTO.builder().postcode(postcode2).build()).build())
                        .build())
                .build();
        final UnderlyingAgreement una = UnderlyingAgreement.builder().facilities(Set.of(facility, facility2)).build();
        final UnderlyingAgreement unaProposed = UnderlyingAgreement.builder().facilities(Set.of(facility, facility2)).build();
        final UnderlyingAgreementTargetUnitDetails unaDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final Determination determination = Determination.builder().type(DeterminationType.ACCEPTED).build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder().sectorUsers(Set.of(user)).build();
        final Map<String, UnderlyingAgreementFacilityReviewDecision> facilitiesReviewGroupDecisions = new HashMap<>(
                Map.ofEntries(
                        Map.entry(facilityId, UnderlyingAgreementFacilityReviewDecision.builder()
                                .changeStartDate(true)
                                .startDate(LocalDate.of(2018, 1, 1))
                                .build()),
                        Map.entry(facilityId2, UnderlyingAgreementFacilityReviewDecision.builder()
                                .changeStartDate(false)
                                .build())
                )
        );

        final UnderlyingAgreementRequestPayload unaRequestPayload = UnderlyingAgreementRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_REQUEST_PAYLOAD)
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreement(una)
                        .underlyingAgreementTargetUnitDetails(unaDetails)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                        .underlyingAgreement(unaProposed)
                        .underlyingAgreementTargetUnitDetails(unaDetails)
                        .build())
                .decisionNotification(decisionNotification)
                .determination(determination)
                .regulatorReviewer("reviewer")
                .facilitiesReviewGroupDecisions(facilitiesReviewGroupDecisions)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(unaRequestPayload)
                .build();

        addResourcesToRequest(accountId, request);

        final UnderlyingAgreementContainer unaContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(una)
                .sectorMeasurementType(MeasurementType.ENERGY_GJ)
                .build();

        final Map<String, String> searchKeywordPairs = Map.of(
                "facilityId_FACILITY_ID", "facilityId",
                "facilityId2_FACILITY_ID", "facilityId2",
                "facilityId_POST_CODE", "postcode",
                "facilityId2_POST_CODE", "postcode2");

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(accountReferenceDetailsService.getAccountReferenceData(accountId)).thenReturn(referenceData);
        when(underlyingAgreementService.createSearchKeywordsForAccount(unaContainer)).thenReturn(searchKeywordPairs);
        when(underlyingAgreementService.submitUnderlyingAgreement(unaContainer, accountId))
                .thenReturn(UnderlyingAgreementEntity.builder().underlyingAgreementContainer(unaContainer).accountId(accountId).build());
        service.activateUnderlyingAgreement(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(underlyingAgreementService, times(1)).submitUnderlyingAgreement(unaContainer, request.getAccountId());
        verify(underlyingAgreementService, times(1)).createSearchKeywordsForAccount(unaContainer);
        verify(accountReferenceDetailsService, times(1)).getAccountReferenceData(request.getAccountId());
        verify(facilityDataUpdateService, times(1)).createFacilitiesData(List.of(FacilityDataCreationDTO.builder()
                .accountId(accountId)
                .facilityId(facilityId)
                .createdDate(any())
                .chargeStartDate(LocalDate.of(2018, 1, 1))
                .build()));
        verify(facilityDataUpdateService, times(1)).createFacilitiesData(List.of(FacilityDataCreationDTO.builder()
                .accountId(accountId)
                .facilityId(facilityId2)
                .createdDate(any())
                .chargeStartDate(null)
                .build()));

        verify(accountSearchAdditionalKeywordService, times(1)).storeKeywordsForAccount(request.getAccountId(), searchKeywordPairs);
        verify(targetUnitAccountUpdateService, times(1)).activateTargetUnitAccount(
                eq(request.getAccountId()), any(TargetUnitAccountUpdateDTO.class), any(LocalDateTime.class));

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
