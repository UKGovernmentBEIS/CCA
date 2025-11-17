package uk.gov.cca.api.facilityaudit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.facilityaudit.domain.FacilityAudit;

import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface FacilityAuditRepository extends JpaRepository<FacilityAudit, Long> {

	Optional<FacilityAudit> findFacilityAuditByFacilityId(Long facilityId);

	Set<FacilityAudit> findAllByAuditRequiredIsTrueAndFacilityIdIn(Set<Long> facilityIds);
}
