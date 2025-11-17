package uk.gov.cca.api.workflow.request.application.item.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemTargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.CcaItemDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemFacilityDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemSectorDTO;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
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
    
    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;
    
    @Mock
    private FacilityDataQueryService facilityDataQueryService;
    
    @Test
    void toItemDTOResponse_same_assignee() {
        String userId = "userId";
        String userRoleType = SECTOR_USER;
        Long sectorId = 100L;
        Long sectorId2 = 200L;
        Long accountId = 1L;
        Long accountId2 = 2L;
        Long facilityId = 1000L;
        AppUser user = AppUser.builder()
            .userId(userId)
            .firstName("firstName")
            .lastName("lastName")
            .authorities(List.of(AppCcaAuthority.builder().sectorAssociationId(sectorId).build()))
            .roleType(SECTOR_USER)
            .build();
        
        Map<String, Map<String, String>> itemRequestResources = 
        		Map.of("999", Map.of(CcaResourceType.SECTOR_ASSOCIATION, "100",
        				ResourceType.ACCOUNT, "1",
        				CcaResourceType.FACILITY, "1000"),
        				"1000", Map.of(CcaResourceType.SECTOR_ASSOCIATION, "200",
                				ResourceType.ACCOUNT, "2"));
        
        TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
            .id(accountId)
            .name("accountName")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .businessId("TU001")
            .sectorAssociationId(sectorId)
            .build();
        
        SectorAssociationDetailsDTO sectorDTO = SectorAssociationDetailsDTO.builder()
        				.id(sectorId)
        				.commonName("name")
        				.acronym("ADS")
        				.build();
        
        FacilityBaseInfoDTO facilityDTO = FacilityBaseInfoDTO.builder()
        		.id(facilityId)
        		.facilityBusinessId("facilityId")
        		.siteName("name")
        		.build();

        Item item = buildItem(userId);

        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item))
            .totalItems(1L)
            .build();

        CcaItemDTO expectedItemDTO = buildItemDTO(
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
        when(targetUnitAccountQueryService.getAccountsByIds(anyList()))
            .thenReturn(List.of(accountDTO));
        when(sectorAssociationQueryService.getSectorsByIds(anyList()))
        	.thenReturn(List.of(sectorDTO));
        when(facilityDataQueryService.getFacilityBaseInfoByIds(anyList()))
    		.thenReturn(List.of(facilityDTO));
        when(userRoleTypeService.getUserRoleTypeByUserId(userId))
            .thenReturn(UserRoleTypeDTO.builder().roleType(userRoleType).build());

        // Invoke
        ItemDTOResponse actualItemDTOResponse = service.toItemDTOResponse(itemPage, itemRequestResources, user);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(userId);
        verify(userAuthService, never()).getUsers(anyList());
        
        ArgumentCaptor<List<Long>> argument = ArgumentCaptor.forClass(List.class);
        verify(targetUnitAccountQueryService, times(1)).getAccountsByIds(argument.capture());
        List<Long> accounts = argument.getValue();
        assertThat(accounts).containsExactlyInAnyOrder(accountId2, accountId);
        
        ArgumentCaptor<List<Long>> argument2 = ArgumentCaptor.forClass(List.class);
        verify(sectorAssociationQueryService, times(1)).getSectorsByIds(argument2.capture());
        List<Long> sectors = argument2.getValue();
        assertThat(sectors).containsExactlyInAnyOrder(sectorId2, sectorId);
        
        ArgumentCaptor<List<Long>> argument3 = ArgumentCaptor.forClass(List.class);
        verify(facilityDataQueryService, times(1)).getFacilityBaseInfoByIds(argument3.capture());
        List<Long> facilities = argument3.getValue();
        assertThat(facilities).containsExactly(facilityId);
    }

    @Test
    void toItemDTOResponse_different_assignee() {
        Long accountId = 1L;
        Long sectorId = 100L;
        AppUser user = AppUser.builder()
            .userId("userId")
            .firstName("fname")
            .lastName("lname")
            .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
            .roleType(REGULATOR)
            .build();
        
        Map<String, Map<String, String>> itemRequestResources = 
        		Map.of("999", Map.of(CcaResourceType.SECTOR_ASSOCIATION, "100",
        				ResourceType.ACCOUNT, "1"));
        
        TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
            .id(accountId)
            .name("accountName")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .businessId("TU001")
            .build();
        SectorAssociationDetailsDTO sectorDTO = SectorAssociationDetailsDTO.builder()
        				.id(sectorId)
        				.commonName("name")
        				.acronym("ADS")
        				.build();
        Item item = buildItem("userId2");
        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item))
            .totalItems(1L)
            .build();

        CcaItemDTO expectedItemDTO = buildItemDTO(
            item,
            UserInfoDTO.builder()
                .firstName("fname2")
                .lastName("lname2")
                .build(),
            REGULATOR);
        expectedItemDTO.setFacility(null);
        
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(expectedItemDTO))
            .totalItems(1L)
            .build();

        // Mock
        when(userAuthService.getUsers(List.of(item.getTaskAssigneeId())))
            .thenReturn(List.of(UserInfo.builder().id("userId2").firstName("fname2").lastName("lname2").build()));

        when(targetUnitAccountQueryService.getAccountsByIds(List.of(accountId)))
            .thenReturn(List.of(accountDTO));
        
        when(sectorAssociationQueryService.getSectorsByIds(List.of(sectorId)))
    		.thenReturn(List.of(sectorDTO));

        when(userRoleTypeService.getUserRoleTypeByUserId("userId2"))
            .thenReturn(UserRoleTypeDTO.builder().roleType(REGULATOR).build());

        // Invoke
        ItemDTOResponse actualItemDTOResponse = service.toItemDTOResponse(itemPage, itemRequestResources, user);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId("userId2");
        verify(userAuthService, times(1)).getUsers(List.of(item.getTaskAssigneeId()));
        verify(targetUnitAccountQueryService, times(1)).getAccountsByIds(List.of(accountId));
        verify(sectorAssociationQueryService, times(1)).getSectorsByIds(List.of(sectorId));
        verify(facilityDataQueryService, never()).getFacilityBaseInfoByIds(anyList());
    }
    
    @Test
    void toItemDTOResponse_no_account_or_sector_or_facility() {
        String userId = "userId";
        String userRoleType = SECTOR_USER;
        Long sectorId = 100L;
        AppUser user = AppUser.builder()
            .userId(userId)
            .firstName("firstName")
            .lastName("lastName")
            .authorities(List.of(AppCcaAuthority.builder().sectorAssociationId(sectorId).build()))
            .roleType(SECTOR_USER)
            .build();
        
        Map<String, Map<String, String>> itemRequestResources = Map.of("999", Map.of());

        Item item = buildItem(userId);

        ItemPage itemPage = ItemPage.builder()
            .items(List.of(item))
            .totalItems(1L)
            .build();

        CcaItemDTO expectedItemDTO = buildItemDTO(
            item,
            UserInfoDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .build(),
            userRoleType);
        expectedItemDTO.setAccount(null);
        expectedItemDTO.setSector(null);
        expectedItemDTO.setFacility(null);
        
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(expectedItemDTO))
            .totalItems(1L)
            .build();

        // Mock
        when(userRoleTypeService.getUserRoleTypeByUserId(userId))
            .thenReturn(UserRoleTypeDTO.builder().roleType(userRoleType).build());

        // Invoke
        ItemDTOResponse actualItemDTOResponse = service.toItemDTOResponse(itemPage, itemRequestResources, user);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(userId);
        verify(userAuthService, never()).getUsers(anyList());
        verify(targetUnitAccountQueryService, never()).getAccountsByIds(anyList());
        verify(sectorAssociationQueryService, never()).getSectorsByIds(anyList());
        verify(facilityDataQueryService, never()).getFacilityBaseInfoByIds(anyList());
    }

    private Item buildItem(String assigneeId) {
        return Item.builder()
            .creationDate(LocalDateTime.now())
            .requestId("999")
            .taskId(1L)
            .taskAssigneeId(assigneeId)
            .taskDueDate(LocalDate.of(2021, 1, 1))
            .build();
    }

    private CcaItemDTO buildItemDTO(Item item, UserInfoDTO taskAssignee, String roleType) {
        return CcaItemDTO.builder()
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
            	.accountId(1L)
                .accountName("accountName")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .businessId("TU001")
                .build())
            .sector(ItemSectorDTO.builder()
            		.sectorId(100L)
            		.sectorName("name")
            		.sectorAcronym("ADS")
            		.build())
            .facility(ItemFacilityDTO.builder()
            		.facilityId(1000L)
            		.facilityBusinessId("facilityId")
            		.siteName("name")
            		.build())
            .build();
    }
}
