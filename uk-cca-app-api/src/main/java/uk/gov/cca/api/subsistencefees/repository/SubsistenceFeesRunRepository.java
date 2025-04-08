package uk.gov.cca.api.subsistencefees.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunMoaDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunSearchResultInfo;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Repository
@Transactional(readOnly = true)
public interface SubsistenceFeesRunRepository extends JpaRepository<SubsistenceFeesRun, Long>{
	
	Page<SubsistenceFeesRun> findSubsistenceFeesRunsByCompetentAuthorityAndSubmissionDateNotNull(Pageable pageable, CompetentAuthorityEnum competentAuthority);

	@Query(value = "select new uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunSearchResultInfo(sr.id, sr.businessId, sr.submissionDate, "
						+ "sum(case when smf.paymentStatus != 'CANCELLED' THEN smf.initialAmount else 0 end), "
						+ "sum(case when smf.paymentStatus = 'IN_PROGRESS' then smf.initialAmount else 0 end), "
						+ "(select sum(sm.regulatorReceivedAmount) from SubsistenceFeesMoa sm where sm.subsistenceFeesRun.id = sr.id)) "
					+ "from SubsistenceFeesRun sr "
					+ "inner join SubsistenceFeesMoa sm on sm.subsistenceFeesRun.id = sr.id "
					+ "inner join SubsistenceFeesMoaTargetUnit smtu on smtu.subsistenceFeesMoa.id = sm.id "
					+ "inner join SubsistenceFeesMoaFacility smf on smf.subsistenceFeesMoaTargetUnit.id = smtu.id "
					+ "where sr.id in (:ids) group by sr.id, sr.businessId, sr.submissionDate order by sr.submissionDate desc")
    List<SubsistenceFeesRunSearchResultInfo> findSubsistenceFeesRunsWithAmountsByIds(Set<Long> ids);

	@Query(value = "select new uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunDetailsInfo(sr.id, sr.businessId, sr.submissionDate, sr.initialTotalAmount, "
			+ "sum(case when smf.paymentStatus != 'CANCELLED' then smf.initialAmount else 0 end)) "
		+ "from SubsistenceFeesRun sr "
		+ "inner join SubsistenceFeesMoa sm on sm.subsistenceFeesRun.id = sr.id "
		+ "inner join SubsistenceFeesMoaTargetUnit smtu on smtu.subsistenceFeesMoa.id = sm.id "
		+ "inner join SubsistenceFeesMoaFacility smf on smf.subsistenceFeesMoaTargetUnit.id = smtu.id "
		+ "where sr.id = :id and sr.submissionDate is not null group by sr.id, sr.businessId")
	Optional<SubsistenceFeesRunDetailsInfo> findSubsistenceFeesRunDetailsById(Long id);

	@Query(value = "select new uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunMoaDetailsInfo( "
			+ "sum(sm.regulatorReceivedAmount), "
			+ "sum(case when sm.moaType = 'SECTOR_MOA' then 1 else 0 end), "
			+ "sum(case when sm.moaType = 'TARGET_UNIT_MOA' then 1 else 0 end)) "
		+ "from SubsistenceFeesMoa sm "
		+ "where sm.subsistenceFeesRun.id = :id ")
	Optional<SubsistenceFeesRunMoaDetailsInfo> findSubsistenceFeesRunMoaDetailsById(Long id);
}
