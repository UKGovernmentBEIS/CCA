package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.domain.BuyOutSurplusFacilityRunCreateActionPayload;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityRunCreateValidatorTest {

    @InjectMocks
    private BuyOutSurplusFacilityRunCreateValidator validator;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Test
    void validateAction() {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        BuyOutSurplusFacilityRunCreateActionPayload payload =
                BuyOutSurplusFacilityRunCreateActionPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .build();

        TargetPeriod tp7 = TargetPeriod.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .businessId(TargetPeriodType.TP7)
                .buyOutCost(10)
                .build();

        TargetPeriod tp8 = TargetPeriod.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .businessId(TargetPeriodType.TP8)
                .buyOutCost(20)
                .build();

        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(false);
        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(false);
        when(targetPeriodService.getTargetPeriodBuyOutCurrentAndPrevious(any(LocalDate.class)))
                .thenReturn(List.of(tp7, tp8));

        RequestCreateValidationResult result = validator.validateAction(ca, payload);

        assertThat(result.isValid()).isTrue();
        verify(requestQueryService).existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN,
                RequestStatuses.IN_PROGRESS,
                ca);
        verify(requestQueryService).existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_RUN,
                RequestStatuses.IN_PROGRESS,
                ca);
        verify(targetPeriodService).getTargetPeriodBuyOutCurrentAndPrevious(any(LocalDate.class));
    }

    @Test
    void validateAction_not_CCA3_target_period_NOT_VALID() {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        BuyOutSurplusFacilityRunCreateActionPayload payload =
                BuyOutSurplusFacilityRunCreateActionPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP6)
                        .build();

        RequestCreateValidationResult result = validator.validateAction(ca, payload);

        assertThat(result.isValid()).isFalse();
        verifyNoInteractions(requestQueryService, targetPeriodService);
    }

    @Test
    void validateAction_facility_run_exists_NOT_VALID() {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        BuyOutSurplusFacilityRunCreateActionPayload payload =
                BuyOutSurplusFacilityRunCreateActionPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .build();

        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(true);
        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(false);

        RequestCreateValidationResult result = validator.validateAction(ca, payload);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes())
                .containsExactly(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN);
        verifyNoInteractions(targetPeriodService);
    }

    @Test
    void validateAction_account_run_exists_NOT_VALID() {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        BuyOutSurplusFacilityRunCreateActionPayload payload =
                BuyOutSurplusFacilityRunCreateActionPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .build();

        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(false);
        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(true);

        RequestCreateValidationResult result = validator.validateAction(ca, payload);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes())
                .containsExactly(CcaRequestType.BUY_OUT_SURPLUS_RUN);
        verifyNoInteractions(targetPeriodService);
    }

    @Test
    void validateAction_selected_target_period_is_not_current_NOT_VALID() {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        BuyOutSurplusFacilityRunCreateActionPayload payload =
                BuyOutSurplusFacilityRunCreateActionPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP8)
                        .build();

        TargetPeriod tp7 = TargetPeriod.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .businessId(TargetPeriodType.TP7)
                .buyOutCost(10)
                .build();

        TargetPeriod tp8 = TargetPeriod.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .businessId(TargetPeriodType.TP8)
                .buyOutCost(20)
                .build();

        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(false);
        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(false);
        when(targetPeriodService.getTargetPeriodBuyOutCurrentAndPrevious(any(LocalDate.class)))
                .thenReturn(List.of(tp7, tp8));

        RequestCreateValidationResult result = validator.validateAction(ca, payload);

        assertThat(result.isValid()).isFalse();
    }

    @Test
    void validateAction_no_cca3_target_periods_NOT_VALID() {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        BuyOutSurplusFacilityRunCreateActionPayload payload =
                BuyOutSurplusFacilityRunCreateActionPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .build();

        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(false);
        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(false);
        when(targetPeriodService.getTargetPeriodBuyOutCurrentAndPrevious(any(LocalDate.class)))
                .thenReturn(List.of());

        RequestCreateValidationResult result = validator.validateAction(ca, payload);

        assertThat(result.isValid()).isFalse();
    }

    @Test
    void validateAction_buy_out_cost_missing_NOT_VALID() {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        BuyOutSurplusFacilityRunCreateActionPayload payload =
                BuyOutSurplusFacilityRunCreateActionPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .build();

        TargetPeriod tp7 = TargetPeriod.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .businessId(TargetPeriodType.TP7)
                .buyOutCost(10)
                .build();

        TargetPeriod tp8 = TargetPeriod.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .businessId(TargetPeriodType.TP8)
                .buyOutCost(null)
                .build();

        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(false);
        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(false);
        when(targetPeriodService.getTargetPeriodBuyOutCurrentAndPrevious(any(LocalDate.class)))
                .thenReturn(List.of(tp7, tp8));

        RequestCreateValidationResult result = validator.validateAction(ca, payload);

        assertThat(result.isValid()).isFalse();
    }

    @Test
    void getRequestType() {
        assertThat(validator.getRequestType()).isEqualTo(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN);
    }
}