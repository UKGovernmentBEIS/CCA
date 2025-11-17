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
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemTargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.CcaItemDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemFacilityDTO;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemSectorDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.application.item.domain.Item;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemAssigneeDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskType;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.domain.dto.UserInfoDTO;


@ExtendWith(MockitoExtension.class)
class CcaItemMapperTest {

	private CcaItemMapper mapper = Mappers.getMapper(CcaItemMapper.class);


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
    void sectorToItemSectorDTO() {
        Long id = 1L;
        String name = "name";
        String acronym = "ADS";
        SectorAssociationDetailsDTO sectorDTO = SectorAssociationDetailsDTO.builder()
            .id(id)
            .commonName(name)
            .acronym(acronym)
            .build();

        ItemSectorDTO expectedItem = ItemSectorDTO.builder()
            .sectorId(id)
            .sectorName(name)
            .sectorAcronym(acronym)
            .build();

        ItemSectorDTO actualItem = mapper.sectorToItemSectorDTO(sectorDTO);

        assertEquals(expectedItem, actualItem);
    }
    
    @Test
    void facilityToItemFacilityDTO() {
        Long id = 1L;
        String name = "name";
        String businessId = "ADS-F0000";
        FacilityBaseInfoDTO facilityDTO = FacilityBaseInfoDTO.builder()
            .id(id)
            .facilityBusinessId(businessId)
            .siteName(name)
            .build();

        ItemFacilityDTO expectedItem = ItemFacilityDTO.builder()
            .facilityId(id)
            .facilityBusinessId(businessId)
            .siteName(name)
            .build();

        ItemFacilityDTO actualItem = mapper.facilityToItemFacilityDTO(facilityDTO);

        assertEquals(expectedItem, actualItem);
    }

    @Test
    void itemToCcaItemDTO() {
        LocalDateTime itemCreationDate = LocalDateTime.now();
        String requestId = "REQ-1";
        Long accountId = 1L;
        Long sectorId = 2L;
        Long facilityId = 3L;
        Long requestTaskId = 15L;
        String taskAssigneeId = "userId";
        String businessId = "TU01";
        
        RequestType requestType = RequestType.builder().code("APPLY_FOR_AN_UNDERLYING_AGREEMENT").build();
        RequestTaskType requestTaskType = RequestTaskType.builder().code("UNDERLYING_AGREEMENT").build();

        Item item = Item.builder()
            .creationDate(itemCreationDate)
            .requestId(requestId)
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
        
        ItemSectorDTO sector = ItemSectorDTO.builder()
        		.sectorId(sectorId)
        		.sectorAcronym("ADS")
        		.sectorName("name")
        		.build();
        
        ItemFacilityDTO facility = ItemFacilityDTO.builder()
        		.facilityId(facilityId)
        		.facilityBusinessId(businessId)
        		.build();

        CcaItemDTO expectedItem = CcaItemDTO.builder()
            .creationDate(itemCreationDate)
            .requestId(requestId)
            .taskId(requestTaskId)
            .itemAssignee(ItemAssigneeDTO.builder().taskAssignee(taskAssignee).taskAssigneeType(taskAssigneeType).build())
            .isNew(false)
            .account(account)
            .sector(sector)
            .facility(facility)
            .requestType("APPLY_FOR_AN_UNDERLYING_AGREEMENT")
            .taskType("UNDERLYING_AGREEMENT")
            .build();

        CcaItemDTO actualItem = mapper.itemToCcaItemDTO(item, taskAssignee, taskAssigneeType, account, sector, facility);

        assertEquals(expectedItem, actualItem);
    }
}
