package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

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
    void create_proposed_document_CCA2() {
        final String requestId = "1";
        final long accountId = 5L;
        final String businessId = "businessId";
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();
        final CcaDecisionNotification notification = CcaDecisionNotification.builder()
                .decisionNotification(DecisionNotification.builder().build())
                .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .decisionNotification(notification)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                		.underlyingAgreement(underlyingAgreement)
                		.build())
                .underlyingAgreementVersionMap(Map.of(SchemeVersion.CCA_2, 100))
                .build();
        final Request request = Request.builder().payload(requestPayload).build();
        addResourcesToRequest(accountId, request);
        
        TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder().businessId(businessId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)).thenReturn(accountDetails);

        service.create(requestId, schemeVersion);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(accountReferenceDetailsService, times(1)).getTargetUnitAccountDetails(accountId);
        verify(ccaOfficialNoticeGeneratorService, times(1)).generateAsync(request, notification, 
        		CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PROPOSED_DOCUMENT_CCA2,
        		CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA2,
        		"businessId CCA2 Underlying Agreement v101 [proposed].pdf", schemeVersion);
    }

    @Test
    void create_proposed_document_CCA3() {
        final String requestId = "1";
        final long accountId = 5L;
        final String businessId = "businessId";
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();
        final CcaDecisionNotification notification = CcaDecisionNotification.builder()
                .decisionNotification(DecisionNotification.builder().build())
                .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .decisionNotification(notification)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .underlyingAgreementVersionMap(Map.of(SchemeVersion.CCA_3, 100))
                .build();
        final Request request = Request.builder().payload(requestPayload).build();
        addResourcesToRequest(accountId, request);

        TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder().businessId(businessId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)).thenReturn(accountDetails);

        service.create(requestId, schemeVersion);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(accountReferenceDetailsService, times(1)).getTargetUnitAccountDetails(accountId);
        verify(ccaOfficialNoticeGeneratorService, times(1)).generateAsync(request, notification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PROPOSED_DOCUMENT_CCA3,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA3,
                "businessId CCA3 Underlying Agreement v101 [proposed].pdf", schemeVersion);
    }

    @Test
    void create_final_document_CCA2() {
        final String requestId = "1";
        final long accountId = 5L;
        final String businessId = "businessId";
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();
        final CcaDecisionNotification notification = CcaDecisionNotification.builder()
                .decisionNotification(DecisionNotification.builder().build())
                .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .decisionNotification(notification)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .underlyingAgreementActivationDetails(UnderlyingAgreementActivationDetails.builder().comments("comments").build())
                .underlyingAgreementVersionMap(Map.of(SchemeVersion.CCA_2, 100))
                .build();
        final Request request = Request.builder().payload(requestPayload).build();
        addResourcesToRequest(accountId, request);
        
        TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder().businessId(businessId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)).thenReturn(accountDetails);

        service.create(requestId, schemeVersion);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(accountReferenceDetailsService, times(1)).getTargetUnitAccountDetails(accountId);
        verify(ccaOfficialNoticeGeneratorService, times(1)).generateAsync(request, notification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_FINAL_DOCUMENT_CCA2,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA2,
                "businessId CCA2 Underlying Agreement v101.pdf", schemeVersion);
    }

    @Test
    void create_final_document_CCA3() {
        final String requestId = "1";
        final long accountId = 5L;
        final String businessId = "businessId";
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();
        final CcaDecisionNotification notification = CcaDecisionNotification.builder()
                .decisionNotification(DecisionNotification.builder().build())
                .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .decisionNotification(notification)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .underlyingAgreementActivationDetails(UnderlyingAgreementActivationDetails.builder().comments("comments").build())
                .underlyingAgreementVersionMap(Map.of(SchemeVersion.CCA_3, 100))
                .build();
        final Request request = Request.builder().payload(requestPayload).build();
        addResourcesToRequest(accountId, request);

        TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder().businessId(businessId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)).thenReturn(accountDetails);

        service.create(requestId, schemeVersion);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(accountReferenceDetailsService, times(1)).getTargetUnitAccountDetails(accountId);
        verify(ccaOfficialNoticeGeneratorService, times(1)).generateAsync(request, notification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_FINAL_DOCUMENT_CCA3,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA3,
                "businessId CCA3 Underlying Agreement v101.pdf", schemeVersion);
    }
    
    private void addResourcesToRequest(Long accountId, Request request) {
		RequestResource accountResource = RequestResource.builder()
				.resourceType(ResourceType.ACCOUNT)
				.resourceId(accountId.toString())
				.request(request)
				.build();

        request.getRequestResources().add(accountResource);
	}
}
