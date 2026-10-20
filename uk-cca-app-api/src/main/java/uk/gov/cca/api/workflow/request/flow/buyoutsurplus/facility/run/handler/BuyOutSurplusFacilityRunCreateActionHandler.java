package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.handler;

import java.util.HashSet;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.domain.BuyOutSurplusFacilityRunCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestCACreateActionHandler;

@Component
@RequiredArgsConstructor
public class BuyOutSurplusFacilityRunCreateActionHandler implements RequestCACreateActionHandler<BuyOutSurplusFacilityRunCreateActionPayload> {

    private final TargetPeriodService targetPeriodService;
    private final StartProcessRequestService startProcessRequestService;

    @Override
    @Transactional
    public String process(CompetentAuthorityEnum ca, BuyOutSurplusFacilityRunCreateActionPayload payload, AppUser appUser) {

        // TODO get account states

        // Get Target Period details
        TargetPeriodInfoDTO targetPeriodDetails = targetPeriodService.getTargetPeriodInfoByTargetPeriodType(payload.getTargetPeriodType());

        // Create process
        CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN)
                .requestResources(Map.of(ResourceType.CA, ca.name()))
                .requestPayload(BuyOutSurplusFacilityRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.BUY_OUT_SURPLUS_FACILITY_RUN_REQUEST_PAYLOAD)
                        .submitterId(appUser.getUserId())
                        .targetPeriodDetails(targetPeriodDetails)
                        .build())
                .requestMetadata(BuyOutSurplusFacilityRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.BUY_OUT_SURPLUS_FACILITY_RUN)
                        .targetPeriodType(targetPeriodDetails.getBusinessId())
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, new HashSet<>()))
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN;
    }
}
