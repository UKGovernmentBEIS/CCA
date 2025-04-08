package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
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

import java.util.HashMap;
import java.util.HashSet;
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

    @Test
    void process() {
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusRunCreateActionPayload payload = BuyOutSurplusRunCreateActionPayload.builder()
                .targetPeriodType(TargetPeriodType.TP6)
                .build();
        final AppUser appUser = AppUser.builder().userId("regulator").build();
        final Map<Long, BuyOutSurplusAccountState> accountStates = new HashMap<>();

        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.BUY_OUT_SURPLUS_RUN)
                .requestResources(Map.of(ResourceType.CA, ca.name()))
                .requestPayload(BuyOutSurplusRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.BUY_OUT_SURPLUS_RUN_REQUEST_PAYLOAD)
                        .buyOutSurplusAccountStates(accountStates)
                        .submitterId("regulator")
                        .build())
                .requestMetadata(BuyOutSurplusRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.BUY_OUT_SURPLUS_RUN)
                        .targetPeriodType(TargetPeriodType.TP6)
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, new HashSet<>(accountStates.keySet()),
                        CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATES, accountStates,
                        CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        when(startProcessRequestService.startProcess(requestParams))
                .thenReturn(Request.builder().id("request-id").build());

        // Invoke
        handler.process(ca, payload, appUser);

        // Verify
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void getRequestType() {
        assertThat(handler.getRequestType()).isEqualTo(CcaRequestType.BUY_OUT_SURPLUS_RUN);
    }
}
