package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.domain.BuyOutSurplusRunCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestCACreateActionHandler;

import java.util.HashSet;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BuyOutSurplusRunCreateActionHandler implements RequestCACreateActionHandler<BuyOutSurplusRunCreateActionPayload> {

    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(CompetentAuthorityEnum ca, BuyOutSurplusRunCreateActionPayload payload, AppUser appUser) {
        // TODO Get eligible accounts
        Map<Long, BuyOutSurplusAccountState> accountStates = Map.of();

        // Create process
        CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.BUY_OUT_SURPLUS_RUN)
                .requestResources(Map.of(ResourceType.CA, ca.name()))
                .requestPayload(BuyOutSurplusRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.BUY_OUT_SURPLUS_RUN_REQUEST_PAYLOAD)
                        .buyOutSurplusAccountStates(accountStates)
                        .submitterId(appUser.getUserId())
                        .build())
                .requestMetadata(BuyOutSurplusRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.BUY_OUT_SURPLUS_RUN)
                        .targetPeriodType(payload.getTargetPeriodType())
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, new HashSet<>(accountStates.keySet()),
                        CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATES, accountStates,
                        CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.BUY_OUT_SURPLUS_RUN;
    }
}
