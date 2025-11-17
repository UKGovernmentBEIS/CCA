package uk.gov.cca.api.facility.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import uk.gov.cca.api.facility.domain.FacilityData;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface FacilityDataRepository extends JpaRepository<FacilityData, Long> {

    @EntityGraph(value = "facility-address-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<FacilityData> findById(Long facilityId);

    boolean existsByFacilityBusinessId(String facilityBusinessId);

    boolean existsByFacilityBusinessIdAndClosedDateIsNull(String facilityBusinessId);

    Optional<FacilityData> findByFacilityBusinessId(String facilityBusinessId);
    
    Optional<FacilityData> findByFacilityBusinessIdAndClosedDateIsNull(String facilityBusinessId);

    List<FacilityData> findAllByIdIn(List<Long> facilityIds);
    
    List<FacilityData> findAllByFacilityBusinessIdIn(Set<String> facilityBusinessIds);

    List<FacilityData> findFacilityDataByAccountIdAndClosedDateIsNull(Long accountId);

    @Query(value = "select fd.id from FacilityData fd where fd.accountId = :accountId and fd.closedDate is NULL")
    List<Long> findFacilityIdsByAccountIdAndClosedDateIsNull(Long accountId);

    @Query(value = "select fd "
            + "from FacilityData fd "
            + "where fd.accountId = (:accountId) "
            + "and (LOWER(fd.facilityBusinessId) like CONCAT('%',:term,'%') "
            + "or LOWER(fd.siteName) like CONCAT('%',:term,'%')) ")
    Page<FacilityData> searchFacilityDataByAccountIdAndTerm(Pageable pageable, Long accountId, String term);
    
    @Query(value = "select fd.id from FacilityData fd where fd.facilityBusinessId = :facilityBusinessId")
    Optional<Long> findIdByFacilityBusinessId(String facilityBusinessId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({ @QueryHint(name = "javax.persistence.query.timeout", value = "5000") })
    @Query(value = "select fd from FacilityData fd where fd.id = :id")
    Optional<FacilityData> findByIdForUpdate(Long id);
}
