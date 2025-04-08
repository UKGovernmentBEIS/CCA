package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.notification.template.domain.TargetUnitAccountTemplateParams;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.core.service.SectorReferenceDetailsService;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.netz.api.common.config.CompetentAuthorityProperties;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.utils.DateService;
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
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateCommonParamsAbstractProvider;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CcaDocumentTemplateCommonParamsProvider extends DocumentTemplateCommonParamsAbstractProvider {

    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final SectorReferenceDetailsService sectorReferenceDetailsService;
    private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
    private final CompetentAuthorityService competentAuthorityService;
    private final RegulatorUserAuthService regulatorUserAuthService;
    private final UserAuthService userAuthService;

    public CcaDocumentTemplateCommonParamsProvider(RegulatorUserAuthService regulatorUserAuthService,
                                                   UserAuthService userAuthService,
                                                   CompetentAuthorityProperties competentAuthorityProperties,
                                                   DateService dateService,
                                                   CompetentAuthorityService competentAuthorityService,
                                                   AccountReferenceDetailsService accountReferenceDetailsService, SectorReferenceDetailsService sectorReferenceDetailsService,
                                                   DocumentTemplateTransformationMapper documentTemplateTransformationMapper) {
        super(regulatorUserAuthService, userAuthService, competentAuthorityProperties, dateService, competentAuthorityService);
        this.accountReferenceDetailsService = accountReferenceDetailsService;
        this.sectorReferenceDetailsService = sectorReferenceDetailsService;
        this.documentTemplateTransformationMapper = documentTemplateTransformationMapper;
        this.competentAuthorityService = competentAuthorityService;
        this.regulatorUserAuthService = regulatorUserAuthService;
        this.userAuthService = userAuthService;
    }

    @Override
    public String getPermitReferenceId(Long accountId) {
        return "";
    }

    @Override
    public TargetUnitAccountTemplateParams getAccountTemplateParams(Long accountId) {
        final TargetUnitAccountDetailsDTO accountDetails = accountReferenceDetailsService.getTargetUnitAccountDetails(accountId);
        final TargetUnitAccountContactDTO responsiblePerson = accountDetails.getResponsiblePerson();


        final SectorAssociationContactDTO sectorAssociationContact = accountReferenceDetailsService.getSectorAssociationContactByAccountId(accountId);
        SectorAssociationDetails sectorAssociationDetails = sectorReferenceDetailsService.getSectorAssociationMeasurementDetails(
                accountDetails.getSectorAssociationId(), accountDetails.getSubsectorAssociationId());
        final SectorAssociationSchemeDTO sectorScheme = sectorReferenceDetailsService.getSectorAssociationSchemeBySectorAssociationId(accountDetails.getSectorAssociationId());
        // sector Contact Location
        String sectorContactAddressString = documentTemplateTransformationMapper.constructAddressDTO(sectorAssociationContact.getAddress());

        return TargetUnitAccountTemplateParams.builder()
                .name(accountDetails.getName())
                .companyRegistrationNumber(accountDetails.getCompanyRegistrationNumber())
                .targetUnitIdentifier(accountDetails.getBusinessId())
                .targetUnitAddress(documentTemplateTransformationMapper.constructAccountAddressDTO(accountDetails.getAddress()))
                .primaryContact(responsiblePerson.getFirstName() + " " + responsiblePerson.getLastName())
                .primaryContactEmail(responsiblePerson.getEmail())
                .location(documentTemplateTransformationMapper.constructAccountAddressDTO(responsiblePerson.getAddress()))
                .competentAuthority(accountDetails.getCompetentAuthority())
                // sectorInformation
                .sectorName(accountReferenceDetailsService.getSectorAssociationNameByAccountId(accountId))
                .sectorIdentifier(accountReferenceDetailsService.getSectorAssociationAcronymAndNameByAccountId(accountId))
                .sectorContactFullName("%s %s".formatted(sectorAssociationContact.getFirstName(), sectorAssociationContact.getLastName()))
                .sectorContactEmail(sectorAssociationContact.getEmail())
                .sectorContactLocation(sectorContactAddressString)
                .sectorThroughputUnit(sectorAssociationDetails.getThroughputUnit())
                .umaDate(documentTemplateTransformationMapper.formatUmaDate(sectorScheme.getUmaDate()))
                .definition(sectorScheme.getSectorDefinition())
                .build();
    }

    public TemplateParams getSectorTemplateParams(String signatory, Long sectorAssociationId, TemplateParams documentTemplateParams) {
        final SectorAssociationDTO sectorAssociationDetails = sectorReferenceDetailsService.getSectorAssociationDetails(sectorAssociationId);
        final SectorAssociationContactDTO sectorAssociationContact = sectorAssociationDetails.getSectorAssociationContact();
        final String sectorContactAddressString = documentTemplateTransformationMapper.constructAddressDTO(sectorAssociationContact.getAddress());
        final Map<String, String> commonParams = Map.of(
                "sectorId", sectorAssociationDetails.getSectorAssociationDetails().getAcronym(),
                "sectorAssociationName", sectorAssociationDetails.getSectorAssociationDetails().getLegalName(),
                "sectorContactFullName", "%s %s".formatted(sectorAssociationContact.getFirstName(), sectorAssociationContact.getLastName()),
                "sectorContactAddress", sectorContactAddressString);

        final Map<String, Object> params = Stream.of(commonParams, documentTemplateParams.getParams())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // CA params
        final CompetentAuthorityEnum competentAuthority = sectorAssociationDetails.getSectorAssociationDetails().getCompetentAuthority();
        final CompetentAuthorityTemplateParams competentAuthorityParams = CompetentAuthorityTemplateParams.builder()
                .competentAuthority(competentAuthorityService.getCompetentAuthorityDTO(competentAuthority))
                .logo(CompetentAuthorityService.getCompetentAuthorityLogo(competentAuthority))
                .build();

        RegulatorUserDTO signatoryUser = this.regulatorUserAuthService.getUserById(signatory);
        FileInfoDTO signatureInfo = signatoryUser.getSignature();
        if (signatureInfo == null) {
            throw new BusinessException(ErrorCode.USER_SIGNATURE_NOT_EXIST, signatory);
        } else {
            FileDTO signatorySignature = this.userAuthService.getUserSignature(signatureInfo.getUuid())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, signatureInfo.getUuid()));

            final SignatoryTemplateParams signatoryParams = SignatoryTemplateParams.builder()
                    .fullName(signatoryUser.getFullName())
                    .jobTitle(signatoryUser.getJobTitle())
                    .signature(signatorySignature.getFileContent())
                    .build();

            return TemplateParams.builder()
                    .competentAuthorityParams(competentAuthorityParams)
                    .signatoryParams(signatoryParams)
                    .params(params)
                    .build();
        }
    }
}
