package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
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
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateByCAValidator;

@RequiredArgsConstructor
@Service
public class BuyOutSurplusFacilityRunCreateValidator implements RequestCreateByCAValidator<BuyOutSurplusFacilityRunCreateActionPayload>  {
	
	private final RequestQueryService requestQueryService;
    private final TargetPeriodService targetPeriodService;

    @Override
    public RequestCreateValidationResult validateAction(CompetentAuthorityEnum competentAuthority, BuyOutSurplusFacilityRunCreateActionPayload payload) {
        
    	// Validate CCA3 TPs
    	if(!Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9).contains(payload.getTargetPeriodType())) {
            return invalid();
        }

    	// Validate in progress requests
    	Set<String> inProgressRequests = findInProgressRequests(competentAuthority);

    	if (!inProgressRequests.isEmpty()) {
    	    return invalid(inProgressRequests);
    	}
        
        // Validate selected TP is current, and TP buy out costs are defined
        List<TargetPeriod> cca3TargetPeriods = targetPeriodService.getTargetPeriodBuyOutCurrentAndPrevious(LocalDate.now())
                .stream()
                .filter(tp -> SchemeVersion.CCA_3.equals(tp.getSchemeVersion()))
                .toList();

        boolean requestedTargetPeriodIsCurrent = !cca3TargetPeriods.isEmpty()
        		&& payload.getTargetPeriodType().equals(cca3TargetPeriods.getFirst().getBusinessId());

        boolean allBuyOutCostsDefined = cca3TargetPeriods.stream().allMatch(tp -> tp.getBuyOutCost() != null);

        if (!requestedTargetPeriodIsCurrent || !allBuyOutCostsDefined) {
            return invalid();
        }
        
        return valid();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN;
    }
    
    private Set<String> findInProgressRequests(CompetentAuthorityEnum authority) {
        Set<String> result = new HashSet<>();

        if (requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN,
                RequestStatuses.IN_PROGRESS,
                authority)) {
            result.add(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN);
        }

        if (requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_RUN,
                RequestStatuses.IN_PROGRESS,
                authority)) {
            result.add(CcaRequestType.BUY_OUT_SURPLUS_RUN);
        }

        return result;
    }
    
    private RequestCreateValidationResult valid() {
		return RequestCreateValidationResult.builder().valid(true).build();
	}

    private RequestCreateValidationResult invalid() {
        return invalid(null);
    }
    
    private RequestCreateValidationResult invalid(Set<String> reportedRequestTypes) {
		return RequestCreateValidationResult.builder()
		        .valid(false)
		        .reportedRequestTypes(reportedRequestTypes)
		        .build();
	}
}
