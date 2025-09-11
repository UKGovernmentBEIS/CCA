package uk.gov.cca.api.subsistencefees.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaDetails;

@Repository
@Transactional(readOnly = true)
public interface SubsistenceFeesMoaRepository extends JpaRepository<SubsistenceFeesMoa, Long>, SubsistenceFeesMoaCustomRepository {

	@Query(value = "select new uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaDetails( "
			+ "sm.id, sm.transactionId, sm.moaType, sm.resourceId, sm.initialTotalAmount, sm.regulatorReceivedAmount, sm.fileDocumentUuid, sm.submissionDate, max(smf.initialAmount), "
			+ "sum(case when smf.paymentStatus != 'CANCELLED' then smf.initialAmount else 0 end) as totalAmount, "
			+ "sum(case when smf.paymentStatus != 'CANCELLED' then 1 else 0 end) as totalFacilities, "
			+ "sum(case when smf.paymentStatus = 'COMPLETED' then 1 else 0 end) as paidFacilities, "
			+ "max(case when sm.moaType = 'TARGET_UNIT_MOA' then smtu.id else null end) as moaTargetUnitId) "
		+ "from SubsistenceFeesMoa sm "
		+ "inner join SubsistenceFeesMoaTargetUnit smtu on smtu.subsistenceFeesMoa.id = sm.id "
		+ "inner join SubsistenceFeesMoaFacility smf on smf.subsistenceFeesMoaTargetUnit.id = smtu.id "
		+ "where sm.id = :moaId group by sm.id")
	Optional<SubsistenceFeesMoaDetails> getMoaDetailsById(Long moaId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<SubsistenceFeesMoa> findMoaById(Long moaId);
	
    Optional<SubsistenceFeesMoa> findByIdAndFileDocumentUuid(Long id, String fileDocumentUuid);

}
