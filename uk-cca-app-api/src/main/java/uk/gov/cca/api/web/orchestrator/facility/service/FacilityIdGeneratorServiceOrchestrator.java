package uk.gov.cca.api.web.orchestrator.facility.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.facility.domain.dto.FacilityBusinessIdDTO;
import uk.gov.cca.api.facility.service.FacilityIdentifierService;
import uk.gov.cca.api.facility.util.FacilityBusinessIdGeneratorUtil;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;

@Service
@RequiredArgsConstructor
public class FacilityIdGeneratorServiceOrchestrator {

    private final FacilityIdentifierService facilityIdentifierService;
    private final SectorAssociationQueryService sectorAssociationQueryService;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Transactional
    public FacilityBusinessIdDTO generateFacilityBusinessId(Long accountId) {
        Long sectorAssociationId = targetUnitAccountQueryService.getAccountSectorAssociationId(accountId);
        final Long identifier = facilityIdentifierService.incrementAndGet(sectorAssociationId);
        String acronym = sectorAssociationQueryService.getSectorAssociationAcronymById(sectorAssociationId);
        String facilityBusinessId = FacilityBusinessIdGeneratorUtil.generate(acronym, identifier);

        return FacilityBusinessIdDTO.builder().facilityBusinessId(facilityBusinessId).build();
    }
}
