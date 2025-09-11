package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusResult;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountProcessingException;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.validation.BuyOutSurplusViolation;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.utils.BuyOutSurplusCalculationUtil;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.service.CalculateWorkingDaysService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TP6BuyOutSurplusCalculation {

    // Injection services
    private final TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;
    private final BuyOutSurplusQueryService buyOutSurplusQueryService;
    private final BuyOutSurplusAccountProcessingOfficialNoticeService buyOutSurplusAccountProcessingOfficialNoticeService;
    private final BuyOutSurplusManagementService buyOutSurplusManagementService;
    private final CalculateWorkingDaysService calculateWorkingDaysService;

    // Init Variables
    private Request request;
    private BuyOutSurplusAccountProcessingRequestPayload requestPayload;
    private BuyOutSurplusAccountProcessingRequestMetadata requestMetadata;
    private TargetPeriodDTO targetPeriodDetails;
    private PerformanceDataBuyOutSurplusDetailsDTO performanceData;

    // Variables
    private List<BuyOutSurplusTransactionInfoDTO> previousTransactions;

    public TP6BuyOutSurplusCalculation(TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients, BuyOutSurplusQueryService buyOutSurplusQueryService,
                                       BuyOutSurplusAccountProcessingOfficialNoticeService buyOutSurplusAccountProcessingOfficialNoticeService,
                                       BuyOutSurplusManagementService buyOutSurplusManagementService, CalculateWorkingDaysService calculateWorkingDaysService) {
        this.targetUnitAccountNoticeRecipients = targetUnitAccountNoticeRecipients;
        this.buyOutSurplusQueryService = buyOutSurplusQueryService;
        this.buyOutSurplusAccountProcessingOfficialNoticeService = buyOutSurplusAccountProcessingOfficialNoticeService;
        this.buyOutSurplusManagementService = buyOutSurplusManagementService;
        this.calculateWorkingDaysService = calculateWorkingDaysService;
    }

    /** Initialize with data **/
    public TP6BuyOutSurplusCalculation init(Request request, PerformanceDataBuyOutSurplusDetailsDTO performanceData) {
        this.request = request;
        this.requestPayload = (BuyOutSurplusAccountProcessingRequestPayload) request.getPayload();
        this.requestMetadata = (BuyOutSurplusAccountProcessingRequestMetadata) request.getMetadata();
        this.targetPeriodDetails = ((BuyOutSurplusAccountProcessingRequestPayload) request.getPayload()).getTargetPeriodDetails();
        this.performanceData = performanceData;

        return this;
    }

    /** Process **/
    public void process() throws BuyOutSurplusAccountProcessingException {
        if(this.performanceData.getSubmissionType().equals(PerformanceDataSubmissionType.PRIMARY)) {
            this.getPerformanceData()
                    .previousPayments()
                    .primaryResult()
                    .validate()
                    .bankSurplus()
                    .createTransaction()
                    .addRefundClaimForm()
                    .trackProcessedData();
        } else {
            this.getPerformanceData()
                    .previousPayments()
                    .secondaryResult()
                    .validate()
                    .bankSurplus()
                    .createTransaction()
                    .addRefundClaimForm()
                    .terminateAwaitPayments()
                    .trackProcessedData();
        }
    }

    /** Initialize Primary **/
    private TP6BuyOutSurplusCalculation primaryResult() {
        BuyOutSurplusResult buyOutSurplus = BuyOutSurplusCalculationUtil
                .initializePrimaryResult(this.performanceData, this.targetPeriodDetails);
        this.requestPayload.setBuyOutSurplus(buyOutSurplus);
        this.requestPayload.setTransactionRequired(
                buyOutSurplus.getBuyOutSurplusContainer().getInvoicedBuyOutFee().compareTo(BigDecimal.ZERO) != 0);

        return this;
    }

    /** Initialize Secondary **/
    private TP6BuyOutSurplusCalculation secondaryResult() {
        BuyOutSurplusResult buyOutSurplus = BuyOutSurplusCalculationUtil
                .initializeSecondaryResult(this.performanceData, this.previousTransactions, this.targetPeriodDetails,
                		this.calculateWorkingDaysService.addWorkingDays(LocalDate.now(), 30, this.request.getCompetentAuthority()));
        this.requestPayload.setBuyOutSurplus(buyOutSurplus);
        this.requestPayload.setTransactionRequired(
                buyOutSurplus.getBuyOutSurplusContainer().getInvoicedBuyOutFee().compareTo(BigDecimal.ZERO) != 0);

        return this;
    }

    /** Get previous completed payments **/
    private TP6BuyOutSurplusCalculation previousPayments() {
        this.previousTransactions = this.buyOutSurplusQueryService
                .getAllTransactionInfoByAccountAndTargetPeriodPessimistic(this.request.getAccountId(), this.targetPeriodDetails.getBusinessId());

        return this;
    }

    /** Validate calculated data **/
    private TP6BuyOutSurplusCalculation validate() throws BuyOutSurplusAccountProcessingException {
        if(this.requestPayload.getBuyOutSurplus().getBuyOutSurplusContainer().getInvoicedPreviousPaidFees().compareTo(BigDecimal.ZERO) < 0) {
            throw new BuyOutSurplusAccountProcessingException(BuyOutSurplusViolation.BuyOutSurplusViolationMessage.PREVIOUS_PAID_FEES_FAILED);
        }

        return this;
    }

    /** Persist Target Unit Account surplus **/
    private TP6BuyOutSurplusCalculation bankSurplus() {
        this.buyOutSurplusManagementService
                .createBankSurplus(this.request.getAccountId(), this.requestMetadata, this.requestPayload);

        return this;
    }

    /** Update awaiting payments to TERMINATED **/
    private TP6BuyOutSurplusCalculation terminateAwaitPayments() {
        this.buyOutSurplusManagementService.terminateAwaitPayments(this.previousTransactions, this.requestMetadata);

        return this;
    }

    /** Update request and metadata payload **/
    private TP6BuyOutSurplusCalculation getPerformanceData() {
        this.requestPayload.setPerformanceData(this.performanceData);

        this.requestMetadata.setPerformanceDataReportVersion(this.performanceData.getReportVersion());
        this.requestMetadata.setTpOutcome(this.performanceData.getTpOutcome());
        this.requestMetadata.setPerformanceDataId(this.performanceData.getPerformanceDataId());

        return this;
    }

    /** Create the buy out surplus transaction with the document **/
    private TP6BuyOutSurplusCalculation createTransaction() {
        if(this.requestPayload.isTransactionRequired()) {
            // Generate Transaction id for payment invoice
            String transactionCode = this.buyOutSurplusManagementService.generateTransactionCode(this.targetPeriodDetails);
            this.requestPayload.getBuyOutSurplus().setTransactionCode(transactionCode);
            this.requestMetadata.setTransactionCode(transactionCode);

            // Find Target Unit Account default recipients
            List<DefaultNoticeRecipient> recipients = this.targetUnitAccountNoticeRecipients
                    .getDefaultNoticeRecipients(this.request.getAccountId());
            this.requestPayload.setDefaultContacts(recipients);

            // Create document
            FileInfoDTO officialNotice = this.buyOutSurplusAccountProcessingOfficialNoticeService.generateOfficialNotice(
                    this.request,
                    this.requestPayload.getPerformanceData().getSubmissionType(),
                    this.requestPayload.getBuyOutSurplus().getPaymentStatus(),
                    this.requestPayload.getBuyOutSurplus().getTransactionCode());

            this.requestPayload.setOfficialNotice(officialNotice);
            this.requestPayload.getBuyOutSurplus().setFileDocumentUuid(officialNotice.getUuid());

            // Persist the buy out surplus transaction
            this.buyOutSurplusManagementService.saveBuyOutSurplusTransaction(this.requestPayload.getBuyOutSurplus());
        }

        return this;
    }

    /** Create the buy out refund claim form **/
    private TP6BuyOutSurplusCalculation addRefundClaimForm() {
        if(this.requestPayload.isTransactionRequired()
                && this.requestPayload.getBuyOutSurplus().getPaymentStatus().equals(BuyOutSurplusPaymentStatus.AWAITING_REFUND)) {
            FileInfoDTO refundClaimForm = this.buyOutSurplusAccountProcessingOfficialNoticeService
                    .generateRefundOfficialNotice(this.request);
            this.requestPayload.setRefundClaimForm(refundClaimForm);
        }

        return this;
    }

    /** Track performance data calculated through batch run **/
    private void trackProcessedData() {
        this.buyOutSurplusManagementService.saveBuyOutSurplusProcessedData(this.performanceData.getPerformanceDataId());
    }
}
