package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.notification.template.domain.TargetUnitAccountTemplateParams;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.netz.api.common.config.CompetentAuthorityProperties;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityService;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateCommonParamsAbstractProvider;

@Service
public class CcaDocumentTemplateCommonParamsProvider extends DocumentTemplateCommonParamsAbstractProvider {

    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    public CcaDocumentTemplateCommonParamsProvider(RegulatorUserAuthService regulatorUserAuthService,
                                                   UserAuthService userAuthService,
                                                   CompetentAuthorityProperties competentAuthorityProperties,
                                                   DateService dateService,
                                                   CompetentAuthorityService competentAuthorityService,
                                                   AccountReferenceDetailsService accountReferenceDetailsService,
                                                   DocumentTemplateTransformationMapper documentTemplateTransformationMapper) {
        super(regulatorUserAuthService, userAuthService, competentAuthorityProperties, dateService, competentAuthorityService);
        this.accountReferenceDetailsService = accountReferenceDetailsService;
        this.documentTemplateTransformationMapper = documentTemplateTransformationMapper;
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
        final SectorAssociationMeasurementInfoDTO sectorMeasurementInfo = accountReferenceDetailsService.getSectorAssociationMeasurementInfo(
        		accountDetails.getSectorAssociationId(), accountDetails.getSubsectorAssociationId());
        final SectorAssociationSchemeDTO sectorScheme = accountReferenceDetailsService.getSectorAssociationSchemeBySectorAssociationId(accountDetails.getSectorAssociationId());
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
                .sectorIdentifier(accountReferenceDetailsService.getSectorAssociationIdentifierByAccountId(accountId))
                .sectorContactFullName("%s %s".formatted(sectorAssociationContact.getFirstName(), sectorAssociationContact.getLastName()))
                .sectorContactEmail(sectorAssociationContact.getEmail())
                .sectorContactLocation(sectorContactAddressString)
                .sectorThroughputUnit(sectorMeasurementInfo.getThroughputUnit())
                .umaDate(documentTemplateTransformationMapper.formatUmaDate(sectorScheme.getUmaDate()))
                .definition(sectorScheme.getSectorDefinition())
                .build();
    }
}
