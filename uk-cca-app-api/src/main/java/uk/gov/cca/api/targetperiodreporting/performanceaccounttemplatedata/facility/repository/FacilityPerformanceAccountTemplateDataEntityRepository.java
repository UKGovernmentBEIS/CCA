package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataEntity;

import java.time.Year;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface FacilityPerformanceAccountTemplateDataEntityRepository extends JpaRepository<FacilityPerformanceAccountTemplateDataEntity, Long> {

    Optional<FacilityPerformanceAccountTemplateDataEntity> findByFacilityIdAndTargetPeriodYear(@NotNull Long facilityId, @NotNull Year targetPeriodYear);
}