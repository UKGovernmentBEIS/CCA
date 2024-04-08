package uk.gov.cca.api.workflow.request.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TestTransaction;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.workflow.request.core.repository.RequestActionRepository;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestAction;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestStatus.IN_PROGRESS;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class RequestActionRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private RequestActionRepository requestActionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllByRequestId() {
        Request request1 = Request.builder()
            .id("1")
            .type(RequestType.DUMMY_REQUEST_TYPE)
            .status(IN_PROGRESS)
            .creationDate(LocalDateTime.now())
            .build();

        RequestAction requestAction1 = RequestAction.builder()
            .request(request1)
            .type(RequestActionType.REQUEST_TERMINATED)
            .submitterId("userId")
            .submitter("username")
            .creationDate(LocalDateTime.now())
            .build();

        entityManager.persist(request1);
        entityManager.persist(requestAction1);

        Request request2 = Request.builder()
            .id("2")
            .type(RequestType.DUMMY_REQUEST_TYPE)
            .status(IN_PROGRESS)
            .creationDate(LocalDateTime.now())
            .build();

        RequestAction requestAction2 = RequestAction.builder()
            .request(request2)
            .type(RequestActionType.REQUEST_TERMINATED)
            .submitterId("userId")
            .submitter("username")
            .creationDate(LocalDateTime.now())
            .build();

        entityManager.persist(request2);
        entityManager.persist(requestAction2);

        List<RequestAction> actual = requestActionRepository.findAllByRequestId(request1.getId());

        assertThat(actual)
                .hasSize(1)
                .containsExactly(requestAction1);
    }

    @Test
    void testLazyInitialization_whenLazyBasicAccessedAfterSessionCloses_thenThrowException() {
        Request request = Request.builder()
                .id("1")
                .type(RequestType.DUMMY_REQUEST_TYPE)
                .status(RequestStatus.IN_PROGRESS)
                .creationDate(LocalDateTime.now())
                .build();

        RequestAction requestAction = RequestAction.builder()
                .request(request)
                .type(RequestActionType.RFI_SUBMITTED)
                .submitterId("userId")
                .submitter("username")
                .creationDate(LocalDateTime.now())
                .build();

        entityManager.persist(request);
        entityManager.persist(requestAction);

        entityManager.flush();
        entityManager.clear();

        final List<RequestAction> requestActions = requestActionRepository.findAll();

        TestTransaction.end();

        assertEquals(1, requestActions.size());
        assertThrows(LazyInitializationException.class, () -> requestActions.get(0).getPayload());
    }
}
