package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.notification.template.domain.TargetUnitAccountTemplateParams;
import uk.gov.cca.api.notification.template.domain.TargetUnitDetailsParams;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
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
import uk.gov.netz.api.workflow.request.core.domain.Request;

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
        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder()
                .name("name")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .companyRegistrationNumber("registrationNumber")
                .businessId("Business Id")
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

        final TargetUnitAccountTemplateParams expected = TargetUnitAccountTemplateParams.builder()
                .name("name")
                .companyRegistrationNumber("registrationNumber")
                .targetUnitIdentifier("Business Id")
                .targetUnitAddress("Acc Line 1\nAcc Line 2\nAcc City\nAcc code\nAcc County\nGreece")
                .primaryContact("First Last")
                .primaryContactEmail("responsible@example.com")
                .location("Res Line 1\nRes Line 2\nRes City\nRes code\nRes County\nGreece")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(documentTemplateTransformationMapper.constructAccountAddressDTO(accountDetails.getAddress()))
                .thenReturn("Acc Line 1\nAcc Line 2\nAcc City\nAcc code\nAcc County\nGreece");
        when(documentTemplateTransformationMapper.constructAccountAddressDTO(accountDetails.getResponsiblePerson().getAddress()))
                .thenReturn("Res Line 1\nRes Line 2\nRes City\nRes code\nRes County\nGreece");


        // Invoke
        TargetUnitAccountTemplateParams actual = ccaDocumentTemplateCommonParamsProvider
                .getAccountTemplateParams(accountId);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(documentTemplateTransformationMapper, times(1))
                .constructAccountAddressDTO(accountDetails.getAddress());
        verify(documentTemplateTransformationMapper, times(1))
                .constructAccountAddressDTO(accountDetails.getResponsiblePerson().getAddress());
    }

    @Test
    void constructTargetUnitDetailsParams() {
        final AccountAddressDTO operatorAddress = AccountAddressDTO.builder().line1("line").build();
        final AccountAddressDTO resAddress = AccountAddressDTO.builder().line1("resLine").build();
        final TargetUnitAccountDetails accountDetails = TargetUnitAccountDetails.builder()
                .operatorName("operatorName")
                .companyRegistrationNumber("companyRegistrationNumber")
                .address(operatorAddress)
                .responsiblePerson(TargetUnitAccountContactDTO.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .email("email")
                        .address(resAddress)
                        .build())
                .build();

        final TargetUnitDetailsParams expected = TargetUnitDetailsParams.builder()
                .name("operatorName")
                .companyRegistrationNumber("companyRegistrationNumber")
                .targetUnitAddress("operatorAddress")
                .primaryContact("firstName lastName")
                .primaryContactEmail("email")
                .location("resAddress")
                .build();

        when(documentTemplateTransformationMapper.constructAccountAddressDTO(operatorAddress)).thenReturn("operatorAddress");
        when(documentTemplateTransformationMapper.constructAccountAddressDTO(resAddress)).thenReturn("resAddress");

        // Invoke
        Map<String, Object> result = ccaDocumentTemplateCommonParamsProvider
                .constructTargetUnitDetailsParams(accountDetails);

        // Verify
        assertThat(result).containsExactlyEntriesOf(Map.of("targetUnitDetails", expected));
    }

    @Test
    void getSectorAndCaAndSignatoryTemplateParams() {
        final long sectorAssociationId = 1L;
        final String sectorAcronym = "acronym";
        final String sectorName = "Sector Name";
        final String sectorLegalName = "Sector legal name";
        final LocalDate date = LocalDate.of(2024, 2, 3);
        final SectorAssociationContactDTO sectorAssociationContact = SectorAssociationContactDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
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
        
        final SectorAssociationSchemeDTO scheme = SectorAssociationSchemeDTO.builder()
                .umaDate(date)
                .sectorDefinition("definition")
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
                        "sectorAcronym", sectorAcronym,
                        "sectorAcronymAndName", sectorAcronym + "-" + sectorName,
                        "sectorName", sectorName,
                        "sectorLegalName", sectorLegalName,
                        "sectorContactFullName", "%s %s".formatted(sectorAssociationContact.getFirstName(), sectorAssociationContact.getLastName()),
                        "sectorContactAddress", "line 1\ncity\ncounty\ncode",
                        "sectorContactEmail", "email",
                        "umaDate", "03/02/2024",
                        "sectorDefinition", "definition"))
                .build();

        when(sectorReferenceDetailsService.getSectorAssociationDetails(sectorAssociationId))
        		.thenReturn(sectorAssociationDTO);
        when(sectorReferenceDetailsService.getSectorAssociationAcronymAndNameBySectorAssociationId(sectorAssociationId))
				.thenReturn(sectorAcronym + "-" + sectorName);
        when(sectorReferenceDetailsService.getSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(sectorAssociationId, SchemeVersion.CCA_2))
        		.thenReturn(scheme);
        when(competentAuthorityService.getCompetentAuthorityDTO(CompetentAuthorityEnum.ENGLAND)).thenReturn(competentAuthorityDTO);
        when(regulatorUserAuthService.getUserById(signatory)).thenReturn(signatoryUser);
        when(userAuthService.getUserSignature(signatureInfo.getUuid())).thenReturn(Optional.of(fileDTO));
        when(documentTemplateTransformationMapper.constructAddressDTO(sectorAssociationContact.getAddress()))
                .thenReturn("line 1\ncity\ncounty\ncode");
        when(documentTemplateTransformationMapper.formatUmaDate(date))
        		.thenReturn("03/02/2024");

        // invoke
        TemplateParams actual = ccaDocumentTemplateCommonParamsProvider.getSectorAndCaAndSignatoryTemplateParams(signatory, sectorAssociationId, TemplateParams.builder().build());

        // verify
        assertThat(actual.getParams()).isEqualTo(expected.getParams());
        verify(sectorReferenceDetailsService, times(2)).getSectorAssociationDetails(sectorAssociationId);
        verify(sectorReferenceDetailsService, times(1))
        		.getSectorAssociationAcronymAndNameBySectorAssociationId(sectorAssociationId);
        verify(sectorReferenceDetailsService, times(1))
        		.getSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(sectorAssociationId, SchemeVersion.CCA_2);
        verify(competentAuthorityService, times(1)).getCompetentAuthorityDTO(CompetentAuthorityEnum.ENGLAND);
        verify(regulatorUserAuthService, times(1)).getUserById(signatory);
        verify(userAuthService, times(1)).getUserSignature(signatureInfo.getUuid());
        verify(documentTemplateTransformationMapper, times(1))
                .constructAddressDTO(sectorAssociationContact.getAddress());
        verify(documentTemplateTransformationMapper, times(1))
        		.formatUmaDate(date);
    }

    @Test
    void getSectorTemplateParams_request_not_related_to_sector() {
    	final Request request = Request.builder().build();
    	
    	Map<String, String> result = ccaDocumentTemplateCommonParamsProvider.getSectorTemplateParams(request, null);
    	
        assertThat(result).isEmpty();
    }
    
    @Test
    void getPermitReferenceId() {
        assertThat(ccaDocumentTemplateCommonParamsProvider.getPermitReferenceId(1L))
                .isEmpty();
    }
}
