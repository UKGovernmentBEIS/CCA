package uk.gov.cca.api.targetperiodreporting.facilitycertification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertification;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface FacilityCertificationRepository extends JpaRepository<FacilityCertification, Long> {

    List<FacilityCertification> findAllByFacilityIdInAndCertificationPeriodId(Set<Long> facilityIds, Long certificationPeriodId);

    List<FacilityCertification> findAllByFacilityId(Long facilityId);

    List<FacilityCertification> findAllByFacilityIdIn(Set<Long> facilityId);

    @Query("SELECT fc FROM FacilityCertification fc " +
            "JOIN FacilityData fd on fc.facilityId = fd.id " +
            "WHERE fd.facilityId=:facilityBusinessId " +
            "  AND fc.certificationPeriodId=:certificationPeriodId")
    Optional<FacilityCertification> findByFacilityIdAndCertificationPeriodId(String facilityBusinessId, Long certificationPeriodId);
    
    boolean existsFacilityCertificationByFacilityIdAndCertificationPeriodId(Long facilityId, Long certificationPeriodId);
}
