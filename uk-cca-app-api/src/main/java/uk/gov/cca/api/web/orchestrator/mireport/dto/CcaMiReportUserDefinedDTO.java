package uk.gov.cca.api.web.orchestrator.mireport.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.netz.api.mireport.userdefined.custom.ValidSqlQuery;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CcaMiReportUserDefinedDTO {

    @NotNull
    @Size(max = 255)
    private String reportName;

    @Size(max = 10000)
    private String description;

    @NotNull
    @Size(max = 50000)
    @ValidSqlQuery
    private String queryDefinition;
}
