package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain;

import jakarta.validation.constraints.NotNull;
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
public class CorrectiveAction {

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    @Size(max = 10000)
    private String details;

    @NotNull
    private LocalDate deadline;
}
