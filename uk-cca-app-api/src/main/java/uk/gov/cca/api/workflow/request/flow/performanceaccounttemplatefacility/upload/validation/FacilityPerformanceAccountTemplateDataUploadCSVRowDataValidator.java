package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.validation.FacilityPerformanceAccountTemplateDataViolation;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadCsvData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataUploadCSVRowDataValidator {

    private final DataValidator<FacilityPerformanceAccountTemplateDataUploadCsvData> dataValidator;
    private static final LocalDate IMPLEMENTATION_DATE_LOWER_LIMIT = LocalDate.of(2023, 1, 1);
    public static final String CSV_ERROR_DELIMITER = " | ";

    public List<FacilityPerformanceAccountTemplateDataViolation> validate(final String filename,
                                                                          final Year targetYear,
                                                                          final Map<String, FacilityDTO> persistedFacilities,
                                                                          final Integer rowNumber,
                                                                          final List<FacilityPerformanceAccountTemplateDataCsvErrorEntry> csvRowErrors,
                                                                          final FacilityPerformanceAccountTemplateDataUploadCsvData rowData) {
        final String facilityBusinessId = rowData.getFacilityBusinessId();

        // Validate model data e.g. required fields etc
        List<FacilityPerformanceAccountTemplateDataViolation> rowDataViolations = validateModelData(rowData);
        if (!rowDataViolations.isEmpty()) {
            rowDataViolations.forEach(violation -> {
                String errorMessage = Arrays.stream(violation.getData())
                        .map(Object::toString)
                        .collect(Collectors.joining(CSV_ERROR_DELIMITER));
                csvRowErrors.add(FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder()
                        .facilityBusinessId(facilityBusinessId)
                        .rowNumber(rowNumber)
                        .filename(filename)
                        .message(errorMessage)
                        .build());
            });
            return rowDataViolations;
        }

        // Validate Implementation date against valid range
        validateImplementationDate(targetYear, rowData).ifPresent(violation -> {
            rowDataViolations.add(violation);
            csvRowErrors.add(FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder()
                    .facilityBusinessId(rowData.getFacilityBusinessId())
                    .rowNumber(rowNumber)
                    .filename(filename)
                    .message(violation.getMessage())
                    .build());
        });

        // Validate facility existence against DB and belongs to selected sector in CCA3 scheme
        if (!persistedFacilities.containsKey(facilityBusinessId)) {
            FacilityPerformanceAccountTemplateDataViolation violation = new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataUploadCsvData.class.getName(),
                    FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_FACILITY_ID,
                    facilityBusinessId);
            rowDataViolations.add(violation);
            csvRowErrors.add(FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder()
                    .facilityBusinessId(rowData.getFacilityBusinessId())
                    .rowNumber(rowNumber)
                    .filename(filename)
                    .message(violation.getMessage())
                    .build());
        } else {
            FacilityDTO facilityDTO = persistedFacilities.get(facilityBusinessId);
            // Validate facility eligibility
            List<FacilityPerformanceAccountTemplateDataViolation> facilityEligibilityViolations =
                    validateFacilityEligibility(targetYear, facilityDTO);
            if (!facilityEligibilityViolations.isEmpty()) {
                facilityEligibilityViolations.forEach(violation -> csvRowErrors.add(FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder()
                        .facilityBusinessId(facilityBusinessId)
                        .rowNumber(rowNumber)
                        .filename(filename)
                        .message(violation.getMessage())
                        .build()));

                rowDataViolations.addAll(facilityEligibilityViolations);
            }
        }

        return rowDataViolations;
    }

    private List<FacilityPerformanceAccountTemplateDataViolation> validateModelData(final FacilityPerformanceAccountTemplateDataUploadCsvData rowData) {
        List<FacilityPerformanceAccountTemplateDataViolation> violations = new ArrayList<>();

        // Validate data
        dataValidator.validate(rowData)
                .map(businessViolation ->
                        new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataUploadCsvData.class.getName(),
                                FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_PERFORMANCE_ACCOUNT_TEMPLATE_DATA,
                                businessViolation.getData()))
                .ifPresent(violations::add);

        return violations;
    }

    // Validate facility eligibility
    private List<FacilityPerformanceAccountTemplateDataViolation> validateFacilityEligibility(final Year targetYear, final FacilityDTO facility) {
        final List<FacilityPerformanceAccountTemplateDataViolation> violations = new ArrayList<>();
        final LocalDateTime createdDate = facility.getCreatedDate();
        final LocalDateTime reportingPeriodStartDate = LocalDate.of(targetYear.getValue() + 1, 1, 1).atStartOfDay();

        // first activation date < 1 January
        if (!createdDate.isBefore(reportingPeriodStartDate)) {
            violations.add(new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataUploadCsvData.class.getName(),
                    FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_FACILITY_ACTIVATION_DATE,
                    facility));
        }

        // still active on 1 January or terminated/excluded after the start in the reporting period
        if (facility.getClosedDate() != null && !facility.getClosedDate().isAfter(reportingPeriodStartDate.toLocalDate())) {
            violations.add(new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataUploadCsvData.class.getName(),
                    FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_FACILITY_CLOSE_DATE,
                    facility));
        }

        return violations;
    }

    private Optional<FacilityPerformanceAccountTemplateDataViolation> validateImplementationDate(final Year targetYear, final FacilityPerformanceAccountTemplateDataUploadCsvData rowData) {
        LocalDate implementationDate = rowData.getImplementationDate();
        boolean isNoAction = rowData.getActionCategoryType() == ActionCategoryType.NO_ACTION;
        // implementation date out of valid range
        if (!isNoAction && (implementationDate.isBefore(IMPLEMENTATION_DATE_LOWER_LIMIT)
                || implementationDate.isAfter(targetYear.atMonth(Month.DECEMBER).atEndOfMonth()))) {
            return Optional.of(new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataUploadCsvData.class.getName(),
                    FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_IMPLEMENTATION_DATE,
                    rowData));
        }
        return Optional.empty();
    }
}
