package uk.gov.cca.api.sectorassociation.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.files.common.domain.FileStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
public class SectorAssociationSchemeDocumentIT extends AbstractContainerBaseTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testSectorAssociationSchemeDocumentPersistence() {
        SectorAssociationSchemeDocument sectorAssociationSchemeDocument = SectorAssociationSchemeDocument.builder()
                .uuid("test")
                .fileName("test")
                .fileType(".pdf")
                .status(FileStatus.SUBMITTED)
                .fileSize(1)
                .createdBy("test user")
                .build();

        entityManager.persist(sectorAssociationSchemeDocument);
        entityManager.flush();

        SectorAssociationSchemeDocument savedSectorAssociationSchemeDocument =
                entityManager.find(SectorAssociationSchemeDocument.class, sectorAssociationSchemeDocument.getId());

        assertThat(savedSectorAssociationSchemeDocument).isNotNull();
        assertThat(savedSectorAssociationSchemeDocument.getId()).isNotNull();
        assertThat(savedSectorAssociationSchemeDocument.getFileName()).isEqualTo("test");
    }
}
