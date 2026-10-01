package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.TargetUnitAccountTemplateParams;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateAccountDataCollectFromAccountService;

@Service
@RequiredArgsConstructor
public class CcaDocumentTemplateAccountDataCollectFromAccountService
        implements DocumentTemplateAccountDataCollectFromAccountService<TargetUnitAccountTemplateParams> {

    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Override
    public TargetUnitAccountTemplateParams collect(Long accountId) {
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
}
