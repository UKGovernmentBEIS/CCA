package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;

import uk.gov.cca.api.migration.underlyingagreement.request.UnderlyingAgreementMigratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.domain.BuyOutSurplusRunCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaGeneratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaGeneratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.domain.UnderlyingAgreementActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementAcceptedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementRejectedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationAcceptedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationRejectedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationSubmittedRequestActionPayload;
import uk.gov.netz.api.swagger.SwaggerSchemasAbstractProvider;
import uk.gov.netz.api.workflow.request.flow.payment.domain.PaymentCancelledRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.payment.domain.PaymentProcessedRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.rde.domain.RdeDecisionForcedRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.rde.domain.RdeRejectedRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.rde.domain.RdeSubmittedRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.rfi.domain.RfiResponseSubmittedRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.rfi.domain.RfiSubmittedRequestActionPayload;

@Component
public class RequestActionPayloadSchemasProvider extends SwaggerSchemasAbstractProvider {
    
    @Override
    public void afterPropertiesSet() {
    	//common
    	addResolvedShemas(RfiResponseSubmittedRequestActionPayload.class.getSimpleName(), RfiResponseSubmittedRequestActionPayload.class);
    	addResolvedShemas(RfiSubmittedRequestActionPayload.class.getSimpleName(), RfiSubmittedRequestActionPayload.class);
    	
    	addResolvedShemas(RdeDecisionForcedRequestActionPayload.class.getSimpleName(), RdeDecisionForcedRequestActionPayload.class);
    	addResolvedShemas(RdeRejectedRequestActionPayload.class.getSimpleName(), RdeRejectedRequestActionPayload.class);
    	addResolvedShemas(RdeSubmittedRequestActionPayload.class.getSimpleName(), RdeSubmittedRequestActionPayload.class);
    	
    	addResolvedShemas(PaymentProcessedRequestActionPayload.class.getSimpleName(), PaymentProcessedRequestActionPayload.class);
    	addResolvedShemas(PaymentCancelledRequestActionPayload.class.getSimpleName(), PaymentCancelledRequestActionPayload.class);
    	
    	//project specific
    	
    	addResolvedShemas(TargetUnitAccountCreationSubmittedRequestActionPayload.class.getSimpleName(), TargetUnitAccountCreationSubmittedRequestActionPayload.class);

        // Underlying Agreement
    	addResolvedShemas(UnderlyingAgreementSubmittedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementSubmittedRequestActionPayload.class);
    	addResolvedShemas(UnderlyingAgreementRejectedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementRejectedRequestActionPayload.class);
		addResolvedShemas(UnderlyingAgreementAcceptedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementAcceptedRequestActionPayload.class);
		addResolvedShemas(UnderlyingAgreementActivatedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementActivatedRequestActionPayload.class);
		
        addResolvedShemas(UnderlyingAgreementMigratedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementMigratedRequestActionPayload.class);

        // Admin Termination
    	addResolvedShemas(AdminTerminationSubmittedRequestActionPayload.class.getSimpleName(), AdminTerminationSubmittedRequestActionPayload.class);
    	addResolvedShemas(AdminTerminationFinalDecisionSubmittedRequestActionPayload.class.getSimpleName(), AdminTerminationFinalDecisionSubmittedRequestActionPayload.class);
    	addResolvedShemas(AdminTerminationWithdrawSubmittedRequestActionPayload.class.getSimpleName(), AdminTerminationWithdrawSubmittedRequestActionPayload.class);
    	
    	//Peer review
    	addResolvedShemas(CcaPeerReviewDecisionSubmittedRequestActionPayload.class.getSimpleName(), CcaPeerReviewDecisionSubmittedRequestActionPayload.class);

        // Underlying Agreement Variation
        addResolvedShemas(UnderlyingAgreementVariationSubmittedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementVariationSubmittedRequestActionPayload.class);
        addResolvedShemas(UnderlyingAgreementVariationAcceptedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementVariationAcceptedRequestActionPayload.class);
		addResolvedShemas(UnderlyingAgreementVariationRejectedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementVariationRejectedRequestActionPayload.class);
		addResolvedShemas(UnderlyingAgreementVariationActivatedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementVariationActivatedRequestActionPayload.class);

		// Performance data upload
		addResolvedShemas(PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload.class.getSimpleName(), PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload.class);
		
		// PΑΤ
		addResolvedShemas(PerformanceAccountTemplateProcessingSubmittedRequestActionPayload.class.getSimpleName(), PerformanceAccountTemplateProcessingSubmittedRequestActionPayload.class);
		
		// Subsistence fees
		addResolvedShemas(SubsistenceFeesRunCompletedRequestActionPayload.class.getSimpleName(), SubsistenceFeesRunCompletedRequestActionPayload.class);
		addResolvedShemas(SectorMoaGeneratedRequestActionPayload.class.getSimpleName(), SectorMoaGeneratedRequestActionPayload.class);
		addResolvedShemas(TargetUnitMoaGeneratedRequestActionPayload.class.getSimpleName(), TargetUnitMoaGeneratedRequestActionPayload.class);

		// Buy Out Surplus
		addResolvedShemas(BuyOutSurplusRunCompletedRequestActionPayload.class.getSimpleName(), BuyOutSurplusRunCompletedRequestActionPayload.class);
		addResolvedShemas(TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload.class.getSimpleName(), TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload.class);
		addResolvedShemas(TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload.class.getSimpleName(), TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload.class);

		// CCA3 Existing Facilities Migration
		addResolvedShemas(Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload.class.getSimpleName(), Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload.class);
		addResolvedShemas(Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload.class.getSimpleName(), Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload.class);

		// Facility Audit
		addResolvedShemas(PreAuditReviewSubmittedRequestActionPayload.class.getSimpleName(), PreAuditReviewSubmittedRequestActionPayload.class);
		addResolvedShemas(AuditDetailsCorrectiveActionsSubmittedRequestActionPayload.class.getSimpleName(), AuditDetailsCorrectiveActionsSubmittedRequestActionPayload.class);
		addResolvedShemas(AuditTrackCorrectiveActionsSubmittedRequestActionPayload.class.getSimpleName(), AuditDetailsCorrectiveActionsSubmittedRequestActionPayload.class);

		// CCA2 Extension Notice
		addResolvedShemas(Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload.class.getSimpleName(), Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload.class);
	}

}
