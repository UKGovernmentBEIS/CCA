package uk.gov.cca.api.web.orchestrator.sectorassociation.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResults;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaQueryService;

@Service
@RequiredArgsConstructor
public class SectorAssociationSubsistenceFeesServiceOrchestrator {

	private final SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;
	
	public SubsistenceFeesMoaSearchResults getSectorSubsistenceFeesMoas(Long sectorAssociationId, SubsistenceFeesMoaSearchCriteria criteria) {
		return subsistenceFeesMoaQueryService.getSectorSubsistenceFeesMoas(sectorAssociationId, criteria);
	}
}
