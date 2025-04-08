package uk.gov.cca.api.subsistencefees.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacility;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResultInfoDTO;

@Repository
@Transactional(readOnly = true)
public interface SubsistenceFeesMoaFacilityRepository extends JpaRepository<SubsistenceFeesMoaFacility, Long>{

	@Query(value = "select new uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResultInfoDTO("
			+ "smf.id, fd.facilityId, fd.siteName, smf.paymentStatus, smf.paymentDate) "
		+ "from SubsistenceFeesMoaFacility smf "
		+ "inner join FacilityData fd on fd.id = smf.facilityId "
		+ "where smf.subsistenceFeesMoaTargetUnit.id = :moaTargetUnitId "
		+ "and (LOWER(fd.facilityId) like CONCAT('%',:term,'%') or LOWER(fd.siteName) like CONCAT('%',:term,'%')) "
		+ "and (:facilityPaymentStatus is null or smf.paymentStatus = :facilityPaymentStatus) "
		+ "order by fd.facilityId")
	Page<SubsistenceFeesMoaFacilitySearchResultInfoDTO> findBySearchCriteria(
			Pageable pageable, Long moaTargetUnitId, String term, FacilityPaymentStatus facilityPaymentStatus);
}
