package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.validation;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;

import java.util.List;

public interface PerformanceDataSpreadsheetTemplateValidator<T extends PerformanceData> {

    BusinessValidationResult validateData(T performanceData);

    List<BusinessValidationResult> validateBusinessData(PerformanceDataReferenceDetails referenceDetails, T performanceData);

    TargetPeriodDocumentTemplate getDocumentTemplateType();
}
