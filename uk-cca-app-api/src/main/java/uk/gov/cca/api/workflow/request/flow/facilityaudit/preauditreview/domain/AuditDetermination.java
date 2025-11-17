package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditDetermination {

    @NotNull
    @PastOrPresent
    private LocalDate reviewCompletionDate;

    @NotNull
    private Boolean furtherAuditNeeded;

    @Size(max = 1000)
    private String reviewComments;
}
