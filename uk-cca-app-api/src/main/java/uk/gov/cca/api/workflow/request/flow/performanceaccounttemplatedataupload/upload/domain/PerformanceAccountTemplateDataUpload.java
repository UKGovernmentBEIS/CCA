package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "#targetPeriodType eq 'TP6'")
public class PerformanceAccountTemplateDataUpload {

    @NotNull
    private TargetPeriodType targetPeriodType;

    @NotEmpty
    @Builder.Default
    private Set<UUID> reportPackages = new HashSet<>();
}
