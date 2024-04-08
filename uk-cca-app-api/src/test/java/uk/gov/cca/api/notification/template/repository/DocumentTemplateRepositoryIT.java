package uk.gov.cca.api.notification.template.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.notification.template.repository.DocumentTemplateRepository;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.notification.template.domain.DocumentTemplate;
import uk.gov.cca.api.notification.template.domain.enumeration.DocumentTemplateType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class DocumentTemplateRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private DocumentTemplateRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByTypeAndCompetentAuthority() {
        DocumentTemplateType documentTemplateType = DocumentTemplateType.IN_RFI;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;

        DocumentTemplate documentTemplate = DocumentTemplate.builder()
                .type(documentTemplateType)
                .competentAuthority(competentAuthority)
                .fileDocumentTemplateId(1L)
                .name("doc template name")
                .workflow("workflow")
                .build();
        
        entityManager.persist(documentTemplate);

        flushAndClear();
        
        Optional<DocumentTemplate> resultOpt =
            repo.findByTypeAndCompetentAuthority(documentTemplateType, competentAuthority);
        assertThat(resultOpt).isNotEmpty();
        assertThat(resultOpt.get().getName()).isEqualTo("doc template name");
    }
    
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
