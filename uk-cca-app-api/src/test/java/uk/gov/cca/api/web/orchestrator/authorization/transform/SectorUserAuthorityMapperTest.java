package uk.gov.cca.api.web.orchestrator.authorization.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityDTO;
import uk.gov.cca.api.user.sectoruser.transform.SectorUserAuthorityMapper;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityInfoDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.netz.api.authorization.core.domain.AuthorityStatus.ACTIVE;

class SectorUserAuthorityMapperTest {

    private SectorUserAuthorityMapper sectorUserAuthorityMapper = Mappers.getMapper(SectorUserAuthorityMapper.class);

    @Test
    void toSectorUsersAuthoritiesInfoDto() {
        SectorUserAuthorityDTO sectorUserAuthorityDTO = SectorUserAuthorityDTO.builder()
        		.userId("b87a35cf-e85e-483c-9c3d-0de3fccb283f")
        		.roleName("Administrator User")
        		.roleCode("roleCode")
        		.authorityStatus(ACTIVE)
        		.authorityCreationDate(LocalDateTime.now())
        		.contactType(ContactType.SECTOR_ASSOCIATION)
        		.build();

        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userId("userId")
                .firstName("FirstName")
                .lastName("Lastname")
                .build();

        SectorUserAuthorityInfoDTO sectorUserAuthorityInfoDTO = SectorUserAuthorityInfoDTO.builder()
                .contactType("Sector Association")
                .roleCode("roleCode")
                .roleName("Administrator User")
                .firstName("FirstName")
                .lastName("Lastname")
                .userId("userId")
                .status(ACTIVE)
                .build();

        SectorUserAuthorityInfoDTO sectorUsersAuthoritiesInfoDto = sectorUserAuthorityMapper.toSectorUsersAuthoritiesInfoDto(sectorUserAuthorityDTO, userInfoDTO);

        assertEquals(sectorUserAuthorityInfoDTO, sectorUsersAuthoritiesInfoDto);
    }
}
