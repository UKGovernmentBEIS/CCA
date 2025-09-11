package uk.gov.cca.api.targetperiodreporting.targetperiod.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface CertificationPeriodRepository extends JpaRepository<CertificationPeriod, Long> {

    List<CertificationPeriod> findAll();

    Optional<CertificationPeriod> findByCertificationBatchTriggerDate(LocalDate triggerDate);

    Optional<CertificationPeriod> findByBusinessId(CertificationPeriodType type);

    @Query(value = "select cp from CertificationPeriod cp where :date between cp.startDate and cp.endDate")
    Optional<CertificationPeriod> findCertificationPeriodByDate(LocalDate date);
}
