package uk.gov.cca.api.workflow.request.application.item.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.time.temporal.ChronoUnit.DAYS;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemTargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemTargetUnitDTO;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.netz.api.workflow.request.application.item.domain.Item;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemAssigneeDTO;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.netz.api.workflow.request.core.domain.dto.UserInfoDTO;

@ExtendWith(MockitoExtension.class)
class CcaItemResponseServiceTest {

	@InjectMocks
    private CcaItemResponseService service;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;
    
    @Test
    void toItemDTOResponse_same_assignee() {
        String userId = "userId";
        String userRoleType = SECTOR_USER;
        Long sectorId = 1L;
        Long accountId = 1L;
        AppUser user = AppUser.builder()
            .userId(userId)
            .firstName("firstName")
            .lastName("lastName")
            .authorities(List.of(AppCcaAuthority.builder().sectorAssociationId(sectorId).build()))
            .roleType(SECTOR_USER)
            .build();
        TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
            .id(accountId)
            .name("accountName")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .businessId("TU001")
            .sectorAssociationId(sectorId)
            .build();

        Item item = buildItem(userId, accountId);

        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item))
            .totalItems(1L)
            .build();

        ItemTargetUnitDTO expectedItemDTO = buildItemDTO(
            item,
            UserInfoDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .build(),
            userRoleType);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(expectedItemDTO))
            .totalItems(1L)
            .build();

        // Mock
        when(targetUnitAccountQueryService.getAccountsByIds(List.of(accountId)))
            .thenReturn(List.of(accountDTO));
        when(userRoleTypeService.getUserRoleTypeByUserId(userId))
            .thenReturn(UserRoleTypeDTO.builder().roleType(userRoleType).build());

        // Invoke
        ItemDTOResponse actualItemDTOResponse = service.toItemDTOResponse(itemPage, user);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(userId);
        verify(userAuthService, never()).getUsers(anyList());
        verify(targetUnitAccountQueryService, times(1)).getAccountsByIds(List.of(accountId));
    }

    @Test
    void toItemDTOResponse_different_assignee() {
        Long accountId = 1L;
        AppUser user = AppUser.builder()
            .userId("userId")
            .firstName("fname")
            .lastName("lname")
            .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
            .roleType(REGULATOR)
            .build();
        TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
            .id(accountId)
            .name("accountName")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .businessId("TU001")
            .build();
        Item item = buildItem("userId2", accountId);
        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item))
            .totalItems(1L)
            .build();

        ItemTargetUnitDTO expectedItemDTO = buildItemDTO(
            item,
            UserInfoDTO.builder()
                .firstName("fname2")
                .lastName("lname2")
                .build(),
            REGULATOR);
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(expectedItemDTO))
            .totalItems(1L)
            .build();

        // Mock
        when(userAuthService.getUsers(List.of(item.getTaskAssigneeId())))
            .thenReturn(List.of(UserInfo.builder().id("userId2").firstName("fname2").lastName("lname2").build()));

        when(targetUnitAccountQueryService.getAccountsByIds(List.of(item.getAccountId())))
            .thenReturn(List.of(accountDTO));

        when(userRoleTypeService.getUserRoleTypeByUserId("userId2"))
            .thenReturn(UserRoleTypeDTO.builder().roleType(REGULATOR).build());

        // Invoke
        ItemDTOResponse actualItemDTOResponse = service.toItemDTOResponse(itemPage, user);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId("userId2");
        verify(userAuthService, times(1)).getUsers(List.of(item.getTaskAssigneeId()));
        verify(targetUnitAccountQueryService, times(1))
            .getAccountsByIds(List.of(item.getAccountId()));
    }

    private Item buildItem(String assigneeId, Long accountId) {
        return Item.builder()
            .creationDate(LocalDateTime.now())
            .requestId("1")
            .taskId(1L)
            .taskAssigneeId(assigneeId)
            .taskDueDate(LocalDate.of(2021, 1, 1))
            .accountId(accountId)
            .build();
    }

    private ItemTargetUnitDTO buildItemDTO(Item item, UserInfoDTO taskAssignee, String roleType) {
        return ItemTargetUnitDTO.builder()
            .creationDate(item.getCreationDate())
            .requestId(item.getRequestId())
            .taskId(item.getTaskId())
            .itemAssignee(taskAssignee != null ?
                ItemAssigneeDTO.builder()
                    .taskAssignee(taskAssignee)
                    .taskAssigneeType(roleType)
                    .build() : null)
            .daysRemaining(DAYS.between(LocalDate.now(), item.getTaskDueDate()))
            .account(ItemTargetUnitAccountDTO.builder()
                .accountId(item.getAccountId())
                .accountName("accountName")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .businessId("TU001")
                .build())
            .build();
    }
}
