package uk.gov.cca.api.workflow.request.flow.common.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;

import static org.assertj.core.api.Assertions.assertThat;

class TargetUnitDetailsMapperTest {

    private final TargetUnitDetailsMapper mapper = Mappers.getMapper(TargetUnitDetailsMapper.class);

    @Test
    void toUnderlyingAgreementTargetUnitDetails() {
        final TargetUnitAccountDetails targetUnitAccountDetails = TargetUnitAccountDetails.builder()
                .operatorName("operatorName")
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .companyRegistrationNumber("companyRegistrationNumber")
                .address(AccountAddressDTO.builder()
                        .line1("line1")
                        .postcode("postcode")
                        .city("City")
                        .country("Country")
                        .build())
                .responsiblePerson(TargetUnitAccountContactDTO.builder()
                        .email("xx@test.gr")
                        .firstName("First")
                        .lastName("Last")
                        .jobTitle("Job")
                        .address(AccountAddressDTO.builder()
                                .line1("Line 1")
                                .line2("Line 2")
                                .city("City")
                                .county("County")
                                .postcode("code")
                                .country("Country")
                                .build())
                        .phoneNumber(PhoneNumberDTO.builder()
                                .countryCode("30")
                                .number("9999999999")
                                .build())
                        .build())
                .subsectorAssociationId(1L)
                .build();
        final String subsectorAssociationName = "subsector-association";

        final UnderlyingAgreementTargetUnitDetails expected = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .operatorAddress(AccountAddressDTO.builder()
                        .line1("line1")
                        .postcode("postcode")
                        .city("City")
                        .country("Country")
                        .build())
                .isCompanyRegistrationNumber(Boolean.TRUE)
                .companyRegistrationNumber("companyRegistrationNumber")
                .responsiblePersonDetails(UnderlyingAgreementTargetUnitResponsiblePerson.builder()
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
                        .build())
                .subsectorAssociationName("subsector-association")
                .subsectorAssociationId(1L)
                .build();

        // Invoke
        UnderlyingAgreementTargetUnitDetails actual = mapper
                .toUnderlyingAgreementTargetUnitDetails(targetUnitAccountDetails, subsectorAssociationName);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toResponsiblePersonNoticeRecipientDTO() {
        final UnderlyingAgreementTargetUnitResponsiblePerson responsiblePerson =
                UnderlyingAgreementTargetUnitResponsiblePerson.builder()
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
                .toResponsiblePersonNoticeRecipientDTO(responsiblePerson);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

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
