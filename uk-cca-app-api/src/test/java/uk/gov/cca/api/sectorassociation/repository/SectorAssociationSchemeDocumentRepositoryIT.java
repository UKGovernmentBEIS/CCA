package uk.gov.cca.api.sectorassociation.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.files.common.domain.FileStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class SectorAssociationSchemeDocumentRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private SectorAssociationSchemeDocumentRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByUuid() throws IOException {
        Path sampleFilePath = Paths.get("src", "test", "resources", "files", "sample.pdf");

        String uuid = UUID.randomUUID().toString();
        SectorAssociationSchemeDocument document = createSchemeDocument(sampleFilePath, uuid);
        entityManager.persist(document);

        flushAndClear();

        assertThat(repository.findByUuid(uuid)).contains(document);
    }

    @Test
    void deleteSchemeDocumentsByStatusAndDateBefore() throws IOException {
        Path sampleFilePath = Paths.get("src", "test", "resources", "files", "sample.pdf");

        String uuid = UUID.randomUUID().toString();
        SectorAssociationSchemeDocument document = createSchemeDocument(sampleFilePath, uuid);
        entityManager.persist(document);

        flushAndClear();

        repository.deleteSchemeDocumentsByStatusAndDateBefore(FileStatus.PENDING, LocalDateTime.now());

        assertThat(repository.findByUuid(uuid)).isEmpty();
    }

    private SectorAssociationSchemeDocument createSchemeDocument(Path sampleFilePath, String uuid) throws IOException {
        byte[] fileContent = Files.readAllBytes(sampleFilePath);
        return SectorAssociationSchemeDocument.builder()
                .uuid(uuid)
                .fileName(sampleFilePath.getFileName().toString())
                .fileContent(fileContent)
                .fileSize(Files.size(sampleFilePath))
                .fileType(Files.probeContentType(sampleFilePath))
                .status(FileStatus.PENDING)
                .createdBy("user")
                .lastUpdatedOn(LocalDateTime.now())
                .build();
    }


    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
