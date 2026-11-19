package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusEntity;

@Repository
@Transactional(readOnly = true)
public interface SurplusRepository extends JpaRepository<SurplusEntity, Long> {

	Optional<SurplusEntity> findByResourceIdAndSchemeVersion(Long resourceId, SchemeVersion schemeVersion);
	
	List<SurplusEntity> findAllByResourceIdInAndSchemeVersion(List<Long> resourceIds, SchemeVersion schemeVersion);
}