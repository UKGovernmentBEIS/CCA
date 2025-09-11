package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestPayload;
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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BuyOutSurplusRunCreateActionHandler implements RequestCACreateActionHandler<BuyOutSurplusRunCreateActionPayload> {

    private final TargetPeriodService targetPeriodService;
    private final StartProcessRequestService startProcessRequestService;
    private final BuyOutSurplusQueryService buyOutSurplusQueryService;

    @Override
    @Transactional
    public String process(CompetentAuthorityEnum ca, BuyOutSurplusRunCreateActionPayload payload, AppUser appUser) {

        Map<Long, BuyOutSurplusAccountState> accountStates = buyOutSurplusQueryService
                .getAllEligibleAccountsByTargetPeriod(payload.getTargetPeriodType()).stream()
                .collect(Collectors
                        .toMap(TargetUnitAccountBusinessInfoDTO::getAccountId,
                                dto -> BuyOutSurplusAccountState.builder()
                                        .accountId(dto.getAccountId())
                                        .businessId(dto.getBusinessId())
                                        .build()));

        // Get Target Period details
        TargetPeriodDTO targetPeriodDetails = targetPeriodService.getTargetPeriodByBusinessId(payload.getTargetPeriodType());

        // Create process
        CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.BUY_OUT_SURPLUS_RUN)
                .requestResources(Map.of(ResourceType.CA, ca.name()))
                .requestPayload(BuyOutSurplusRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.BUY_OUT_SURPLUS_RUN_REQUEST_PAYLOAD)
                        .buyOutSurplusAccountStates(accountStates)
                        .submitterId(appUser.getUserId())
                        .targetPeriodDetails(targetPeriodDetails)
                        .build())
                .requestMetadata(BuyOutSurplusRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.BUY_OUT_SURPLUS_RUN)
                        .targetPeriodType(targetPeriodDetails.getBusinessId())
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, new HashSet<>(accountStates.keySet()),
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
