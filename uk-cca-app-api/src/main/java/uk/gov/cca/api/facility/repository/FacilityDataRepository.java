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
public interface FacilityDataRepository extends JpaRepository<FacilityData, Long> {

    @Transactional(readOnly = true)
    @EntityGraph(value = "facility-address-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<FacilityData> findByFacilityId(String facilityId);

    @Transactional(readOnly = true)
    boolean existsByFacilityId(String facilityId);

	@Transactional(readOnly = true)
    boolean existsByFacilityIdAndClosedDateIsNull(String facilityId);

    @Transactional(readOnly = true)
    List<FacilityData> findAllByFacilityIdIn(Set<String> facilityIds);

    @Transactional(readOnly = true)
    List<FacilityData> findFacilityDataByAccountIdAndClosedDateIsNull(Long accountId);

    @Transactional(readOnly = true)
    @Query(value = "select fd "
            + "from FacilityData fd "
            + "where fd.accountId = (:accountId) "
            + "and (LOWER(fd.facilityId) like CONCAT('%',:term,'%') "
            + "or LOWER(fd.siteName) like CONCAT('%',:term,'%')) "
            + "order by fd.facilityId asc")
    Page<FacilityData> searchFacilityDataByAccountIdAndTerm(Pageable pageable, Long accountId, String term);

}
