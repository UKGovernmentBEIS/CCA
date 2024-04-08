package uk.gov.cca.api.workflow.request.flow.common.taskhandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.cca.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.cca.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.taskhandler.DefaultUserTaskCreatedHandler;
import uk.gov.cca.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultUserTaskCreatedHandlerTest {

	@InjectMocks
	private DefaultUserTaskCreatedHandler cut;

	@Mock
	private RequestTaskCreateService requestTaskCreateService;

	@Test
	void createRequestTask_dynamic_default_request_type() {
		String requestId = "1";
		String processTaskId = "proc";
		DynamicUserTaskDefinitionKey taskDefinitionKey = DynamicUserTaskDefinitionKey.APPLICATION_REVIEW;
		Map<String, Object> variables = Map.of(
				BpmnProcessConstants.REQUEST_TYPE, RequestType.DUMMY_REQUEST_TYPE.name()
		);

		cut.createRequestTask(requestId, processTaskId, taskDefinitionKey.name(), variables);

		verify(requestTaskCreateService, times(1)).create(requestId, processTaskId, RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW);
	}

	@Test
	void createRequestTask_fixed_request_task_type() {
		String requestId = "1";
		String processTaskId = "proc";
		String taskDefinitionKey = RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW.name();
		Map<String, Object> variables = Map.of(
				BpmnProcessConstants.REQUEST_TYPE, RequestType.DUMMY_REQUEST_TYPE.name()
		);

		cut.createRequestTask(requestId, processTaskId, taskDefinitionKey, variables);

		verify(requestTaskCreateService, times(1)).create(requestId, processTaskId, RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW);
	}
}
