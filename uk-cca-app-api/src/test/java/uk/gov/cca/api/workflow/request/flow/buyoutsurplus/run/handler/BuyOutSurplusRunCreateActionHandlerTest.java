package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusRunCreateActionHandlerTest {

    @InjectMocks
    private BuyOutSurplusRunCreateActionHandler handler;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private BuyOutSurplusQueryService buyOutSurplusQueryService;

    @Test
    void process() {
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final BuyOutSurplusRunCreateActionPayload payload = BuyOutSurplusRunCreateActionPayload.builder()
                .targetPeriodType(targetPeriodType)
                .build();
        final AppUser appUser = AppUser.builder().userId("regulator").build();
        final Long accountId = 999L;
        final String accountBusinessId = "businessId";
        final TargetUnitAccountBusinessInfoDTO accountBusinessInfoDTO = TargetUnitAccountBusinessInfoDTO.builder()
                .accountId(accountId).businessId(accountBusinessId).build();
        final BuyOutSurplusAccountState buyOutSurplusAccountState = BuyOutSurplusAccountState.builder()
                .accountId(accountBusinessInfoDTO.getAccountId())
                .businessId(accountBusinessInfoDTO.getBusinessId())
                .build();
        final Map<Long, BuyOutSurplusAccountState> accountStates = new HashMap<>(Map.of(buyOutSurplusAccountState.getAccountId(), buyOutSurplusAccountState));

        final TargetPeriodInfoDTO targetPeriodDetails = TargetPeriodInfoDTO.builder()
                .businessId(targetPeriodType)
                .build();
        final CcaRequestParams requestParams = CcaRequestParams.builder()
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

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriodDetails);
        when(startProcessRequestService.startProcess(requestParams))
                .thenReturn(Request.builder().id("request-id").build());
        when(buyOutSurplusQueryService
                .getAllEligibleAccountsByTargetPeriod(payload.getTargetPeriodType()))
                .thenReturn(List.of(accountBusinessInfoDTO));


        // Invoke
        handler.process(ca, payload, appUser);

        // Verify
        verify(targetPeriodService, times(1)).getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        verify(buyOutSurplusQueryService, times(1))
                .getAllEligibleAccountsByTargetPeriod(targetPeriodType);
    }

    @Test
    void getRequestType() {
        assertThat(handler.getRequestType()).isEqualTo(CcaRequestType.BUY_OUT_SURPLUS_RUN);
    }
}
