package uk.gov.cca.api.workflow.request.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountService;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationInfoService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeService;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.transform.AccountReferenceDataMapper;

@Component
@RequiredArgsConstructor
public class AccountReferenceDetailsService {

    private final TargetUnitAccountService targetUnitAccountService;
    private final SectorAssociationInfoService sectorAssociationInfoService;
    private final AccountReferenceDataMapper accountReferenceDataMapper;
    private final SectorAssociationQueryService sectorAssociationQueryService;
    private final SectorAssociationSchemeService sectorAssociationSchemeService;

    public TargetUnitAccountDetailsDTO getTargetUnitAccountDetails(Long accountId) {
        return targetUnitAccountService.getTargetUnitAccountDetailsById(accountId);
    }

    public SectorAssociationContactDTO getSectorAssociationContactByAccountId(Long accountId) {
        Long sectorAssociationId = getTargetUnitAccountDetails(accountId).getSectorAssociationId();
        return sectorAssociationInfoService.getSectorAssociationContact(sectorAssociationId);
    }
    
    public String getSectorAssociationNameByAccountId(Long accountId) {
        long sectorAssociationId = getTargetUnitAccountDetails(accountId).getSectorAssociationId();
        return sectorAssociationQueryService.getSectorAssociationName(sectorAssociationId);
    }

    public String getSectorAssociationIdentifierByAccountId(Long accountId) {
        long sectorAssociationId = getTargetUnitAccountDetails(accountId).getSectorAssociationId();
        return sectorAssociationQueryService.getSectorAssociationIdentifier(sectorAssociationId);
    }
    
    public SectorAssociationMeasurementInfoDTO getSectorAssociationMeasurementInfo(Long sectorAssociationId, Long subSectorAssociationId) {
    	return sectorAssociationInfoService.getSectorAssociationMeasurementInfo(sectorAssociationId, subSectorAssociationId);
	}
    
    public SectorAssociationSchemeDTO getSectorAssociationSchemeBySectorAssociationId(Long sectorAssociationId) {
    	return sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorAssociationId);
	}

    public AccountReferenceData getAccountReferenceData(Long accountId) {
        final TargetUnitAccountDetailsDTO targetUnitAccountDetails = getTargetUnitAccountDetails(accountId);
        final Long subsectorAssociationId = targetUnitAccountDetails.getSubsectorAssociationId();
        final Long sectorAssociationId = targetUnitAccountDetails.getSectorAssociationId();

        final SectorAssociationMeasurementInfoDTO sectorAssociationMeasurementInfoDTO = getSectorAssociationMeasurementInfo(
        		sectorAssociationId, subsectorAssociationId);
        
        return accountReferenceDataMapper.toAccountReferenceData(targetUnitAccountDetails, sectorAssociationMeasurementInfoDTO);
    }

	
}
