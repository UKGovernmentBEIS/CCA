package uk.gov.cca.api.web.orchestrator.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.user.operator.service.OperatorUserAuthorityInfoService;
import uk.gov.cca.api.user.sectoruser.service.SectorUserAuthorityInfoService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class NoticeRecipientsServiceOrchestratorTest {

    @InjectMocks
    private NoticeRecipientsServiceOrchestrator noticeRecipientsServiceOrchestrator;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Mock
    private OperatorUserAuthorityInfoService operatorUserAuthorityInfoService;

    @Mock
    private SectorUserAuthorityInfoService sectorUserAuthorityInfoService;

    @Test
    void getAdditionalNoticeRecipients() {
        final long accountId = 1L;
        final long sectorAssociationId = 1L;
        final AppUser user = AppUser.builder().userId("userId").roleType(REGULATOR).build();
        List<NoticeRecipientDTO> additionalNoticeRecipients = new ArrayList<>();

        final List<AdditionalNoticeRecipientDTO> operators = Collections.singletonList(AdditionalNoticeRecipientDTO.builder()
        		.userId("id1")
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.OPERATOR)
                .build());

        final List<AdditionalNoticeRecipientDTO> sectors = Collections.singletonList(AdditionalNoticeRecipientDTO.builder()
        		.userId("id2")
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.SECTOR_USER)
                .build());

        additionalNoticeRecipients.addAll(operators);
        additionalNoticeRecipients.addAll(sectors);

        when(operatorUserAuthorityInfoService.getCandidateOperatorNoticeRecipients(user, accountId))
                .thenReturn(operators);
        when(targetUnitAccountQueryService.getAccountSectorAssociationId(accountId))
                .thenReturn(sectorAssociationId);
        when(sectorUserAuthorityInfoService.getCandidateSectorUsersNoticeRecipients(user, sectorAssociationId))
                .thenReturn(sectors);

        List<AdditionalNoticeRecipientDTO> result = noticeRecipientsServiceOrchestrator.getAdditionalNoticeRecipients(user, accountId);

        assertThat(additionalNoticeRecipients).isEqualTo(result);
        verify(operatorUserAuthorityInfoService, times(1)).getCandidateOperatorNoticeRecipients(user, accountId);
        verify(targetUnitAccountQueryService, times(1)).getAccountSectorAssociationId(accountId);
        verify(sectorUserAuthorityInfoService, times(1)).getCandidateSectorUsersNoticeRecipients(user, sectorAssociationId);
    }

}
