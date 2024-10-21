package uk.gov.cca.api.web.orchestrator.authorization.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.ObjectUtils;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityUpdateService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityUpdateWrapperDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserNotificationGateway;
import uk.gov.netz.api.authorization.operator.domain.NewUserActivated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class SectorUserAuthorityUpdateOrchestrator {

	private final SectorUserAuthorityUpdateService sectorUserAuthorityUpdateService;
	private final SectorUserNotificationGateway sectorUserNotificationGateway;
	
	@Transactional
    public void updateSectorAuthorities(SectorUserAuthorityUpdateWrapperDTO wrapperDTO, final Long sectorAssociationId) {

        if (wrapperDTO == null || wrapperDTO.getSectorUserAuthorityUpdateDTOList() == null) {
            throw new BusinessException(ErrorCode.PARAMETERS_VALIDATION);
        }
        final List<NewUserActivated> activatedSectorUsers = sectorUserAuthorityUpdateService.
        		updateSectorUserAuthorities(wrapperDTO.getSectorUserAuthorityUpdateDTOList(), sectorAssociationId);
        

        if (ObjectUtils.isNotEmpty(activatedSectorUsers)) {
            sectorUserNotificationGateway.notifyUsersUpdateStatus(activatedSectorUsers);
        }
	}
}
