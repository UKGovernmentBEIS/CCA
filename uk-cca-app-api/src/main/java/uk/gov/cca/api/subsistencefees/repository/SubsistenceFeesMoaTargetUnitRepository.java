package uk.gov.cca.api.subsistencefees.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaTargetUnit;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitDetailsDTO;

@Repository
@Transactional(readOnly = true)
public interface SubsistenceFeesMoaTargetUnitRepository 
		extends JpaRepository<SubsistenceFeesMoaTargetUnit, Long>, SubsistenceFeesMoaTargetUnitCustomRepository {

	@Query(value = "select new uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitDetailsDTO( "
			+ "smtu.id, a.businessId, a.name, smtu.initialTotalAmount, sm.submissionDate, max(smf.initialAmount), "
			+ "sum(case when smf.paymentStatus != 'CANCELLED' then smf.initialAmount else 0 end) as totalAmount, "
			+ "sum(case when smf.paymentStatus != 'CANCELLED' then 1 else 0 end) as totalFacilities, "
			+ "sum(case when smf.paymentStatus = 'COMPLETED' then 1 else 0 end)) as paidFacilities "
		+ "from SubsistenceFeesMoaTargetUnit smtu "
		+ "inner join Account a on a.id = smtu.accountId "
		+ "inner join SubsistenceFeesMoa sm on sm.id = smtu.subsistenceFeesMoa.id "
		+ "inner join SubsistenceFeesMoaFacility smf on smf.subsistenceFeesMoaTargetUnit.id = smtu.id "
		+ "where smtu.id = :moaTargetUnitId group by smtu.id, a.businessId, a.name, sm.submissionDate ")
	Optional<SubsistenceFeesMoaTargetUnitDetailsDTO> getMoaTargetUnitDetailsById(Long moaTargetUnitId);
}
