package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationService;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;

@Validated
@Service
@RequiredArgsConstructor
public class TargetUnitAccountCreationValidationService {

    private final SubsectorAssociationService subsectorAssociationService;

    public void validate(@Valid TargetUnitAccountDTO accountDTO) {
        List<Long> subsectorAssociationIds = subsectorAssociationService
                .getSubsectorAssociationIdsBySectorAssociationId(accountDTO.getSectorAssociationId());

        if (CollectionUtils.isEmpty(subsectorAssociationIds)) {
            if(accountDTO.getSubsectorAssociationId() != null) {
                throw new BusinessException(CcaErrorCode.TARGET_UNIT_ACCOUNT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION);
            }
        } else {
            if(accountDTO.getSubsectorAssociationId() == null || !subsectorAssociationIds.contains(accountDTO.getSubsectorAssociationId())) {
                throw new BusinessException(CcaErrorCode.TARGET_UNIT_ACCOUNT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION);
            }
        }
    }
}
