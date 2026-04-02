package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineEnergyConsumption;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationCompletedServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationCompletedService service;

    @Mock
    private RequestService requestService;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private UnderlyingAgreementService underlyingAgreementService;

    @Mock
    private UnderlyingAgreementVariationService underlyingAgreementVariationService;

    @Test
    void completeUnderlyingAgreementVariation() {
        final String requestId = "1";
        final Long accountId = 2L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final LocalDateTime creationDate = LocalDateTime.now();

        final Facility facility = Facility.builder()
                .status(FacilityStatus.LIVE)
                .facilityItem(FacilityItem.builder()
                        .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                .facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder().build())
                                .build())
                        .build())
                .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .workflowSchemeVersion(schemeVersion)
                .originalUnderlyingAgreementContainer(UnderlyingAgreementContainer.builder().build())
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(facility))
                                .build())
                        .build())
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .creationDate(creationDate)
                .payload(requestPayload)
                .build();
        addResourcesToRequest(accountId, request);
        final AccountReferenceData referenceData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder()
                        .schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
                                .sectorMeasurementType(MeasurementType.ENERGY_GJ)
                                .build()))
                        .build())
                .build();
        final UnderlyingAgreementContainer unaContainerFinal = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
                        .sectorMeasurementType(MeasurementType.ENERGY_GJ)
                        .build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(creationDate)
                .schemeVersion(schemeVersion)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(accountReferenceDetailsService.getAccountReferenceData(accountId)).thenReturn(referenceData);

        // Invoke
        service.completeUnderlyingAgreementVariation(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(accountReferenceDetailsService, times(1)).getAccountReferenceData(accountId);
        verify(underlyingAgreementService, times(1))
                .updateUnderlyingAgreement(unaContainerFinal, accountId, underlyingAgreementValidationContext, false);
        verify(underlyingAgreementVariationService, times(1))
                .updateFacilitiesAndAccount(accountId, unaContainerFinal, requestPayload);
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
