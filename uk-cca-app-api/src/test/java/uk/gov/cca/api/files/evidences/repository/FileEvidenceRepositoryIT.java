package uk.gov.cca.api.files.evidences.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.files.evidences.domain.FileEvidence;
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
class FileEvidenceRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private FileEvidenceRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deleteEvidenceFilesByStatusAndDateBefore() throws IOException {
        Path sampleFilePath = Paths.get("src", "test", "resources", "files", "sample.pdf");

        String uuid = UUID.randomUUID().toString();
        FileEvidence evidence = createEvidence(sampleFilePath, uuid);
        entityManager.persist(evidence);

        flushAndClear();

        repository.deleteEvidenceFilesByStatusAndDateBefore(FileStatus.PENDING, LocalDateTime.now());

        assertThat(repository.findByUuid(uuid)).isEmpty();
    }

    @Test
    void findByUuid() throws IOException {
        Path sampleFilePath = Paths.get("src", "test", "resources", "files", "sample.pdf");

        String uuid = UUID.randomUUID().toString();
        FileEvidence evidence = createEvidence(sampleFilePath, uuid);
        entityManager.persist(evidence);

        flushAndClear();

        assertThat(repository.findByUuid(uuid)).contains(evidence);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
        flushAndClear();
    }


    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private FileEvidence createEvidence(Path sampleFilePath, String uuid) throws IOException {
        byte[] fileContent = Files.readAllBytes(sampleFilePath);
        return FileEvidence.builder()
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
}
