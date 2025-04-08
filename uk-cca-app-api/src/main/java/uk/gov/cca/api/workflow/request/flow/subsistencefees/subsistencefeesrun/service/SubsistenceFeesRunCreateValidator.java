package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.subsistencefees.config.SubsistenceFeesConfig;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateByCAValidator;

@RequiredArgsConstructor
@Service
public class SubsistenceFeesRunCreateValidator implements RequestCreateByCAValidator<RequestCreateActionEmptyPayload> {

	private final RequestQueryService requestQueryService;
	private final SubsistenceFeesConfig subsistenceFeesConfig;
	
	@Override
	public RequestCreateValidationResult validateAction(CompetentAuthorityEnum competentAuthority,
			RequestCreateActionEmptyPayload payload) {
		final boolean inProgressExist = requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
				CcaRequestType.SUBSISTENCE_FEES_RUN, RequestStatuses.IN_PROGRESS, competentAuthority);
		if(inProgressExist) {
			return RequestCreateValidationResult.builder()
					.valid(false)
					.reportedRequestTypes(Set.of(CcaRequestType.SUBSISTENCE_FEES_RUN))
					.build();
		}
		
		if(LocalDate.now().isBefore(subsistenceFeesConfig.getTriggerDate())) {
			return RequestCreateValidationResult.builder()
					.valid(false)
					.build();
		}
		
		return RequestCreateValidationResult.builder().valid(true).build();
	}
	
	@Override
	public String getRequestType() {
		return CcaRequestType.SUBSISTENCE_FEES_RUN;
	}

}
