package uk.gov.cca.api.workflow.request.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.account.service.TargetUnitAccountService;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationInfoService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.core.transform.AccountReferenceDataMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountReferenceDetailsService {

    private final TargetUnitAccountService targetUnitAccountService;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final SectorAssociationInfoService sectorAssociationInfoService;
    private final AccountReferenceDataMapper accountReferenceDataMapper;
    private final SectorAssociationQueryService sectorAssociationQueryService;
    private final SectorReferenceDetailsService sectorReferenceDetailsService;

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

    public String getSectorAssociationAcronymAndNameByAccountId(Long accountId) {
        long sectorAssociationId = getTargetUnitAccountDetails(accountId).getSectorAssociationId();
        return sectorAssociationQueryService.getSectorAssociationAcronymAndName(sectorAssociationId);
    }

    public AccountReferenceData getAccountReferenceData(Long accountId) {
        final TargetUnitAccountDetailsDTO targetUnitAccountDetails = getTargetUnitAccountDetails(accountId);
        final Long subsectorAssociationId = targetUnitAccountDetails.getSubsectorAssociationId();
        final Long sectorAssociationId = targetUnitAccountDetails.getSectorAssociationId();

        final SectorAssociationDetails sectorAssociationDetails =
                sectorReferenceDetailsService.getSectorAssociationMeasurementDetails(sectorAssociationId, subsectorAssociationId);

        return AccountReferenceData.builder()
                .targetUnitAccountDetails(accountReferenceDataMapper.toTargetUnitAccountDetails(targetUnitAccountDetails))
                .sectorAssociationDetails(sectorAssociationDetails)
                .build();
    }

    public Map<TargetUnitAccountDTO, SectorAssociationInfo> getTargetUnitAccountsAndSectorsDetails(List<Long> accountIds) {
        List<TargetUnitAccountDTO> accountsDetails = targetUnitAccountQueryService.getAccountsByIds(accountIds);
        List<Long> sectorIds = accountsDetails.stream().map(TargetUnitAccountDTO::getSectorAssociationId).collect(Collectors.toList());
        Map<Long, SectorAssociationInfo> sectorsDetails = sectorReferenceDetailsService.getSectorAssociationsInfo(sectorIds).stream().
                collect(Collectors.toMap(SectorAssociationInfo::getId, Function.identity()));
        return accountsDetails.stream().
                collect(Collectors.toMap(Function.identity(), entry -> sectorsDetails.get(entry.getSectorAssociationId())));
    }
}
