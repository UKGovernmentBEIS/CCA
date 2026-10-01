package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
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
    private BuyOutSurplusQueryService buyOutSurplusQueryService;
    
    @Mock
    private TargetPeriodService targetPeriodService;

    @Test
    void process() {

    	final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final LocalDate currentDate = LocalDate.now();

        final BuyOutSurplusFacilityRunCreateActionPayload payload =
                BuyOutSurplusFacilityRunCreateActionPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .build();

        final AppUser appUser = AppUser.builder()
                .userId("regulator")
                .build();

        final TargetUnitAccountBusinessInfoDTO eligibleAccount =
                TargetUnitAccountBusinessInfoDTO.builder()
                        .accountId(1L)
                        .businessId("AIC-0001")
                        .build();

        final TargetPeriod targetPeriod = TargetPeriod.builder()
        		.schemeVersion(SchemeVersion.CCA_3)
                .build();

        final TargetPeriodInfoDTO targetPeriodDetails =
                TargetPeriodInfoDTO.builder()
                        .businessId(targetPeriodType)
                        .secondaryReportingStartDate(currentDate.plusDays(10))
                        .build();

        final BuyOutSurplusFacilityAccountState accountState =
                BuyOutSurplusFacilityAccountState.builder()
                        .accountId(1L)
                        .businessId("AIC-0001")
                        .build();

        final CcaRequestParams requestParams =
                CcaRequestParams.builder()
                        .type(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN)
                        .requestResources(Map.of(ResourceType.CA, ca.name()))
                        .requestPayload(BuyOutSurplusFacilityRunRequestPayload.builder()
                                .payloadType(CcaRequestPayloadType.BUY_OUT_SURPLUS_FACILITY_RUN_REQUEST_PAYLOAD)
                                .submitterId(appUser.getUserId())
                                .creationDate(currentDate)
                                .targetPeriodsDetails(List.of(targetPeriodDetails))
                                .accountStates(
                                        Map.of(1L, accountState))
                                .applyPrimaryRules(true)
                                .build())
                        .requestMetadata(BuyOutSurplusFacilityRunRequestMetadata.builder()
                                .type(CcaRequestMetadataType.BUY_OUT_SURPLUS_FACILITY_RUN)
                                .targetPeriodType(targetPeriodType)
                                .build())
                        .processVars(Map.of(
                                CcaBpmnProcessConstants.ACCOUNT_IDS,
                                Set.of(1L)))
                        .build();

        when(buyOutSurplusQueryService.getAllEligibleAccountsByTargetPeriod(targetPeriodType))
                .thenReturn(List.of(eligibleAccount));
        when(targetPeriodService.findByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(targetPeriodService.getTargetPeriodsInfoForSchemeUpTo(
        		targetPeriod.getSchemeVersion(), currentDate))
        		.thenReturn(List.of(targetPeriodDetails));
        when(startProcessRequestService.startProcess(requestParams))
        		.thenReturn(Request.builder().id("request-id").build());

        String result = handler.process(ca, payload, appUser);

        // Verify
        assertThat(result).isEqualTo("request-id");
        verify(buyOutSurplusQueryService).getAllEligibleAccountsByTargetPeriod(targetPeriodType);
        verify(targetPeriodService).findByTargetPeriodType(targetPeriodType);
        verify(targetPeriodService).getTargetPeriodsInfoForSchemeUpTo(
        		targetPeriod.getSchemeVersion(), currentDate);
        verify(startProcessRequestService).startProcess(requestParams);
    }

    @Test
    void getRequestType() {
        assertThat(handler.getRequestType())
                .isEqualTo(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN);
    }
}
