package uk.gov.cca.api.mireport.common.customreport;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportParams;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CustomMiReportParams extends MiReportParams {
    @NotEmpty
    private String sqlQuery;
}
