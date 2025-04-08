package uk.gov.cca.api.subsistencefees.repository;


import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultsInfo;

@Transactional(readOnly = true)
public interface SubsistenceFeesMoaCustomRepository {
	
	SubsistenceFeesMoaSearchResultsInfo findBySearchCriteriaForCAView(Long runId, SubsistenceFeesMoaSearchCriteria searchCriteria);
	
	SubsistenceFeesMoaSearchResultsInfo findBySearchCriteriaForSectorAssociationView(Long sectorAssociationId, SubsistenceFeesMoaSearchCriteria searchCriteria);
}
