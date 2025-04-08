package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;

import java.time.LocalDate;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TP6PerformanceData.class, name = "TP6"),
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceData {

    @NotNull
    private PerformanceDataTargetPeriodType type;

    // Target Period
    @NotNull
    private PerformanceDataTargetPeriodType targetPeriod;

    // Sector's acronym
    @NotBlank
    private String sector;

    // Report Version
    @Positive
    private Integer reportVersion;

    // Template Vers.
    @NotBlank
    private String templateVersion;

    // Report Date
    private LocalDate reportDate;

    @NotNull(message = "{performanceData.submissionType}")
    private PerformanceDataSubmissionType submissionType;

    // Target type
    @NotNull
    private AgreementCompositionType targetType;
}
