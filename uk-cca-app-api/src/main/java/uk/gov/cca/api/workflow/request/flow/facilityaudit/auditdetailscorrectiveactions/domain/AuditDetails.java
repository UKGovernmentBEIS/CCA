package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditDetails {

    @NotNull
    private AuditTechnique auditTechnique;

    @NotNull
    @PastOrPresent
    private LocalDate auditDate;

    @NotNull
    @Size(max = 10000)
    private String comments;

    @NotNull
    private LocalDate finalAuditReportDate;

    @NotEmpty
    @Builder.Default
    private Set<UUID> auditDocuments = new HashSet<>();
}
