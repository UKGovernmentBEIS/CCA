package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataUpload {

    @NotNull
    private PerformanceDataTargetPeriodType performanceDataTargetPeriodType;

    @NotEmpty
    @Builder.Default
    private Set<UUID> reportPackages = new HashSet<>();
}
