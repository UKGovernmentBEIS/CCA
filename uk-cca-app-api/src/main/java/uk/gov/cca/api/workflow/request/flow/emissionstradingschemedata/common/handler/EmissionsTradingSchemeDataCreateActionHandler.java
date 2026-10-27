package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.underlyingagreement.utils.UnderlyingAgreementContainerUtil;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.RequestFacilityCreateActionHandler;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateFacilityAndAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.time.Year;
import java.util.Map;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class EmissionsTradingSchemeDataCreateActionHandler
        implements RequestFacilityCreateActionHandler<EmissionsTradingSchemeDataRequestCreateActionPayload> {

    private final RequestCreateFacilityAndAccountAndSectorResourcesService requestCreateFacilityAndAccountAndSectorResourcesService;
    private final FacilityDataQueryService facilityDataQueryService;
    private final StartProcessRequestService startProcessRequestService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Override
    public String process(Long facilityId, EmissionsTradingSchemeDataRequestCreateActionPayload payload, AppUser appUser) {

        Year targetYear = payload.getTargetYear();
        FacilityItem unaFacility = getUnaFacility(facilityId);
        String uketsId = unaFacility.getFacilityDetails().getUketsId();
        MeasurementType measurementType = getMeasurementType(unaFacility);

        final Map<String, String> requestResources = requestCreateFacilityAndAccountAndSectorResourcesService.createRequestResources(facilityId);

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

        Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.EMISSIONS_TRADING_SCHEME_DATA;
    }

    private MeasurementType getMeasurementType(FacilityItem unaFacility) {
        return unaFacility.getCca3BaselineAndTargets().getTargetComposition().getMeasurementType();
    }

    private FacilityItem getUnaFacility(Long facilityId) {
        final FacilityBaseInfoDTO facility = facilityDataQueryService.getFacilityBaseInfo(facilityId);
        Long accountId = facilityDataQueryService.getAccountIdByFacilityId(facilityId);
        UnderlyingAgreementDTO una = underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId);
        return UnderlyingAgreementContainerUtil
                .getUnaFacility(facility.getFacilityBusinessId(), una.getUnderlyingAgreementContainer())
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
}
