package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.handler;

import java.util.List;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

public class PerformanceAccountTemplateDataUploadCompleteActionHandler
		implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

	@Override
	public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
			RequestTaskActionEmptyPayload payload) {
		//TODO
		return null;
	}

	@Override
	public List<String> getTypes() {
		return List.of(CcaRequestTaskActionType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_COMPLETE);
	}

}
