package uk.gov.cca.api.workflow.request.flow.cca2termination.common.validation;

import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateByCAValidator;

@Service
@RequiredArgsConstructor
public class Cca2TerminationCreateValidator implements RequestCreateByCAValidator<RequestCreateActionEmptyPayload> {
	
	private final RequestQueryService requestQueryService;

    @Override
    public RequestCreateValidationResult validateAction(CompetentAuthorityEnum competentAuthority, RequestCreateActionEmptyPayload payload) {
        final boolean inProgressExist = requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.CCA2_TERMINATION_RUN, RequestStatuses.IN_PROGRESS, competentAuthority);

        if(inProgressExist) {
            return RequestCreateValidationResult.builder()
                    .valid(false)
                    .reportedRequestTypes(Set.of(CcaRequestType.CCA2_TERMINATION_RUN))
                    .build();
        }

        return RequestCreateValidationResult.builder().valid(true).build();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.CCA2_TERMINATION_RUN;
    }
}
