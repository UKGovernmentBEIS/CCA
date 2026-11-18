package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
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

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityRunCreateActionHandlerTest {

	@InjectMocks
    private BuyOutSurplusFacilityRunCreateActionHandler handler;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Test
    void process() {
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final BuyOutSurplusFacilityRunCreateActionPayload payload = BuyOutSurplusFacilityRunCreateActionPayload.builder()
                .targetPeriodType(targetPeriodType)
                .build();
        final AppUser appUser = AppUser.builder().userId("regulator").build();
        final TargetPeriodInfoDTO targetPeriodDetails = TargetPeriodInfoDTO.builder()
                .businessId(targetPeriodType)
                .build();
        final CcaRequestParams requestParams = CcaRequestParams.builder()
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
                .processVars(Map.of(CcaBpmnProcessConstants.ACCOUNT_IDS, new HashSet<>()))
                .build();

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriodDetails);
        when(startProcessRequestService.startProcess(requestParams))
                .thenReturn(Request.builder().id("request-id").build());

        // Invoke
        handler.process(ca, payload, appUser);

        // Verify
        verify(targetPeriodService, times(1)).getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void getRequestType() {
        assertThat(handler.getRequestType()).isEqualTo(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN);
    }
}
