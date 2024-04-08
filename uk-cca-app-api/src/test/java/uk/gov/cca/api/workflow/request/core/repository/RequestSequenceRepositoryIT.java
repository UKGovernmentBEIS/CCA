package uk.gov.cca.api.workflow.request.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.cca.api.workflow.request.core.domain.RequestSequence;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class RequestSequenceRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private RequestSequenceRepository cut;
    
    @Autowired
	private EntityManager entityManager;
    
    @Test
    void findByType() {
    	RequestSequence requestSequence = new RequestSequence(RequestType.DUMMY_REQUEST_TYPE);
    	entityManager.persist(requestSequence);
    	
        flushAndClear();
        
        Optional<RequestSequence> result = cut.findByType(mock(RequestType.class));
        assertThat(result).isEmpty();
        
        result = cut.findByType(RequestType.DUMMY_REQUEST_TYPE);
        assertThat(result).isNotEmpty();
        assertThat(result.get().getType()).isEqualTo(RequestType.DUMMY_REQUEST_TYPE);
    }
    
    @Test
    void findByBusinessIdentifierAndType() {
    	String businessIdentifier = "bi";
    	RequestSequence requestSequence = new RequestSequence(businessIdentifier, RequestType.DUMMY_REQUEST_TYPE);
    	entityManager.persist(requestSequence);
    	
        flushAndClear();
        
        Optional<RequestSequence> result = cut.findByBusinessIdentifierAndType("another_bi", RequestType.DUMMY_REQUEST_TYPE);
        assertThat(result).isEmpty();
        
        result = cut.findByBusinessIdentifierAndType(businessIdentifier, mock(RequestType.class));
        assertThat(result).isEmpty();
        
        result = cut.findByBusinessIdentifierAndType(businessIdentifier, RequestType.DUMMY_REQUEST_TYPE);
        assertThat(result).isNotEmpty();
        assertThat(result.get().getType()).isEqualTo(RequestType.DUMMY_REQUEST_TYPE);
        
    }
    
    private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}