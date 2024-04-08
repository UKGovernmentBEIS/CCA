package uk.gov.cca.api.workflow.request.core.repository;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.workflow.request.core.repository.RequestRepository;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
class RequestRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private RequestRepository requestRepository;
    
    @Autowired
	private EntityManager entityManager;
    
    @Test
    void findByAccountIdAndStatus() {
        Long accountId = 1L;
        Request request = createRequest(accountId, CompetentAuthorityEnum.ENGLAND, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, null);
        createRequest(accountId, CompetentAuthorityEnum.ENGLAND, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.CANCELLED, null);

        flushAndClear();
        
        List<Request> result = requestRepository.findByAccountIdAndStatus(accountId, RequestStatus.IN_PROGRESS);
        
        assertThat(result).containsExactlyInAnyOrder(request);
    }

    @Test
    void findAllByAccountId() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;

        Request acc1Request = createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, null);
        createRequest(accountId2, CompetentAuthorityEnum.ENGLAND, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, null);

        flushAndClear();

        List<Request> retrievedRequests = requestRepository.findAllByAccountId(accountId1);

        assertThat(retrievedRequests).hasSize(1);
        assertThat(retrievedRequests)
            .extracting(Request::getId)
            .containsExactly(acc1Request.getId());
    }

    @Test
    void findAllByAccountIdInAndStatusIn() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        Long accountId3 = 3L;

        Request acc1Request = createRequest(accountId1, CompetentAuthorityEnum.ENGLAND, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.APPROVED, LocalDateTime.now());
        Request acc2Request = createRequest(accountId2, CompetentAuthorityEnum.ENGLAND, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, null);
        createRequest(accountId3, CompetentAuthorityEnum.ENGLAND, RequestType.DUMMY_REQUEST_TYPE, RequestStatus.IN_PROGRESS, null);

        flushAndClear();

        List<Request> retrievedRequests = requestRepository.findAllByAccountIdIn(Set.of(accountId1, accountId2));

        assertThat(retrievedRequests).hasSize(2);
        assertThat(retrievedRequests)
            .extracting(Request::getId)
            .containsExactlyInAnyOrder(acc1Request.getId(), acc2Request.getId());
    }
    
    @Test
    void existsByTypeAndStatusAndCompetentAuthority_exists() {
    	RequestType type = RequestType.DUMMY_REQUEST_TYPE;
    	RequestStatus status= RequestStatus.IN_PROGRESS;
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	
    	createRequest(1L, competentAuthority, type, status, LocalDateTime.now());
    	
        flushAndClear();
        
        boolean result = requestRepository.existsByTypeAndStatusAndCompetentAuthority(type, status, competentAuthority);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void existsByTypeAndStatusAndCompetentAuthority_not_exist() {
    	RequestType type = RequestType.DUMMY_REQUEST_TYPE;
    	RequestStatus status= RequestStatus.IN_PROGRESS;
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	
    	createRequest(1L, competentAuthority, type, RequestStatus.APPROVED, LocalDateTime.now());
        createRequest(2L, CompetentAuthorityEnum.OPRED, type, status, LocalDateTime.now());
        
        flushAndClear();
        
        boolean result = requestRepository.existsByTypeAndStatusAndCompetentAuthority(type, status, competentAuthority);
        
        assertThat(result).isFalse();
    }
    
	private Request createRequest(Long accountId, CompetentAuthorityEnum competentAuthority, RequestType type,
			RequestStatus status, LocalDateTime endDate) {
		return createRequest(accountId, competentAuthority, type, status, endDate, null);
	}
    
	private Request createRequest(Long accountId, CompetentAuthorityEnum competentAuthority, RequestType type,
			RequestStatus status, LocalDateTime endDate, Long verificationBodyId) {
		return createRequest(accountId, competentAuthority, type, status, endDate, verificationBodyId, null);
	}
    
	private Request createRequest(Long accountId, CompetentAuthorityEnum competentAuthority, RequestType type,
			RequestStatus status, LocalDateTime endDate, Long verificationBodyId, RequestMetadata metadata) {
        Request request = 
                Request.builder()
                    .id(RandomStringUtils.random(5))
                    .accountId(accountId)
                    .type(type)
                    .status(status)
                    .competentAuthority(competentAuthority)
                    .verificationBodyId(verificationBodyId)
                    .endDate(endDate)
                    .metadata(metadata)
                    .build();
        entityManager.persist(request);
        return request;
    }
    
    private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}

}
