package uk.gov.cca.api.facility.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.facility.domain.FacilityData;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface FacilityDataRepository extends JpaRepository<FacilityData, Long> {

    @EntityGraph(value = "facility-address-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<FacilityData> findByFacilityId(String facilityId);

    boolean existsByFacilityId(String facilityId);

    boolean existsByFacilityIdAndClosedDateIsNull(String facilityId);

    Optional<FacilityData> findByFacilityIdAndClosedDateIsNull(String facilityId);

    List<FacilityData> findAllByFacilityIdIn(Set<String> facilityIds);

    List<FacilityData> findFacilityDataByAccountIdAndClosedDateIsNull(Long accountId);

    @Query(value = "select fd.id from FacilityData fd where fd.accountId = :accountId and fd.closedDate is NULL")
    List<Long> findFacilityIdsByAccountIdAndClosedDateIsNull(Long accountId);

    @Query(value = "select fd "
            + "from FacilityData fd "
            + "where fd.accountId = (:accountId) "
            + "and (LOWER(fd.facilityId) like CONCAT('%',:term,'%') "
            + "or LOWER(fd.siteName) like CONCAT('%',:term,'%')) ")
    Page<FacilityData> searchFacilityDataByAccountIdAndTerm(Pageable pageable, Long accountId, String term);
    
    @Query(value = "select fd.id from FacilityData fd where fd.facilityId = :facilityId")
    Optional<Long> findIdByFacilityId(String facilityId);
}
