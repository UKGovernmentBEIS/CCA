package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataEntity;

import java.time.Year;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface EmissionsTradingSchemeDataEntityRepository extends JpaRepository<EmissionsTradingSchemeDataEntity, Long> {

    Optional<EmissionsTradingSchemeDataEntity> findByFacilityIdAndTargetPeriodYear(Long facilityId, Year targetPeriodYear);
}
