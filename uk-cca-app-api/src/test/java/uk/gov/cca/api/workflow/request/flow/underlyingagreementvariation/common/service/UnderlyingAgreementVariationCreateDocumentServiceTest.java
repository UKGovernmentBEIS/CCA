package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationCreateDocumentServiceTest {

	@InjectMocks
    private UnderlyingAgreementVariationCreateDocumentService service;

    @Mock
    private RequestService requestService;

    @Mock
    private CcaFileDocumentGeneratorService ccaOfficialNoticeGeneratorService;
    
    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Test
    void create_proposed_document() {

        final String requestId = "1";
        final long accountId = 5L;
        final String businessId = "businessId";
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();
        final CcaDecisionNotification notification = CcaDecisionNotification.builder()
                .decisionNotification(DecisionNotification.builder().build())
                .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .decisionNotification(notification)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                		.underlyingAgreement(underlyingAgreement)
                		.build())
                .underlyingAgreementVersion(100)
                .build();
        final Request request =
                Request.builder().accountId(accountId).payload(requestPayload).build();
        TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder().businessId(businessId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)).thenReturn(accountDetails);

        service.create(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(accountReferenceDetailsService, times(1)).getTargetUnitAccountDetails(accountId);
        verify(ccaOfficialNoticeGeneratorService, times(1)).generateAsync(request, notification, 
        		CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PROPOSED_DOCUMENT,
        		CcaDocumentTemplateType.UNDERLYING_AGREEMENT,
        		"businessId Underlying Agreement v101 [proposed].pdf");
    }
}
