package uk.gov.cca.api.workflow.request.application.item.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemTargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemTargetUnitDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.application.item.domain.Item;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemAssigneeDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskType;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.domain.dto.UserInfoDTO;


@ExtendWith(MockitoExtension.class)
class TargetUnitItemMapperTest {

	private TargetUnitItemMapper mapper = Mappers.getMapper(TargetUnitItemMapper.class);


    @Test
    void accountToItemTargetUnitAccountDTO() {
        Long id = 1L;
        String name = "name";
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        String businessId = "TU01";
        TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
            .id(id)
            .name(name)
            .competentAuthority(ca)
            .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
            .businessId(businessId)
            .status(TargetUnitAccountStatus.LIVE)
            .build();

        ItemTargetUnitAccountDTO expectedItem = ItemTargetUnitAccountDTO.builder()
            .accountId(id)
            .accountName(name)
            .competentAuthority(ca)
            .businessId(businessId)
            .build();

        ItemTargetUnitAccountDTO actualItem = mapper.accountToItemTargetUnitAccountDTO(accountDTO);

        assertEquals(expectedItem, actualItem);
    }

    @Test
    void itemToItemOrganisationDTO() {
        LocalDateTime itemCreationDate = LocalDateTime.now();
        String requestId = "REQ-1";
        Long accountId = 1L;
        Long requestTaskId = 15L;
        String taskAssigneeId = "userId";
        String businessId = "TU01";
        
        RequestType requestType = RequestType.builder().code("APPLY_FOR_AN_UNDERLYING_AGREEMENT").build();
        RequestTaskType requestTaskType = RequestTaskType.builder().code("UNDERLYING_AGREEMENT").build();

        Item item = Item.builder()
            .creationDate(itemCreationDate)
            .requestId(requestId)
            .accountId(accountId)
            .taskId(requestTaskId)
            .taskAssigneeId(taskAssigneeId)
            .requestType(requestType)
            .taskType(requestTaskType)
            .build();

        UserInfoDTO taskAssignee = UserInfoDTO.builder().firstName("fname").lastName("lname").build();
        String taskAssigneeType = REGULATOR;
        ItemTargetUnitAccountDTO account = ItemTargetUnitAccountDTO.builder()
            .accountId(accountId)
            .accountName("name")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .businessId(businessId)
            .build();

        ItemTargetUnitDTO expectedItem = ItemTargetUnitDTO.builder()
            .creationDate(itemCreationDate)
            .requestId(requestId)
            .taskId(requestTaskId)
            .itemAssignee(ItemAssigneeDTO.builder().taskAssignee(taskAssignee).taskAssigneeType(taskAssigneeType).build())
            .isNew(false)
            .account(account)
            .requestType("APPLY_FOR_AN_UNDERLYING_AGREEMENT")
            .taskType("UNDERLYING_AGREEMENT")
            .build();

        ItemTargetUnitDTO actualItem = mapper.itemToItemTargetUnitDTO(item, taskAssignee, taskAssigneeType, account);

        assertEquals(expectedItem, actualItem);
    }
}
