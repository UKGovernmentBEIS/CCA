package uk.gov.cca.api.account.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetUnitAccountSearchCriteria {

    @Schema(description = "The term to search by account name")
    @Size(min = 3, max = 255)
    private String term;

    @Schema(description = "The page number starting from zero")
    @NotNull
    @Min(0)
    private Integer page;

    @Schema(description = "The page size")
    @NotNull
    @Min(1)
    private Integer size;
}
