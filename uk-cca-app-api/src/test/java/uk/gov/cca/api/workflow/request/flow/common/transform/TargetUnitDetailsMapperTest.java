package uk.gov.cca.api.workflow.request.flow.common.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;

import static org.assertj.core.api.Assertions.assertThat;

class TargetUnitDetailsMapperTest {

    private final TargetUnitDetailsMapper mapper = Mappers.getMapper(TargetUnitDetailsMapper.class);

    @Test
    void toNoticeRecipientDTO() {
        final TargetUnitAccountContactDTO accountContact = TargetUnitAccountContactDTO.builder()
                .email("xx@test.gr")
                .firstName("First")
                .lastName("Last")
                .address(AccountAddressDTO.builder()
                        .line1("Line 1")
                        .line2("Line 2")
                        .city("City")
                        .county("County")
                        .postcode("code")
                        .country("Country")
                        .build())
                .build();

        final NoticeRecipientDTO expected = NoticeRecipientDTO.builder()
                .firstName("First")
                .lastName("Last")
                .email("xx@test.gr")
                .type(NoticeRecipientType.RESPONSIBLE_PERSON)
                .build();

        // Invoke
        NoticeRecipientDTO actual = mapper
                .toNoticeRecipientDTO(accountContact, NoticeRecipientType.RESPONSIBLE_PERSON);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toSectorAssociationNoticeRecipientDTO() {
        final SectorAssociationContactDTO sectorAssociationContact = SectorAssociationContactDTO.builder()
                .email("xx@test.gr")
                .firstName("First")
                .lastName("Last")
                .build();

        final NoticeRecipientDTO expected = NoticeRecipientDTO.builder()
                .firstName("First")
                .lastName("Last")
                .email("xx@test.gr")
                .type(NoticeRecipientType.SECTOR_CONTACT)
                .build();

        // Invoke
        NoticeRecipientDTO actual = mapper
                .toSectorAssociationNoticeRecipientDTO(sectorAssociationContact);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }
}
