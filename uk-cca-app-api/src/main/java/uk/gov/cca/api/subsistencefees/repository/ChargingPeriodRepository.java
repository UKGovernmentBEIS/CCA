package uk.gov.cca.api.subsistencefees.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.subsistencefees.domain.ChargingPeriod;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

@Repository
public interface ChargingPeriodRepository extends JpaRepository<ChargingPeriod, Long> {

    @Transactional(readOnly = true)
    @Query(value = "select chargingYear from ChargingPeriod where :currentDate between startDate and endDate")
    Optional<Year> findChargingYear(LocalDate currentDate);
}
