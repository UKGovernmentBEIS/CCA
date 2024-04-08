package uk.gov.cca.api.workflow.request.application.taskview;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.workflow.request.application.taskview.RequestTaskDTO;
import uk.gov.cca.api.workflow.request.application.taskview.RequestTaskMapper;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.cca.api.user.operator.domain.OperatorUserDTO;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import static org.assertj.core.api.Assertions.assertThat;

class RequestTaskMapperTest {
	
	private RequestTaskMapper mapper;
	
	@BeforeEach
	public void init() {
		mapper = Mappers.getMapper(RequestTaskMapper.class);
	}
	
	@Test
	void toTaskDTO_no_assignee_user_assignable() {
		final String requestId = "1";
	    Long requestTaskId = 2L;
	    Request request = createRequest(requestId, RequestType.DUMMY_REQUEST_TYPE);
		RequestTaskType requestTaskType = RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW;
		RequestTask requestTask = createRequestTask(requestTaskId, null, requestTaskType, request);
		
		//invoke
		RequestTaskDTO result = mapper.toTaskDTO(requestTask, null);
		
		//assert
		assertThat(result.getAssigneeFullName()).isNull();
		assertThat(result.getAssigneeUserId()).isNull();
		assertThat(result.getType()).isEqualTo(requestTaskType);
		assertThat(result.getId()).isEqualTo(requestTaskId);
		assertThat(result.isAssignable()).isTrue();
    }
	
	@Test
	void toTaskDTO_with_assignee_user() {
		final String requestId = "1";
        Long requestTaskId = 2L;
        String task_assignee = "task_assignee";
        Request request = createRequest(requestId, RequestType.DUMMY_REQUEST_TYPE);
		RequestTaskType requestTaskType = RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW;
		RequestTask requestTask = createRequestTask(requestTaskId, task_assignee, requestTaskType, request);
		final String fn = "fn";
		final String ln = "ln";
		ApplicationUserDTO assigneeUser = OperatorUserDTO.builder()
							.firstName(fn)
							.lastName(ln)
							.build();
		
		//invoke
		RequestTaskDTO result = mapper.toTaskDTO(requestTask, assigneeUser);
		
		//assert
		assertThat(result.getAssigneeFullName()).isEqualTo(fn + " " + ln);
		assertThat(result.getAssigneeUserId()).isEqualTo(task_assignee);
		assertThat(result.getType()).isEqualTo(requestTaskType);
		assertThat(result.getId()).isEqualTo(requestTaskId);
		assertThat(result.isAssignable()).isTrue();
	}

	private Request createRequest(String requestId, RequestType requestType) {
	    return Request.builder()
            .id(requestId)
            .type(requestType)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(RequestStatus.IN_PROGRESS)
            .accountId(1L)
            .build();
    }
	
    private RequestTask createRequestTask(Long requestTaskId, String assignee, RequestTaskType requestTaskType,
            Request request) {
		return RequestTask.builder()
	            .id(requestTaskId)
	            .request(request)
	            .type(requestTaskType)
	            .assignee(assignee)
	            .build();
    }
}
