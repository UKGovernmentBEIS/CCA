package uk.gov.cca.api.subsistencefees.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacility;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResultInfoDTO;

@Repository
public interface SubsistenceFeesMoaFacilityRepository extends JpaRepository<SubsistenceFeesMoaFacility, Long> {

    @Transactional(readOnly = true)
    @Query(value = "select new uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResultInfoDTO("
            + "smf.id, fd.facilityId, fd.siteName, smf.paymentStatus, smf.paymentDate, (smf.markingStatusHistoryList IS NOT EMPTY)) "
            + "from SubsistenceFeesMoaFacility smf "
            + "inner join FacilityData fd on fd.id = smf.facilityId "
            + "where smf.subsistenceFeesMoaTargetUnit.id = :moaTargetUnitId "
            + "and (LOWER(fd.facilityId) like CONCAT('%',:term,'%') or LOWER(fd.siteName) like CONCAT('%',:term,'%')) "
            + "and (:facilityPaymentStatus is null or smf.paymentStatus = :facilityPaymentStatus) ")
    Page<SubsistenceFeesMoaFacilitySearchResultInfoDTO> findBySearchCriteria(
            Pageable pageable, Long moaTargetUnitId, String term, FacilityPaymentStatus facilityPaymentStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<SubsistenceFeesMoaFacility> findBySubsistenceFeesMoaTargetUnitIdInAndPaymentStatusNotInOrderById(Set<Long> moaTargetUnitIds, List<FacilityPaymentStatus> facilityPaymentStatusList);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select smf " +
            "FROM SubsistenceFeesMoaFacility smf " +
            "where exists (select 1 from SubsistenceFeesMoaTargetUnit smtu " +
            "   where smtu.subsistenceFeesMoa.id = :moaId " +
            "   and smf.subsistenceFeesMoaTargetUnit.id = smtu.id) " +
            "and smf.paymentStatus != 'CANCELLED' and smf.paymentStatus != :newStatus " +
            "order by smf.id")
    List<SubsistenceFeesMoaFacility> findMoaFacilitiesEligibleForStatusUpdate(Long moaId, FacilityPaymentStatus newStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select smf " +
            "FROM SubsistenceFeesMoaFacility smf " +
            "where smf.subsistenceFeesMoaTargetUnit.id = :moaTargetUnitId " +
            "and smf.paymentStatus != 'CANCELLED' and smf.paymentStatus != :newStatus " +
            "order by smf.id")
    List<SubsistenceFeesMoaFacility> findTargetUnitMoaFacilitiesEligibleForStatusUpdate(Long moaTargetUnitId, FacilityPaymentStatus newStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<SubsistenceFeesMoaFacility> findByIdInAndPaymentStatusNotInOrderById(Set<Long> moaFacilityIds, List<FacilityPaymentStatus> facilityPaymentStatusList);

    @Query(value = "select id from SubsistenceFeesMoaFacility where subsistenceFeesMoaTargetUnit.id = :moaTargetUnitId")
    Set<Long> findMoaFacilityIdsByMoaTargetUnitId(Long moaTargetUnitId);

    @EntityGraph(value = "moa-facility-moa-target-unit-graph", type = EntityGraph.EntityGraphType.FETCH)
    @Query(value = "select smf from SubsistenceFeesMoaFacility smf where smf.id = :id")
    Optional<SubsistenceFeesMoaFacility> findWithMoaTargetUnit(Long id);

    @EntityGraph(value = "moa-facility-status-history-graph", type = EntityGraph.EntityGraphType.FETCH)
    @Query(value = "select smf from SubsistenceFeesMoaFacility smf where smf.id = :id")
    Optional<SubsistenceFeesMoaFacility> findWithMarkingStatusHistory(Long id);
}
