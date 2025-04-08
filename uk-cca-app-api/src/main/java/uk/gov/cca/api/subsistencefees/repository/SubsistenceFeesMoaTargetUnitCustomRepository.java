package uk.gov.cca.api.subsistencefees.repository;

import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaTargetUnitSearchResultsInfo;

public interface SubsistenceFeesMoaTargetUnitCustomRepository {

	@Transactional(readOnly = true)
	SubsistenceFeesMoaTargetUnitSearchResultsInfo findBySearchCriteria(Long runId, SubsistenceFeesSearchCriteria searchCriteria);
}
