package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.notification.template.domain.TargetUnitAccountTemplateParams;
import uk.gov.cca.api.notification.template.domain.TargetUnitDetailsParams;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
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
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateCommonParamsAbstractProvider;

import java.util.Collections;
import java.util.HashMap;
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

        return TargetUnitAccountTemplateParams.builder()
                .name(accountDetails.getName())
                .companyRegistrationNumber(accountDetails.getCompanyRegistrationNumber())
                .targetUnitIdentifier(accountDetails.getBusinessId())
                .targetUnitAddress(documentTemplateTransformationMapper.constructAccountAddressDTO(accountDetails.getAddress()))
                .primaryContact(responsiblePerson.getFirstName() + " " + responsiblePerson.getLastName())
                .primaryContactEmail(responsiblePerson.getEmail())
                .location(documentTemplateTransformationMapper.constructAccountAddressDTO(responsiblePerson.getAddress()))
                .competentAuthority(accountDetails.getCompetentAuthority())
                .build();
    }

    public Map<String, Object> constructTargetUnitDetailsParams(final TargetUnitAccountDetails targetUnitDetails) {
        TargetUnitDetailsParams targetUnitDetailsParams = TargetUnitDetailsParams.builder()
                .name(targetUnitDetails.getOperatorName())
                .companyRegistrationNumber(targetUnitDetails.getCompanyRegistrationNumber())
                .targetUnitAddress(documentTemplateTransformationMapper.constructAccountAddressDTO(targetUnitDetails.getAddress()))
                .primaryContact(targetUnitDetails.getResponsiblePerson().getFirstName() + " " + targetUnitDetails.getResponsiblePerson().getLastName())
                .primaryContactEmail(targetUnitDetails.getResponsiblePerson().getEmail())
                .location(documentTemplateTransformationMapper.constructAccountAddressDTO(targetUnitDetails.getResponsiblePerson().getAddress()))
                .build();

        return Map.of("targetUnitDetails", targetUnitDetailsParams);
    }

    public TemplateParams getSectorAndCaAndSignatoryTemplateParams(String signatory, Long sectorAssociationId, TemplateParams documentTemplateParams) {
    	// Sector params
    	final Map<String, String> sectorParams = getSectorTemplateParams(sectorAssociationId, SchemeVersion.CCA_2);
        final Map<String, Object> params = Stream.of(sectorParams, documentTemplateParams.getParams())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // CA params
        final SectorAssociationDTO sectorAssociationDetails = sectorReferenceDetailsService.getSectorAssociationDetails(sectorAssociationId);
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

            // Signatory params
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
    
    public Map<String, String> getSectorTemplateParams(Request request, SchemeVersion version) {
		    	
    	return request.getRequestResourcesMap().get(CcaResourceType.SECTOR_ASSOCIATION) != null 
    			? getSectorTemplateParams(Long.parseLong(request.getRequestResourcesMap().get(CcaResourceType.SECTOR_ASSOCIATION)), version) 
    					: Collections.emptyMap();
    }
    
    /**
     * A Utility method that constructs the sector related parameters for the document templates
     * If a scheme version is specified, then the sector scheme-specific data will be included
     *
     * @param sectorAssociationId The sector association Id
     * @param version The scheme version
     * @return sectorTemplateParams
     */
    private Map<String, String> getSectorTemplateParams(Long sectorAssociationId, SchemeVersion version) {
    	final SectorAssociationDTO sectorAssociationDetails = sectorReferenceDetailsService
    			.getSectorAssociationDetails(sectorAssociationId);
        final SectorAssociationContactDTO sectorAssociationContact = sectorAssociationDetails.getSectorAssociationContact();
        final String sectorContactAddressString = documentTemplateTransformationMapper
        		.constructAddressDTO(sectorAssociationContact.getAddress());
        
        Map<String, String> paramMap = new HashMap<>(Map.of(
                "sectorAcronym", sectorAssociationDetails.getSectorAssociationDetails().getAcronym(),
                "sectorAcronymAndName", sectorReferenceDetailsService.getSectorAssociationAcronymAndNameBySectorAssociationId(sectorAssociationId),
                "sectorName", sectorAssociationDetails.getSectorAssociationDetails().getCommonName(),
                "sectorLegalName", sectorAssociationDetails.getSectorAssociationDetails().getLegalName(),
                "sectorContactFullName", "%s %s".formatted(sectorAssociationContact.getFirstName(), sectorAssociationContact.getLastName()),
                "sectorContactEmail", sectorAssociationContact.getEmail(),
                "sectorContactAddress", sectorContactAddressString));
        
        // Add sector scheme data if version is specified
        if (version != null) {
        	final SectorAssociationSchemeDTO sectorScheme = sectorReferenceDetailsService
        			.getSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(sectorAssociationId, version);
        	
        	paramMap.putAll(new HashMap<>(Map.of(
        			"umaDate", documentTemplateTransformationMapper.formatUmaDate(sectorScheme.getUmaDate()),
                    "sectorDefinition", sectorScheme.getSectorDefinition())));
        }
        return paramMap;
    }
}
