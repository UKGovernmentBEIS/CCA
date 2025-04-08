package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.domain.BuyOutSurplusRunCreateActionPayload;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateByCAValidator;

import java.time.LocalDate;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BuyOutSurplusRunCreateValidator implements RequestCreateByCAValidator<BuyOutSurplusRunCreateActionPayload> {

    private final RequestQueryService requestQueryService;
    private final TargetPeriodService targetPeriodService;

    @Override
    public RequestCreateValidationResult validateAction(CompetentAuthorityEnum competentAuthority, BuyOutSurplusRunCreateActionPayload payload) {
        if(!TargetPeriodType.TP6.equals(payload.getTargetPeriodType())) {
            return RequestCreateValidationResult.builder()
                    .valid(false)
                    .build();
        }

        final boolean exist = requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, competentAuthority);
        if(exist) {
            return RequestCreateValidationResult.builder()
                    .valid(false)
                    .reportedRequestTypes(Set.of(CcaRequestType.BUY_OUT_SURPLUS_RUN))
                    .build();
        }

        final TargetPeriodDTO targetPeriod = targetPeriodService.getTargetPeriodByBusinessId(payload.getTargetPeriodType());
        if(LocalDate.now().isBefore(targetPeriod.getBuyOutStartDate())) {
            return RequestCreateValidationResult.builder()
                    .valid(false)
                    .build();
        }

        return RequestCreateValidationResult.builder().valid(true).build();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.BUY_OUT_SURPLUS_RUN;
    }
}
