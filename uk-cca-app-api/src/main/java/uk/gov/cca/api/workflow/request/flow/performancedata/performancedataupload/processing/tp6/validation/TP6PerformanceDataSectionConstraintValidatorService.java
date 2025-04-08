package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceDataSection;

import java.util.List;

@Service
@RequiredArgsConstructor
public abstract class TP6PerformanceDataSectionConstraintValidatorService<T extends TP6PerformanceDataSection> {

    protected abstract List<PerformanceDataUploadViolation> validateSection(T section, PerformanceDataReferenceDetails referenceDetails, TP6PerformanceData performanceData);

    protected abstract String getSectionName();
}
