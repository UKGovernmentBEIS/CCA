package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.run.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.service.SchemeTerminationHelper;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.run.domain.BuyOutSurplusRunCreateActionPayload;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusRunCreateValidatorTest {

    @InjectMocks
    private BuyOutSurplusRunCreateValidator validator;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private TargetPeriodService targetPeriodService;
    
    @Mock
    private SchemeTerminationHelper schemeTerminationHelper;

    @Test
    void validateAction() {
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusRunCreateActionPayload payload = BuyOutSurplusRunCreateActionPayload.builder()
                .targetPeriodType(TargetPeriodType.TP6)
                .build();
        final TargetPeriodInfoDTO targetPeriod = TargetPeriodInfoDTO.builder()
                .buyOutStartDate(LocalDate.of(2020, 1, 1))
                .build();

        when(requestQueryService
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, ca))
                .thenReturn(false);
        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(TargetPeriodType.TP6))
                .thenReturn(targetPeriod);
        when(schemeTerminationHelper.isAfterCca2TerminationDate(any(LocalDate.class)))
        		.thenReturn(false);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(ca, payload);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, ca);
        verify(targetPeriodService, times(1)).getTargetPeriodInfoByTargetPeriodType(TargetPeriodType.TP6);
        verify(schemeTerminationHelper).isAfterCca2TerminationDate(any(LocalDate.class));
    }

    @Test
    void validateAction_not_TP6_NOT_VALID() {
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusRunCreateActionPayload payload = BuyOutSurplusRunCreateActionPayload.builder()
                .targetPeriodType(TargetPeriodType.TP5)
                .build();

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(ca, payload);

        // Verify
        assertThat(result.isValid()).isFalse();
        verifyNoInteractions(requestQueryService, targetPeriodService, schemeTerminationHelper);
    }

    @Test
    void validateAction_exist_NOT_VALID() {
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusRunCreateActionPayload payload = BuyOutSurplusRunCreateActionPayload.builder()
                .targetPeriodType(TargetPeriodType.TP6)
                .build();

        when(requestQueryService
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, ca))
                .thenReturn(true);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(ca, payload);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, ca);
        verifyNoInteractions(targetPeriodService, schemeTerminationHelper);
    }

    @Test
    void validateAction_before_period_NOT_VALID() {
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusRunCreateActionPayload payload = BuyOutSurplusRunCreateActionPayload.builder()
                .targetPeriodType(TargetPeriodType.TP6)
                .build();
        final TargetPeriodInfoDTO targetPeriod = TargetPeriodInfoDTO.builder()
                .buyOutStartDate(LocalDate.of(3020, 1, 1))
                .build();

        when(requestQueryService
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, ca))
                .thenReturn(false);
        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(TargetPeriodType.TP6))
                .thenReturn(targetPeriod);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(ca, payload);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, ca);
        verify(targetPeriodService, times(1)).getTargetPeriodInfoByTargetPeriodType(TargetPeriodType.TP6);
        verifyNoInteractions(schemeTerminationHelper);
    }
    
    @Test
    void validateAction_after_cca2_termination_NOT_VALID() {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        BuyOutSurplusRunCreateActionPayload payload =
                BuyOutSurplusRunCreateActionPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP6)
                        .build();

        TargetPeriodInfoDTO targetPeriod = TargetPeriodInfoDTO.builder()
                .buyOutStartDate(LocalDate.of(2020, 1, 1))
                .build();

        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_RUN,
                RequestStatuses.IN_PROGRESS,
                ca)).thenReturn(false);
        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(TargetPeriodType.TP6))
                .thenReturn(targetPeriod);
        when(schemeTerminationHelper.isAfterCca2TerminationDate(any(LocalDate.class)))
                .thenReturn(true);

        RequestCreateValidationResult result = validator.validateAction(ca, payload);

        assertThat(result.isValid()).isFalse();
        verify(schemeTerminationHelper).isAfterCca2TerminationDate(any(LocalDate.class));
    }

    @Test
    void getRequestType() {
        assertThat(validator.getRequestType()).isEqualTo(CcaRequestType.BUY_OUT_SURPLUS_RUN);
    }
}
