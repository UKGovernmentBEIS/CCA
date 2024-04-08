package uk.gov.cca.api.workflow.request.application.item.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.workflow.request.application.item.repository.ItemRegulatorRepository;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.workflow.request.application.item.domain.Item;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.cca.api.workflow.request.application.item.domain.RequestTaskVisit;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, ItemRegulatorRepository.class})
class ItemRegulatorRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ItemRegulatorRepository cut;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findItems_assigned_to_me() {
        Long account = 1L;
        String user = "reg";

        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(CompetentAuthorityEnum.ENGLAND, Set.of(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW));

        Request request1 = createRequest(account, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 = createRequestTask(user, request1, RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW, "t1", request1.getCreationDate());
        createOpenedItem(requestTask1.getId(), user);

        Request request3 = createRequest(account, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask("another user", request3, RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW, "t3", request3.getCreationDate());

        Request request4 = createRequest(account, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask("another user", request4, RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW, "t4", request4.getCreationDate());

        createRequestTask(null, request1, RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW, "t5", request1.getCreationDate());

        ItemPage itemPage =
                cut.findItems(user, ItemAssignmentType.ME, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());

        Item item1 = itemPage.getItems().get(0);
        assertThat(item1.getRequestId()).isEqualTo(request1.getId());
        assertEquals(item1.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item1.getRequestId(), request1.getId());
        assertEquals(item1.getRequestType(), request1.getType());
        assertEquals(item1.getTaskId(), requestTask1.getId());
        assertEquals(item1.getTaskType(), requestTask1.getType());
        assertEquals(item1.getTaskAssigneeId(), requestTask1.getAssignee());
        assertEquals(item1.getTaskDueDate(), requestTask1.getDueDate());
        assertEquals(item1.getAccountId(), account);
        assertFalse(item1.isNew());
    }

    @Test
    void findItems_assigned_to_others() {
        Long account = 1L;
        String user = "reg";

        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(CompetentAuthorityEnum.ENGLAND, Set.of(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW));

        Request request1 = createRequest(account, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request1, RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW, "t1", request1.getCreationDate());

        Request request3 = createRequest(account, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request3, RequestTaskType.DUMMY_REQUEST_TASK_TYPE2, "t3", request3.getCreationDate());

        Request request4 = createRequest(account, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask4 = createRequestTask("another user", request4, RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW, "t4", request4.getCreationDate());

        Request request5 = createRequest(account, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.SCOTLAND, LocalDateTime.now());
        createRequestTask("another user", request5, RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW, "t5", request5.getCreationDate());

        createRequestTask(null, request1, RequestTaskType.DUMMY_REQUEST_TASK_TYPE2, "t6", request1.getCreationDate());

        ItemPage itemPage =
                cut.findItems(user, ItemAssignmentType.OTHERS, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());

        Item item = itemPage.getItems().get(0);
        assertThat(item.getRequestId()).isEqualTo(request4.getId());
        assertEquals(item.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask4.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item.getRequestId(), request4.getId());
        assertEquals(item.getRequestType(), request4.getType());
        assertEquals(item.getTaskId(), requestTask4.getId());
        assertEquals(item.getTaskType(), requestTask4.getType());
        assertEquals(item.getTaskAssigneeId(), requestTask4.getAssignee());
        assertEquals(item.getTaskDueDate(), requestTask4.getDueDate());
        assertEquals(item.getAccountId(), account);
    }

    @Test
    void findItems_unassigned() {
        Long account = 1L;
        String user = "reg";

        Map<CompetentAuthorityEnum, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(CompetentAuthorityEnum.ENGLAND, Set.of(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW));

        Request request1 = createRequest(account, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 = createRequestTask(null, request1, RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW, "t1", request1.getCreationDate());

        createRequestTask(user, request1,RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW, "t2", request1.getCreationDate());

        Request request3 = createRequest(account, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask(user, request3, RequestTaskType.DUMMY_REQUEST_TASK_TYPE2, "t4", request3.getCreationDate());

        Request request4 = createRequest(account, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, CompetentAuthorityEnum.ENGLAND, LocalDateTime.now());
        createRequestTask("another user", request4, RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW, "t5", request4.getCreationDate());

        ItemPage itemPage =
                cut.findItems(user, ItemAssignmentType.UNASSIGNED, scopedRequestTaskTypes, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());

        Item item1 = itemPage.getItems().get(0);
        assertThat(item1.getRequestId()).isEqualTo(request1.getId());
        assertEquals(item1.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item1.getRequestId(), request1.getId());
        assertEquals(item1.getRequestType(), request1.getType());
        assertEquals(item1.getTaskId(), requestTask1.getId());
        assertEquals(item1.getTaskType(), requestTask1.getType());
        assertEquals(item1.getTaskAssigneeId(), requestTask1.getAssignee());
        assertEquals(item1.getTaskDueDate(), requestTask1.getDueDate());
        assertEquals(item1.getAccountId(), account);
    }

    private Request createRequest(Long accountId, RequestType type, RequestStatus status, CompetentAuthorityEnum ca, LocalDateTime creationDate) {
        Request request = Request.builder()
                .id(RandomStringUtils.random(5))
                .competentAuthority(ca)
                .type(type)
                .status(status)
                .accountId(accountId)
                .creationDate(creationDate)
                .build();

        entityManager.persist(request);

        return request;
    }

    private RequestTask createRequestTask(String assignee, Request request, RequestTaskType taskType,
                                          String processTaskId, LocalDateTime startDate) {
        RequestTask requestTask =
                RequestTask.builder()
                        .request(request)
                        .processTaskId(processTaskId)
                        .type(taskType)
                        .assignee(assignee)
                        .startDate(LocalDateTime.now())
                        .dueDate(LocalDate.now().plusMonths(1L))
                        .build();

        entityManager.persist(requestTask);
        requestTask.setStartDate(startDate);

        return requestTask;
    }

    private void createOpenedItem(Long taskId, String userId) {
        RequestTaskVisit requestTaskVisit =
                RequestTaskVisit.builder()
                        .taskId(taskId)
                        .userId(userId)
                        .build();

        entityManager.persist(requestTaskVisit);
    }
}