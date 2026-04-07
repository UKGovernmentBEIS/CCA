package uk.gov.cca.api.workflow.request.flow.cca2termination.run.handler;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.config.Cca2TerminationWorkflowConfig;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.service.Cca2TerminationRunInitiateService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestCACreateActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;

@Component
@RequiredArgsConstructor
public class Cca2TerminationRunCreateActionHandler implements RequestCACreateActionHandler<RequestCreateActionEmptyPayload>{

	private final Cca2TerminationRunInitiateService cca2TerminationRunInitiateService;
	private final Cca2TerminationWorkflowConfig cca2TerminationWorkflowConfig;

    @Override
	public String process(CompetentAuthorityEnum ca, RequestCreateActionEmptyPayload payload, AppUser appUser) {
    	cca2TerminationRunInitiateService.createCca2TerminationRun(cca2TerminationWorkflowConfig.getAccountBusinessIds());
        return "";
	}


	@Override
	public String getRequestType() {
		return CcaRequestType.CCA2_TERMINATION_RUN;
	}
}
