package uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Repository
@Transactional(readOnly = true)
public interface PerformanceDataFacilityStatusRepository extends JpaRepository<PerformanceDataFacilityStatus, Long> {

	Optional<PerformanceDataFacilityStatus> findByFacilityIdAndTargetPeriodYear(Long facilityId, Year targetPeriodYear);

	List<PerformanceDataFacilityStatus> findByFacilityIdInAndTargetPeriodYearAndLocked(Set<Long> facilityIds,
			Year targetPeriodYear, boolean locked);

	@EntityGraph(attributePaths = { "targetPeriod", "lastPerformanceData" })
	List<PerformanceDataFacilityStatus> findWithDetailsByFacilityIdAndTargetPeriodBusinessId(Long facilityId,
			TargetPeriodType targetPeriodType);

	@Modifying
	@Query("""
			UPDATE PerformanceDataFacilityStatus pdfs
			SET pdfs.variationIndicator = true
			WHERE pdfs.facilityId IN (
			    SELECT fd.id
			    FROM FacilityData fd
			    WHERE fd.facilityBusinessId IN :facilityBusinessIds
			)
			AND pdfs.targetPeriodYear = :targetPeriodYear
			""")
	void updateVariationIndicatorByFacilityBusinessIdInAndTargetPeriodYear(Set<String> facilityBusinessIds,
			Year targetPeriodYear);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT pdfs
            FROM PerformanceDataFacilityStatus pdfs
            WHERE pdfs.lastPerformanceData.id IN :performanceDataIds
              AND pdfs.locked = false
            ORDER BY pdfs.id 
            """)
    List<PerformanceDataFacilityStatus> findUnlockedForUpdate(
            @Param("performanceDataIds") Set<Long> performanceDataIds);
}
