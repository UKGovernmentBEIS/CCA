package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargetComposition;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateFacilityAndAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.time.Year;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataCreateActionHandlerTest {

    @InjectMocks
    private EmissionsTradingSchemeDataCreateActionHandler handler;

    @Mock
    private RequestCreateFacilityAndAccountAndSectorResourcesService requestCreateFacilityAndAccountAndSectorResourcesService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Test
    void process() {
        final Year targetYear = Year.of(2026);
        final EmissionsTradingSchemeDataRequestCreateActionPayload payload = EmissionsTradingSchemeDataRequestCreateActionPayload.builder()
                .targetYear(targetYear)
                .build();
        final AppUser appUser = AppUser.builder().userId("sector").build();
        final Long facilityId = 1L;
        final String facilityBusinessId = "facilityBusinessId";
        final String siteName = "siteName";
        final String uketsId = "uketsId";
        final Long sectorAssociationId = 2L;
        final Long accountId = 3L;
        final MeasurementType measurementType = MeasurementType.ENERGY_KWH;
        final Map<String, String> requestResources = Map.of(
                CcaResourceType.FACILITY, facilityId.toString(),
                ResourceType.ACCOUNT, accountId.toString(),
                CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString());

        final FacilityBaseInfoDTO facilityBaseInfoDTO = FacilityBaseInfoDTO.builder()
                .id(facilityId)
                .facilityBusinessId(facilityBusinessId)
                .siteName(siteName)
                .build();

        final UnderlyingAgreementDTO underlyingAgreementDTO = UnderlyingAgreementDTO.builder()
                .accountId(accountId)
                .underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(Facility.builder()
                                        .facilityItem(FacilityItem.builder()
                                                .facilityDetails(FacilityDetails.builder()
                                                        .isCoveredByUkets(true)
                                                        .uketsId(uketsId)
                                                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                        .build())
                                                .facilityId(facilityBusinessId)
                                                .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                                        .targetComposition(FacilityTargetComposition.builder()
                                                                .measurementType(measurementType)
                                                                .build())
                                                        .build())
                                                .build())
                                        .build()))
                                .build())
                        .build())
                .build();

        CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.EMISSIONS_TRADING_SCHEME_DATA)
                .requestResources(requestResources)
                .requestPayload(EmissionsTradingSchemeDataRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.EMISSIONS_TRADING_SCHEME_DATA_PAYLOAD)
                        .sectorUserAssignee(appUser.getUserId())
                        .targetYear(targetYear)
                        .uketsId(uketsId)
                        .measurementType(measurementType)
                        .build())
                .requestMetadata(EmissionsTradingSchemeDataRequestMetadata.builder()
                        .type(CcaRequestMetadataType.EMISSIONS_TRADING_SCHEME_DATA)
                        .targetYear(targetYear)
                        .uketsId(uketsId)
                        .build())
                .build();

        when(requestCreateFacilityAndAccountAndSectorResourcesService.createRequestResources(facilityId)).thenReturn(requestResources);
        when(facilityDataQueryService.getFacilityBaseInfo(facilityId)).thenReturn(facilityBaseInfoDTO);
        when(facilityDataQueryService.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId)).thenReturn(underlyingAgreementDTO);
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(Request.builder().id("request-id").build());

        // invoke
        handler.process(facilityId, payload, appUser);

        // verify
        verify(requestCreateFacilityAndAccountAndSectorResourcesService, times(1)).createRequestResources(facilityId);
        verify(facilityDataQueryService, times(1)).getFacilityBaseInfo(facilityId);
        verify(facilityDataQueryService, times(1)).getAccountIdByFacilityId(facilityId);
        verify(underlyingAgreementQueryService, times(1)).getUnderlyingAgreementByAccountId(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void getRequestType() {
        assertThat(handler.getRequestType()).isEqualTo(CcaRequestType.EMISSIONS_TRADING_SCHEME_DATA);
    }
}
