package uk.gov.cca.api.web.orchestrator.facility.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityIdentifierService;
import uk.gov.cca.api.facility.util.FacilityIdGeneratorUtil;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;

@Service
@RequiredArgsConstructor
public class FacilityIdGeneratorServiceOrchestrator {

    private final FacilityIdentifierService facilityIdentifierService;
    private final SectorAssociationQueryService sectorAssociationQueryService;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Transactional
    public FacilityDTO generateFacilityId(Long accountId) {
        Long sectorAssociationId = targetUnitAccountQueryService.getAccountSectorAssociationId(accountId);
        final Long identifier = facilityIdentifierService.incrementAndGet(sectorAssociationId);
        String acronym = sectorAssociationQueryService.getSectorAssociationAcronymById(sectorAssociationId);
        String facilityId = FacilityIdGeneratorUtil.generate(acronym, identifier);

        return FacilityDTO.builder().facilityId(facilityId).build();
    }
}
