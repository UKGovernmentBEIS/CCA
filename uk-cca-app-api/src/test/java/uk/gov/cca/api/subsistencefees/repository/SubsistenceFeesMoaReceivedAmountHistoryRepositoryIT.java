package uk.gov.cca.api.subsistencefees.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaReceivedAmountHistory;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaReceivedAmountHistoryPayload;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class SubsistenceFeesMoaReceivedAmountHistoryRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private SubsistenceFeesMoaReceivedAmountHistoryRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByMoaId() {
        UUID fileEvidenceUuid = UUID.randomUUID();
        String evidenceFileName = "EvidenceFile1";
        // Persist run
        SubsistenceFeesRun run = SubsistenceFeesRun.builder()
                .businessId("S2501")
                .chargingYear(Year.of(2025))
                .initialTotalAmount(BigDecimal.valueOf(1000L))
                .submissionDate(LocalDateTime.now())
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        entityManager.persist(run);
        // Persist Moa
        SubsistenceFeesMoa moa = SubsistenceFeesMoa.builder()
                .transactionId("CCACM1201")
                .subsistenceFeesRun(run)
                .moaType(MoaType.SECTOR_MOA)
                .resourceId(2L)
                .initialTotalAmount(BigDecimal.valueOf(6000))
                .regulatorReceivedAmount(BigDecimal.valueOf(5000))
                .fileDocumentUuid("111-111-111")
                .submissionDate(LocalDateTime.now())
                .build();
        entityManager.persist(moa);
        // Persist History
        SubsistenceFeesMoaReceivedAmountHistory receivedAmountHistory = SubsistenceFeesMoaReceivedAmountHistory.builder()
                .submitterId("submitterId")
                .submitter("submitter")
                .subsistenceFeesMoa(moa)
                .submissionDate(LocalDateTime.now())
                .payload(SubsistenceFeesMoaReceivedAmountHistoryPayload.builder()
                        .previousReceivedAmount(BigDecimal.valueOf(5000))
                        .transactionAmount(BigDecimal.valueOf(185))
                        .comments("bla bla bla")
                        .evidenceFiles(Map.of(fileEvidenceUuid, evidenceFileName))
                        .build())
                .build();
        entityManager.persist(receivedAmountHistory);

        flushAndClear();

        // invoke
        List<SubsistenceFeesMoaReceivedAmountHistory> amountHistories = repository.findByMoaId(1L);

        assertThat(amountHistories).hasSize(1);
        assertThat(amountHistories.getFirst().getSubmitterId()).isEqualTo("submitterId");
        assertThat(amountHistories.getFirst().getSubmitter()).isEqualTo("submitter");
        assertThat(amountHistories.getFirst().getPayload().getTransactionAmount()).isEqualTo(BigDecimal.valueOf(185));
        assertThat(amountHistories.getFirst().getPayload().getComments()).isEqualTo("bla bla bla");
        assertThat(amountHistories.getFirst().getPayload().getEvidenceFiles())
                .hasSize(1)
                .containsEntry(fileEvidenceUuid, evidenceFileName);
    }

    public void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
