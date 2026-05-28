package uk.gov.cca.api.underlyingagreement.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementDocument;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class UnderlyingAgreementIT extends AbstractContainerBaseTest {

	//TODO: Add una repository and relevant methods

	@Autowired
    private UnderlyingAgreementDocumentRepository unaDocumentRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
    	UnderlyingAgreementEntity una1 = UnderlyingAgreementEntity.builder()
                .accountId(1L)
                .underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                		.underlyingAgreement(UnderlyingAgreement.builder()
                				.authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                						.authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                						.build())
                				.build())
                		.schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().build()))
                		.build())
                .build();
        entityManager.persist(una1);
        
        UnderlyingAgreementEntity una2 = UnderlyingAgreementEntity.builder()
                .accountId(2L)
                .underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                		.underlyingAgreement(UnderlyingAgreement.builder()
                				.authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                						.authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                						.build())
                				.build())
                		.schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().build()))
                		.build())
                .build();
        entityManager.persist(una2);

        UnderlyingAgreementDocument doc1 = UnderlyingAgreementDocument.builder()
                .underlyingAgreementEntity(una1)
                .activationDate(LocalDateTime.now())
                .consolidationNumber(1)
                .schemeVersion(SchemeVersion.CCA_3)
                .build();
        entityManager.persist(doc1);
            
        UnderlyingAgreementDocument doc2 = UnderlyingAgreementDocument.builder()
                .underlyingAgreementEntity(una2)
                .activationDate(LocalDateTime.now())
                .consolidationNumber(1)
                .schemeVersion(SchemeVersion.CCA_3)
                .terminatedDate(LocalDateTime.now())
                .build();
        entityManager.persist(doc2);

        flushAndClear();
    }

    @Test
    void findBySchemeVersionAndTerminatedDateIsNull() { 	
    	List<UnderlyingAgreementDocument> result = unaDocumentRepository.findBySchemeVersionAndTerminatedDateIsNull(SchemeVersion.CCA_3);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getSchemeVersion()).isEqualTo(SchemeVersion.CCA_3);
        assertThat(result.getFirst().getUnderlyingAgreementEntity().getAccountId()).isEqualTo(1L);
    }
    
    @AfterEach
    void tearDown() {
    	unaDocumentRepository.deleteAll();
        flushAndClear();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
