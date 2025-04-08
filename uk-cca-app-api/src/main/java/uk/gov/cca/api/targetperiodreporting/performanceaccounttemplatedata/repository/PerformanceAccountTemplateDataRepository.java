package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataEntity;

@Repository
@Transactional(readOnly = true)
public interface PerformanceAccountTemplateDataRepository
		extends JpaRepository<PerformanceAccountTemplateDataEntity, Long> {

}
