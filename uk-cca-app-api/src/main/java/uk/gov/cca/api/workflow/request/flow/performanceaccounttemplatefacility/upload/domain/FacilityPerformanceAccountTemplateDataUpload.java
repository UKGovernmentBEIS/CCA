package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityPerformanceAccountTemplateDataUpload {

    @NotNull
    private Year targetYear;

    @NotEmpty
    @Builder.Default
    private Set<UUID> files = new HashSet<>();
}
