package uk.gov.cca.api.facility.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.facility.domain.FacilityData;

import java.util.List;
import java.util.Set;

@Repository
public interface FacilityDataRepository extends JpaRepository<FacilityData, Long> {

    @Transactional(readOnly = true)
    boolean existsByFacilityId(String facilityId);

	@Transactional(readOnly = true)
    boolean existsByFacilityIdAndClosedDateIsNull(String facilityId);

    @Transactional(readOnly = true)
    List<FacilityData> findAllByFacilityIdIn(Set<String> facilityIds);

    @Transactional(readOnly = true)
    List<FacilityData> findFacilityDataByAccountIdAndClosedDateIsNull(Long accountId);

}
