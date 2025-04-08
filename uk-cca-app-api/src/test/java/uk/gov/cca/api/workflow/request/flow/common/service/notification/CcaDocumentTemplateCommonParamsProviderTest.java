package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.notification.template.domain.TargetUnitAccountTemplateParams;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.core.service.SectorReferenceDetailsService;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.competentauthority.CompetentAuthorityService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.netz.api.documenttemplate.domain.templateparams.SignatoryTemplateParams;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.netz.api.user.regulator.service.RegulatorUserAuthService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaDocumentTemplateCommonParamsProviderTest {

    @InjectMocks
    private CcaDocumentTemplateCommonParamsProvider ccaDocumentTemplateCommonParamsProvider;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Mock
    private CompetentAuthorityService competentAuthorityService;

    @Mock
    private RegulatorUserAuthService regulatorUserAuthService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private SectorReferenceDetailsService sectorReferenceDetailsService;

    @Test
    void getAccountTemplateParams() {
        final long accountId = 1L;
        final long sectorId = 2L;
        final long subsectorId = 3L;

        final String sectorIdentifier = "Sector Id";
        final String sectorName = "Sector Name";
        final LocalDate date = LocalDate.of(2024, 2, 3);
        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder()
                .name("name")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .companyRegistrationNumber("registrationNumber")
                .businessId("Business Id")
                .sectorAssociationId(sectorId)
                .subsectorAssociationId(subsectorId)
                .address(AccountAddressDTO.builder()
                        .line1("Acc Line 1")
                        .line2("Acc Line 2")
                        .city("Acc City")
                        .county("Acc County")
                        .postcode("Acc code")
                        .country("GR")
                        .build())
                .responsiblePerson(TargetUnitAccountContactDTO.builder()
                        .firstName("First")
                        .lastName("Last")
                        .email("responsible@example.com")
                        .address(AccountAddressDTO.builder()
                                .line1("Res Line 1")
                                .line2("Res Line 2")
                                .city("Res City")
                                .county("Res County")
                                .postcode("Res code")
                                .country("GR")
                                .build())
                        .build())
                .build();

        final SectorAssociationContactDTO sectorContact = SectorAssociationContactDTO.builder()
                .lastName("lastname")
                .firstName("firstname")
                .email("email@email.com")
                .address(AddressDTO.builder()
                        .line1("Line 1")
                        .line2("Line 2")
                        .city("City")
                        .county("County")
                        .postcode("code").build())
                .build();

        final TargetUnitAccountTemplateParams expected = TargetUnitAccountTemplateParams.builder()
                .name("name")
                .companyRegistrationNumber("registrationNumber")
                .targetUnitIdentifier("Business Id")
                .targetUnitAddress("Acc Line 1\nAcc Line 2\nAcc City\nAcc code\nAcc County\nGreece")
                .primaryContact("First Last")
                .primaryContactEmail("responsible@example.com")
                .location("Res Line 1\nRes Line 2\nRes City\nRes code\nRes County\nGreece")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .sectorName(sectorName)
                .sectorIdentifier(sectorIdentifier)
                .sectorContactFullName("%s %s".formatted(sectorContact.getFirstName(), sectorContact.getLastName()))
                .sectorContactEmail(sectorContact.getEmail())
                .sectorContactLocation("Line 1\nLine 2\nCity\ncode\nCounty")
                .sectorThroughputUnit("unit")
                .umaDate("03/02/2024")
                .definition("definition")
                .build();

        final SectorAssociationSchemeDTO scheme = SectorAssociationSchemeDTO.builder()
                .umaDate(date)
                .sectorDefinition("definition")
                .build();

        final SectorAssociationDetails sectorMeasurementInfo = SectorAssociationDetails.builder()
                .throughputUnit("unit")
                .build();

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(accountReferenceDetailsService.getSectorAssociationAcronymAndNameByAccountId(accountId))
                .thenReturn(sectorIdentifier);
        when(accountReferenceDetailsService.getSectorAssociationNameByAccountId(accountId))
                .thenReturn(sectorName);
        when(accountReferenceDetailsService.getSectorAssociationContactByAccountId(accountId))
                .thenReturn(sectorContact);
        when(sectorReferenceDetailsService.getSectorAssociationSchemeBySectorAssociationId(sectorId))
                .thenReturn(scheme);
        when(sectorReferenceDetailsService.getSectorAssociationMeasurementDetails(sectorId, subsectorId))
                .thenReturn(sectorMeasurementInfo);
        when(documentTemplateTransformationMapper.constructAddressDTO(sectorContact.getAddress()))
                .thenReturn("Line 1\nLine 2\nCity\ncode\nCounty");
        when(documentTemplateTransformationMapper.constructAccountAddressDTO(accountDetails.getAddress()))
                .thenReturn("Acc Line 1\nAcc Line 2\nAcc City\nAcc code\nAcc County\nGreece");
        when(documentTemplateTransformationMapper.constructAccountAddressDTO(accountDetails.getResponsiblePerson().getAddress()))
                .thenReturn("Res Line 1\nRes Line 2\nRes City\nRes code\nRes County\nGreece");
        when(documentTemplateTransformationMapper.formatUmaDate(date))
                .thenReturn("03/02/2024");

        // Invoke
        TargetUnitAccountTemplateParams actual = ccaDocumentTemplateCommonParamsProvider
                .getAccountTemplateParams(accountId);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(accountReferenceDetailsService, times(1))
                .getSectorAssociationAcronymAndNameByAccountId(accountId);
        verify(accountReferenceDetailsService, times(1))
                .getSectorAssociationContactByAccountId(accountId);
        verify(accountReferenceDetailsService, times(1))
                .getSectorAssociationNameByAccountId(accountId);
        verify(sectorReferenceDetailsService, times(1))
                .getSectorAssociationSchemeBySectorAssociationId(sectorId);
        verify(sectorReferenceDetailsService, times(1))
                .getSectorAssociationMeasurementDetails(sectorId, subsectorId);
        verify(documentTemplateTransformationMapper, times(1))
                .constructAddressDTO(sectorContact.getAddress());
        verify(documentTemplateTransformationMapper, times(1))
                .constructAccountAddressDTO(accountDetails.getAddress());
        verify(documentTemplateTransformationMapper, times(1))
                .constructAccountAddressDTO(accountDetails.getResponsiblePerson().getAddress());
        verify(documentTemplateTransformationMapper, times(1))
                .formatUmaDate(date);
    }

    @Test
    void getSectorTemplateParams() {
        final long sectorAssociationId = 1L;
        final String sectorAcronym = "acronym";
        final String sectorName = "Sector Name";
        final String sectorLegalName = "Sector legal name";
        final SectorAssociationContactDTO sectorAssociationContact = SectorAssociationContactDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .address(AddressDTO.builder()
                        .city("city")
                        .county("county")
                        .postcode("postcode")
                        .line1("line 1")
                        .build())
                .build();

        final SectorAssociationDTO sectorAssociationDTO = SectorAssociationDTO.builder()
                .sectorAssociationDetails(SectorAssociationDetailsDTO.builder()
                        .acronym(sectorAcronym)
                        .legalName(sectorLegalName)
                        .commonName(sectorName)
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build())
                .sectorAssociationContact(sectorAssociationContact)
                .build();

        final String signatory = UUID.randomUUID().toString();
        final RegulatorUserDTO signatoryUser = RegulatorUserDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .jobTitle("Job Title")
                .signature(FileInfoDTO.builder()
                        .uuid(UUID.randomUUID().toString())
                        .build())
                .build();
        final FileInfoDTO signatureInfo = signatoryUser.getSignature();
        final byte[] logo = new byte[]{1, 2, 3, 4};
        final FileDTO fileDTO = FileDTO.builder().fileContent(logo).build();
        final CompetentAuthorityDTO competentAuthorityDTO = CompetentAuthorityDTO.builder().build();

        final SignatoryTemplateParams signatoryParams = SignatoryTemplateParams.builder()
                .fullName(signatoryUser.getFullName())
                .jobTitle(signatoryUser.getJobTitle())
                .signature(logo)
                .build();

        TemplateParams expected = TemplateParams.builder()
                .competentAuthorityParams(CompetentAuthorityTemplateParams
                        .builder()
                        .competentAuthority(CompetentAuthorityDTO.builder()
                                .id(CompetentAuthorityEnum.ENGLAND)
                                .build())
                        .logo(CompetentAuthorityService.getCompetentAuthorityLogo(CompetentAuthorityEnum.ENGLAND))
                        .build())
                .signatoryParams(signatoryParams)
                .params(Map.of(
                        "sectorId", sectorAcronym,
                        "sectorAssociationName", sectorLegalName,
                        "sectorContactFullName", "%s %s".formatted(sectorAssociationContact.getFirstName(), sectorAssociationContact.getLastName()),
                        "sectorContactAddress", "line 1\ncity\ncounty\ncode"))
                .build();

        when(sectorReferenceDetailsService.getSectorAssociationDetails(sectorAssociationId)).thenReturn(sectorAssociationDTO);
        when(competentAuthorityService.getCompetentAuthorityDTO(CompetentAuthorityEnum.ENGLAND)).thenReturn(competentAuthorityDTO);
        when(regulatorUserAuthService.getUserById(signatory)).thenReturn(signatoryUser);
        when(userAuthService.getUserSignature(signatureInfo.getUuid())).thenReturn(Optional.of(fileDTO));
        when(documentTemplateTransformationMapper.constructAddressDTO(sectorAssociationContact.getAddress()))
                .thenReturn("line 1\ncity\ncounty\ncode");

        // invoke
        TemplateParams actual = ccaDocumentTemplateCommonParamsProvider.getSectorTemplateParams(signatory, sectorAssociationId, TemplateParams.builder().build());

        // verify
        assertThat(actual.getParams()).isEqualTo(expected.getParams());
        verify(sectorReferenceDetailsService, times(1)).getSectorAssociationDetails(sectorAssociationId);
        verify(competentAuthorityService, times(1)).getCompetentAuthorityDTO(CompetentAuthorityEnum.ENGLAND);
        verify(regulatorUserAuthService, times(1)).getUserById(signatory);
        verify(userAuthService, times(1)).getUserSignature(signatureInfo.getUuid());
        verify(documentTemplateTransformationMapper, times(1))
                .constructAddressDTO(sectorAssociationContact.getAddress());

    }

    @Test
    void getPermitReferenceId() {
        assertThat(ccaDocumentTemplateCommonParamsProvider.getPermitReferenceId(1L))
                .isEmpty();
    }
}
